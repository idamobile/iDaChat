package com.idamobile.platform.chatbot.facebook.api.dto;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Button {

    public static final String TYPE_POSTBACK = "postback";

    @SerializedName("type")
    private String type;

    @SerializedName("title")
    private String title;

    @SerializedName("payload")
    private String payload;

    public static Button createPostback(String title, String payload) {
        return new Button(TYPE_POSTBACK, title, payload);
    }
}
