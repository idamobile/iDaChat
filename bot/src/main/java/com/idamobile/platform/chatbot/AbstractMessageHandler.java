package com.idamobile.platform.chatbot;

import com.github.zjor.telegram.bot.api.dto.ReplyKeyboardMarkup;
import com.github.zjor.telegram.bot.api.dto.SendMessageRequest;
import com.github.zjor.telegram.bot.framework.dispatch.MessageContext;
import com.github.zjor.telegram.bot.framework.dispatch.MessageHandler;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public abstract class AbstractMessageHandler implements MessageHandler {

    protected List<SendMessageRequest> replyWithText(MessageContext context, String text) {
        return Collections.singletonList(new SendMessageRequest(context.getUser().getTelegramId(), text));
    }

    protected List<SendMessageRequest> replyWithText(MessageContext context, String text, ReplyKeyboardMarkup keyboard) {
        SendMessageRequest req = new SendMessageRequest(context.getUser().getTelegramId(), text);
        req.setReplyMarkup(keyboard);
        return Collections.singletonList(req);
    }

    protected boolean checkPreviousMessage(MessageContext context, Predicate<String> predicate) {
        return checkNth(context, predicate, 0);
    }

    protected boolean checkNth(MessageContext context, Predicate<String> predicate, int n) {
        return context.getConversation().stream()
                .skip(n)
                .findFirst()
                .flatMap(m -> Optional.ofNullable(m.getText()))
                .filter(predicate)
                .isPresent();
    }



}
