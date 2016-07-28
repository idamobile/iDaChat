package com.idamobile.platform.chatbot.facebook.api.dto;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplatePayload {

    public static final String TEMPLATE_TYPE = "button";

    @SerializedName("template_type")
    private String templateType;

    @SerializedName("text")
    private String text;

    @SerializedName("buttons")
    private List<Button> buttons;

    public static TemplatePayload createButtons(String text, Button... buttons) {
        return new TemplatePayload(TEMPLATE_TYPE, text, Arrays.asList(buttons));
    }

}
