package topiarytool;

import java.util.*;
import java.lang.*;
import java.awt.*;

class SparseTable {

    HashMap data = new HashMap();
    int maxrow = 0;
    int maxcol = 0;
    
    public SparseTable() {
    }
    
    public void add(int row, int col, Object value) {
        data.put(new Point(row, col), value);
        maxrow = Math.max(row, maxrow);
        maxcol = Math.max(col, maxcol);
    }
    
    public Object get(int row, int col) {
        return data.get(new Point(row, col));
    }
    
    public int maxRow() { return maxrow+1; }
    public int maxCol() { return maxcol+1; }
}