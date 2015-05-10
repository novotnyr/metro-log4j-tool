package sk.upjs.ics.novotnyr.mlt;

public interface ErrorLogEventDetector {
	public boolean hasError(LogEvent event);

	public boolean supports(Class<? extends LogEvent> logEventClass);
}
