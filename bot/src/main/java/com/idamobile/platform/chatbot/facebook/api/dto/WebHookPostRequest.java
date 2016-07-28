package com.idamobile.platform.chatbot.facebook.api.dto;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebHookPostRequest {

    @SerializedName("object")
    private String object;

    @SerializedName("entry")
    private List<Entry> entry;
}
