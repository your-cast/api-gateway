package com.yourcast.apigateway.api.model;

import lombok.Builder;
import lombok.Data;

import com.yourcast.apigateway.model.dto.Payload;

@Data
@Builder
public class GatewayApiResponseError<E> implements Payload {
    private static final long serialVersionUID = 3841590309317891274L;

    private String code;
    private String message;
    private E data;

    public static GatewayApiResponseError badRequest(String message) {
        return GatewayApiResponseError.builder()
                .code("BAD_REQUEST")
                .message(message)
                .build();
    }
}
