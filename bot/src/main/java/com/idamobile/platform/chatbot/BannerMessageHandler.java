package com.idamobile.platform.chatbot;

import com.github.zjor.telegram.bot.api.dto.Message;
import com.github.zjor.telegram.bot.api.dto.ParseMode;
import com.github.zjor.telegram.bot.api.dto.methods.SendPhoto;
import com.github.zjor.telegram.bot.framework.dispatch.AbstractMessageHandler;
import com.github.zjor.telegram.bot.framework.dispatch.HandlingFailedException;
import com.idamobile.platform.chatbot.util.ImageLoaderUtils;
import com.idamobile.platform.chatbot.util.LocalizationUtils;
import com.idamobile.platform.light.core.ws.client.WsEndpointClient;
import com.idamobile.platform.light.core.ws.dto.banners.WsBannerDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
public class BannerMessageHandler extends AbstractMessageHandler {

    public static final String COMMAND_BANNERS = "/banners";

    private static final String BANNER_TYPE_PHONE_SMALL = "PHONE_SMALL";

    @Inject
    private WsEndpointClient client;

    @Override
    public boolean handle(Message message) throws HandlingFailedException {

        int userId = message.getFrom().getId();

        if (COMMAND_BANNERS.equals(message.getText())) {

            List<WsBannerDTO> bs = Arrays.stream(client.getBanners().getBanners())
                    .filter(b -> BANNER_TYPE_PHONE_SMALL.equals(b.getType()))
                    .collect(Collectors.toList());

            if (bs.isEmpty()) {
                replyWithText(userId, "No banners");
            } else {
                Random rnd = new Random(System.currentTimeMillis());
                sendBanner(bs.get(rnd.nextInt(bs.size())), userId);
            }

            return true;
        }

        return false;
    }

    private void sendBanner(WsBannerDTO banner, int userId) throws HandlingFailedException {
        String title = LocalizationUtils.getLocalizedValue(StringUtils.defaultString(banner.getTitle()));
        String text = LocalizationUtils.getLocalizedValue(StringUtils.defaultString(banner.getText()));

        String imageUrl = StringUtils.defaultString(banner.getImageURL());
        imageUrl = imageUrl.replaceAll("\\$\\{image\\.type}", "ios-small");
        log.info("Requesting image from: {}", imageUrl);

        ImageLoaderUtils.fetchImage(imageUrl, (filename, in) -> {
            SendPhoto photoReq = new SendPhoto("" + userId, null, null, null);
            getTelegram().sendPhoto(photoReq, in, filename);
            return null;
        });

        if (title.length() > 0 || text.length() > 0) {
            StringBuilder content = new StringBuilder("<b>");
            content.append(title).append("</b>\n\n").append(text);
            replyWithText(userId, content.toString(), ParseMode.HTML, Keyboard.KEYBOARD);
        }
    }


}
