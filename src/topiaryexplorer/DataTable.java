package topiaryexplorer;

import java.util.*;
import java.io.*;
import java.text.*;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

/**
* 
**/
public class DataTable {
    private ArrayList<String> columnNames = new ArrayList<String>();
    private ArrayList<String> rowNames = new ArrayList<String>();
    private ArrayList<Class> columnClasses = new ArrayList<Class>();
    private SparseTable data = new SparseTable();

    public DataTable() {
        data = new SparseTable();
		columnNames = new java.util.ArrayList<String>();
		rowNames = new ArrayList<String>();
    }
    
    public DataTable(ArrayList<String> lines) throws ParseException{
        loadData(lines);
    }
    
    public DataTable(dbConnect conn) {
        loadData(conn);
    }

    public DataTable(InputStream is) throws IOException, ParseException{
        loadData(is);
    }
    
    public void loadData(dbConnect conn) {
        data = new SparseTable();
        int c = 0;
        for(int r = 0; r < conn.resultLines.size(); r++) {
			Splitter splitter = Splitter.on('\t');
        	ArrayList<String> vals = Lists.newArrayList(splitter.split(conn.resultLines.get(r)));
		    // String vals[] = conn.resultLines.get(r).split("\t");
            c = 0;
            rowNames.add(vals.get(0));
		    for (String obj : vals) {
		        Object val = TopiaryFunctions.objectify(obj);
		        if (val != null) {
		            data.add(r, c, val);
		        }
		        c = c + 1;
		    }
        }
        // int numCols = conn.colNames.size();
        // System.out.println(conn.colNamesStr);
		columnNames = conn.colNames;
		setColumnClasses();
    }
    
    public void loadData(ArrayList<String> lines) throws ParseException{
        data = new SparseTable();
        List<String> commentedLines = new java.util.ArrayList<String>();
        
        for(String line:lines)
        {
            try {
            if(line.charAt(0)=='#')
                commentedLines.add(line);
            }
            catch(StringIndexOutOfBoundsException e)
            {
                commentedLines.add(line);
            }
        }
        
        for(String line:commentedLines)
            lines.remove(line);
        
		for(String line: lines)
			line.replace("\n","");
        
        int curr_c = 0;
        int old_c = 0;
        ArrayList<String> vals = new ArrayList<String>();
        Object val;
		Splitter splitter = Splitter.on('\t');
		
		int numCols = Lists.newArrayList(splitter.split(lines.get(0))).size();
		
		for(int r = 0; r < lines.size(); r++) {
        	vals = Lists.newArrayList(splitter.split(lines.get(r)));
		    rowNames.add(vals.get(0));
		    curr_c = 0;
		    for (String obj : vals) {
		        val = TopiaryFunctions.objectify(obj);
                if (val != null) {
		            data.add(r, curr_c, val);
                }
		        curr_c = curr_c + 1;
		    }
		
		    if(numCols != vals.size())
		        throw new ParseException("Number of columns in table are not consistant across rows. ", curr_c);

        }
        
        //parse each commented line until we get one that has the same number of
        //rows as the data
        for (String currline : commentedLines) {
			//parse it, removing leading '#'
			columnNames = parseLine(currline.substring(1));
			if (columnNames.size() == numCols) break;
		}
		setColumnClasses();
    }

