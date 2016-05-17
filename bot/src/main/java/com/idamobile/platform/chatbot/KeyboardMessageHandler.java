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
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
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


//    @Override
//    public void apply(Message message) throws MessageHandlingCompletedException, MessageHandlingFailedException {
//
//        if (KEY_NEWS.equals(message.getText())) {
//            String responseText = "*Официальное сообщение пресс-службы КБ «ЛОКО-Банк» (АО)*\n\n" +
//                    "Уважаемые клиенты!\n" +
//                    "\n" +
//                    "В связи с обращениями третьих лиц по фактам заключения Банком договоров поручительства с юридическими лицами, привлекающими денежные средства для строительства в рамках Федерального закона от 30.12.2004 года № 214-ФЗ «Об участии в долевом строительстве» многоквартирных домов и иных объектов недвижимости и о внесении изменений в некоторые законодательные акты Российской Федерации», официально сообщаем, что Банк никогда не оказывал и не оказывает услуги по предоставлению подобных поручительств.";
//
//            complete(new SendMessageRequest(message.getFrom().getId(), responseText, SendMessageRequest.PARSE_MODE_MD));
//        } else if (KEY_CONTACTS.equals(message.getText())) {
//            completeWithText(message, getContacts());
//        } else if (KEY_RATES.equals(message.getText())) {
//            complete(new SendMessageRequest(message.getFrom().getId(), getExchangeRates(), SendMessageRequest.PARSE_MODE_MD));
//        } else if (KEY_ATM.equals(message.getText())) {
//            completeWithText(message, "Trying to find ATM close to: " + message.getLocation());
//        }
//
//    }

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
            contacts.append(MessageFormat.format("[{0}] {1}: {2}", c.getType(), getLocalizedValue(c.getName()), getLocalizedValue(c.getValue())));
            contacts.append('\n');
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
        StringBuilder rates = new StringBuilder();
        for (String type : index.keySet()) {
            rates.append(names.get(type)).append('\n');
            index.get(type).stream().forEach(r -> {
                rates.append('\t').append(MessageFormat.format("{0}/{1} {2} {3}", r.getFirstCurrency(), r.getSecondCurrency(), r.getBuy(), r.getSell()));
                rates.append('\n');
            });
        }
        return rates.toString();
    }

    private String getLastNews() {
        WsGetNewsResponseDTO res = client.getNewsFeed(new WsGetNewsRequestDTO(1));
        StringBuilder news = new StringBuilder();
        Arrays.stream(res.getNews()).forEach(n -> {
            news
                    .append(n.getPreview())
                    .append('\n')
                    .append(n.getUrl()).append('\n')
                    .append(n.getCreationDate());
        });
        return news.toString();
    }

    public String getNearestLocation() {
        client.getNearestLocation()

    }



    @Override
    public List<SendMessageRequest> handle(MessageContext context) throws HandlingFailedException {
        String text = context.getCurrentMessage().getText();

        if (Keyboard.KEY_ATM.equals(text)) {
            //TODO: support location in the incoming message &
            return replyWithText(context, null, Keyboard.KEYBOARD);

        } else if (Keyboard.KEY_CONTACTS.equals(text)) {
            return replyWithText(context, getContacts(), Keyboard.KEYBOARD);
        } else if (Keyboard.KEY_RATES.equals(text)) {
            return replyWithText(context, getExchangeRates(), Keyboard.KEYBOARD);
        } else if (Keyboard.KEY_NEWS.equals(text)) {
            return replyWithText(context, getLastNews(), Keyboard.KEYBOARD);
        }

        return Collections.emptyList();
    }
}
