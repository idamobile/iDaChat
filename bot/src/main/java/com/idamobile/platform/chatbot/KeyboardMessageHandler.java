package com.idamobile.platform.chatbot;

import com.idamobile.platform.telegram.bot.api.dto.KeyboardButton;
import com.idamobile.platform.telegram.bot.api.dto.Message;
import com.idamobile.platform.telegram.bot.api.dto.SendMessageRequest;
import com.idamobile.platform.telegram.bot.framework.MessageHandler;
import com.idamobile.platform.telegram.bot.framework.MessageHandlingCompletedException;
import com.idamobile.platform.telegram.bot.framework.MessageHandlingFailedException;

//TODO: it's a temporary stub keyboard handler
//should be replaced with separate class per functionality

public class KeyboardMessageHandler implements MessageHandler {

    public static final String KEY_NEWS = "News";
    public static final String KEY_CONTACTS = "Contacts";
    public static final String KEY_RATES = "Exchange Rates";
    public static final String KEY_ATM = "Nearest ATM";

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
            completeWithText(message, "Tel: 8 800 250 50 50");
        } else if (KEY_RATES.equals(message.getText())) {
            String responseText = "USD/RUB 63.45/64.75\nEUR/RUB 72.40/73.70";
            complete(new SendMessageRequest(message.getFrom().getId(), responseText, SendMessageRequest.PARSE_MODE_MD));
        } else if (KEY_ATM.equals(message.getText())) {
            completeWithText(message, "Trying to find ATM close to: " + message.getLocation());
        }

    }
}
