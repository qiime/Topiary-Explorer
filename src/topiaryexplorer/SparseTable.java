package topiaryexplorer;

import java.util.*;
import java.lang.*;
import java.awt.*;

class SparseTable {

    private ArrayList<HashMap> data = new ArrayList<HashMap>();
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
        maxrow = Math.max(row, maxrow);
        maxcol = Math.max(col, maxcol);
    }
    
    public int size() {
        return data.size();
    }
    
    public void set(Object value, int row, int col) {
        data.get(row).put(col, value);
    }
    
    public void removeRow(int row) {
        data.remove(row);
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
    }
    
    public String getstr(int row, int col) {
        try {
        return (String)data.get(row).get(col);
        }
        catch(IndexOutOfBoundsException e)
        {
            return null;
        }
    }
    
    public void removeElements() {
        data = new ArrayList<HashMap>();
    }
    
      public Class getColumnClass(int c) {
      try {
          Class cl = data.get(0).get(c).getClass();
          for(int i = 0; i < size(); i++)
          {
              if(!cl.equals(data.get(i).get(c).getClass()))
                return Object.class;
          }
          return cl;
        }
        catch(NullPointerException e)
        {
            return Object.class;
        }
    }
    
    public int maxRow() { return maxrow+1; }
    public int maxCol() { return maxcol+1; }
}