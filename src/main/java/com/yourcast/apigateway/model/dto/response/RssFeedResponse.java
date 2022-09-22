package com.yourcast.apigateway.model.dto.response;

import lombok.Getter;
import lombok.Setter;

import com.yourcast.apigateway.model.dto.Payload;

@Getter
@Setter
public class RssFeedResponse implements Payload {
    private String token;
}
