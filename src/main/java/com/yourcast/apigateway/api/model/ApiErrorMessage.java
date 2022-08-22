package com.yourcast.apigateway.api.model;

import lombok.Getter;
import lombok.Setter;

import com.yourcast.apigateway.model.dto.Payload;

@Getter
@Setter
public class ApiErrorMessage  implements Payload {
    private String code;
    private String message;
}
