package com.stackabuse.springcloudkafkastreams.listener;

import com.stackabuse.springcloudkafkastreams.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.Stores;
import org.apache.kafka.streams.state.WindowBytesStoreSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
@Component
public class ConsumerListener {

    @Bean
    public Consumer<Message<Order>> consumer() {
        return o -> log.info("Received in fulfillment station: {}", o.getPayload());
    }

    @Bean
    public Consumer<InventoryItem> inventory() {
        return i -> log.info("Received inventory details in fulfillment station: {}", i);
    }

    // Single Input and Output Binding
    @Bean
    public Function<KStream<String, Order>, KStream<String, Order>> singleIOBind() {

        return input -> input
                .filter((key, value) -> value.getStatus() == OrderStatus.AVAILABLE
                        || value.getStatus() == OrderStatus.UNAVAILABLE
                        || value.getStatus() == OrderStatus.PACKED
                        || value.getStatus() == OrderStatus.READY_FOR_MANIFESTATION)
                .mapValues(v -> {
                    v.setStatus(v.getStatus() == OrderStatus.AVAILABLE
                        ? OrderStatus.PLACED : (v.getStatus() == OrderStatus.UNAVAILABLE
                            ? OrderStatus.CANCELLED : (v.getStatus() == OrderStatus.PACKED
                                ? OrderStatus.READY_TO_PICK : (v.getStatus() == OrderStatus.READY_FOR_MANIFESTATION
                                    ? OrderStatus.SHIPPED : OrderStatus.CANCELLED))));
                    return v;
                })
                .peek((k, v) -> log.info("Single IO Binding of Orders Processed: {}", v));
    }

    // Multiple output bindings through Kafka Streams branching
    @Bean
    public Function<KStream<String, Order>, KStream<String, Order>[]> multiIOBranchBinding() {

        Predicate<Object, Order> bookCourier = (k, v) -> v.getStatus().equals(OrderStatus.READY_TO_PICK);
        Predicate<Object, Order> isFedEx = (k, v) -> v.getCourierCompany().equals("FedEx");
        Predicate<Object, Order> isBlueDart = (k, v) -> v.getCourierCompany().equals("BlueDart");
        Predicate<Object, Order> isDHL = (k, v) -> v.getCourierCompany().equals("DHL");

        return input -> input
                .filter((key, value) -> value.getStatus() == OrderStatus.COURIER_BOOKED
                        || value.getStatus() == OrderStatus.READY_TO_PICK)
                .mapValues(v -> {
                    v.setStatus(v.getStatus() == OrderStatus.COURIER_BOOKED
                            ? OrderStatus.READY_FOR_MANIFESTATION
                            : v.getStatus());
                    return v;
                })
                .branch(bookCourier, isFedEx, isBlueDart, isDHL);
    }

    // Two input bindings and a single output binding
    @Bean
    public BiFunction<KStream<String, Order>, KTable<String, InventoryItem>, KStream<String, Order>> twoInputSingleOutputBinding() {
        return (orderStream, inventoryItemTable) -> orderStream
                .filter((key, value) -> value.getStatus() == OrderStatus.PLACED)
                .mapValues(v -> {
                    inventoryItemTable
                            .filter((ki, vi) -> vi.getName().equals(v.getProductName()))
                            .toStream()
                            .selectKey((ki, vi) -> vi.getName())
                            .mapValues(vi -> {
                                if (vi.getName().equals(v.getProductName())) {
                                    v.setStatus(OrderStatus.AVAILABLE);
                                } else {
                                    v.setStatus(OrderStatus.UNAVAILABLE);
                                }
                                log.info("Checking InventoryItem: {}", vi);
                                return vi;
                            });
                    log.info("Checking Order with InventoryItem: {}", v);
                    return v;
                });
    }

