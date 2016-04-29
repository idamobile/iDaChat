package com.idamobile.platform.chatbot;

import com.idamobile.platform.telegram.bot.api.dto.Message;
import com.idamobile.platform.telegram.bot.framework.MessageHandler;
import com.idamobile.platform.telegram.bot.framework.MessageHandlingCompletedException;
import com.idamobile.platform.telegram.bot.framework.MessageHandlingFailedException;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;

public class StartMessageHandler implements MessageHandler {

    public static final String START_COMMAND = "/start";

    public static final String GREETING_MESSAGE = "Welcome, {0}! How can I help you?";

    @Override
    public void apply(Message message) throws MessageHandlingCompletedException, MessageHandlingFailedException {
        String text = message.getText();
        if (StringUtils.isNotEmpty(text) && text.startsWith(START_COMMAND)) {

            //TODO: read message from resources
            //TODO: support languages
            String response = MessageFormat.format(GREETING_MESSAGE, message.getFrom().getFirstName());
            completeWithText(message, response);
        }

    }
}
