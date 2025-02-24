package org.safetynet.alerts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogWorker {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogWorker.class);

    public void generateLogs(String msg) {
        LOGGER.trace(msg);
        LOGGER.debug(msg);
        LOGGER.info(msg);
        LOGGER.warn(msg);
        LOGGER.error(msg);
    }
}