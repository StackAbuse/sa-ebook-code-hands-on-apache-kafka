package com.stackabuse.processor;

import com.stackabuse.model.RewardAccumulator;
import com.stackabuse.model.Transaction;
import org.apache.kafka.streams.processor.AbstractProcessor;

public class CustomerRewards extends AbstractProcessor<String, Transaction> {

    @Override
    public void process(String key, Transaction value) {
        RewardAccumulator accumulator = RewardAccumulator.builder(value).build();
        context().forward(key, accumulator);
        context().commit();
    }
}
