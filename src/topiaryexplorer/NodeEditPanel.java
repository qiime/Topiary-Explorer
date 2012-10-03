package topiaryexplorer;

import processing.core.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.event.*;
import javax.swing.border.LineBorder;
import javax.swing.event.*;
import javax.media.opengl.*;
import java.sql.*;
import javax.swing.table.*;
import java.io.*;
import javax.jnlp.*;
import javax.swing.plaf.basic.BasicArrowButton;

/**
 * <<Class summary>>
 *
 * @author Meg Pirrung &lt;&gt;
 * @version $Rev$
 */
public final class NodeEditPanel extends JPanel{
    TreeWindow frame = null;
    TreeVis vis = null;
    JLabel wedgeLabel = new JLabel("Customize nodes:");
    
    JCheckBox pieChartCheckBox = new JCheckBox("Pie chart");
    JLabel pieChartLabel = new JLabel(" Pie chart radius");
    JSlider pieChartRadius = new JSlider(1,100,15);
    JCheckBox nodeLabelCheckBox = new JCheckBox("Tip labels");
    JCheckBox internalLabelCheckBox = new JCheckBox("Internal node labels");
    JPanel labelPanel = new JPanel();
    CollapsablePanel labelPanelCP = new CollapsablePanel("Labels",labelPanel, 
        false, true);
    Boolean warningShown = false;
    JButton tipLabelButton = new JButton("Set as...");
    
    JLabel fntSizeLabel = new JLabel("Size: ");
    JTextField fntSize = new JTextField("",3);
    String[] fntFaces = {"SansSerif","Serif","Courier"};//PFont.list();
    JComboBox fntFace = new JComboBox(fntFaces);
    JLabel ptLabel = new JLabel("pt");
    BasicArrowButton fntIncButton = new BasicArrowButton(SwingConstants.NORTH);
    BasicArrowButton fntDecButton = new BasicArrowButton(SwingConstants.SOUTH);
    // JButton fntIncButton = new JButton("^");
    // JButton fntDecButton = new JButton("v");
    JPanel fntPanel = new JPanel();
    JPanel fntSizePanel = new JPanel();
    JPanel fntButtonPanel = new JPanel();
    JPanel fntColorPanel = new JPanel();
    JLabel fntColorLabel = new JLabel("Color: ");
    JButton colorButton = new JButton("Color");
    JLabel colorLabel = new JLabel("  ");
    JColorChooser colorChooser = new JColorChooser();
    
    JPanel holder = new JPanel();
    JPanel holder1 = new JPanel();
    JPanel holder2 = new JPanel();
    JPanel pieChartHolder = new JPanel();
    JPanel holder3 = new JPanel();
    
    JLabel colorByLabel = new JLabel("Color by:");
    JButton colorByButton = new JButton("Color by...");
    ColorByPopupMenu colorByMenu;// = new ColorByComboBox()
    
	// {{{ NodeEditPanel constructor
    /**
     * 
     */
    public NodeEditPanel(TreeWindow _frame) {
        super();
        frame = _frame;
        vis = frame.tree;
        colorByMenu = new ColorByPopupMenu(frame.frame, frame, 
            frame.frame.labelColorPanel,1);
        this.setToolTipText("Customize nodes");
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		// setLayout(new GridLayout(4,1));
        
        pieChartHolder.setLayout(new BorderLayout());
        pieChartRadius.addChangeListener(new ChangeListener() {
            public synchronized void stateChanged(ChangeEvent e) {
                if (pieChartRadius.getValueIsAdjusting()){
                    frame.tree.setPieChartRadius(pieChartRadius.getValue());
                }
                frame.tree.redraw();
            }
        });
        
        // pieChartRadius.setPreferredSize(new Dimension(100, 20));
        
        pieChartRadius.setMajorTickSpacing(10);
        pieChartRadius.setMinorTickSpacing(5);
        pieChartRadius.setSnapToTicks(true);
        pieChartHolder.add(pieChartLabel, BorderLayout.NORTH);
        pieChartHolder.add(pieChartRadius, BorderLayout.CENTER);
        add(pieChartHolder);
        
        internalLabelCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                vis.setDrawInternalNodeLabels(
                    internalLabelCheckBox.isSelected());
                labelPanelCP.setVisible(nodeLabelCheckBox.isSelected() || internalLabelCheckBox.isSelected());
                frame.treePopupMenu.getComponent(5).setEnabled(
                nodeLabelCheckBox.isSelected() || internalLabelCheckBox.isSelected());
                vis.checkBounds();
                vis.redraw();
            }
        });
		holder1.setLayout(new BorderLayout());
        holder1.add(internalLabelCheckBox, BorderLayout.CENTER);
        // holder1.add(new JLabel());
        add(holder1);
        
        nodeLabelCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                    if(nodeLabelCheckBox.isSelected() && !warningShown && 
                    !vis.getZoomDrawNodeLabels())
                    {
                        JCheckBox checkbox = new JCheckBox("Do not show this message again.");  
                        String message = "Node labels will not show at your current zoom level.\nYou must zoom in more along the y-axis for the labels to appear.\n";  
                        Object[] params = {message, checkbox};  
                        JOptionPane.showMessageDialog(null, params, 
                            "Node labels not shown", 
                            JOptionPane.WARNING_MESSAGE);  
                        warningShown = checkbox.isSelected();
                    }
                    vis.setDrawExternalNodeLabels(nodeLabelCheckBox.isSelected());
                    vis.checkBounds();
                    vis.redraw();
                    labelPanelCP.setVisible(nodeLabelCheckBox.isSelected() || internalLabelCheckBox.isSelected());
                    frame.treePopupMenu.getComponent(5).setEnabled(
                    nodeLabelCheckBox.isSelected() || internalLabelCheckBox.isSelected());
            }
        });
		holder.setLayout(new BorderLayout());
        holder.add(nodeLabelCheckBox, BorderLayout.CENTER);
        // holder.add(new JLabel());
        add(holder);
        
