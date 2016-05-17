package com.idamobile.platform.chatbot;

import com.github.zjor.telegram.bot.api.dto.SendMessageRequest;
import com.github.zjor.telegram.bot.framework.dispatch.HandlingFailedException;
import com.github.zjor.telegram.bot.framework.dispatch.MessageContext;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

public class StartMessageHandler extends AbstractMessageHandler {

    public static final String START_COMMAND = "/start";

    public static final String GREETING_MESSAGE = "Welcome, {0}! How can I help you?";

    @Override
    public List<SendMessageRequest> handle(MessageContext context) throws HandlingFailedException {
        String text = context.getCurrentMessage().getText();
        if (StringUtils.isNotEmpty(text) && text.startsWith(START_COMMAND)) {

            //TODO: read message from resources
            //TODO: support languages
            String response = MessageFormat.format(GREETING_MESSAGE, context.getUser().getFirstName());
            return replyWithText(context, response, Keyboard.KEYBOARD);
        }
        return Collections.emptyList();
    }
}
