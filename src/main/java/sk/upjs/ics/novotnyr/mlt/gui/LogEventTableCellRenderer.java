package sk.upjs.ics.novotnyr.mlt.gui;

import sk.upjs.ics.novotnyr.mlt.ErrorLogEventDetector;
import sk.upjs.ics.novotnyr.mlt.HttpTransportPipeEvent;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Color;
import java.awt.Component;

import static sk.upjs.ics.novotnyr.mlt.gui.EmptyTableModelDecorator.unwrapModel;

public class LogEventTableCellRenderer extends DefaultTableCellRenderer {
	private ErrorLogEventDetector errorLogEventDetector;

	public LogEventTableCellRenderer(ErrorLogEventDetector errorLogEventDetector) {
		this.errorLogEventDetector = errorLogEventDetector;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		LogEventsTableModel tableModel = unwrapModel(table, LogEventsTableModel.class);
		if(tableModel.getRowCount() > 0) {
			HttpTransportPipeEvent logEvent = tableModel.getLogEvent(row);
			if (errorLogEventDetector.hasError(logEvent)) {
				setBackground(Color.RED);
			} else {
				setBackground(table.getBackground());
			}
		}
		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}


}
