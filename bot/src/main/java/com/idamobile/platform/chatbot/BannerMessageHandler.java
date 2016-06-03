package com.idamobile.platform.chatbot;

import com.github.zjor.telegram.bot.api.dto.Message;
import com.github.zjor.telegram.bot.api.dto.ParseMode;
import com.github.zjor.telegram.bot.api.dto.methods.SendPhoto;
import com.github.zjor.telegram.bot.framework.dispatch.AbstractMessageHandler;
import com.github.zjor.telegram.bot.framework.dispatch.HandlingFailedException;
import com.idamobile.platform.chatbot.util.LocalizationUtils;
import com.idamobile.platform.light.core.ws.client.WsEndpointClient;
import com.idamobile.platform.light.core.ws.dto.banners.WsBannerDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
        String filename = getFilename(imageUrl);
        log.info("Requesting image from: {}", imageUrl);

        sendPhoto(imageUrl, in -> {
            SendPhoto photoReq = new SendPhoto("" + userId, null, null, null);
            getTelegram().sendPhoto(photoReq, in, filename);
        });

        if (title.length() > 0 || text.length() > 0) {
            StringBuilder content = new StringBuilder("<b>");
            content.append(title).append("</b>\n\n").append(text);
            replyWithText(userId, content.toString(), ParseMode.HTML, Keyboard.KEYBOARD);
        }
    }

    private void sendPhoto(String imageUrl, Consumer<InputStream> sender) throws HandlingFailedException {
        HttpClient httpClient = HttpClients.createDefault();
        try {
            HttpResponse httpResponse = httpClient.execute(new HttpGet(imageUrl));
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                sender.accept(httpResponse.getEntity().getContent());
                EntityUtils.consume(httpResponse.getEntity());
            } else {
                throw new HandlingFailedException("Failed to download banner image: " + httpResponse.getStatusLine());
            }
        } catch (IOException e) {
            throw new HandlingFailedException("Error requesting banner image", e);
        }
    }

    private static final Pattern FILENAME_REGEX = Pattern.compile("([^/.]*\\.\\w*)$");

    private static String getFilename(String url) {
        Matcher m = FILENAME_REGEX.matcher(url);
        if (m.find()) {
            return m.group(1);
        }

        throw new IllegalArgumentException("Unable to parse filename");
    }

}
