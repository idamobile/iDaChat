package com.idamobile.platform.telegram.bot.framework;


import com.idamobile.platform.telegram.bot.api.dto.Message;
import com.idamobile.platform.telegram.bot.api.dto.SendMessageRequest;

public interface MessageHandler {

    void apply(Message message) throws MessageHandlingCompletedException, MessageHandlingFailedException;

    default void complete(SendMessageRequest req) throws MessageHandlingCompletedException {
        throw new MessageHandlingCompletedException(req, this.getClass());
    }

    default void completeWithText(Message message, String responseText) throws MessageHandlingCompletedException {
        int userId = message.getFrom().getId();
        throw new MessageHandlingCompletedException(new SendMessageRequest(userId, responseText), this.getClass());
    }

}