/*        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));*/
        // labelPanel.setLayout(new GridLayout(4,1));
		labelPanel.setLayout(new BorderLayout());
        holder2.setLayout(new GridLayout(2,1));
        tipLabelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.resetTipLabelCustomizer(true);
            }
        });
		holder2.add(tipLabelButton);
        
		colorByButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
				colorByMenu.show(colorByButton, 100, 10);
           } 
        });
		holder2.add(colorByButton);
        labelPanel.add(holder2, BorderLayout.NORTH);

        fntFace.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               vis.setNodeFontFace((String)fntFace.getSelectedItem());
               vis.redraw();
            }
        });
        labelPanel.add(fntFace, BorderLayout.CENTER);
		

        // fntSizePanel.add(fntSizeLabel);
		fntSizePanel.setLayout(new BorderLayout());
		fntButtonPanel.setLayout(new GridLayout(2,1));
        fntSize.setToolTipText("Change the font size of node labels.");
        fntSize.setMaximumSize(new Dimension(30,20));
        fntSize.setText(""+(int)vis.getNodeFontSize());
		fntSize.setFont(new Font("Helvetica",0,10));
        fntSize.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
            }
            public void keyPressed(KeyEvent e) {
            }
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER)
                    vis.setNodeFontSize(Float.parseFloat(fntSize.getText()));
            }
        });
		fntSizePanel.add(fntSize, BorderLayout.CENTER);
		
        fntDecButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                vis.setNodeFontSize(vis.getNodeFontSize()-1);
                fntSize.setText(""+(int)vis.getNodeFontSize());
                    
            }
        });
        fntIncButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                vis.setNodeFontSize(vis.getNodeFontSize()+1);
                fntSize.setText(""+(int)vis.getNodeFontSize());
            }
        }); 
   		fntButtonPanel.add(fntIncButton);
		fntButtonPanel.add(fntDecButton);
		fntSizePanel.add(fntButtonPanel, BorderLayout.EAST);
		labelPanel.add(fntSizePanel, BorderLayout.EAST);

        colorLabel.setPreferredSize(new Dimension(30,15));
        colorLabel.setOpaque(true);
        colorLabel.setBorder(LineBorder.createGrayLineBorder());
        colorLabel.setBackground(Color.WHITE);
        colorLabel.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                Color newColor = JColorChooser.showDialog(
                                     NodeEditPanel.this,
                                     "Choose node label color",
                                     colorLabel.getBackground());
                 if(newColor != null)
                 {
                     colorLabel.setBackground(newColor);
                     vis.setNodeFontColor(newColor.getRGB());
                 }
            }
            
            public void mouseExited(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {}
        }); 
        colorLabel.setToolTipText("Change the node label font color...");
/*        fntColorPanel.add(new JLabel("Font Color: "));*/
/*        fntColorPanel.add(colorLabel);*/
/*        labelPanel.add(fntColorPanel);*/
        
        // labelPanel.add(colorByButton, BorderLayout.CENTER);
/*        labelPanel.add(colorByComboBox);*/
        
        labelPanelCP.setVisible(nodeLabelCheckBox.isSelected());
        add(labelPanelCP);
    }
	// }}}
}
