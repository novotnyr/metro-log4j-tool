package sk.upjs.ics.novotnyr.mlt.scp;

public class JSchChannelOpenException extends RuntimeException {
    public JSchChannelOpenException() {
    }

    public JSchChannelOpenException(String message) {
        super(message);
    }

    public JSchChannelOpenException(String message, Throwable cause) {
        super(message, cause);
    }

    public JSchChannelOpenException(Throwable cause) {
        super(cause);
    }
}
