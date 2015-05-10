package sk.upjs.ics.novotnyr.mlt;

public class SoapFaultLogEventDetector implements ErrorLogEventDetector {
	@Override
	public boolean hasError(LogEvent event) {
		HttpTransportPipeEvent soapEvent = (HttpTransportPipeEvent) event;
		String payloadXmlString = soapEvent.getPayloadXmlString();

		return payloadXmlString.contains(":Fault>");
	}

	@Override
	public boolean supports(Class<? extends LogEvent> logEventClass) {
		return HttpTransportPipeEvent.class.isAssignableFrom(logEventClass);
	}
}
