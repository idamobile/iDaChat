package com.idamobile.platform.chatbot;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.idamobile.platform.light.core.ws.client.WsEndpointClient;
import com.idamobile.platform.light.core.ws.dto.WsCurrencyRateDTO;
import com.idamobile.platform.light.core.ws.dto.contacts.WsContactDTO;
import com.idamobile.platform.telegram.bot.api.dto.KeyboardButton;
import com.idamobile.platform.telegram.bot.api.dto.Message;
import com.idamobile.platform.telegram.bot.api.dto.SendMessageRequest;
import com.idamobile.platform.telegram.bot.framework.MessageHandler;
import com.idamobile.platform.telegram.bot.framework.MessageHandlingCompletedException;
import com.idamobile.platform.telegram.bot.framework.MessageHandlingFailedException;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

//TODO: it's a temporary stub keyboard handler
//should be replaced with separate class per functionality

public class KeyboardMessageHandler implements MessageHandler {

    public static final String KEY_NEWS = "News";
    public static final String KEY_CONTACTS = "Contacts";
    public static final String KEY_RATES = "Exchange Rates";
    public static final String KEY_ATM = "Nearest ATM";
    public static final String I18N = "i18n";

    @Inject
    private WsEndpointClient client;

    public static final KeyboardButton[][] KEYBOARD = new KeyboardButton[][]{
            {new KeyboardButton(KEY_NEWS), new KeyboardButton(KEY_CONTACTS)},
            {new KeyboardButton(KEY_RATES), new KeyboardButton(KEY_ATM, true)}
    };

    @Override
    public void apply(Message message) throws MessageHandlingCompletedException, MessageHandlingFailedException {

        if (KEY_NEWS.equals(message.getText())) {
            String responseText = "*Официальное сообщение пресс-службы КБ «ЛОКО-Банк» (АО)*\n\n" +
                    "Уважаемые клиенты!\n" +
                    "\n" +
                    "В связи с обращениями третьих лиц по фактам заключения Банком договоров поручительства с юридическими лицами, привлекающими денежные средства для строительства в рамках Федерального закона от 30.12.2004 года № 214-ФЗ «Об участии в долевом строительстве» многоквартирных домов и иных объектов недвижимости и о внесении изменений в некоторые законодательные акты Российской Федерации», официально сообщаем, что Банк никогда не оказывал и не оказывает услуги по предоставлению подобных поручительств.";

            complete(new SendMessageRequest(message.getFrom().getId(), responseText, SendMessageRequest.PARSE_MODE_MD));
        } else if (KEY_CONTACTS.equals(message.getText())) {
            completeWithText(message, getContacts());
        } else if (KEY_RATES.equals(message.getText())) {
            complete(new SendMessageRequest(message.getFrom().getId(), getExchangeRates(), SendMessageRequest.PARSE_MODE_MD));
        } else if (KEY_ATM.equals(message.getText())) {
            completeWithText(message, "Trying to find ATM close to: " + message.getLocation());
        }

    }

    private String getLocalizedValue(String value) {
        if (StringUtils.isEmpty(value)) {
            return value;
        }
        if (value.startsWith(I18N)) {
            String json = value.substring(I18N.length());
            Map<String, String> values = new Gson().fromJson(json, new TypeToken<Map<String, String>>() {}.getType());
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
        for (String type: index.keySet()) {
            rates.append(names.get(type)).append('\n');
            index.get(type).stream().forEach(r -> {
                rates.append('\t').append(MessageFormat.format("{0}/{1} {2} {3}", r.getFirstCurrency(), r.getSecondCurrency(), r.getBuy(), r.getSell()));
                rates.append('\n');
            });
        }
        return rates.toString();
    }
}
