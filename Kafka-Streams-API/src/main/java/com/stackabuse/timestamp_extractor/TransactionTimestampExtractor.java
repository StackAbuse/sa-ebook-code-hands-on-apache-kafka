package com.stackabuse.timestamp_extractor;

import com.stackabuse.model.Transaction;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;

public class TransactionTimestampExtractor implements TimestampExtractor {

    @Override
    public long extract(ConsumerRecord<Object, Object> record, long previousTimestamp) {
        Transaction purchasePurchaseTransaction = (Transaction) record.value();
        return purchasePurchaseTransaction.getPurchaseDate().getTime();
    }
}
