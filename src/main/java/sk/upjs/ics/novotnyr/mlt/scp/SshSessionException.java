package sk.upjs.ics.novotnyr.mlt.scp;

public class SshSessionException extends RuntimeException {
    public SshSessionException() {
        super();
    }

    public SshSessionException(String message) {
        super(message);
    }

    public SshSessionException(String message, Throwable cause) {
        super(message, cause);
    }

    public SshSessionException(Throwable cause) {
        super(cause);
    }
}
