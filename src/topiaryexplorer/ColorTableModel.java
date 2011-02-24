package topiaryexplorer;

import java.util.ArrayList;
import javax.swing.table.*;


class ColorTableModel extends AbstractTableModel {

    private ArrayList<String> columnNames;
    private ArrayList<ArrayList<Object>> data;
    
    public ColorTableModel() {
        data = new ArrayList<ArrayList<Object>>();
        columnNames = new ArrayList<String>();
    }

    public ColorTableModel(ArrayList<ArrayList<Object>> data, ArrayList<String> columnNames) {
    	this.columnNames = columnNames;
    	this.data = data;
   	}

    public int getColumnCount() {
        return columnNames.size();
    }

    public int getRowCount() {
        return data.size();
    }

    public ArrayList<ArrayList<Object>> getData() {
    	return data;
    }

    public void setData(ArrayList<ArrayList<Object>> data) {
    	this.data = data;
    }

    public ArrayList<Object> getColumn(int col) {
    	ArrayList<Object> colData = new ArrayList<Object>();
    	for (ArrayList<Object> row : data) {
    		colData.add(row.get(col));
    	}
    	return colData;
   	}

    public String getColumnName(int col) {
        return columnNames.get(col);
    }

    public Object getValueAt(int row, int col) {
        return data.get(row).get(col);
    }

    public Class getColumnClass(int c) {
        return getValueAt(1, c).getClass();
    }

    public boolean isCellEditable(int row, int col) {
		if (col == 0) {
			return false;
		} else {
			return true;
		}
	}

    public void clearTable() {
        data = new ArrayList<ArrayList<Object>>();
        columnNames = new ArrayList<String>();
    }

    public void setValueAt(Object value, int row, int col) {
    	if (data.get(row).get(col) != value) {
        	data.get(row).set(col, value);
        	fireTableCellUpdated(row, col);
        }
    }
}
