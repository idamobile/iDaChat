package com.idamobile.platform.chatbot;

import com.github.zjor.telegram.bot.api.dto.KeyboardButton;

public class Keyboard {

    public static final String KEY_NEWS = "News";
    public static final String KEY_CONTACTS = "Contacts";
    public static final String KEY_RATES = "Exchange Rates";
    public static final String KEY_ATM = "Nearest ATM";

    public static final KeyboardButton[][] KEYBOARD = new KeyboardButton[][]{
            {new KeyboardButton(KEY_NEWS), new KeyboardButton(KEY_CONTACTS)},
            {new KeyboardButton(KEY_RATES), new KeyboardButton(KEY_ATM, true)}
    };


}
