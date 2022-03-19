package com.stackabuse.transformer;

import com.stackabuse.model.RewardAccumulator;
import com.stackabuse.model.Transaction;
import org.apache.kafka.streams.kstream.ValueTransformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;

import java.util.Objects;

public class TransactionRewardTransformer implements ValueTransformer<Transaction, RewardAccumulator> {

    private KeyValueStore<String, Integer> stateStore;
    private final String storeName;

    public TransactionRewardTransformer(String storeName) {
        Objects.requireNonNull(storeName,"Store Name can't be null");
        this.storeName = storeName;
    }

    @Override
    public void init(ProcessorContext context) {
        stateStore = context.getStateStore(storeName);
    }

    @Override
    public RewardAccumulator transform(Transaction value) {
        RewardAccumulator rewardAccumulator = RewardAccumulator.builder(value).build();
        Integer accumulatedSoFar = stateStore.get(rewardAccumulator.getCustomerName());

        if (Objects.nonNull(accumulatedSoFar)) {
            rewardAccumulator.addRewardPoints(accumulatedSoFar);
        }
        stateStore.put(rewardAccumulator.getCustomerName(), rewardAccumulator.getTotalRewardPoints());
        return rewardAccumulator;
    }

    @Override
    public void close() {}
}
