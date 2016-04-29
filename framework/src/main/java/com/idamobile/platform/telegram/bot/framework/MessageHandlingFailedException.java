package com.idamobile.platform.telegram.bot.framework;

import lombok.Getter;

public class MessageHandlingFailedException extends Exception {

    /**
     * Processed by
     */
    @Getter
    private Class<? extends MessageHandler> messageHandler;

    public MessageHandlingFailedException(String message, Class<? extends MessageHandler> messageHandler) {
        super(message);
        this.messageHandler = messageHandler;
    }

    public MessageHandlingFailedException(Throwable cause, Class<? extends MessageHandler> messageHandler) {
        super(cause);
        this.messageHandler = messageHandler;
    }

    public MessageHandlingFailedException(String message, Throwable cause, Class<? extends MessageHandler> messageHandler) {
        super(message, cause);
        this.messageHandler = messageHandler;
    }



}
