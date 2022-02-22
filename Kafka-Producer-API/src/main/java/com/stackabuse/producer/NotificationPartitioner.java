package com.stackabuse.producer;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;

import java.util.*;

public class NotificationPartitioner implements Partitioner {

    private final Set<String> importantNotifications;
    public NotificationPartitioner() {
        importantNotifications = new HashSet<>();
    }

    @Override
    public int partition(final String topic,
                         final Object objectKey,
                         final byte[] keyBytes,
                         final Object value,
                         final byte[] valueBytes,
                         final Cluster cluster) {

        final List<PartitionInfo> partitionInfoList = cluster.availablePartitionsForTopic(topic);
        final int partitionCount = partitionInfoList.size();
        final int importantPartition = partitionCount -1;
        final int normalPartitionCount = partitionCount -1;

        final String key = ((String) objectKey);

        if (importantNotifications.contains(key)) {
            return importantPartition;
        } else {
            return Math.abs(key.hashCode()) % normalPartitionCount;
        }
    }

    @Override
    public void close() {}

    @Override
    public void configure(Map<String, ?> configs) {
        final String importantNotificationsStr = (String) configs.get("importantNotifications");
        importantNotifications.addAll(Arrays.asList(importantNotificationsStr.split(",")));
    }
}
