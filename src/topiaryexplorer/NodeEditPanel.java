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
    JCheckBox nodeLabelCheckBox = new JCheckBox("Labels");
    JPanel labelPanel = new JPanel();
    CollapsablePanel labelPanelCP = new CollapsablePanel("Labels",labelPanel, false, true);
    
    JLabel fontSizeLabel = new JLabel("Size: ");
    JTextField fntSize = new JTextField("",3);
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
        colorByMenu = new ColorByPopupMenu(frame.frame, frame);
        this.setToolTipText("Customize Nodes");
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        
        nodeLabelCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                vis.setDrawNodeLabels(nodeLabelCheckBox.isSelected());
                vis.redraw();
                labelPanelCP.setVisible(nodeLabelCheckBox.isSelected());
            }
        });
        holder.add(nodeLabelCheckBox);
        holder.add(new JLabel());
        add(holder);
        
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
        
/*        labelPanel.add(fontSizeLabel);*/
        fontSizePanel.add(fontSizeLabel);
        fntSize.setToolTipText("Change the font size of node labels.");
        fntSize.setText(""+vis.getNodeFontSize());
        fntSize.setMaximumSize(new Dimension(20,20));
        fontSizePanel.add(fntSize);
        fontSizePanel.add(ptLabel);
        fntPanel.setLayout(new GridLayout(2,1));
        fntPanel.setMaximumSize(new Dimension(10,20));
        fntIncButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                vis.setNodeFontSize(vis.getNodeFontSize()+1);
                fntSize.setText(""+vis.getNodeFontSize());
            }
        });
        fntPanel.add(fntIncButton);
        fntDecButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                vis.setNodeFontSize(vis.getNodeFontSize()-1);
                fntSize.setText(""+vis.getNodeFontSize());
            }
        });
        fntPanel.add(fntDecButton);
        fontSizePanel.add(fntPanel);
        labelPanel.add(fontSizePanel);
        
/*        add(fontColorLabel);*/

        colorLabel.setPreferredSize(new Dimension(30,20));
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
				colorByMenu.show(colorByButton, colorByButton.getX(), colorByButton.getY());
           } 
        });
        
        labelPanel.add(colorByButton);
/*        labelPanel.add(colorByComboBox);*/
        
        labelPanelCP.setVisible(nodeLabelCheckBox.isSelected());
        add(labelPanelCP);
    }
	// }}}
}
