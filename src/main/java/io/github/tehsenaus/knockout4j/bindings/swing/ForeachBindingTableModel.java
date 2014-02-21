package io.github.tehsenaus.knockout4j.bindings.swing;

import io.github.tehsenaus.knockout4j.KO;

import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.util.List;

public abstract class ForeachBindingTableModel<T> extends SwingBinding<List<T>> implements TableModel {
    final protected AbstractTableModel tableModel;

    private List<T> values;

    protected abstract Object getColumnValue(T row, int columnIndex);

    protected ForeachBindingTableModel() {
        super(null, KO.Computed.DEFER_EVAL);

        tableModel = new AbstractTableModel() {
            @Override
            public int getRowCount() {
                return values.size();
            }

            @Override
            public int getColumnCount() {
                return ForeachBindingTableModel.this.getColumnCount();
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                return getColumnValue(values.get(rowIndex), columnIndex);
            }
        };

        this.updater.get();
    }

    @Override
    protected void updateBinding(List<T> value) {
        this.values = value;
        tableModel.fireTableDataChanged();
    }

    @Override
    public String getColumnName(int column) {
        return tableModel.getColumnName(column);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return tableModel.getColumnClass(columnIndex);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return tableModel.isCellEditable(rowIndex, columnIndex);
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        tableModel.addTableModelListener(l);
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
        tableModel.removeTableModelListener(l);
    }

    @Override
    public int getRowCount() {
        return values.size();
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        tableModel.setValueAt(aValue, rowIndex, columnIndex);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return tableModel.getValueAt(rowIndex, columnIndex);
    }
}
