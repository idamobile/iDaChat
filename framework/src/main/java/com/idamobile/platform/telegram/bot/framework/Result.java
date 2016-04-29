package com.idamobile.platform.telegram.bot.framework;

import com.idamobile.platform.telegram.bot.api.dto.Message;
import com.idamobile.platform.telegram.bot.api.dto.SendMessageRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

/**
 * Contains message handling result
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {

    private Optional<SendMessageRequest> result;

    private Message message;

    private Optional<Throwable> exception;

    private Optional<Class<? extends MessageHandler>> handlerClass;

    public static Result empty(Message m) {
        return new Result(Optional.empty(), m, Optional.empty(), Optional.empty());
    }

    public static Result completed(SendMessageRequest req, Message m, Class<? extends MessageHandler> h) {
        return new Result(Optional.of(req), m, Optional.empty(), Optional.of(h));
    }

    public static Result failed(Message m, Throwable t, Class<? extends MessageHandler> h) {
        return new Result(Optional.empty(), m, Optional.of(t), Optional.of(h));
    }

}
