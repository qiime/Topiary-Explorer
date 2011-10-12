package topiaryexplorer;

import com.sun.opengl.util.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.media.opengl.*;
import java.sql.*;
import javax.swing.table.*;
import java.io.*;
import javax.jnlp.*;
import javax.swing.*;
import java.text.*;

public class ConsensusLineageDialog extends JDialog {
    MainFrame frame = null;
    TreeWindow treeWindow = null;
    DataTable data = null;
    TreeVis tree = null;
    JLabel taxLabel = new JLabel("Taxonomy Column");
    JComboBox taxComboBox = new JComboBox();
    JComboBox levelComboBox = new JComboBox();
    JLabel thresholdLabel = new JLabel("Threshold");
    Integer[] ops = {10,20,30,40,50,60,70,80,90,95,96,97,98,
          99,100};
    JComboBox thresholdComboBox = new JComboBox(ops);
    JLabel percLabel = new JLabel("%");
    JButton okButton = new JButton("OK");
    JButton cancelButton = new JButton("Cancel");
    JCheckBox levelCheckBox = new JCheckBox();
        
    /** Creates new form ConsensusLineageDialog */
    public ConsensusLineageDialog(MainFrame _frame, TreeWindow _treeWindow) {
        frame = _frame;
        treeWindow = _treeWindow;
        tree = treeWindow.tree;
        data = frame.otuMetadata;
        
        this.setSize(new Dimension(310,250));
        // super(parent, modal);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        
        DefaultComboBoxModel model = new DefaultComboBoxModel(
            data.getColumnNames().toArray());
        taxComboBox.setModel(model);
        
        taxComboBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                taxSelected();
            }
        });
        
        levelComboBox.setEnabled(levelCheckBox.isSelected());

        levelCheckBox.setText("Taxonomic Level");
        levelCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                levelComboBox.setEnabled(levelCheckBox.isSelected());
            }
        });
        
        thresholdComboBox.setSelectedIndex(11);
        
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                okButtonPressed();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                cancelButtonPressed();
            }
        });
        
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addComponent(levelComboBox, 0, 300, Short.MAX_VALUE)
                            .addComponent(taxComboBox, 0, 300, Short.MAX_VALUE))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(thresholdComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addComponent(cancelButton)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
                                .addComponent(okButton))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(percLabel)
                                .addGap(163, 163, 163)))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(thresholdLabel)
                        .addContainerGap(254, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(taxLabel)
                        .addContainerGap(198, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(levelCheckBox)
                        .addContainerGap(179, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(taxLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(taxComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(levelCheckBox)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(levelComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(thresholdLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(thresholdComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(percLabel))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(okButton))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        this.show();
    }    
    
    public void taxSelected() {
        String tax = (String)taxComboBox.getSelectedItem();
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        HashSet set = new HashSet(data.getColumn(data.getColumnIndex(tax)));
        HashSet counts = new HashSet();
        int max = 0;
        for(Object o : set)
            if (((String)o).split(";").length > max)
                max = ((String)o).split(";").length;
                
        for(int i = 1; i <= max; i++)
            model.addElement(i);
            
        levelComboBox.setModel(model);
    }
    
    public void okButtonPressed() {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        double f = ((Integer)thresholdComboBox.getSelectedItem())/100.0;
        int col = data.getColumnNames().indexOf((String)taxComboBox.getSelectedItem());
        for (Node n : tree.getTree().getLeaves()) {
            String nodeName = n.getName();
            int id = data.getRowNames().indexOf(nodeName);
            n.setLineage(""+data.getValueAt(id, col));
         }
         
         if(levelCheckBox.isSelected()) {
             for(Node n : tree.getTree().getNodes())
                 n.setConsensusLineage(n.getConsensusLineageF(f, levelComboBox.getSelectedIndex()));
         }
         else {
             for(Node n : tree.getTree().getNodes())
                 n.setConsensusLineage(n.getConsensusLineageF(f));
         }
         
         tree.redraw();
         this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
         this.dispose();
    }
    
    public void cancelButtonPressed() {
        this.dispose();
    }

}
