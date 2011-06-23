package topiaryexplorer;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * databaseFilterPane.java
 *
 * Created on Jun 16, 2011, 12:30:52 PM
 */

/**
 *
 * @author meg pirrung
 */

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.media.opengl.*;
import java.sql.*;
import javax.swing.table.*;
import java.io.*;
import javax.swing.table.*;
import javax.jnlp.*;
import javax.swing.text.*;
import java.text.*;

public class DatabaseFilterPanel extends JPanel {
    MainFrame frame = null;
    JPanel selectTablePanel = new JPanel();
    JTable tableNamesTable = new JTable(new SparseTableModel());
    JScrollPane tableNamesScrollPane = new JScrollPane();
    DataTable tableNames = new DataTable();
    // JList tableNames = new JList();
    JLabel selectTableLabel = new JLabel("Select A Table");
    JTextField searchField = new JTextField();
    JLabel searchLabel = new JLabel("Search");
    JPanel columnNamesPanel = new JPanel();
    JLabel columnsLabel = new JLabel("Select Columns");
    JScrollPane columnNamesScrollPane = new JScrollPane();
    JList columnNamesList = new JList();
    JButton addColumnFilterButton = new JButton("v");
    JButton resetColumnFilterButton = new JButton("Reset");
    JPanel columnFiltersPanel = new JPanel();
    JTabbedPane columnFiltersTabPane = new JTabbedPane();
    JButton filterTableButton = new JButton("Filter Table");

    /** Creates new form databaseFilterPane */
    public DatabaseFilterPanel(MainFrame _frame) {
        frame = _frame;
        tableNamesScrollPane = new JScrollPane(tableNamesTable);
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
    		public void changedUpdate(javax.swing.event.DocumentEvent evt) {
    			doSearch();
    		}

    		public void insertUpdate(javax.swing.event.DocumentEvent evt) {
    			doSearch();
    		}

    		public void removeUpdate(javax.swing.event.DocumentEvent evt) {
    			doSearch();
    		}

    		private void doSearch() {
                // String str = search.getText().trim().toUpperCase();
    			
    		}
    	});
        
        columnNamesScrollPane.setViewportView(columnNamesList);

        addColumnFilterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                addColumnFilterButtonActionPerformed(evt);
            }
        });

        resetColumnFilterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                resetColumnFilterButtonActionPerformed(evt);
            }
        });
        
        
        
        // obnoxious layout stuff
        GroupLayout selectTablePanelLayout = new GroupLayout(selectTablePanel);
        selectTablePanel.setLayout(selectTablePanelLayout);
        selectTablePanelLayout.setHorizontalGroup(
            selectTablePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(selectTablePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(selectTablePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(selectTablePanelLayout.createSequentialGroup()
                        .addComponent(tableNamesScrollPane, GroupLayout.PREFERRED_SIZE, 211, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(selectTablePanelLayout.createSequentialGroup()
                        .addComponent(searchLabel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(searchField, GroupLayout.PREFERRED_SIZE, 108, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(71, Short.MAX_VALUE))
                    .addGroup(selectTablePanelLayout.createSequentialGroup()
                        .addComponent(selectTableLabel)
                        .addContainerGap(142, Short.MAX_VALUE))))
        );
        selectTablePanelLayout.setVerticalGroup(
            selectTablePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(selectTablePanelLayout.createSequentialGroup()
                .addComponent(selectTableLabel)
                .addGap(4, 4, 4)
                .addComponent(tableNamesScrollPane, GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(selectTablePanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(searchLabel)
                    .addComponent(searchField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
        );

        GroupLayout columnNamesPanelLayout = new GroupLayout(columnNamesPanel);
        columnNamesPanel.setLayout(columnNamesPanelLayout);
        columnNamesPanelLayout.setHorizontalGroup(
            columnNamesPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, columnNamesPanelLayout.createSequentialGroup()
                // .addGap(106, 106, 106)
                .addComponent(resetColumnFilterButton)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(addColumnFilterButton, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
                // .addContainerGap()
                .addGap(10, 10, 10)
                )
            .addGroup(columnNamesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(columnsLabel)
                .addContainerGap(133, Short.MAX_VALUE))
            .addGroup(columnNamesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(columnNamesScrollPane, GroupLayout.PREFERRED_SIZE, 220, GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        columnNamesPanelLayout.setVerticalGroup(
            columnNamesPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(columnNamesPanelLayout.createSequentialGroup()
                .addComponent(columnsLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(columnNamesScrollPane, GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(columnNamesPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(addColumnFilterButton)
                    .addComponent(resetColumnFilterButton)))
        );

        GroupLayout columnFiltersPanelLayout = new GroupLayout(columnFiltersPanel);
        columnFiltersPanel.setLayout(columnFiltersPanelLayout);
        columnFiltersPanelLayout.setHorizontalGroup(
            columnFiltersPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(columnFiltersPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(columnFiltersTabPane, GroupLayout.DEFAULT_SIZE, 487, Short.MAX_VALUE))
        );
        columnFiltersPanelLayout.setVerticalGroup(
            columnFiltersPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, columnFiltersPanelLayout.createSequentialGroup()
                .addComponent(columnFiltersTabPane, GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
                .addContainerGap())
        );

        columnFiltersTabPane.getAccessibleContext().setAccessibleName("SampleID");

        filterTableButton.setText("Filter Table");

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(selectTablePanel, GroupLayout.PREFERRED_SIZE, 250, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(columnNamesPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addComponent(columnFiltersPanel, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(386, Short.MAX_VALUE)
                        .addComponent(filterTableButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addComponent(selectTablePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(columnNamesPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(7, 7, 7)
                .addComponent(columnFiltersPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(filterTableButton)
                .addContainerGap())
        );
    }

    public void addColumnFilterButtonActionPerformed(ActionEvent evt) {
        // TODO add your handling code here:
    }

    public void resetColumnFilterButtonActionPerformed(ActionEvent evt) {
        // TODO add your handling code here:
    }
    
    // public void setDatabaseConnection1(dbConnect c) {
    //     c.getAvailableTables();
    //     tableNames = new JList();
    //     
    //     TableSorter sorter = new TableSorter(tableNames.getModel(), );
    //          tableNamesTable.setModel(sorter);
    // }

    public void setDatabaseConnection(dbConnect c) {
        c.getAvailableTables();
        tableNames = new DataTable(c);
        SparseTableModel model = new SparseTableModel(tableNames.getData(),
   		 	tableNames.getColumnNames(), false);
   		TableSorter sorter = new TableSorter(model, tableNamesTable.getTableHeader());
   		tableNamesTable.setModel(sorter);
    }
}
