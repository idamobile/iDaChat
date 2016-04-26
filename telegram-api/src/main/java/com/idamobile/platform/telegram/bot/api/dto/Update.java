package com.idamobile.platform.telegram.bot.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Update {

    private int updateId;

    private Message message;

    private Object inlineQuery;

    private Object chosenInlineResult;

    private Object callbackQuery;
}
