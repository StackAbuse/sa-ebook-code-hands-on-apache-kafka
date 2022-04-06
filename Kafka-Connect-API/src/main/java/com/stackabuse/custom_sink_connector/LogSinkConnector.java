package com.stackabuse.custom_sink_connector;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.stackabuse.custom_sink_connector.LogSinkConfig.TASK_ID;
import static com.stackabuse.custom_sink_connector.LogSinkConfig.TASK_MAX;

public class LogSinkConnector extends SinkConnector {

    final Logger logger = LoggerFactory.getLogger(LogSinkConnector.class);
    private static final String VERSION = "1.0";
    private Map<String, String> configProps;

    @Override
    public void start(final Map<String, String> properties) {
        logger.info("Starting LogSinkConnector with properties {}", properties);
        configProps = properties;
    }

    @Override
    public Class<? extends Task> taskClass() {
        return LogSinkTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(final int maxTasks) {
        logger.info("Setting task configurations for {} workers.", maxTasks);
        final List<Map<String, String>> configs = new ArrayList<>(maxTasks);
        for (int i = 0; i < maxTasks; ++i) {
            final Map<String, String> taskConfig = new HashMap<>(configProps);
            // add task specific values
            taskConfig.put(TASK_ID, String.valueOf(i));
            taskConfig.put(TASK_MAX, String.valueOf(maxTasks));
            configs.add(taskConfig);
        }
        return configs;
    }

    @Override
    public void stop() {
        logger.info("Stopping LogSinkConnector.");
    }

    @Override
    public ConfigDef config() {
        return LogSinkConfig.CONFIG_DEF;
    }

    @Override
    public String version() {
        return VERSION;
    }
}
