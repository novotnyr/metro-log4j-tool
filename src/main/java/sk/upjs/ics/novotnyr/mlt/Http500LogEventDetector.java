package sk.upjs.ics.novotnyr.mlt;

import sun.rmi.runtime.Log;

public class Http500LogEventDetector implements ErrorLogEventDetector {
	@Override
	public boolean hasError(LogEvent event) {
		HttpTransportPipeEvent soapEvent = (HttpTransportPipeEvent) event;

		return "500".equals(soapEvent.getHttpStatus());
	}

	@Override
	public boolean supports(Class<? extends LogEvent> logEventClass) {
		return HttpTransportPipeEvent.class.isAssignableFrom(logEventClass);
	}
}
