package com.idamobile.platform.chatbot;

import com.github.zjor.telegram.bot.api.dto.Location;
import com.github.zjor.telegram.bot.api.dto.Message;
import com.github.zjor.telegram.bot.api.dto.ParseMode;
import com.github.zjor.telegram.bot.api.dto.methods.SendLocation;
import com.github.zjor.telegram.bot.framework.dispatch.AbstractMessageHandler;
import com.github.zjor.telegram.bot.framework.dispatch.HandlingFailedException;
import com.idamobile.platform.light.core.ws.client.WsEndpointClient;
import com.idamobile.platform.light.core.ws.dto.WsCurrencyRateDTO;
import com.idamobile.platform.light.core.ws.dto.contacts.WsContactDTO;
import com.idamobile.platform.light.core.ws.dto.locations.WsGetNearestLocationRequestDTO;
import com.idamobile.platform.light.core.ws.dto.locations.WsLocationDTO;
import com.idamobile.platform.light.core.ws.dto.news.WsGetNewsRequestDTO;
import com.idamobile.platform.light.core.ws.dto.news.WsGetNewsResponseDTO;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
    private WsEndpointClient client;

    private String getContacts() {
        WsContactDTO[] cs = client.getContacts().getContacts();
        if (cs == null || cs.length == 0) {
            return "No contacts";
        }
        StringBuilder contacts = new StringBuilder();
        Arrays.stream(cs).forEach(c -> {
            contacts.append("<b>").append(getLocalizedValue(c.getName())).append("</b>\n");
            if ("PHONE".equals(c.getType())) {
                contacts.append("&#128222; ");
            }
            contacts.append(getLocalizedValue(c.getValue())).append("\n\n");

        });
        return contacts.toString();
    }

    private String getExchangeRates() {
        Map<String, List<WsCurrencyRateDTO>> index = new HashMap<>();
        Map<String, String> names = new HashMap<>();
        Arrays.stream(client.getCurrencyRates().getRates()).forEach(rate -> {
            if (!index.containsKey(rate.getType())) {
                index.put(rate.getType(), new LinkedList<>());
                names.put(rate.getType(), getLocalizedValue(rate.getGroupName()));
            }
            index.get(rate.getType()).add(rate);
        });
        DateFormat dateFormat = new SimpleDateFormat("dd.mm.yyyy");
        StringBuilder rates = new StringBuilder("&#128197; Данные актуальны на ").append(dateFormat.format(new Date())).append("\n");
        for (String type : index.keySet()) {
            rates.append("\n[ ").append(names.get(type)).append(" ]\n");
            index.get(type).stream().forEach(r -> {
                rates.append(MessageFormat.format("<b>{0} &#8596; {1}</b>\n", r.getFirstCurrency(), r.getSecondCurrency()));
                rates.append("  &#8226; Покупка:  ").append(r.getBuy()).append('\n');
                rates.append("  &#8226; Продажа:  ").append(r.getSell()).append('\n');
            });
        }
        return rates.toString();
    }

    private String getLastNews() {
        WsGetNewsResponseDTO res = client.getNewsFeed(new WsGetNewsRequestDTO(1));
        StringBuilder news = new StringBuilder();
        Arrays.stream(res.getNews()).forEach(n -> {
            news
                    .append(sanitize(n.getPreview()))
                    .append('\n')
                    .append(n.getUrl()).append('\n')
                    .append(n.getCreationDate());
        });
        return news.toString();
    }

    private String sanitize(String content) {
        content = content.replaceAll("</p><p>", "\n\n");
        return content.replaceAll("<.*?>", "");
    }

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

            String contacts = submit(() -> getContacts(), getProgressNotifier(userId), 1000);
            replyWithText(userId, contacts, ParseMode.HTML, Keyboard.KEYBOARD);

        } else if (Keyboard.KEY_RATES.equals(text)) {

            String rates = submit(() -> getExchangeRates(), getProgressNotifier(userId), 1000);
            replyWithText(userId, rates, ParseMode.HTML, Keyboard.KEYBOARD);

        } else if (Keyboard.KEY_NEWS.equals(text)) {

            String news = submit(() -> getLastNews(), getProgressNotifier(userId), 1000);
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
