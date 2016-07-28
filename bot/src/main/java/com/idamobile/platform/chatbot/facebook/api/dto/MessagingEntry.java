package com.idamobile.platform.chatbot.facebook.api.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class MessagingEntry {

    @SerializedName("sender")
    private Sender sender;

    @SerializedName("recipient")
    private Recipient recipient;

    @SerializedName("timestamp")
    private long timestamp;

    @SerializedName("message")
    private Message message;

    @SerializedName("postback")
    private PostBack postBack;

}
