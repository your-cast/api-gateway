package com.yourcast.apigateway.channel;

import com.yourcast.apigateway.model.Transport;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class KafkaListenerQueue {
    private ConcurrentLinkedQueue<Transport> inRequest = new ConcurrentLinkedQueue<>();

    public void push(Transport request) {
        inRequest.offer(request);
    }

    public Transport pop() {
        if (inRequest.size() == 0) {
            return null;
        }

        return  inRequest.poll();
    }
}