    // More than two input bindings
    @Bean
    public Function<KStream<Long, Order>,
            Function<KTable<Long, InventoryItem>,
                    Function<KTable<Long, Transaction>,
                            KStream<Long, Order>>>> multiInputsingleOutputBinding() {

        return orderStream -> (
                inventoryItem -> (
                        transaction -> (
                                orderStream
                                        .filter((key, value) -> value.getStatus() == OrderStatus.PLACED
                                                || value.getStatus() == OrderStatus.AVAILABLE)
                                        .mapValues(v -> {
                                            inventoryItem
                                                    .filter((ki, vi) ->
                                                            vi.getName().equals(v.getProductName()))
                                                    .toStream()
                                                    .mapValues(vi -> {
                                                        transaction
                                                                .filter((kt, vt) ->
                                                                        vt.getOrderId().equals(v.getId()))
                                                                .toStream()
                                                                .mapValues(vt -> {
                                                                    if (vi.getName()
                                                                            .equals(v.getProductName())) {
                                                                        v.setStatus(OrderStatus.PACKED);
                                                                    }
                                                                    log.info("Checking Transaction: {}", vt);
                                                                    return vt;
                                                                });
                                                        log.info("Checking InventoryItem: {}", vi);
                                                        return vi;
                                                    });
                                            log.info("Checking Order with InventoryItem and Transaction: {}", v);
                                            return v;
                                        })
                        )
                )
        );
    }

    @Bean
    public Consumer<KStream<String, Order>> total() {
        KeyValueBytesStoreSupplier storeSupplier = Stores.persistentKeyValueStore("all-orders-store");
        return orders -> orders
                .groupBy((k, v) -> v.getStatus().toString(),
                        Grouped.with(Serdes.String(), new JsonSerde<>(Order.class)))
                .aggregate(
                        TotalOrderProcessed::new,
                        (k, v, a) -> {
                            a.setStatus(v.getStatus());
                            a.setOrderCount(a.getOrderCount() + 1);
                            a.setProductCount(a.getProductCount() + 1);
                            a.setTotalAmount(a.getTotalAmount() + (v.getPrice() * v.getProductCount()));
                            return a;
                        },
                        Materialized.<String, TotalOrderProcessed> as(storeSupplier)
                                .withKeySerde(Serdes.String())
                                .withValueSerde(new JsonSerde<>(TotalOrderProcessed.class)))
                .toStream()
                .peek((k, v) -> log.info("Total Orders Processed: {}", v));
    }

    @Bean
    public Consumer<KStream<String, Order>> totalPerProduct() {
        KeyValueBytesStoreSupplier storeSupplier = Stores.persistentKeyValueStore("orders-per-product-store");
        return order -> order
                .selectKey((k, v) -> v.getId())
                .groupBy((k, v) -> v.getProductName(),
                        Grouped.with(Serdes.String(), new JsonSerde<>(Order.class)))
                .aggregate(
                        TotalOrderProcessed::new,
                        (k, v, a) -> {
                            a.setStatus(v.getStatus());
                            a.setOrderCount(a.getOrderCount() + 1);
                            a.setProductCount(a.getProductCount() + v.getProductCount());
                            a.setTotalAmount(a.getTotalAmount() + (v.getPrice() * v.getProductCount()));
                            return a;
                        },
                        Materialized.<String, TotalOrderProcessed> as(storeSupplier)
                                .withKeySerde(Serdes.String())
                                .withValueSerde(new JsonSerde<>(TotalOrderProcessed.class)))
                .toStream()
                .peek((k, v) -> log.info("Total orders per product({}): {}", k, v));
    }

    @Bean
    public Consumer<KStream<String, Order>> latestPerProduct() {
        WindowBytesStoreSupplier storeSupplier = Stores.persistentWindowStore(
                "latest-orders-per-product-store",
                Duration.ofSeconds(30),
                Duration.ofSeconds(30),
                false);
        return order -> order
                .selectKey((k, v) -> v.getId())
                .groupBy((k, v) -> v.getProductName(),
                        Grouped.with(Serdes.String(), new JsonSerde<>(Order.class)))
                .windowedBy(TimeWindows.of(Duration.ofSeconds(30)))
                .aggregate(
                        TotalOrderProcessed::new,
                        (k, v, a) -> {
                            a.setStatus(v.getStatus());
                            a.setOrderCount(a.getOrderCount() + 1);
                            a.setProductCount(a.getProductCount() + v.getProductCount());
                            a.setTotalAmount(a.getTotalAmount() + (v.getPrice() * v.getProductCount()));
                            return a;
                        },
                        Materialized.<String, TotalOrderProcessed> as(storeSupplier)
                                .withKeySerde(Serdes.String())
                                .withValueSerde(new JsonSerde<>(TotalOrderProcessed.class)))
                .toStream()
                .peek((k, v) -> log.info("Total per product within last 30s({}): {}", k, v));
    }
}
