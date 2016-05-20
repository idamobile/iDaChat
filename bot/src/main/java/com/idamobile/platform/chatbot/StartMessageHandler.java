package com.idamobile.platform.chatbot;

import com.github.zjor.telegram.bot.api.dto.Message;
import com.github.zjor.telegram.bot.framework.dispatch.AbstractMessageHandler;
import com.github.zjor.telegram.bot.framework.dispatch.HandlingFailedException;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;

public class StartMessageHandler extends AbstractMessageHandler {

    public static final String START_COMMAND = "/start";

    public static final String GREETING_MESSAGE = "Welcome, {0}! How can I help you?";

    @Override
    public boolean handle(Message message) throws HandlingFailedException {
        String text = message.getText();
        if (StringUtils.isNotEmpty(text) && text.startsWith(START_COMMAND)) {

            //TODO: read message from resources
            //TODO: support languages
            String response = MessageFormat.format(GREETING_MESSAGE, message.getFrom().getFirstName());
            replyWithText(message.getFrom().getId(), response, Keyboard.KEYBOARD);
            return true;
        }
        return false;
    }
}
