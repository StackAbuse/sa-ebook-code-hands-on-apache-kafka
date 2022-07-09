package com.stackabuse.springcloudkafka.subscriber;

import com.stackabuse.springcloudkafka.binder.OrderBinder;
import com.stackabuse.springcloudkafka.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderSubscriber {

    @Autowired
    private OrderBinder orderBinder;

    @StreamListener(OrderBinder.ORDER_CHECKOUT)
    public void consumeAndForward(@Payload Order order) {
        log.info("Order consumed from Checkout -> {}", order);
        orderBinder.orderWarehouse().send(
                MessageBuilder
                        .withPayload(order)
                        .setHeader(KafkaHeaders.MESSAGE_KEY, order.getId().getBytes())
                        .build());
        log.info("Order forwarded to Fulfillment for further processing -> {}", order);
    }
}
