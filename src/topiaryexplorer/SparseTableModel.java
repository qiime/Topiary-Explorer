package topiaryexplorer;

import javax.swing.table.*;
import java.awt.*;
import java.util.*;

class SparseTableModel extends AbstractTableModel {

  private SparseTable lookup = new SparseTable();

  private final ArrayList<String> headers;
  
  public SparseTableModel() {
  	headers = new ArrayList<String>();
  	headers.add("");
  	lookup = new SparseTable();
  }

  public SparseTableModel(SparseTable table, ArrayList<String> columnHeaders) {
    headers = columnHeaders;
    lookup = table;
  }
  
  public void clearTable() {
      lookup.removeElements();
      headers.removeAll(headers);
  }

  public int getColumnCount() {
    return lookup.maxCol();
  }

  public int getRowCount() {
    return lookup.maxRow();
  }

  public String getColumnName(int column) {
    return headers.get(column);
  }

  public Object getValueAt(int row, int column) {
    return lookup.get(row, column);
  }

  public void setValueAt(Object value, int row, int column) {
      lookup.add(row, column, value);
  }
  
  public boolean isCellEditable(int rowIndex, int colIndex) {
  	  return true;
  }
  
  public Class getColumnClass(int c) {
      try {
          Class cl = lookup.get(1, c).getClass();
          for(int i = 0; i < getRowCount(); i++)
          {
              if(!cl.equals(lookup.get(i, c).getClass()))
                return Object.class;
          }
          return cl;
        }
        catch(NullPointerException e)
        {
            return Object.class;
        }
  }

}