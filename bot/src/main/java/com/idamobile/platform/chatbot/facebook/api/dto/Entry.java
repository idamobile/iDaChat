package com.idamobile.platform.chatbot.facebook.api.dto;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Entry {

    @SerializedName("id")
    private String id;

    @SerializedName("time")
    private long time;

    @SerializedName("messaging")
    private List<MessagingEntry> messaging;
}
