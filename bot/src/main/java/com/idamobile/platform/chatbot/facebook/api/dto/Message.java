package com.idamobile.platform.chatbot.facebook.api.dto;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @SerializedName("mid")
    private String messageId;

    @SerializedName("seq")
    private Integer seq;

    @SerializedName("text")
    private String text;

    @SerializedName("attachment")
    private Attachment attachment;

    public Message(String text) {
        this.text = text;
    }

    public Message(Attachment attachment) {
        this.attachment = attachment;
    }

}
