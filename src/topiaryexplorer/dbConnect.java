package topiaryexplorer;

import java.sql.*;
import java.lang.*;
import java.util.*;
import oracle.jdbc.pool.*;

public class dbConnect
{
    String userName = "";
    String password = "";
    String databaseName = "";
    String serverName = "";
    String currentTable = "";
    Connection conn = null;
    ResultSet res = null;
    ArrayList<String> resultLines = new ArrayList<String>();
    ArrayList<String> colNames = new ArrayList<String>();
    String colNamesStr = new String();
    OracleDataSource ods = null;// new OracleDataSource();
    Statement stmt = null;
    
    public dbConnect(String un, String pw, String dbName, String svName)
    {
        userName = un;
        password = pw;
        databaseName = dbName;
        serverName = svName;
    }
    
    public ArrayList<String> getResultLines()
    {
        return this.resultLines;
    }
    
    public void getAvailableTables()
    {
        reset();
        stmt = null;
        currentTable = "";

          try {
              stmt = conn.createStatement();
              this.res = stmt.executeQuery("select object_name from user_objects where object_type = 'TABLE'");
              parseResultSet();
              }
              // Now do something with the ResultSet ....
          catch (SQLException ex){
              // handle any errors
              System.out.println("getavailabletables");
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
    
    public boolean getDataFromTable(String tableName) {
        reset();
        currentTable = tableName;
        boolean success = false;

          try {
              stmt = conn.createStatement();
              this.res = stmt.executeQuery("select * from " + tableName);
              parseResultSet();
              success = true;
              }
              // Now do something with the ResultSet ....
          catch (SQLException ex){
              // handle any errors
              System.out.println("getdatafromtable");
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
          return success;
    }
    
    public void resetCurrentTable()
    {
        getDataFromTable(currentTable);
    }
    
    public boolean searchCurrentTable(String query)
    {
        reset();
        stmt = null;
        boolean success = false;
          try {
              stmt = conn.createStatement();
              this.res = stmt.executeQuery(query);
              parseResultSet();
              success = true;
              }
              // Now do something with the ResultSet ....
          catch (SQLException ex){
              // handle any errors
              System.out.println("setresultset");
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
        return success;
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
    
    public Boolean makeConnection() //throws ClassNotFoundException, SQLException
    {
        try
           {
               ods = new OracleDataSource();
               ods.setDriverType("thin");
               ods.setServerName(serverName);
               ods.setPortNumber(1521);
               ods.setDatabaseName(databaseName);
               ods.setUser(userName);
               ods.setPassword(password);
             // Connect to the databse
             this.conn = ods.getConnection();
             return true;
           }
           catch (Exception e)
           {
               System.err.println(e);
               System.err.println("Cannot connect to database server");
               return false;
           }
    }
    
    public void reset()
    {
        resultLines = new ArrayList<String>();
        colNames = new ArrayList<String>();
        colNamesStr = new String();
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
    
    public void close_connection()
    {
        reset();
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
         dbConnect c = new dbConnect("meg","omgwtfbbq","microbe","microbiome1.colorado.edu");
         c.makeConnection();
         c.getAvailableTables();
         System.out.println(c.toString());
    }
}