    public void loadData(InputStream is) throws IOException,ParseException{
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

		//mark the beginning of the file so that we can come back if the next line is not commented out
/*      br.mark(10000);*/
		String line = br.readLine();

		//Commented lines start with a #
		List<String> commentedLines = new java.util.ArrayList<String>();

		while (line.charAt(0) == '#') {
			commentedLines.add(line);
/*          br.mark(10000);*/
			line = br.readLine();
		}
		//now we've gone one line too far, so return to our last mark
      // br.reset();


        data = new SparseTable();
        int r = 0;
        int curr_c = 0;
		Splitter splitter = Splitter.on('\t');
        ArrayList<String> vals = Lists.newArrayList(splitter.split(line));
        Object val;
		int numCols = vals.size();
		
		while ((line) != null) {
		    vals = Lists.newArrayList(splitter.split(line));
		    rowNames.add(vals.get(0));
		    curr_c = 0;
		    for (String obj : vals) {
		        val = TopiaryFunctions.objectify(obj);
                if (val != null) {
		            data.add(r, curr_c, val);
                }
		        curr_c = curr_c + 1;
		    }
		    
		    if(numCols != vals.size())
			{
		        throw new ParseException("Number of columns in table are not consistant across rows.",curr_c);
			}

	        r = r + 1;
		    line = br.readLine();
        }
    

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
		setColumnClasses();
    }
    
    public void setColumnClasses() {
        Class cl = Object.class;
        for(int i = 0; i < columnNames.size(); i++)
        {
            cl = data.get(0,i).getClass();
            for(int j = 0; j < rowNames.size(); j++)
            {
                if(!data.get(j,i).getClass().equals(cl))
                {
                    cl = Object.class;
                    break;
                }
            }
            columnClasses.add(i,cl);
        }
        
        for(int i = 0; i < columnNames.size(); i++)
        {
            if(columnClasses.get(i).equals(Object.class))
            {
                for(int j = 0; j < rowNames.size(); j++)
                {
                    data.set(data.get(j,i).toString(),j,i);
                }
                columnClasses.set(i, String.class);
            }
        }
    }
    
    public Class getColumnClass(int i) {
        return columnClasses.get(i);
    }

    public SparseTable getData() {
		return data;
	}


	public void setData(SparseTable data) {
		this.data = data;
	}

    public void addColumn(ArrayList<Object> newColumn) {
        columnNames.add(""+newColumn.get(0));
        newColumn.remove(0);
        for(int i = 0; i < newColumn.size(); i++)
        {
            data.add(i, columnNames.size()-1, newColumn.get(i));
            if(columnNames.size() == 1) // adding the first column
                rowNames.add(newColumn.get(i)+"");
        }   
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
    
    public void removeRow(String id) {
        data.removeRow(rowNames.indexOf(id));
        rowNames.remove(id);
    }
    
    public void removeRow(int id) {
        data.removeRow(id);
        rowNames.remove(id);
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
	
	public int getRowCount() {
	    return rowNames.size();
	}

	public String getColumnName(int index) {
		return columnNames.get(index);
	}
	
	public int getColumnIndex(String name) {
	    return columnNames.indexOf(name);
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
	    columnNames = new ArrayList<String>();
	    rowNames = new ArrayList<String>();
	}
	
/*  public void highlightRow(Node n) {
        String otuID = n.getName();
        int idx = rowNames.indexOf(otuID);
    }*/
	
	public String toString() {
	    String s = "";
	    
	    for(String h : getColumnNames())
	        s += h + '\t';
	    
		s = s.substring(0,s.length()-1);
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
	                // s += '0';
	            }
	            s += '\t';
	        }
	        s = s.substring(0,s.length()-1);
	        s += "\n";
	    }
	    return s;
	}
	
	public ArrayList<String> toStrings() {
	    ArrayList<String> lines = new ArrayList<String>();
	    String s = "#";
	    
	    for(String h : getColumnNames())
	        s += h + '\t';
	    s = s.substring(0,s.length()-1);
	    s += "\n";
	    lines.add(s);
	    
	    for(int i = 0; i < getRowCount(); i++)
	    {
	        s = "";
	        for(int j = 0; j < getColumnCount(); j++)
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
	        s = s.substring(0,s.length()-1);
	        s += '\n';
	        lines.add(s);
	    }
	    return lines;
	}
	
    private ArrayList<String> parseLine(String line) {
	    line = line.replace("\n","").replace("#","");
		Splitter splitter = Splitter.on('\t');
		return Lists.newArrayList(splitter.split(line));
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
