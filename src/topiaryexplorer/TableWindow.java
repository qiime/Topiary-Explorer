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

public class TableWindow extends JFrame {
    MainFrame frame = null;
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
        
        setAsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                setTableAs();
            }
        });
        
        add(setAsButton, BorderLayout.NORTH);
        this.setVisible(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    public void setTableAs() {
         Object[] possibilities = {"Tip Data", "OTU Table", "Sample Data"};
         String tableName = (String)JOptionPane.showInputDialog(
                             this,
                             "Use database results in which table?",
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
