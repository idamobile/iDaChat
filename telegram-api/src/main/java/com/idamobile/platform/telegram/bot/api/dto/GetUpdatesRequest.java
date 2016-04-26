package com.idamobile.platform.telegram.bot.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetUpdatesRequest {

    private Integer offset;

    private Integer limit;

    private Integer timeout;

}
