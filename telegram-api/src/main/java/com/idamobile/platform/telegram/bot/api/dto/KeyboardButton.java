package com.idamobile.platform.telegram.bot.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class KeyboardButton {

    /**
     * Text of the button. If none of the optional fields are used, it will be sent to the bot as a message when the button is pressed
     */
    private String text;

    /**
     * Optional. If True, the user's phone number will be sent as a contact when the button is pressed. Available in private chats only
     */
    private Boolean requestContact;

    /**
     * Optional. If True, the user's current location will be sent when the button is pressed. Available in private chats only
     */
    private Boolean requestLocation;

    public KeyboardButton(String text) {
        this.text = text;
    }

    public KeyboardButton(String text, boolean requestLocation) {
        this.text = text;
        this.requestLocation = requestLocation;
    }
}
