package topiarytool;

import java.util.*;
import java.lang.*;
import java.awt.*;

class SparseTable {

    private HashMap data = new HashMap();
    private int maxrow = 0;
    private int maxcol = 0;
    
    public SparseTable() {
    }
    
    public void add(int row, int col, Object value) {
        data.put(new Point(row, col), value);
        maxrow = Math.max(row, maxrow);
        maxcol = Math.max(col, maxcol);
    }
    
    public void set(Object value, int row, int col) {
        data.put(new Point(row, col), value);
    }
    
    public Object get(int row, int col) {
        return data.get(new Point(row, col));
    }
    
    public String getstr(int row, int col) {
        return (String)data.get(new Point(row, col));
    }
    
    public void removeElements() {
        data.clear();
    }
    
    public int maxRow() { return maxrow+1; }
    public int maxCol() { return maxcol+1; }
}