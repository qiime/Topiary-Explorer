package topiarytool;

import java.util.*;
import java.io.*;

public class DataTable {
    private ArrayList<String> columnNames = new ArrayList<String>();
    private ArrayList<ArrayList<Object>> data = new ArrayList<ArrayList<Object>>();


    public DataTable() {
        data = new java.util.ArrayList<ArrayList<Object>>();
		columnNames = new java.util.ArrayList<String>();
    }
    
    public DataTable(mysqlConnect conn) {
        loadData(conn);
    }

    public DataTable(InputStream is) throws IOException{
        loadData(is);
    }
    
    public void loadData(mysqlConnect conn) {
        data = new java.util.ArrayList<ArrayList<Object>>();
        for(int i = 0; i < conn.resultLines.size(); i++) {
			data.add(objectify(parseLine(conn.resultLines.get(i))));
		}
		int numCols = conn.colNames.size();
		
		columnNames = parseLine(conn.colNamesStr);
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


        data = new java.util.ArrayList<ArrayList<Object>>();
		while ((line = br.readLine()) != null) {
			data.add(objectify(parseLine(line)));
		}

        int numCols = data.get(0).size();

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

    public ArrayList<ArrayList<Object>> getData() {
		return data;
	}

	public Object[][] getDataAsArray() {
		Object[][] r = new Object[data.size()][];
		for (int i = 0; i < data.size(); i++) {
			r[i] = data.get(i).toArray();
		}
		return r;
	}

	public void setData(ArrayList<ArrayList<Object>> data) {
		this.data = data;
	}

    public ArrayList<Object> getColumn(int index) {
        ArrayList<Object> result = new ArrayList<Object>();
        for (ArrayList<Object> row : data) {
            result.add(row.get(index));
        }
        return result;
    }

    public Object getValueAt(int row, int col) {
        return data.get(row).get(col);
    }

	public int getColumnCount() {
		return columnNames.size();
	}

	public String getColumnName(int index) {
		return columnNames.get(index);
	}

	public ArrayList<String> getColumnNames() {
		return columnNames;
	}

	public void setColumnNames(ArrayList<String> columnNames) {
		this.columnNames = columnNames;
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
