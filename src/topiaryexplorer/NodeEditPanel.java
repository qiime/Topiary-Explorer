package topiaryexplorer;

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

/**
 * <<Class summary>>
 *
 * @author Meg Pirrung &lt;&gt;
 * @version $Rev$
 */
public final class NodeEditPanel extends JPanel{
    TreeWindow frame = null;
    TreeVis vis = null;
    JLabel wedgeLabel = new JLabel("Customize Nodes:");
    
    JCheckBox pieChartCheckBox = new JCheckBox("Pie Chart");
    JCheckBox nodeLabelCheckBox = new JCheckBox("Tip Labels");
    JCheckBox internalLabelCheckBox = new JCheckBox("Internal Node Labels");
    JPanel labelPanel = new JPanel();
    CollapsablePanel labelPanelCP = new CollapsablePanel("Labels",labelPanel, false, true);
    
    JButton tipLabelButton = new JButton("Customize...");
    
    JLabel fontSizeLabel = new JLabel("Size: ");
    JTextField fntSize = new JTextField("",3);
    String[] fntFaces = {"courier","helvetica","timesroman"};
    JComboBox fntFace = new JComboBox(fntFaces);
    JLabel ptLabel = new JLabel("pt");
    JButton fntIncButton = new JButton("^");
    JButton fntDecButton = new JButton("v");
    JPanel fntPanel = new JPanel();
    JPanel fontSizePanel = new JPanel();
    
    JPanel fontColorPanel = new JPanel();
    JLabel fontColorLabel = new JLabel("Color: ");
    JButton colorButton = new JButton("Color");
    JLabel colorLabel = new JLabel("  ");
    JColorChooser colorChooser = new JColorChooser();
    
    JPanel holder = new JPanel();
    JPanel holder1 = new JPanel();
    
    JLabel colorByLabel = new JLabel("Color By:");
    JButton colorByButton = new JButton("Color By...");
    ColorByPopupMenu colorByMenu;// = new ColorByComboBox()
    
	// {{{ NodeEditPanel constructor
    /**
     * 
     */
    public NodeEditPanel(TreeWindow _frame) {
        super();
        frame = _frame;
        vis = frame.tree;
        colorByMenu = new ColorByPopupMenu(frame.frame, frame, frame.frame.labelColorPanel,1);
        this.setToolTipText("Customize Nodes");
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
/*        setLayout(new GridLayout(1,1));*/
        
        internalLabelCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                vis.setDrawInternalNodeLabels(internalLabelCheckBox.isSelected());
                vis.redraw();
            }
        });
        holder1.add(internalLabelCheckBox);
        holder1.add(new JLabel());
        add(holder1);
        
        nodeLabelCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                    if(nodeLabelCheckBox.isSelected())
                        JOptionPane.showMessageDialog(null, "Node labels will not show unless you are zoomed in enough that the labels would not overlap.", "Warning", JOptionPane.WARNING_MESSAGE);
                    vis.setDrawNodeLabels(nodeLabelCheckBox.isSelected());
                    vis.redraw();
                    labelPanelCP.setVisible(nodeLabelCheckBox.isSelected());
            }
        });
        holder.add(nodeLabelCheckBox);
        holder.add(new JLabel());
        add(holder);
        
/*        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));*/
        labelPanel.setLayout(new GridLayout(4,1));
        
        tipLabelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.resetTipLabelCustomizer(true);
            }
        });
        labelPanel.add(tipLabelButton);
/*        labelPanel.add(fontSizeLabel);*/
        fontSizePanel.add(fontSizeLabel);
        fntSize.setToolTipText("Change the font size of node labels.");
        fntSize.setMaximumSize(new Dimension(30,20));
        fntSize.setText(""+vis.getNodeFontSize());
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
/*        fntSize.setText(""+vis.getNodeFontSize());*/
/*        fntSize.setMaximumSize(new Dimension(20,10));*/
/*        fontSizePanel.add(fntSize);*/
/*        fontSizePanel.add(ptLabel);*/
/*        labelPanel.add(fntFace); */
        fntPanel.setLayout(new GridLayout(2,1));
/*        fntPanel.setMaximumSize(new Dimension(10,10));*/
        fntDecButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                vis.setNodeFontSize(vis.getNodeFontSize()-1);
                fntSize.setText(""+vis.getNodeFontSize());
                    
            }
        });
        
/*        fntPanel.add(fntDecButton);*/
/*        fontSizePanel.add(fntPanel);*/
        fontSizePanel.add(fntDecButton);
        fontSizePanel.add(fntSize);
        fntIncButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                vis.setNodeFontSize(vis.getNodeFontSize()+1);
                fntSize.setText(""+vis.getNodeFontSize());
            }
        });
/*        fntPanel.add(fntIncButton);*/
        fontSizePanel.add(fntIncButton);       
        labelPanel.add(fontSizePanel);
        
/*        add(fontColorLabel);*/

        colorLabel.setPreferredSize(new Dimension(30,15));
        colorLabel.setOpaque(true);
        colorLabel.setBorder(LineBorder.createGrayLineBorder());
        colorLabel.setBackground(Color.WHITE);
        colorLabel.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                Color newColor = JColorChooser.showDialog(
                                     NodeEditPanel.this,
                                     "Choose Node Label Color",
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
        fontColorPanel.add(new JLabel("Font Color: "));
        fontColorPanel.add(colorLabel);
        labelPanel.add(fontColorPanel);
        
        colorByButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
				colorByMenu.show(colorByButton, colorByLabel.getX(), colorByLabel.getY());
           } 
        });
        
        labelPanel.add(colorByButton);
/*        labelPanel.add(colorByComboBox);*/
        
        labelPanelCP.setVisible(nodeLabelCheckBox.isSelected());
        add(labelPanelCP);
    }
	// }}}
}
