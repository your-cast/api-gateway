package com.yourcast.apigateway.channel;

import com.yourcast.apigateway.api.model.ApiErrorMessage;
import com.yourcast.apigateway.api.model.GatewayApiResponse;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.yourcast.apigateway.api.model.GatewayApiResponseError;
import com.yourcast.apigateway.model.Transport;
import com.yourcast.apigateway.model.dto.Payload;
import com.yourcast.apigateway.model.enums.Status;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class ResponseConsumer implements Runnable {
    private KafkaListenerQueue inQueue;
    private Map<String, ResponseTuple> deferredRegister;
    private ObjectMapper objectMapper;

    public ResponseConsumer(
            KafkaListenerQueue inQueue,
            Map<String, ResponseTuple> deferredRegister,
            ObjectMapper objectMapper
    ) {
        this.inQueue = inQueue;
        this.deferredRegister = deferredRegister;
        this.objectMapper = objectMapper;
    }

    public void run() {
        try {
            do {
                Transport response = inQueue.pop();
                if (response == null) {
                    Thread.sleep(10);
                } else {
                    processResponse(response);
                }
            } while (!Thread.currentThread().isInterrupted());
        } catch (InterruptedException ie) {
            LOG.error("ResponseConsumer.<main>: Interrupted! " + ie.getMessage(), ie);
            Thread.currentThread().interrupt();
        }
    }

    private void processResponse(Transport response) {
        LOG.info("ResponseConsumer.processTransport: " + response.toString());
        ResponseTuple responseTuple = deferredRegister.remove(response.getToken());
        try {
            if (responseTuple != null) {
                Payload payload = parsePayload(response.getPayload(), responseTuple.getResponseType());
                Long now = new Date().getTime();
                Long responseTime = now - responseTuple.getInitDate();
                LOG.info("RESPONSE_TIME to Receive Request with token {} = {} ms", response.getToken(), responseTime);
                int status = 200;

                if (response.getStatus() != null) {
                    status = convertStatusToHttpStatus(response.getStatus()).value();
                    LOG.debug("ResponseConsumer.processTransport: payload has status {}", status);
                }

                if (status != 200) {
                    List<GatewayApiResponseError> errors = new ArrayList<>();
                    if (response.getErrors() != null && response.getErrors().length > 0) {
                        try {
                            List<ApiErrorMessage> salaryErrors = objectMapper.readValue(response.getErrors(), new TypeReference<>() {});
                            errors = salaryErrors.stream()
                                    .map(salaryError -> GatewayApiResponseError.builder()
                                            .code(salaryError.getCode())
                                            .message(salaryError.getMessage())
                                            .build())
                                    .collect(Collectors.toList());
                        } catch (IOException e) {
                            LOG.error("ResponseConsumer.parsePayload: failed to parse errors.", e);
                        }
                    }

//                    responseTuple.getDeferredResult().setErrorResult(
//                            GatewayApiResponse.status(convertStatusToHttpStatus(response.getStatus())).body(GatewayApiResponse.errors(errors))
//                    );
                } else if (payload == null) {
//                    responseTuple.getDeferredResult().setResult(
//                            ResponseEntity.ok(GatewayApiResponse.ok(GenericResponseData.builder().message(response.getStatus()).build()))
//                    );
                } else {
                    responseTuple.getDeferredResult().setResult(ResponseEntity.ok(GatewayApiResponse.ok(payload)));
                    LOG.debug("ResponseConsumer.processResponse: result is set");
                    if (responseTuple.getResultHandler() != null) {
                        LOG.debug("ResponseConsumer.processResponse: responseTuple has result handler. Processing...");
                        responseTuple.getResultHandler().run();
                    }
                }
            }
        } catch (Exception ex) {
            LOG.error("Exception occurs while processing response : " + ex.getMessage());
        }
    }

    private Payload parsePayload(byte[] payload, Class clazz) {
        if (clazz == null || payload == null || payload.length < 1) {
            return null;
        }
        Object result = null;
        try {
            result = objectMapper.readValue(payload, clazz);
        } catch (IOException e) {
            LOG.error("ResponseConsumer.parsePayload: failed to parse payload.", e);
        }
        return (Payload) result;
    }

    private static HttpStatus convertStatusToHttpStatus(Status status) {
        return switch (status) {
            case OK -> HttpStatus.OK;
            case NO_CONTENT -> HttpStatus.NO_CONTENT;
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case BAD_REQUEST -> HttpStatus.BAD_REQUEST;
            case FORBIDDEN -> HttpStatus.FORBIDDEN;
            case INTERNAL_SERVER_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
