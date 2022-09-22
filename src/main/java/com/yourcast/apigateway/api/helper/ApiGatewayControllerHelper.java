package com.yourcast.apigateway.api.helper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

import com.yourcast.apigateway.api.model.GatewayApiResponse;
import com.yourcast.apigateway.channel.RestKafkaChannel;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Getter
@Component
@AllArgsConstructor
public class ApiGatewayControllerHelper {
    @Value("${spring.application.deferred-result-ttl}")
    private final long deferredResultTTL = 600;

    private final RestKafkaChannel channel;
    private final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    public <T> DeferredResult<T> createResult(String api, String key) {
        DeferredResult<T> result = new DeferredResult<>(deferredResultTTL);

        result.onError((Throwable t) -> {
            result.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GatewayApiResponse.badRequest(api + ": An error occurred.")));
            channel.deleteResponse(key);
        });

        result.onCompletion(() -> LOG.info(api + ": Processing complete"));
        result.onTimeout(() -> {
            result.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                    .body(GatewayApiResponse.badRequest(api + ": Request timeout occurred.")));
            LOG.error(api + ": Request timeout occurred.");
            channel.deleteResponse(key);
        });

        return result;
    }
}
