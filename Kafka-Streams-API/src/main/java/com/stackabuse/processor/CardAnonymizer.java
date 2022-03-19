package com.stackabuse.processor;

import com.stackabuse.model.Transaction;
import org.apache.kafka.streams.processor.AbstractProcessor;

public class CardAnonymizer extends AbstractProcessor<String, Transaction> {

    private static final String CC_NUMBER_REPLACEMENT="xxxx-xxxx-xxxx-";

    @Override
    public void process(String key, Transaction transaction) {
        String last4Digits = transaction.getCardNumber().split("-")[3];
        Transaction updated = Transaction.builder(transaction).cardNumber(CC_NUMBER_REPLACEMENT+last4Digits).build();
        context().forward(key,updated);
        context().commit();
    }
}
