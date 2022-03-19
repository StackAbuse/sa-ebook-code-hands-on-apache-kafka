package com.stackabuse.processor;

import com.stackabuse.model.Transaction;
import com.stackabuse.model.TransactionPattern;
import org.apache.kafka.streams.processor.AbstractProcessor;

public class TransactionPatterns extends AbstractProcessor<String, Transaction> {

    @Override
    public void process(String key, Transaction transaction) {
        TransactionPattern transactionPattern = TransactionPattern.newBuilder().date(transaction.getPurchaseDate())
                .item(transaction.getItemPurchased())
                .zipCode(transaction.getZipCode()).build();
        context().forward(key, transactionPattern);
        context().commit();
    }
}
