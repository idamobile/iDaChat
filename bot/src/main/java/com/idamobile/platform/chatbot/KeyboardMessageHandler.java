package com.idamobile.platform.chatbot;

import com.github.zjor.telegram.bot.api.dto.Location;
import com.github.zjor.telegram.bot.api.dto.Message;
import com.github.zjor.telegram.bot.api.dto.ParseMode;
import com.github.zjor.telegram.bot.api.dto.methods.SendLocation;
import com.github.zjor.telegram.bot.framework.dispatch.AbstractMessageHandler;
import com.github.zjor.telegram.bot.framework.dispatch.HandlingFailedException;
import com.idamobile.platform.chatbot.service.LiteIntegrationService;
import com.idamobile.platform.light.core.ws.client.WsEndpointClient;
import com.idamobile.platform.light.core.ws.dto.locations.WsGetNearestLocationRequestDTO;
import com.idamobile.platform.light.core.ws.dto.locations.WsLocationDTO;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.idamobile.platform.chatbot.util.LocalizationUtils.getLocalizedValue;


@Slf4j
public class KeyboardMessageHandler extends AbstractMessageHandler {

    @Inject
    private LiteIntegrationService integrationService;

    @Inject
    private WsEndpointClient client;

    public void handleNearestAtm(Message message) {
        int userId = message.getFrom().getId();
        if (message.getLocation() == null) {
            replyWithText(userId, "Sorry, I don't know your location", Keyboard.KEYBOARD);
        } else {
            Location l = message.getLocation();
            try {
                WsLocationDTO res = submit(
                        () -> client.getNearestLocation(new WsGetNearestLocationRequestDTO(l.getLatitude(), l.getLongitude())).getLocation(),
                        getProgressNotifier(userId), 1000);

                getTelegram().sendLocation(new SendLocation("" + userId, res.getLat(), res.getLng(), Keyboard.KEYBOARD));

                StringBuilder info = new StringBuilder("<b>" + getLocalizedValue(res.getName()) + "</b>\n")
                        .append('\n').append("[ Address ]")
                        .append('\n').append(getLocalizedValue(res.getAddress()))
                        .append("\n\n[ Working hours ]")
                        .append('\n').append(getLocalizedValue(res.getOperationTime()));
                replyWithText(userId, info.toString(), ParseMode.HTML, Keyboard.KEYBOARD);
            } catch (Throwable t) {
                log.error("Failed to get nearest location: " + t.getMessage(), t);
                replyWithText(userId, "Sorry, I couldn't get the nearest location: " + t.getMessage());
            }
        }
    }


    @Override
    public boolean handle(Message message) throws HandlingFailedException {
        String text = message.getText();
        int userId = message.getFrom().getId();

        if (Keyboard.KEY_ATM.equals(text) || message.getLocation() != null) {

            handleNearestAtm(message);

        } else if (Keyboard.KEY_CONTACTS.equals(text)) {

            String contacts = submit(() -> integrationService.getContacts(), getProgressNotifier(userId), 1000);
            replyWithText(userId, contacts, ParseMode.HTML, Keyboard.KEYBOARD);

        } else if (Keyboard.KEY_RATES.equals(text)) {

            String rates = submit(() -> integrationService.getExchangeRates(), getProgressNotifier(userId), 1000);
            replyWithText(userId, rates, ParseMode.HTML, Keyboard.KEYBOARD);

        } else if (Keyboard.KEY_NEWS.equals(text)) {

            String news = submit(() -> integrationService.getLastNews(), getProgressNotifier(userId), 1000);
            replyWithText(userId, news, ParseMode.HTML, Keyboard.KEYBOARD);

        } else {
            return false;
        }

        return true;
    }

    private Consumer<Integer> getProgressNotifier(int userId) {
        String[] phrases = new String[]{
                "Wait a second, please...",
                "I'm still thinking...",
                "Seems that it takes a bit longer...",
                "Almost there!"
        };
        return n -> replyWithText(userId, phrases[n % phrases.length] + " #" + (n + 1));
    }

    /**
     * Submits task asynchronously, checks its status each interval in millis,
     * invokes notifier with number of verifications performed
     *
     * @param task
     * @param notifier
     * @param interval
     */
    public <T> T submit(Callable<T> task, Consumer<Integer> notifier, long interval) throws HandlingFailedException {
        ExecutorService exec = Executors.newFixedThreadPool(2);
        Future<T> future = exec.submit(task);
        exec.submit(() -> {
            int n = 0;
            do {
                try {
                    Thread.sleep(interval);
                    if (!future.isDone()) {
                        notifier.accept(n++);
                    }
                } catch (InterruptedException e) {
                    log.error(e.getMessage(), e);
                }
            } while (!future.isDone());
        });
        exec.shutdown();
        try {
            exec.awaitTermination(15, TimeUnit.SECONDS);
            return future.get();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new HandlingFailedException();
        }
    }

}
