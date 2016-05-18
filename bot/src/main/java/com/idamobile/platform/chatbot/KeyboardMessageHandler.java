package com.idamobile.platform.chatbot;

import com.github.zjor.telegram.bot.api.dto.SendMessageRequest;
import com.github.zjor.telegram.bot.framework.dispatch.HandlingFailedException;
import com.github.zjor.telegram.bot.framework.dispatch.MessageContext;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.idamobile.platform.light.core.ws.client.WsEndpointClient;
import com.idamobile.platform.light.core.ws.dto.WsCurrencyRateDTO;
import com.idamobile.platform.light.core.ws.dto.contacts.WsContactDTO;
import com.idamobile.platform.light.core.ws.dto.news.WsGetNewsRequestDTO;
import com.idamobile.platform.light.core.ws.dto.news.WsGetNewsResponseDTO;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


//TODO: it's a temporary stub keyboard handler
//should be replaced with separate class per functionality

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

    public String getNearestLocation() {
//        client.getNearestLocation()
        return null;
    }



    @Override
    public List<SendMessageRequest> handle(MessageContext context) throws HandlingFailedException {
        String text = context.getCurrentMessage().getText();

        if (Keyboard.KEY_ATM.equals(text)) {
            //TODO: support location in the incoming message &
            return replyWithText(context, null, Keyboard.KEYBOARD);

        } else if (Keyboard.KEY_CONTACTS.equals(text)) {
            return replyWithText(context, getContacts(), SendMessageRequest.PARSE_MODE_HTML, Keyboard.KEYBOARD);
        } else if (Keyboard.KEY_RATES.equals(text)) {
            return replyWithText(context, getExchangeRates(), SendMessageRequest.PARSE_MODE_HTML, Keyboard.KEYBOARD);
        } else if (Keyboard.KEY_NEWS.equals(text)) {
            return replyWithText(context, getLastNews(), SendMessageRequest.PARSE_MODE_HTML, Keyboard.KEYBOARD);
        }

        return Collections.emptyList();
    }
}
