package topiaryexplorer;

import com.sun.opengl.util.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.media.opengl.*;
import java.sql.*;
import javax.swing.table.*;
import java.io.*;
import javax.jnlp.*;
import java.text.*;

public class TableWindow extends JFrame {
    MainFrame frame = null;
    JPanel buttonPanel = new JPanel();
    JButton filterButton = new JButton("Filter...");
    JButton mergeButton = new JButton("Merge...");
    JButton setAsButton = new JButton("Set As...");
    DataTable dataTable = new DataTable();
    JTable table = new JTable(new SparseTableModel());
    JScrollPane scrollPane = new JScrollPane();

    public TableWindow(MainFrame _frame, DataTable _dataTable) {
        frame = _frame;
        dataTable = _dataTable;
        this.setSize(new Dimension(810,800));
        setLayout(new BorderLayout());
        
        SparseTableModel model = new SparseTableModel(dataTable.getData(),
        dataTable.getColumnNames());
        TableSorter sorter = new TableSorter(model, table.getTableHeader());
        table.setModel(sorter);
        
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setCellSelectionEnabled(true);
        scrollPane = new JScrollPane(table);
        scrollPane.setWheelScrollingEnabled(true);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);

		scrollPane.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER, new AddColumnButton(frame, dataTable, table));
        
        setAsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                setTableAs();
            }
        });
        
        filterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                filterTable();
            }
        });
        
        mergeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mergeButtonPressed();
            }
        });
        buttonPanel.setLayout(new GridLayout(1,3));
        buttonPanel.add(setAsButton);
        buttonPanel.add(filterButton);
        buttonPanel.add(mergeButton);
        add(buttonPanel, BorderLayout.NORTH);
        this.setVisible(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    public void filterTable() {
        TableFilterDialog tfd = new TableFilterDialog(frame, this, dataTable);
    }
    
    public void mergeButtonPressed() {
     Object[] possibilities = {"Tip Data", "OTU Table", "Sample Data"};
         String tableName = (String)JOptionPane.showInputDialog(
                             this,
                             "Merge with which table?",
                             "Merge...",
                             JOptionPane.PLAIN_MESSAGE,
                             null,
                             possibilities,
                             possibilities[0]);

         //If a string was returned, say so.
         if (tableName != null) {
             DataTable mergedTable = null;
             switch(tableName.charAt(0))
             {
                 case 'O':
                    mergedTable = mergeTables(frame.otuSampleMap);
                    frame.setOtuSampleMap(mergedTable.toStrings());
                     break;
                 case 'T':
                    mergedTable = mergeTables(frame.otuMetadata);
                    frame.setOtuMetadata(mergedTable.toStrings());
                     break;
                 case 'S':
                    mergedTable = mergeTables(frame.sampleMetadata);
                    frame.setSampleMetadata(mergedTable.toStrings());
                     break;
                 default:
             }
             
             this.dispose();
         }   
    }
    
    public DataTable mergeTables(DataTable curr_table) {
            DataTable newTable = new DataTable();
            int ref_col = 0;
            ArrayList<String> headers = curr_table.getColumnNames();
            ArrayList<ArrayList<Object>> columns = new ArrayList<ArrayList<Object>>();
            ArrayList<Object> tempColumn = new ArrayList<Object>();
            
            for(int i = 0; i < headers.size(); i++)
            {
                tempColumn = new ArrayList<Object>();
                tempColumn.add(headers.get(i));
                tempColumn.addAll(curr_table.getColumn(i));
                columns.add(tempColumn);
            }
            
            ArrayList<String> merge_headers = dataTable.getColumnNames();
            merge_headers.remove(0);
            // headers.addAll(merge_headers);
            
            for(int i = 0; i < merge_headers.size(); i++)
            {
                tempColumn = new ArrayList<Object>();
                tempColumn.add(merge_headers.get(i));
                columns.add(tempColumn);
            }
        
            ArrayList<String> ref_ids = curr_table.getRowNames();
            ArrayList<String> merge_ids = dataTable.getRowNames();
            String ref_id = "";
            int merge_index = 0;
            int offset = curr_table.getColumnCount();
            for(int i = 0; i < curr_table.getRowCount(); i++)
            {
                ref_id = ref_ids.get(i);
                merge_index = merge_ids.indexOf(ref_id);
                for(int j = 0; j < merge_headers.size(); j++)
                {
 columns.get(j+offset).add(dataTable.getValueAt(merge_index,j+1));
                }
            
            }
       
            for(int i = 0; i < columns.size(); i++)
                newTable.addColumn(columns.get(i));
        
            return newTable;
    }
    
    public void setTableAs() {
         Object[] possibilities = {"Tip Data", "OTU Table", "Sample Data"};
         String tableName = (String)JOptionPane.showInputDialog(
                             this,
                             "Set as which table?",
                             "Set results as",
                             JOptionPane.PLAIN_MESSAGE,
                             null,
                             possibilities,
                             possibilities[0]);

         //If a string was returned, say so.
         if (tableName != null) {
             SparseTableModel model = null;
             TableSorter sorter = null;
             switch(tableName.charAt(0))
             {
                 case 'O':
                    frame.setOtuSampleMap(dataTable.toStrings());
                     break;
                 case 'T':
                    frame.setOtuMetadata(dataTable.toStrings());
                     break;
                 case 'S':
                    frame.setSampleMetadata(dataTable.toStrings());
                     break;
                 default:
             }
             
             this.dispose();
         }
     }
}
