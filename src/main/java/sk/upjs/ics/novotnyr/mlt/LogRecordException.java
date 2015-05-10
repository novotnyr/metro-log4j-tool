package sk.upjs.ics.novotnyr.mlt;

import java.io.IOException;

public class LogRecordException extends RuntimeException {
	public LogRecordException() {
	}

	public LogRecordException(String message) {
		super(message);
	}

	public LogRecordException(String message, Throwable cause) {
		super(message, cause);
	}

	public LogRecordException(Throwable cause) {
		super(cause);
	}
}
