package topiaryexplorer;

import java.util.*;
import java.lang.*;
import java.awt.*;

class SparseTable {

    private ArrayList<HashMap> data = new ArrayList<HashMap>();
/*    private HashMap data = new HashMap();*/
    private int maxrow = 0;
    private int maxcol = 0;
    
    public SparseTable() {
    }
    
    public void add(int row, int col, Object value) {
        try {
            data.get(row).put(col, value);
        }
        catch(IndexOutOfBoundsException e)
        {
            data.add(row, new HashMap());
            data.get(row).put(col, value);
        }
/*        data.put(new Point(row, col), value);*/
        maxrow = Math.max(row, maxrow);
        maxcol = Math.max(col, maxcol);
    }
    
    public void set(Object value, int row, int col) {
        data.get(row).put(col, value);
/*        data.put(new Point(row, col), value);*/
    }
    
    public HashMap getRow(int row) {
        try {
        return data.get(row);
        }
        catch(IndexOutOfBoundsException e)
        {
            return null;
        }
    }
     
    public Object get(int row, int col) {
        try {
        return data.get(row).get(col);
        }
        catch(IndexOutOfBoundsException e)
        {
            return null;
        }
/*        return data.get(new Point(row, col));*/
    }
    
    public String getstr(int row, int col) {
        try {
        return (String)data.get(row).get(col);
        }
        catch(IndexOutOfBoundsException e)
        {
            return null;
        }
/*        return (String)data.get(new Point(row, col));*/
    }
    
    public void removeElements() {
        data = new ArrayList<HashMap>();
/*        data.clear();*/
    }
    
    public int maxRow() { return maxrow+1; }
    public int maxCol() { return maxcol+1; }
}