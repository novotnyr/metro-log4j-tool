package sk.upjs.ics.novotnyr.mlt.scp;

public class JSchChannelOperationException extends RuntimeException {
    public JSchChannelOperationException() {
    }

    public JSchChannelOperationException(String message) {
        super(message);
    }

    public JSchChannelOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public JSchChannelOperationException(Throwable cause) {
        super(cause);
    }
}
