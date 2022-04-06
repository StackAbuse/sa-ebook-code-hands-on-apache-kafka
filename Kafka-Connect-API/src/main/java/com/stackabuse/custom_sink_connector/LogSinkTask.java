package com.stackabuse.custom_sink_connector;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;

public class LogSinkTask extends SinkTask {

    final Logger logger = LoggerFactory.getLogger(LogSinkTask.class);
    private static final String VERSION = "1.0";
    private LogSinkConfig.LogLevel logLevel;
    private LogSinkConfig.LogContent logContent;
    private String logPatternFormat;

    @Override
    public String version() {
        return VERSION;
    }

    @Override
    public void start(final Map<String, String> properties) {
        final LogSinkConfig config = new LogSinkConfig(properties);
        logLevel = config.getLogLevel();
        logContent = config.getLogContent();
        logPatternFormat = config.getLogPatternFormat();
        logger.info("Starting LogSinkTask with properties {}", properties);
    }

    @Override
    public void put(final Collection<SinkRecord> records) {
        switch (logLevel) {
            case INFO:
                records.forEach(record -> logger.info(logPatternFormat,
                        getLoggingArgs(logContent, record)));
                break;
            case WARN:
                records.forEach(record -> logger.warn(logPatternFormat,
                        getLoggingArgs(logContent, record)));
                break;
            case DEBUG:
                records.forEach(record -> logger.debug(logPatternFormat,
                        getLoggingArgs(logContent, record)));
                break;

            case TRACE:
                records.forEach(record -> logger.trace(logPatternFormat,
                        getLoggingArgs(logContent, record)));
                break;

            case ERROR:
                records.forEach(record -> logger.error(logPatternFormat,
                        getLoggingArgs(logContent, record)));
                break;

        }
    }

    private Object[] getLoggingArgs(final LogSinkConfig.LogContent logContent, final SinkRecord record) {
        switch (logContent) {
            case KEY:
                return new Object[]{record.key(), StringUtils.EMPTY};
            case VALUE:
                return new Object[]{record.value(), StringUtils.EMPTY};
            case KEY_VALUE:
                return new Object[]{record.key(), record.value()};
            default:
                // case ALL
                return new Object[]{record, StringUtils.EMPTY};
        }
    }

    @Override
    public void stop() {
        logger.info("Stopping LogSinkTask.");
    }
}
