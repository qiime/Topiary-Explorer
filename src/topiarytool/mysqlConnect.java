package topiarytool;

// location of test database: usr/local/mysql-5.1.30-osx10.5-x86/data/topiarytool
import java.sql.*;
import java.lang.*;
import java.util.*;

public class mysqlConnect
{
    String userName = "";
    String password = "";
    String url = "";
    Connection conn = null;
    ResultSet res = null;
    ArrayList<String> resultLines = new ArrayList<String>();
    ArrayList<String> colNames = new ArrayList<String>();
    String colNamesStr = new String();
    
    public mysqlConnect(String un, String pw, String ur)
    {
        userName = un;
        password = pw;
        url = ur;
    }
    
    public ArrayList<String> getResultLines()
    {
        return this.resultLines;
    }
    
    public void getAvailableTables()
    {
        Statement stmt = null;

          try {
              stmt = conn.createStatement();
              this.res = stmt.executeQuery("SHOW TABLES;");
              parseResultSet();
              }
              // Now do something with the ResultSet ....
          catch (SQLException ex){
              // handle any errors
              System.out.println("SQLException: " + ex.getMessage());
              System.out.println("SQLState: " + ex.getSQLState());
              System.out.println("VendorError: " + ex.getErrorCode());
          }

          // close SQL connections
          if (this.res != null) {
              try {
                  this.res.close();
              } catch (SQLException sqlEx) { } // ignore
              this.res = null;
          }

          if (stmt != null) {
              try {
                  stmt.close();
              } catch (SQLException sqlEx) { } // ignore
              stmt = null;
          }
    }
    
    public void setData()
    {
        this.makeConnection();
        //this.getAvailableTables();
        this.setResultSet();
    }
    
    public void setResultSet()
    {
        Statement stmt = null;

          try {
              stmt = conn.createStatement();
              this.res = stmt.executeQuery("SELECT * FROM test_mapping");
              parseResultSet();
              }
              // Now do something with the ResultSet ....
          catch (SQLException ex){
              // handle any errors
              System.out.println("SQLException: " + ex.getMessage());
              System.out.println("SQLState: " + ex.getSQLState());
              System.out.println("VendorError: " + ex.getErrorCode());
          }

          // close SQL connections
          if (this.res != null) {
              try {
                  this.res.close();
              } catch (SQLException sqlEx) { } // ignore
              this.res = null;
          }

          if (stmt != null) {
              try {
                  stmt.close();
              } catch (SQLException sqlEx) { } // ignore
              stmt = null;
          }
    }
    
    public void parseResultSet() throws java.sql.SQLException
    {   
        ResultSetMetaData rsMetaData = res.getMetaData();
        int numberOfColumns = rsMetaData.getColumnCount();

        // get the column names; column indexes start from 1
        for (int i = 1; i < numberOfColumns + 1; i++) {
          String columnName = rsMetaData.getColumnName(i);
          colNamesStr += columnName + '\t';
          colNames.add(columnName);
        }
            
        String temp = "";
        while(this.res.next())
        {
            for(int i = 0; i < this.colNames.size(); i++)
            {
                temp += this.res.getString(this.colNames.get(i)) + "\t";
            }
            this.resultLines.add(temp);
            temp = "";
        }
    }
    
    public void makeConnection()
    {
        try
           {
               Class.forName ("com.mysql.jdbc.Driver").newInstance();
               this.conn = DriverManager.getConnection (this.url, this.userName, this.password);
               System.out.println ("Database connection established");
           }
           catch (Exception e)
           {
               System.err.println(e);
               System.err.println ("Cannot connect to database server");
           }
    }
    
    public void close_connection()
    {
        if (conn != null)
             {
                 try
                 {
                     conn.close();
                     System.out.println ("Database connection terminated");
                 }
                 catch (Exception e) { /* ignore close errors */  }
             }
    }
    
    public String toString()
    {
        String lines = "";
        for(int i = 0; i < this.colNames.size(); i++)
            lines += colNames.get(i) + "\t";
        lines += "\n";
        for(int i = 0; i < this.resultLines.size(); i++)
            lines += resultLines.get(i) + "\n";
        return lines;
    }
    
   public static void main (String[] args)
   {
        mysqlConnect c = new mysqlConnect("root","desudesu","jdbc:mysql://127.0.0.1/topiarytool");
        c.setData();
        System.out.println(c.toString());          
   }
}
