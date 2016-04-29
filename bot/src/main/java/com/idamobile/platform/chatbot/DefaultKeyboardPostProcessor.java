package com.idamobile.platform.chatbot;

import com.idamobile.platform.telegram.bot.api.dto.ReplyKeyboardMarkup;
import com.idamobile.platform.telegram.bot.framework.ChainManager.PostProcessor;
import com.idamobile.platform.telegram.bot.framework.PostProcessingException;
import com.idamobile.platform.telegram.bot.framework.Result;

public class DefaultKeyboardPostProcessor implements PostProcessor {

    @Override
    public Result apply(Result result) throws PostProcessingException {
        result.getResult().ifPresent(reply -> {
            if (reply.getReplyMarkup() == null) {
                reply.setReplyMarkup(new ReplyKeyboardMarkup(KeyboardMessageHandler.KEYBOARD));
            }
        });
        return result;
    }
}
