package com.idamobile.platform.chatbot;

import com.github.zjor.telegram.bot.api.dto.Location;
import com.github.zjor.telegram.bot.api.dto.Message;
import com.github.zjor.telegram.bot.api.dto.ParseMode;
import com.github.zjor.telegram.bot.api.dto.SendLocationRequest;
import com.github.zjor.telegram.bot.framework.dispatch.AbstractMessageHandler;
import com.github.zjor.telegram.bot.framework.dispatch.HandlingFailedException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.idamobile.platform.light.core.ws.client.WsEndpointClient;
import com.idamobile.platform.light.core.ws.dto.WsCurrencyRateDTO;
import com.idamobile.platform.light.core.ws.dto.contacts.WsContactDTO;
import com.idamobile.platform.light.core.ws.dto.locations.WsGetNearestLocationRequestDTO;
import com.idamobile.platform.light.core.ws.dto.locations.WsLocationDTO;
import com.idamobile.platform.light.core.ws.dto.news.WsGetNewsRequestDTO;
import com.idamobile.platform.light.core.ws.dto.news.WsGetNewsResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


@Slf4j
public class KeyboardMessageHandler extends AbstractMessageHandler {

    public static final String I18N = "i18n";

    @Inject
    private WsEndpointClient client;


    private String getLocalizedValue(String value) {
        if (StringUtils.isEmpty(value)) {
            return value;
        }
        if (value.startsWith(I18N)) {
            String json = value.substring(I18N.length());
            Map<String, String> values = new Gson().fromJson(json, new TypeToken<Map<String, String>>() {
            }.getType());
            return values.get("ru");
        } else {
            return value;
        }
    }

    private String getContacts() {
        WsContactDTO[] cs = client.getContacts().getContacts();
        if (cs == null || cs.length == 0) {
            return "No contacts";
        }
        StringBuilder contacts = new StringBuilder();
        Arrays.stream(cs).forEach(c -> {
            contacts.append("<b>").append(getLocalizedValue(c.getName())).append("</b>\n");
            if ("PHONE".equals(c.getType())) {
                contacts.append("&#128222; ");
            }
            contacts.append(getLocalizedValue(c.getValue())).append("\n\n");

        });
        return contacts.toString();
    }

    private String getExchangeRates() {
        Map<String, List<WsCurrencyRateDTO>> index = new HashMap<>();
        Map<String, String> names = new HashMap<>();
        Arrays.stream(client.getCurrencyRates().getRates()).forEach(rate -> {
            if (!index.containsKey(rate.getType())) {
                index.put(rate.getType(), new LinkedList<>());
                names.put(rate.getType(), getLocalizedValue(rate.getGroupName()));
            }
            index.get(rate.getType()).add(rate);
        });
        DateFormat dateFormat = new SimpleDateFormat("dd.mm.yyyy");
        StringBuilder rates = new StringBuilder("&#128197; Данные актуальны на ").append(dateFormat.format(new Date())).append("\n");
        for (String type : index.keySet()) {
            rates.append("\n[ ").append(names.get(type)).append(" ]\n");
            index.get(type).stream().forEach(r -> {
                rates.append(MessageFormat.format("<b>{0} &#8596; {1}</b>\n", r.getFirstCurrency(), r.getSecondCurrency()));
                rates.append("  &#8226; Покупка:  ").append(r.getBuy()).append('\n');
                rates.append("  &#8226; Продажа:  ").append(r.getSell()).append('\n');
            });
        }
        return rates.toString();
    }

    private String getLastNews() {
        WsGetNewsResponseDTO res = client.getNewsFeed(new WsGetNewsRequestDTO(1));
        StringBuilder news = new StringBuilder();
        Arrays.stream(res.getNews()).forEach(n -> {
            news
                    .append(sanitize(n.getPreview()))
                    .append('\n')
                    .append(n.getUrl()).append('\n')
                    .append(n.getCreationDate());
        });
        return news.toString();
    }

    private String sanitize(String content) {
        content = content.replaceAll("</p><p>", "\n\n");
        return content.replaceAll("<.*?>", "");
    }

    public void handleNearestAtm(Message message) {
        int userId = message.getFrom().getId();
        if (message.getLocation() == null) {
            replyWithText(userId, "Sorry, I don't know your location", Keyboard.KEYBOARD);
        } else {
            Location l = message.getLocation();
            try {
                WsLocationDTO res = client.getNearestLocation(new WsGetNearestLocationRequestDTO(l.getLatitude(), l.getLongitude())).getLocation();
                getTelegram().sendLocation(new SendLocationRequest("" + userId, res.getLat(), res.getLng(), null, null, Keyboard.KEYBOARD));

                StringBuilder info = new StringBuilder("<b>" + getLocalizedValue(res.getName()) + "</b>\n")
                        .append('\n').append("[ Address ]")
                        .append('\n').append(getLocalizedValue(res.getAddress()))
                        .append("\n\n[ Working hours ]")
                        .append('\n').append(getLocalizedValue(res.getOperationTime()));
                replyWithText(userId, info.toString(), ParseMode.HTML, Keyboard.KEYBOARD);
            } catch (Throwable t) {
                log.error("Failed to get nearest location: " + t.getMessage(), t);
                replyWithText(userId, "Sorry, I couldn't get the nearest location: " + t.getMessage());
            }
        }
    }


    @Override
    public boolean handle(Message message) throws HandlingFailedException {
        String text = message.getText();
        int userId = message.getFrom().getId();

        if (Keyboard.KEY_ATM.equals(text) || message.getLocation() != null) {
            handleNearestAtm(message);
        } else if (Keyboard.KEY_CONTACTS.equals(text)) {
            replyWithText(userId, getContacts(), ParseMode.HTML, Keyboard.KEYBOARD);
        } else if (Keyboard.KEY_RATES.equals(text)) {
            replyWithText(userId, getExchangeRates(), ParseMode.HTML, Keyboard.KEYBOARD);
        } else if (Keyboard.KEY_NEWS.equals(text)) {
            replyWithText(userId, getLastNews(), ParseMode.HTML, Keyboard.KEYBOARD);
        } else {
            return false;
        }

        return true;
    }

}
