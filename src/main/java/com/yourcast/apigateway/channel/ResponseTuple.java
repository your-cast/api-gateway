package com.yourcast.apigateway.channel;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.context.request.async.DeferredResult;

import java.time.ZonedDateTime;

@Getter
@Setter
@ToString
public class ResponseTuple {
    private final Long initDate = ZonedDateTime.now().toInstant().toEpochMilli();

    private DeferredResult deferredResult;
    private Class responseType;
    private Runnable resultHandler;

    public ResponseTuple(DeferredResult deferredResult, Class responseType, Runnable resultHandler) {
        this.deferredResult = deferredResult;
        this.responseType = responseType;
        this.resultHandler = resultHandler;
    }
}
