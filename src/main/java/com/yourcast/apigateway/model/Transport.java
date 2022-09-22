package com.yourcast.apigateway.model;

import com.yourcast.apigateway.model.enums.Status;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transport implements Serializable {
    private String command;
    private String token;
    private String sourceIdentity;
    private Status status;
    private byte[] payload;
    private byte[] errors;
}
