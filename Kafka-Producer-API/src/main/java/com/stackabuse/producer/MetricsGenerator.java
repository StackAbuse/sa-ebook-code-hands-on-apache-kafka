package com.stackabuse.producer;

import com.stackabuse.model.MetricPair;
import com.stackabuse.model.Notification;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class MetricsGenerator implements Runnable {
    private final Producer<String, Notification> producer;
    private final Logger logger = LoggerFactory.getLogger(MetricsGenerator.class);

    // Filter only the metrics that we require
    private final Set<String> metricsNameFilter = new HashSet<>(Arrays.asList(
            "record-queue-time-avg", "record-send-rate", "records-per-request-avg",
            "request-size-max", "network-io-rate", "record-queue-time-avg",
            "incoming-byte-rate", "batch-size-avg", "response-rate", "requests-in-flight"
    ));

    public MetricsGenerator(final Producer<String, Notification> producer) {
        this.producer = producer;
    }

    @Override
    public void run() {
        while (true) {
            final Map<MetricName, ? extends Metric> metrics = producer.metrics();
            displayAllMetrics(metrics);
            try {
                Thread.sleep(3_000);
            } catch (InterruptedException e) {
                logger.warn("metrics interrupted");
                Thread.interrupted();
                break;
            }
        }
    }

    private void displayAllMetrics(Map<MetricName, ? extends Metric> metrics) {
        final Map<String, MetricPair> metricsDisplayMap = MetricPair.metricsDisplayer(metricsNameFilter, metrics);

        //Output metrics
        final StringBuilder builder = new StringBuilder(255);
        builder.append("\n---------------------------------------\n");
        metricsDisplayMap.forEach((name, metricPair) -> builder
                .append(String.format(Locale.US, "%50s%25s\t\t%s\t\t%s%n",
                    name,
                    metricPair.getMetricName().name(),
                    metricPair.getMetric().metricValue(),
                    metricPair.getMetricName().description()
        )));
        builder.append("\n---------------------------------------\n");
        logger.info(builder.toString());
    }
}
