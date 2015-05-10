package sk.upjs.ics.novotnyr.mlt;

import java.util.LinkedList;
import java.util.List;

public class CompositeErrorLogEventDetector implements ErrorLogEventDetector {
	private List<ErrorLogEventDetector> detectors = new LinkedList<ErrorLogEventDetector>();

	@Override
	public boolean hasError(LogEvent event) {
		for (ErrorLogEventDetector detector : this.detectors) {
			if(detector.supports(event.getClass()) && detector.hasError(event)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean supports(Class<? extends LogEvent> logEventClass) {
		return true;
	}

	public void addDetector(ErrorLogEventDetector detector) {
		this.detectors.add(detector);
	}
}
