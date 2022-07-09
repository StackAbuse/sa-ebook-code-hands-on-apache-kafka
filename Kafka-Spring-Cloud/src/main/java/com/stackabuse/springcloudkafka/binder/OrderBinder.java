package com.stackabuse.springcloudkafka.binder;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.SubscribableChannel;

public interface OrderBinder {

    // channels
    String ORDER_CHECKOUT = "order-checkout-0";
    String ORDER_WAREHOUSE = "order-warehouse-0";

    @Input(ORDER_CHECKOUT)
    SubscribableChannel orderCheckout();

    @Output(ORDER_WAREHOUSE)
    SubscribableChannel orderWarehouse();
}
