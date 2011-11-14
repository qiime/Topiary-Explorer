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


/**
 * <<Class summary>>
 *
 * @author Meg Pirrung &lt;&gt;
 * @version $Rev$
 */
public class PruneTreeDialog extends JFrame{
    MainFrame frame = null;
    TreeWindow treeWindow = null;
    
    JLabel pruneLabel = new JLabel("Prune tree by");
    
    JRadioButton metadataButton = new JRadioButton("Metadata");
    JRadioButton branchLengthButton = new JRadioButton("Branch length");
    JRadioButton numNodesButton = new JRadioButton("Number of nodes", true);
    ButtonGroup radioButtons = new ButtonGroup();
    
    JRadioButton sampleRadioButton = new JRadioButton("Sample metadata");
    JRadioButton otuRadioButton = new JRadioButton("OTU metadata", true);
    ButtonGroup metaRadioGroup = new ButtonGroup();
    
    DataTable data = null;
    JComboBox categoryComboBox = new JComboBox();
    JList valuesList = new JList();
    JScrollPane valuesPane = new JScrollPane();
    
    NumberFormat lformat;// = new IntegerFormat();
    NumberFormatter lengthFormat;// = new NumberFormatter(lformat.getIntegerInstance());
    JFormattedTextField branchLengthField;// = new JFormattedTextField(lengthFormat);
    // JTextField branchLengthField = new JTextField("1",3);
    JLabel branchLengthLabel = new JLabel("%");
    
    NumberFormatter numFormat = new NumberFormatter(NumberFormat.getIntegerInstance());
    JFormattedTextField nodeNumField = new JFormattedTextField(numFormat);
    JLabel nodeNumLabel = new JLabel("nodes");
    
    JButton okButton = new JButton("Prune");
    
    boolean sampleMeta = false;
    boolean otuMeta = false;
    
