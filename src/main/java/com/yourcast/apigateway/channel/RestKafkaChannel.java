package com.yourcast.apigateway.channel;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.context.request.async.DeferredResult;

import com.yourcast.apigateway.model.Transport;
import com.yourcast.apigateway.model.enums.CommandType;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestKafkaChannel {
    @Value("${spring.kafka.consumer.topics.message-bus-request}")
    private String topic;

    @Value("${spring.kafka.producer.topics.message-bus-response}")
    private String listenerTopic;

    @Value("${spring.application.consumer-threads}")
    private int consumerThreads = 2;

    private final KafkaTemplate<String, Transport> kafkaTemplate;
    private final KafkaListenerQueue inQueue;
    private final ObjectMapper objectMapper;
    private final ThreadPoolExecutor consumerExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(consumerThreads);
    private final Map<String, ResponseTuple> deferredRegister;

    @PostConstruct
    public void afterInitBean() {
        consumerExecutor.submit(new ResponseConsumer(inQueue, deferredRegister, objectMapper));
    }

    @PreDestroy
    public void preDestroyBean() {
        consumerExecutor.shutdownNow();
    }

    public KafkaListenerQueue getInQueue() {
        return inQueue;
    }

    public synchronized void deleteResponse(String key) {
        deferredRegister.remove(key);
    }

    public void sendRequest(
        String token,
        CommandType command,
        byte[] payload,
        DeferredResult result,
        Runnable responseHandler
    ) {
        if (result != null) {
            deferredRegister.put(token, new ResponseTuple(result, command.getResponsePayload(), responseHandler));
        }

        LOG.debug("RestKafkaChannel.sendRequest: command " + command.getCommand());

        try {
            Transport request = Transport.builder()
                    .token(token)
                    .command(command.getCommand())
                    .payload(payload)
                    .sourceIdentity(listenerTopic)
                    .build();

            ListenableFuture<SendResult<String, Transport>> future = kafkaTemplate.send(topic, token, request);

            future.addCallback(new ListenableFutureCallback<>() {
                @Override
                public void onSuccess(SendResult<String, Transport> result) {
                    LOG.info("RestKafkaChannel.sendRequest: successfully sent: " + request);
                }

                @Override
                public void onFailure(Throwable ex) {
                    LOG.error("RestKafkaChannel.sendRequest: failed to send. Msg: " + ex.getMessage(), ex);
                }
            });
        } catch (Exception ex) {
            LOG.error("RestKafkaChannel.sendRequest: " + ex.getMessage(), ex);
        }
    }
}
