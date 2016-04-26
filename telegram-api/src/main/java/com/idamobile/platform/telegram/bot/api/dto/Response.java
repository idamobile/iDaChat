package com.idamobile.platform.telegram.bot.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> {

    private boolean ok;

    private String description;

    private Integer errorCode;

    private T result;

}
