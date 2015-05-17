package sk.upjs.ics.novotnyr.mlt.scp;

import com.jcraft.jsch.Logger;
import org.slf4j.LoggerFactory;

public class Slf4jJSchLogger implements Logger {
    public static final org.slf4j.Logger slf4jLogger = LoggerFactory.getLogger(Slf4jJSchLogger.class);


    @Override
    public boolean isEnabled(int level) {
        return true;
    }

    @Override
    public void log(int level, String message) {
        switch (level) {
            case DEBUG:
                slf4jLogger.debug(message);
                break;
            case INFO:
                slf4jLogger.info(message);
                break;
            case WARN:
                slf4jLogger.warn(message);
                break;
            case ERROR:
                slf4jLogger.error(message);
                break;
            case FATAL:
                slf4jLogger.error(message);
                break;
            default:
                slf4jLogger.error("Unknown error (level {}): {}", level, message);
        }
    }
}
