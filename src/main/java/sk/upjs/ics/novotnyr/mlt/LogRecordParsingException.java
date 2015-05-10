package sk.upjs.ics.novotnyr.mlt;

import nu.xom.ParsingException;

public class LogRecordParsingException extends RuntimeException {
	public LogRecordParsingException() {
	}

	public LogRecordParsingException(String message) {
		super(message);
	}

	public LogRecordParsingException(String message, Throwable cause) {
		super(message, cause);
	}

	public LogRecordParsingException(Throwable cause) {
		super(cause);
	}
}
