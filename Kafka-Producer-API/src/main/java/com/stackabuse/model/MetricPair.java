package com.stackabuse.model;

import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class MetricPair {
    private final MetricName metricName;
    private final Metric metric;

    public MetricPair(MetricName metricName, Metric metric) {
        this.metricName = metricName;
        this.metric = metric;
    }

    public MetricName getMetricName() {
        return metricName;
    }

    public Metric getMetric() {
        return metric;
    }

    public String toString() {
        return metricName.group() + "." + metricName.name();
    }

    public static final Map<String, MetricPair> metricsDisplayer(Set<String> metricsNameFilter,
                                                          Map<MetricName, ? extends Metric> metrics) {
        return metrics
                .entrySet()
                .stream()
                //Filter out metrics not in metricsNameFilter
                .filter(metricNameEntry -> metricsNameFilter.contains(metricNameEntry.getKey().name()))
                //Turn Map<MetricName,Metric> into TreeMap<String, MetricPair>
                .map(entry -> new MetricPair(entry.getKey(), entry.getValue()))
                .collect(Collectors.toMap(MetricPair::toString, it -> it, (a, b) -> a, TreeMap::new));
    }
}
