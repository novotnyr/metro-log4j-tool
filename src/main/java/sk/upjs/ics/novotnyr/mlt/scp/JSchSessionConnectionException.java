package sk.upjs.ics.novotnyr.mlt.scp;

import com.jcraft.jsch.JSchException;

public class JSchSessionConnectionException extends RuntimeException {
    public JSchSessionConnectionException() {
    }

    public JSchSessionConnectionException(String message) {
        super(message);
    }

    public JSchSessionConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public JSchSessionConnectionException(Throwable cause) {
        super(cause);
    }
}
