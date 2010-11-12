package topiaryexplorer;

import java.util.*;
import java.io.*;

public class DataTable {
    private ArrayList<String> columnNames = new ArrayList<String>();
    private ArrayList<String> rowNames = new ArrayList<String>();
    private SparseTable data = new SparseTable();

    public DataTable() {
        data = new SparseTable();
		columnNames = new java.util.ArrayList<String>();
		rowNames = new ArrayList<String>();
    }
    
    public DataTable(ArrayList<String> lines){
        loadData(lines);
    }
    
    public DataTable(dbConnect conn) {
        loadData(conn);
    }

    public DataTable(InputStream is) throws IOException{
        loadData(is);
    }
    
    public void loadData(dbConnect conn) {
        data = new SparseTable();
        int c = 0;
        for(int r = 0; r < conn.resultLines.size(); r++) {
		    String vals[] = conn.resultLines.get(r).split("\t");
		    c = 0;
		    for (String obj : vals) {
		        Object val = TopiaryFunctions.objectify(obj);
		        if (val != null) {
		            data.add(r, c, val);
		        }
		        c = c + 1;
		    }
        }
		int numCols = conn.colNames.size();
		columnNames = parseLine(conn.colNamesStr);
    }
    
    public void loadData(ArrayList<String> lines) {
        data = new SparseTable();
        int c = 0;
        String vals[];
        Object val;
        columnNames = parseLine(lines.get(0));
        lines.remove(0);
		for(int r = 0; r < lines.size(); r++) {
		    vals = lines.get(r).split("\t");
		    rowNames.add(vals[0]);
		    c = 0;
		    for (String obj : vals) {
		        val = obj;
		        if (c!=0) {
		            val = TopiaryFunctions.objectify(obj);
		        }
		        if (val != null) {
		            data.add(r, c, val);
		        }
		        c = c + 1;
		    }
        }
    }

    public void loadData(InputStream is) throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

		//mark the beginning of the file so that we can come back if the next line is not commented out
		br.mark(10000);
		String line = br.readLine();

		//Commented lines start with a #
		List<String> commentedLines = new java.util.ArrayList<String>();

		while (line.charAt(0) == '#') {
			commentedLines.add(line);
			br.mark(10000);
			line = br.readLine();
		}
		//now we've gone one line too far, so return to our last mark
		br.reset();


        data = new SparseTable();
        int r = 0;
        int c = 0;
        String vals[];
        Object val;
		while ((line = br.readLine()) != null) {
		    vals = line.split("\t");
		    rowNames.add(vals[0]);
		    c = 0;
		    for (String obj : vals) {
		        val = obj;
		        if (c!=0) {
		            val = TopiaryFunctions.objectify(obj);
		        }
		        if (val != null) {
		            data.add(r, c, val);
		        }
		        c = c + 1;
		    }
		    r = r + 1;
        }

        int numCols = c;
    

        //parse each commented line until we get one that has the same number of
        //rows as the data
        for (String currline : commentedLines) {
			//parse it, removing leading '#'
			columnNames = parseLine(currline.substring(1));
			if (columnNames.size() == numCols) break;
		}

        //if we still don't have a header, use default value
		if (columnNames.size() != numCols) {
			String headerLine = "#ID";
			//start at 1 since we've already added the ID label
			for (int i = 1; i < numCols; i++) {
				headerLine += "\tCATEGORY" + Integer.toString(i);
			}
			columnNames = parseLine(headerLine.substring(1));
		}
		
    }

    public SparseTable getData() {
		return data;
	}


	public void setData(SparseTable data) {
		this.data = data;
	}

    public ArrayList<Object> getColumn(int index) {
        ArrayList<Object> result = new ArrayList<Object>();
        for (int i = 0; i < data.maxRow(); i++) {
            result.add(data.get(i, index));
        }
        return result;
    }
    public ArrayList<Object> getRow2(int index) {
        ArrayList<Object> result = new ArrayList<Object>();
        HashMap row = data.getRow(index);
        for (int i = 0; i < data.maxCol(); i++) {
            result.add(row.get((Integer)i));
        }
        return result;
    }
    
    public HashMap getRow(int index) {
        return data.getRow(index);
    }
    
    public ArrayList<Integer> getRowAsInts(int index) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < data.maxCol(); i++) {
            result.add(((Number)data.get(index,i)).intValue());
        }
        return result;
    }
    

    public Object getValueAt(int row, int col) {
        return data.get(row,col);
    }

	public int getColumnCount() {
		return columnNames.size();
	}

	public String getColumnName(int index) {
		return columnNames.get(index);
	}
	
	public ArrayList<String> getRowNames() {
		return rowNames;
	}

	public ArrayList<String> getColumnNames() {
		return columnNames;
	}

	public void setColumnNames(ArrayList<String> columnNames) {
		this.columnNames = columnNames;
	}
	
	public void clearTable() {
	    data = new SparseTable();
	    ArrayList<String> columnNames = new ArrayList<String>();
	}
	
	public String toString() {
	    String s = "";
	    
	    for(String h : getColumnNames())
	        s += h + '\t';
	    
	    s += '\n';
	    
	    for(int i = 0; i < data.maxRow(); i++)
	    {
	        for(int j = 0; j < data.maxCol(); j++)
	        {
	            try {
	            s += getValueAt(i,j).toString();
	            }
	            catch(Exception e)
	            {
	                s += '0';
	            }
	            s += '\t';
	        }
	        s = s.trim();
	        s += "\n";
	    }
	    return s.trim();
	}
	
	public ArrayList<String> toStrings() {
	    ArrayList<String> lines = new ArrayList<String>();
	    String s = "";
	    
	    for(String h : getColumnNames())
	        s += h + '\t';
	    s += "\n";
	    lines.add(s);
	    
	    for(int i = 0; i < data.maxRow(); i++)
	    {
	        s = "";
	        for(int j = 0; j < data.maxCol(); j++)
	        {
	            try {
	            s += getValueAt(i,j).toString();
	            }
	            catch(Exception e)
	            {
	                //s += '\t';
	            }
	            s += '\t';
	        }
	        s = s.trim();
	        s += '\n';
	        lines.add(s);
	    }
	    return lines;
	}
	
    private ArrayList<String> parseLine(String line) {
		return new ArrayList<String>(Arrays.asList(line.split("\t")));
	}

    /** 
     * Convert strings to Integers, Date, etc. so they'll sort properly.
     */
	private ArrayList<Object> objectify(List<String> values) {
		ArrayList<Object> result = new ArrayList<Object>();
		for (String str : values) {
			//remove surrounding quotes from label
			while (str.charAt(0) == '\'' || str.charAt(0) == '"') str = str.substring(1);
			while (str.charAt(str.length()-1) == '\'' || str.charAt(str.length()-1) == '"') str = str.substring(0,str.length()-1);

            result.add(TopiaryFunctions.objectify(str));
		}
		return result;
	}
}
