package sk.upjs.ics.novotnyr.mlt.gui;

import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class EmptyTableModelDecorator implements TableModel {
	private TableModel delegate;

	public EmptyTableModelDecorator(TableModel delegate) {
		this.delegate = delegate;
	}

	public TableModel getDelegate() {
		return delegate;
	}

	public static <T> T unwrapModel(JTable table, Class<T> modelClass) {
		TableModel model = table.getModel();
		if(model instanceof EmptyTableModelDecorator) {
			EmptyTableModelDecorator decoratingModel = (EmptyTableModelDecorator) model;
			return (T) decoratingModel.getDelegate();
		} else {
			return (T) model;
		}
	}

	@Override
	public int getRowCount() {
		int delegateRowCount = delegate.getRowCount();
		if(delegateRowCount == 0) {
			return 1;
		}
		return delegateRowCount;
	}

	@Override
	public int getColumnCount() {
		int delegateColumnCount = delegate.getColumnCount();
		if(this.delegate.getRowCount() == 0) {
			return 1;
		}
		return delegateColumnCount;
	}

	@Override
	public String getColumnName(int columnIndex) {
		if(this.delegate.getRowCount() == 0) {
			return "-";
		}

		return delegate.getColumnName(columnIndex);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if(this.delegate.getRowCount() == 0) {
			return String.class;
		}
		return delegate.getColumnClass(columnIndex);
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if(this.delegate.getRowCount() == 0) {
			return false;
		}
		return delegate.isCellEditable(rowIndex, columnIndex);
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if(this.delegate.getRowCount() == 0) {
			return "No data";
		}
		return delegate.getValueAt(rowIndex, columnIndex);
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if(this.delegate.getRowCount() == 0) {
			return;
		}
		delegate.setValueAt(aValue, rowIndex, columnIndex);
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
		delegate.addTableModelListener(l);
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
		delegate.removeTableModelListener(l);
	}
}
