package com.idamobile.platform.telegram.bot.framework;

import com.idamobile.platform.telegram.bot.api.Telegram;
import com.idamobile.platform.telegram.bot.api.TelegramException;
import com.idamobile.platform.telegram.bot.api.dto.Message;
import com.idamobile.platform.telegram.bot.api.dto.SendMessageRequest;
import com.idamobile.platform.telegram.bot.api.dto.Update;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.concurrent.ExecutorService;

@Slf4j
@Component
public class UpdateHandler {

    @Inject
    private ChainManager chainManager;

    @Inject
    private ExecutorService executorService;

    @Inject
    private Telegram telegram;

    public void handle(Update update) {
        executorService.submit(() -> {
            log.info("<= {}", update.getMessage());

            Message m = update.getMessage();
            try {
                Result result = chainManager.process(m);
                if (result.getResult().isPresent()) {
                    telegram.sendMessage(result.getResult().get());

                } else {
                    int userId = m.getFrom().getId();

                    if (result.getException().isPresent()) {
                        telegram.sendMessage(
                                new SendMessageRequest(userId,
                                        "Processing failed. Handler: " +
                                                result.getHandlerClass().get().getSimpleName() + "; Message: " +
                                                result.getException().get().getMessage()));
                    } else {
                        telegram.sendMessage(new SendMessageRequest(userId, "Unrecognized command"));
                    }
                }
            } catch (TelegramException | PreProcessingException | PostProcessingException e) {
                log.error(e.getMessage(), e);
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
            }

        });
    }

}
