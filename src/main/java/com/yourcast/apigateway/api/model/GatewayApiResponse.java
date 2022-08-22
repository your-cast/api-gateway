package com.yourcast.apigateway.api.model;

import lombok.Builder;
import lombok.Data;

import com.yourcast.apigateway.model.dto.Payload;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Collections;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GatewayApiResponse<T> implements Payload {
    private static final long serialVersionUID = 1076455323301546499L;

    private T data;
    private List<GatewayApiResponseError> errors;

    public static GatewayApiResponse errors(List<GatewayApiResponseError> errors) {
        return GatewayApiResponse.builder()
                .errors(errors)
                .build();
    }

    public static GatewayApiResponse badRequest(String message) {
        return GatewayApiResponse
                .errors(Collections.singletonList(GatewayApiResponseError.badRequest(message)));
    }

    public static GatewayApiResponse ok(Payload data) {
        return GatewayApiResponse.builder()
                .data(data)
                .build();
    }
}
