package com.idamobile.platform.chatbot.facebook.api.dto;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attachment {

    public static final String TYPE_TEMPLATE = "template";

    @SerializedName("type")
    private String type;

    @SerializedName("payload")
    private TemplatePayload payload;

    public static Attachment createTemplate(TemplatePayload payload) {
        return new Attachment(TYPE_TEMPLATE, payload);
    }

}
