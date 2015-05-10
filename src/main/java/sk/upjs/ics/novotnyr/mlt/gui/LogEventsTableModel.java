package sk.upjs.ics.novotnyr.mlt.gui;

import sk.upjs.ics.novotnyr.mlt.HttpTransportPipeEvent;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class LogEventsTableModel extends AbstractTableModel {

	private List<HttpTransportPipeEvent> logEvents = new LinkedList<HttpTransportPipeEvent>();

	public static final String[] COLUMN_NAMES = { "Type", "Timestamp", "Remote Host", "Thread", "HTTP Status" };

	public static final Class[] COLUMN_CLASSES = { Icon.class, String.class, String.class, String.class, String.class };

	private static final int COLUMN_INDEX_TYPE = 0;

	public static final int COLUMN_INDEX_TIMESTAMP = 1;

	public static final int COLUMN_INDEX_REMOTE_HOST = 2;

	public static final int COLUMN_INDEX_THREAD_NAME = 3;

	public static final int COLUMN_INDEX_HTTP_STATUS = 4;

	public static Icon REQUEST_ICON;

	public static Icon RESPONSE_ICON;

	static {
		REQUEST_ICON = new ImageIcon(ClassLoader.getSystemResource("icon-request.png"));
		RESPONSE_ICON = new ImageIcon(ClassLoader.getSystemResource("icon-response.png"));
	}

	public LogEventsTableModel(List<HttpTransportPipeEvent> logEvents) {
		this.logEvents = logEvents;
	}

	@Override
	public int getRowCount() {
		return logEvents.size();
	}

	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		HttpTransportPipeEvent logEvent = logEvents.get(rowIndex);
		switch(columnIndex) {
			case COLUMN_INDEX_TYPE:
				return getTypeDescription(logEvent);
			case COLUMN_INDEX_REMOTE_HOST:
				return logEvent.getRemoteHostName();
			case COLUMN_INDEX_TIMESTAMP:
				return logEvent.getTimestamp();
			case COLUMN_INDEX_THREAD_NAME:
				return logEvent.getThreadName();
			case COLUMN_INDEX_HTTP_STATUS:
				return logEvent.getHttpStatus();
		}
		return null;
	}

	private Icon getTypeDescription(HttpTransportPipeEvent logEvent) {
		switch(logEvent.getType()) {
			case REQUEST:
				return REQUEST_ICON;
			case RESPONSE:
				return RESPONSE_ICON;
			default:
				return null;
		}
	}

	@Override
	public String getColumnName(int column) {
		return COLUMN_NAMES[column];
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return COLUMN_CLASSES[columnIndex];
	}

	public HttpTransportPipeEvent getLogEvent(int index) {
		return this.logEvents.get(index);
	}
}
