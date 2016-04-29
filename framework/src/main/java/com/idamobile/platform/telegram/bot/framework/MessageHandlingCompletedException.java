package com.idamobile.platform.telegram.bot.framework;

import com.idamobile.platform.telegram.bot.api.dto.SendMessageRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MessageHandlingCompletedException extends Exception {

    /**
     * Response to be sent
     */
    private SendMessageRequest sendMessageRequest;

    /**
     * Processed by
     */
    private Class<? extends MessageHandler> messageHandler;

}
