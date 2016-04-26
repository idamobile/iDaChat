package com.idamobile.platform.telegram.bot.api;

import org.apache.http.impl.client.HttpClientBuilder;

public class Main {
    public static void main(String[] args) throws TelegramException {
        String token = "...";
        Telegram telegram = new Telegram(token, HttpClientBuilder.create().build());

        System.out.println(telegram.getMe());
        System.out.println(telegram.getUpdates(null, null, null));

    }
}
