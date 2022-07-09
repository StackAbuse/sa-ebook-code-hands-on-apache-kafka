package com.stackabuse.springcloudkafkastreams.controller;

import com.stackabuse.springcloudkafkastreams.model.TotalOrderProcessed;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/orders")
public class QueryController {

    @Autowired
    private InteractiveQueryService queryService;

    @GetMapping("/all")
    public TotalOrderProcessed getAllTransactionsSummary() {
        ReadOnlyKeyValueStore<String, TotalOrderProcessed> keyValueStore =
                queryService.getQueryableStore("all-orders-store",
                        QueryableStoreTypes.keyValueStore());
        return keyValueStore.get("NEW");
    }

    @GetMapping("/{productId}")
    public TotalOrderProcessed getSummaryByProductId(@PathVariable("productId") String productId) {
        ReadOnlyKeyValueStore<String, TotalOrderProcessed> keyValueStore =
                queryService.getQueryableStore("orders-per-product-store",
                        QueryableStoreTypes.keyValueStore());
        return keyValueStore.get(productId);
    }

    @GetMapping("/latest/{productId}")
    public TotalOrderProcessed getLatestSummaryByProductId(@PathVariable("productId") String productId) {
        ReadOnlyKeyValueStore<String, TotalOrderProcessed> keyValueStore =
                queryService.getQueryableStore("latest-orders-per-product-store",
                        QueryableStoreTypes.keyValueStore());
        return keyValueStore.get(productId);
    }

    @GetMapping("/")
    public Map<String, TotalOrderProcessed> getSummaryByAllProducts() {
        Map<String, TotalOrderProcessed> m = new HashMap<>();
        ReadOnlyKeyValueStore<String, TotalOrderProcessed> keyValueStore =
                queryService.getQueryableStore("orders-per-product-store",
                        QueryableStoreTypes.keyValueStore());
        KeyValueIterator<String, TotalOrderProcessed> it = keyValueStore.all();
        while (it.hasNext()) {
            KeyValue<String, TotalOrderProcessed> kv = it.next();
            m.put(kv.key, kv.value);
        }
        return m;
    }

}
