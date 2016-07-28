package com.idamobile.platform.chatbot.facebook.api.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class SendMessageRequest {

    @SerializedName("recipient")
    private Recipient recipient;

    @SerializedName("message")
    private Message message;

    public SendMessageRequest(String recipientId, String text) {
        recipient = new Recipient(recipientId);
        message = new Message(text);
    }

    public SendMessageRequest(String recipientId, Message message) {
        recipient = new Recipient(recipientId);
        this.message = message;
    }

}
