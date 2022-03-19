package com.stackabuse.partitioner;

import com.stackabuse.model.Transaction;
import org.apache.kafka.streams.processor.StreamPartitioner;

public class RewardsStreamPartitioner implements StreamPartitioner<String, Transaction> {

    @Override
    public Integer partition(String s, String s2, Transaction transaction, int numPartitions) {
        return transaction.getCustomerId().hashCode() % numPartitions;
    }
}
