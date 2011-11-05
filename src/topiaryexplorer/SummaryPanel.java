package topiaryexplorer;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.media.opengl.*;
import javax.swing.table.*;
import javax.jnlp.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.sql.*;
import java.text.*;
import java.io.*;

public class SummaryPanel extends JPanel{
     TreeWindow frame = null;
     Node root = null;
     JScrollPane summaryScrollPane = new JScrollPane();
     JTable summaryTable = new JTable(new ColorTableModel());
     ArrayList<ArrayList<Object>> dataList = new ArrayList<ArrayList<Object>>();
     JLabel numTipsLabel = new JLabel();
     JPanel pieChartPanel = new JPanel();
     private ArrayList<Double> data = new ArrayList<Double>();
     private ArrayList<Object> colors = new ArrayList<Object>();
     private ArrayList<Object> values = new ArrayList<Object>();
     private PieChartVis pVis = new PieChartVis();
     
    SummaryPanel(TreeWindow _frame) {
        frame = _frame;
        
         // summaryTable.setDragEnabled(true);
        summaryScrollPane = new JScrollPane(summaryTable);
        summaryScrollPane.setWheelScrollingEnabled(true);
        
        pieChartPanel.setLayout(new BorderLayout());
        pieChartPanel.add(pVis, BorderLayout.CENTER);
        
        this.setLayout(new GridLayout(3,1));

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(summaryScrollPane, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(numTipsLabel))
            .addComponent(pieChartPanel, GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(numTipsLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pieChartPanel, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(summaryScrollPane, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))
        );
        
        pieChartPanel.setVisible(false);
        summaryScrollPane.setVisible(false);
    }
    
    public void setTree(Node r) {
        root = r;
        numTipsLabel.setText(String.format("%d leaves", root.getNumberOfLeaves()));
    }
    
    public void showPanel(boolean b) {
        pieChartPanel.setVisible(b);
        summaryScrollPane.setVisible(b);
    }
    
    public void treeColored() {
        pVis.init();
        pVis.setPieChartVis(root);
        pieChartPanel.setVisible(true);
        summaryScrollPane.setVisible(true);
        data = root.getGroupBranchFraction();
        colors = new ArrayList<Object>();
        values = new ArrayList<Object>();
        colors.addAll(root.getGroupBranchColor());
        values.addAll(root.getGroupBranchValue());
        buildTable();
    }
    
    // public void setTable(DataTable d) {
        // SparseTableModel model = new SparseTableModel(d.getData(),
         // d.getColumnNames());
        // TableSorter sorter = new TableSorter(model, summaryTable.getTableHeader());
        // summaryTable.setModel(sorter);
        // summaryScrollPane = new JScrollPane(summaryTable);
    // }
    
    public void buildTable() {
        // DataTable dt = new DataTable();
        ArrayList<String> headers = new ArrayList<String>();
        headers.add("%");
        headers.add("");
        headers.add("Value");
        dataList = new ArrayList<ArrayList<Object>>();
        double total = 0;
        for (int i = 0; i < data.size(); i++) 
          total += data.get(i);        
        
        for (int i = 0; i < data.size(); i++) {
          ArrayList<Object> row = new ArrayList<Object>();
          row.add(String.format("%.2f",100*(data.get(i)/total)));
          row.add(colors.get(i));
          row.add(values.get(i));
          dataList.add(row);
        }
         ColorTableModel model = new ColorTableModel(dataList, headers);
 		 TableSorter sorter = new TableSorter(model, summaryTable.getTableHeader());

         //set color blocks
 		 for(int i = 0; i < data.size(); i++)
 		    sorter.setValueAt((Color)colors.get(i),i,1);
 		 
         summaryTable.setModel(sorter);
 		 summaryTable.setDefaultRenderer(Color.class, new ColorRenderer(true));
 		 summaryTable.setSelectionBackground(new Color(255,255,255));
 		 summaryTable.setSelectionForeground(new Color(0,0,0));      summaryTable.getColumnModel().getColumn(0).setPreferredWidth(40);
         summaryTable.getColumnModel().getColumn(1).setPreferredWidth(20);
         summaryTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        // summaryTable.setModel(sorter);
    }
}