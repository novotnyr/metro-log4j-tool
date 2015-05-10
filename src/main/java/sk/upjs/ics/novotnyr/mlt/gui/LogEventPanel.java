package sk.upjs.ics.novotnyr.mlt.gui;

import net.miginfocom.swing.MigLayout;
import sk.upjs.ics.novotnyr.mlt.HttpTransportPipeEvent;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class LogEventPanel extends JPanel {
	private final HeadersPanel headersPanel;

	private JTextArea previewTextArea;

	private HttpTransportPipeEvent logEvent;

	public LogEventPanel() {
		setLayout(new BorderLayout());

		this.headersPanel = new HeadersPanel();
		add(this.headersPanel, BorderLayout.NORTH);

		this.previewTextArea = new JTextArea();
		add(new JScrollPane(this.previewTextArea), BorderLayout.CENTER);
	}

	private class HeadersPanel extends JPanel {
		public HeadersPanel() {
			setLayout(new MigLayout("wrap 2", "[][fill,grow]"));
		}

		public void setLogEvent(HttpTransportPipeEvent event) {
			removeAll();

			Map<String, String> httpHeaders = event.getHttpHeaders();
			for (String headerName : httpHeaders.keySet()) {
				String headerValue = httpHeaders.get(headerName);
				add(new JLabel(headerName));
				add(new JLabel(headerValue));
			}
			revalidate();
			repaint();
		}
	}

	public void setLogEvent(HttpTransportPipeEvent logEvent) {
		this.logEvent = logEvent;
		this.headersPanel.setLogEvent(logEvent);

		this.previewTextArea.setText(logEvent.getPayloadXmlString());
		this.previewTextArea.setCaretPosition(0);
	}
}
