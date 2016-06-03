package com.idamobile.platform.chatbot.ws;

import com.github.jtail.jpa.util.EntityUtils;
import com.github.zjor.telegram.bot.api.Telegram;
import com.github.zjor.telegram.bot.api.dto.ParseMode;
import com.github.zjor.telegram.bot.api.dto.methods.SendMessage;
import com.github.zjor.telegram.bot.api.dto.methods.SendPhoto;
import com.github.zjor.telegram.bot.framework.model.TelegramUser;
import com.idamobile.platform.chatbot.Keyboard;
import com.idamobile.platform.chatbot.util.ImageLoaderUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

@Slf4j
@WebService(endpointInterface = "com.idamobile.platform.chatbot.ws.ManagementService")
public class ManagementServiceImpl implements ManagementService {

    @PersistenceContext
    private EntityManager em;

    @Inject
    private Telegram telegram;

    @Override
    public Response announce(AnnouncementRequest req) {

        String content = prepareContent(req);

        try {
            Optional<String> imageFilename = saveImage(Optional.ofNullable(StringUtils.defaultIfEmpty(req.getImageUrl(), null)));

            List<TelegramUser> users = EntityUtils.find(em, TelegramUser.class).list();
            users.stream().forEach(u -> {
                int userId = u.getTelegramId();
                imageFilename.ifPresent(filename -> {
                    try (FileInputStream image = new FileInputStream(filename)) {
                        SendPhoto photoReq = new SendPhoto("" + userId, null, null, null);
                        telegram.sendPhoto(photoReq, image, ImageLoaderUtils.getFilename(filename));
                    } catch (Exception e) {
                        log.error("Failed to send image to user: " + u + " reason: " + e.getMessage(), e);
                    }
                });

                SendMessage sendMessage = new SendMessage(userId, content, ParseMode.HTML);
                sendMessage.setReplyMarkup(Keyboard.KEYBOARD);
                telegram.sendMessage(sendMessage);
                log.info("{} send to user: {}", req, u);
            });
            return new Response(true, null);
        } catch (Throwable t) {
            log.error("Failed to save image: " + t.getMessage(), t);
            return new Response(false, t.getMessage());
        }
    }

    private String prepareContent(AnnouncementRequest req) {
        String template = "<b>{0}</b>\n\n{1}";
        return MessageFormat.format(template, req.getTitle(), req.getText());
    }

    private Optional<String> saveImage(Optional<String> urlOpt) throws Throwable {
        if (!urlOpt.isPresent()) {
            return Optional.empty();
        }

        //TODO: move to config
        String dest = "/tmp/";
        ImageLoaderUtils.Result<String> res = ImageLoaderUtils.fetchImage(urlOpt.get(), (f, i) -> {
            File output = new File(dest + f);
            FileOutputStream o = new FileOutputStream(output);
            IOUtils.copy(i, o);
            return output.getAbsolutePath();
        });

        if (res.getError() != null) {
            throw res.getError();
        }

        return Optional.of(res.getResult());
    }




}
