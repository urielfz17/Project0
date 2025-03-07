package util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerUtil {
    private LoggerUtil() {

    }

    public static final Logger LOGGER = LoggerFactory.getLogger(LoggerUtil.class);

    public static void info(String message, Object... args) {
        LOGGER.info(message, args);
    }

    public static void warn(String message, Object... args) {
        LOGGER.warn(message, args);
    }

    public static void error(String message, Throwable throwable) {
        LOGGER.error(message, throwable);
    }
}

