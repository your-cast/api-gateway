package com.yourcast.apigateway.utils;

import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import org.apache.commons.lang3.StringUtils;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.yourcast.apigateway.api.model.GatewayApiResponse;

import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlerAdvice {
    private static final String EXCEPTION_MESSAGE = "Exception message: {}";

    @ExceptionHandler(value = {MethodArgumentTypeMismatchException.class})
    public ResponseEntity<GatewayApiResponse> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            WebRequest request) {
        String message = "See input parameters: " + ex.getName() + " value is invalid.";

        LOG.info(EXCEPTION_MESSAGE, message);
        return ResponseEntity.badRequest().body(GatewayApiResponse.badRequest(message));
    }

//    @ExceptionHandler(value = {MissingRequestHeaderException.class})
//    public ResponseEntity<GatewayApiResponse> handleMissingRequestHeaderException(
//            MissingRequestHeaderException ex,
//            WebRequest request) {
//        String message = "Missing required header " + ex.getHeaderName();
//
//        LOG.info(EXCEPTION_MESSAGE, message);
//        return ResponseEntity.badRequest().body(GatewayApiResponse.badRequest(message));
//    }
//
//    @ExceptionHandler(value = {MissingServletRequestParameterException.class})
//    public ResponseEntity<GatewayApiResponse> handleMissingServletRequestParameterException(
//            MissingServletRequestParameterException ex,
//            WebRequest request
//    ) {
//        return ResponseEntity.badRequest().body(GatewayApiResponse.badRequest(ex.getMessage()));
//    }

    @ExceptionHandler(value = {MismatchedInputException.class})
    public ResponseEntity<GatewayApiResponse> handleDateTimeParseException(
            MismatchedInputException ex,
            WebRequest request) {
        String message;
        if (StringUtils.containsIgnoreCase(ex.getMessage(), "Cannot deserialize instance of `java.time.LocalDate`")) {
            message = "Input date parameter is invalid: " + ex.getPath().get(0).getFieldName();
        } else {
            message = "Bad input.";
        }

        LOG.error(EXCEPTION_MESSAGE, ex.getMessage());
        return ResponseEntity.badRequest().body(GatewayApiResponse.badRequest(message));
    }

    @ExceptionHandler(value = {InvalidFormatException.class})
    public ResponseEntity<GatewayApiResponse> handleInvalidFormatException(InvalidFormatException ex, WebRequest request) {
        String message = "Input parameter is invalid";
        if (!Objects.isNull(ex.getMessage())) {
            message = message + ", value [" + ex.getValue() + "]";
            if (!ex.getPath().isEmpty()) {
                message = message + ", field [" + ex.getPath().get(0).getFieldName() + "]";
                LOG.info(EXCEPTION_MESSAGE, message);
            }
        }

        return ResponseEntity.badRequest().body(GatewayApiResponse.badRequest(message));
    }
}
