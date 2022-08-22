package com.yourcast.apigateway.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommandType {
    CMD_FEED_GET_LIST("cmd_feed_get_list", String.class, String.class);

    private final String command;
    private final Class requestPayload;
    private final Class responsePayload;
}
