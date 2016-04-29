package com.idamobile.platform.telegram.bot.framework;

import com.idamobile.platform.telegram.bot.api.Telegram;
import com.idamobile.platform.telegram.bot.api.dto.Update;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.ExecutorService;

@Slf4j
public class Dispatcher {

    @Inject
    private Telegram telegram;

    private int updatesOffset;

    @Inject
    private UpdateHandler updateHandler;

    private ExecutorService executor;

    private boolean started;

    public Dispatcher(ExecutorService executor) {
        this.executor = executor;
    }

    public void start() {
        started = true;

        executor.submit(() -> {
            log.info("started");
            while (started) {
                try {

                    List<Update> updates = telegram.getUpdates(updatesOffset + 1, 5, 30);
                    log.info("{}", updates);
                    updates.stream().forEach(u -> {
                        updatesOffset = Math.max(updatesOffset, u.getUpdateId());
                        updateHandler.handle(u);
                    });
                } catch (Throwable e) {
                    log.error("Updates processing failed: " + e.getMessage(), e);
                    started = false;
                }
            }
            log.info("stopped");
        });

    }

    public void stop() {
        started = false;
        executor.shutdown();
    }
}
