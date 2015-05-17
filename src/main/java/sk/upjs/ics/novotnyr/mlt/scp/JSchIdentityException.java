package sk.upjs.ics.novotnyr.mlt.scp;

import com.jcraft.jsch.JSchException;

public class JSchIdentityException extends RuntimeException {
    public JSchIdentityException() {
    }

    public JSchIdentityException(String message) {
        super(message);
    }

    public JSchIdentityException(String message, Throwable cause) {
        super(message, cause);
    }

    public JSchIdentityException(Throwable cause) {
        super(cause);
    }
}