	// {{{ PruneTreeWindow constructor
    /**
     * 
     */
    public PruneTreeDialog(MainFrame _frame, TreeWindow _treeWindow, 
        boolean _otuMeta, boolean _sampleMeta) {

        this.setTitle("Prune Tree");
        frame = _frame;
        treeWindow = _treeWindow;
        otuMeta = _otuMeta;
        sampleMeta = _sampleMeta;
        this.setSize(new Dimension(310,300));
        
        GroupLayout layout = new GroupLayout(getContentPane());
        this.setLayout(layout);
        
        metadataButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                metadataButtonPressed();
                togglePanes();
            }
        });
        metadataButton.setToolTipText("Prune tree based on selected metadata."
            );
        
        branchLengthButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                togglePanes();
            }
        });
        branchLengthButton.setToolTipText("Prune tips which are less than selected percentage of total branch length. Must be between 0 and 100.");
        
        numNodesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                togglePanes();
            }
        });
        numNodesButton.setToolTipText("Prune tips with shortest branch length until the tree is less than the desired number of nodes.");
        
        radioButtons.add(metadataButton);
        radioButtons.add(branchLengthButton);
        radioButtons.add(numNodesButton);
        
        sampleRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                metadataButtonPressed();
            }
        });
        otuRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                metadataButtonPressed();
            }
        });
        
        metaRadioGroup.add(sampleRadioButton);
        metaRadioGroup.add(otuRadioButton);
        
        categoryComboBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                categorySelected();
            }
        });
        
        valuesPane = new JScrollPane(valuesList);
        
        lformat = NumberFormat.getIntegerInstance();
        lformat.setMaximumIntegerDigits(2);
        lengthFormat = new NumberFormatter(lformat.getIntegerInstance());
        lengthFormat.setAllowsInvalid(false);
        lengthFormat.setOverwriteMode(true);
        
        branchLengthField = new JFormattedTextField(lengthFormat);
        branchLengthField.setValue(new Integer(1));
        branchLengthField.setColumns(3);
        
        numFormat.setAllowsInvalid(false);
        numFormat.setOverwriteMode(true);
        nodeNumField.setColumns(5);
        nodeNumField.setValue(new Integer(30000));
        
        togglePanes();
        
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pruneTree();
            }
        });
        
        // obnoxious layout stuff
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(numNodesButton)
                            .addComponent(metadataButton)
                            .addComponent(branchLengthButton)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(29, 29, 29)
                                .addComponent(branchLengthField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                // .addGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(branchLengthLabel))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(29, 29, 29)
                                .addComponent(nodeNumField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                // .addGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(nodeNumLabel))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                            .addComponent(valuesPane, GroupLayout.Alignment.LEADING)
                            .addComponent(categoryComboBox, GroupLayout.Alignment.LEADING, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(otuRadioButton)
                                // .addGap(LayoutStyle.UNRELATED)
                                .addComponent(sampleRadioButton)))))
                // .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                )
            .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(249, Short.MAX_VALUE)
                .addComponent(okButton))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                // .addContainerGap()
                .addComponent(metadataButton)
                // .addGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(otuRadioButton)
                    .addComponent(sampleRadioButton))
                // .addGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(categoryComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                // .addGap(LayoutStyle.UNRELATED)
                .addComponent(valuesPane, GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE)
                // .addGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(branchLengthButton)
                        // .addGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(branchLengthField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(branchLengthLabel))
                        // .addGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(numNodesButton)
                        // .addGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(nodeNumField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(nodeNumLabel))
                        // .addContainerGap()
                        )
                    )
                    .addComponent(okButton))
        );
        this.show();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    public void togglePanes() {
        sampleRadioButton.setEnabled((metadataButton.isSelected() && sampleMeta));
        otuRadioButton.setEnabled((metadataButton.isSelected() && otuMeta));
        categoryComboBox.setEnabled(metadataButton.isSelected());
        valuesList.setEnabled(metadataButton.isSelected());
        branchLengthField.setEnabled(branchLengthButton.isSelected());
        nodeNumField.setEnabled(numNodesButton.isSelected());
    }
    
    public void metadataButtonPressed() {
        if(otuRadioButton.isSelected() && otuMeta)
        {
            data = frame.otuMetadata;
        }
        else if(sampleRadioButton.isSelected() && sampleMeta)
        {
            data = frame.sampleMetadata;
        }
        
        DefaultComboBoxModel model = new DefaultComboBoxModel(
            data.getColumnNames().toArray());
        categoryComboBox.setModel(model);
        categorySelected();
    }
    
    public void categorySelected() {
        String category = (String)categoryComboBox.getSelectedItem();
        DefaultListModel model = new DefaultListModel();
        HashSet set = new HashSet(data.getColumn(data.getColumnIndex(category)));
        for(Object o : set)
            model.addElement(o);
        valuesList.setModel(model);
    }
	
	public void pruneTree() {
	    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	    if(metadataButton.isSelected())
	    {
	        int index = valuesList.getSelectedIndex();
	        if(index == -1)
	        {
	            JOptionPane.showMessageDialog(null, "ERROR: You have not selected any values to prune by.",
	            "Error", JOptionPane.ERROR_MESSAGE);
	            this.setCursor(Cursor.getPredefinedCursor(
	                Cursor.DEFAULT_CURSOR));
	            return;
	        }
	        
	        if(otuRadioButton.isSelected() && otuMeta)
            {
                for(Object o : valuesList.getSelectedValues())
                  treeWindow.pruneTreeByOtu(
                    (String)categoryComboBox.getSelectedItem(), o);
            }
            else if(sampleRadioButton.isSelected() && sampleMeta)
            {
                for(Object o : valuesList.getSelectedValues())
                  treeWindow.pruneTreeBySample(
                    (String)categoryComboBox.getSelectedItem(), o);
            }
            
            treeWindow.setTreeVals(treeWindow.tree.getTree());
            treeWindow.tree.setTree(treeWindow.tree.getTree());
            treeWindow.tree.getTree().updateBranchColorFromChildren();
	    }
	    else if(branchLengthButton.isSelected())
	    {
	        int i = ((Number)branchLengthField.getValue()).intValue();
	        if(i < 100 && i > 0)
	            treeWindow.pruneTreeByBranchLength(i/100.0);
	        else
	            {
	                JOptionPane.showMessageDialog(null, "ERROR: Value must be between 0 and 100.", "Error", JOptionPane.ERROR_MESSAGE);
	                branchLengthField.setValue(new Integer(1));
	                this.setCursor(Cursor.getPredefinedCursor(
	                    Cursor.DEFAULT_CURSOR));
	                return;
	            }
	    }
	    else if(numNodesButton.isSelected())
	    {
	        int i = ((Number)nodeNumField.getValue()).intValue();
	        int numLeaves = treeWindow.tree.getTree().getNumberOfLeaves();
	        if(i < numLeaves)
	            treeWindow.pruneTreeByNumNodes(i);
	        else
	        {
	            JOptionPane.showMessageDialog(null, "ERROR: Value must be less than the number of nodes in the current tree ("+numLeaves+").", "Error", JOptionPane.ERROR_MESSAGE);
	            nodeNumField.setValue(new Integer(30000));
	            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	            return;
	        }
	        
	    }
	    treeWindow.tree.redraw();
	    treeWindow.treeEditToolbar.summaryPanel.updateTable();
	    this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    this.dispose();
	}
}
