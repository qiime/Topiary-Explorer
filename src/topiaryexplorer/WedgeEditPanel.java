package topiaryexplorer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.event.*;
import javax.swing.border.LineBorder;
/**
 * <<Class summary>>
 *
 * @author Meg Pirrung &lt;&gt;
 * @version $Rev$
 */
public class WedgeEditPanel extends JPanel{
    TreeWindow frame = null;
    TreeVis vis = null;
    JLabel wedgeLabel = new JLabel("Customize Wedges:");
    
    JPanel labelPanel = new JPanel();
    CollapsablePanel labelPanelCP = new CollapsablePanel("Labels",labelPanel, false, true);
    
    JPanel heightPanel = new JPanel();
    JLabel heightLabel = new JLabel("Height:  ");
    JSlider wedgeHeightSlider = new JSlider(0,101,100);
    
    JButton consensusButton = new JButton("Consensus Lineage");
    
    JLabel position = new JLabel("Position:  ");
    JPanel positionPanel = new JPanel();
    JButton upButton = new JButton("^");
    JButton downButton = new JButton("v");
    JButton rightButton = new JButton(">");
    JButton leftButton = new JButton("<");
    JButton resetPosition = new JButton(" ");
    JPanel pPanel = new JPanel();
    
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
    JCheckBox wedgeLabelCheckBox = new JCheckBox("Labels", true);

	// {{{ WedgeCustomizerDialog constructor
    /**
     * 
     */
    public WedgeEditPanel(TreeWindow _frame) {
        super();
        frame = _frame;
        vis = frame.tree;
        this.setToolTipText("Customize Wedges");
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        heightPanel.add(heightLabel);
        wedgeHeightSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (wedgeHeightSlider.getValueIsAdjusting()){
                    syncTreeWithWedgeSlider();
                }
            }
        });
        wedgeHeightSlider.setToolTipText("Slide to change the size of wedges.");
        wedgeHeightSlider.setPreferredSize(new Dimension(120,20));
        heightPanel.add(wedgeHeightSlider);
        add(heightPanel);
        
        wedgeLabelCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                vis.setDrawWedgeLabels(wedgeLabelCheckBox.isSelected());
                labelPanelCP.setVisible(wedgeLabelCheckBox.isSelected());
            }
        });
        holder.add(wedgeLabelCheckBox);
        holder.add(new JLabel());
        add(holder);
        
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
/*        labelPanel.setLayout(new FlowLayout());*/
        
/*        addSeparator();*/
        consensusButton.setToolTipText("Consensus Lineage");
        consensusButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.resetConsensusLineage();
            }
        });
/*        add(consensusButton);*/
/*        add(separator);*/
        pPanel.add(position);
        positionPanel.setLayout(new GridLayout(3,3));
        positionPanel.setMaximumSize(new Dimension(35,35));
        positionPanel.add(new JLabel());
        upButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                vis.setLabelYOffset(vis.getLabelYOffset()-1);
            }
        });
        positionPanel.add(upButton);
        positionPanel.add(new JLabel());
        leftButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                vis.setLabelXOffset(vis.getLabelXOffset()-1);
            }
        });
        positionPanel.add(leftButton);
        resetPosition.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                vis.setLabelYOffset(0);
                vis.setLabelXOffset(0);
            }
        });
        positionPanel.add(resetPosition);
        rightButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                vis.setLabelXOffset(vis.getLabelXOffset()+1);
            }
        });
        positionPanel.add(rightButton);
        positionPanel.add(new JLabel());
        downButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                vis.setLabelYOffset(vis.getLabelYOffset()+1);
            }
        });
        positionPanel.add(downButton);
        positionPanel.add(new JLabel());
        positionPanel.setToolTipText("Change the position of wedge labels.");
        pPanel.add(positionPanel);
        labelPanel.add(pPanel);
        
/*        labelPanel.add(fontSizeLabel);*/
        fontSizePanel.add(fontSizeLabel);
        fntSize.setToolTipText("Change the font size of wedge labels.");
        fntSize.setText(""+vis.getWedgeFontSize());
        fntSize.setMaximumSize(new Dimension(20,20));
        fntSize.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
            }
            public void keyPressed(KeyEvent e) {
            }
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER)
                    vis.setWedgeFontSize(Float.parseFloat(fntSize.getText()));
            }
        });
        fontSizePanel.add(fntSize);
        fontSizePanel.add(ptLabel);
        fntPanel.setLayout(new GridLayout(2,1));
        fntPanel.setMaximumSize(new Dimension(10,20));
        fntIncButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                vis.setWedgeFontSize(vis.getWedgeFontSize()+1);
                fntSize.setText(""+vis.getWedgeFontSize());
            }
        });
        fntPanel.add(fntIncButton);
        fntDecButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                vis.setWedgeFontSize(vis.getWedgeFontSize()-1);
                fntSize.setText(""+vis.getWedgeFontSize());
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
                                     WedgeEditPanel.this,
                                     "Choose Wedge Label Color",
                                     colorButton.getBackground());
                 if(newColor != null)
                 {
                     colorLabel.setBackground(newColor);
                     vis.setWedgeFontColor(newColor.getRGB());
                 }
            }
            
            public void mouseExited(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {}
        }); 
        colorLabel.setToolTipText("Change the wedge label font color...");
        fontColorPanel.add(new JLabel("Font Color: "));
        fontColorPanel.add(colorLabel);
        labelPanel.add(fontColorPanel);
        labelPanelCP.setVisible(wedgeLabelCheckBox.isSelected());
        add(labelPanelCP);
    }
	// }}}
	
	public void syncTreeWithWedgeSlider() {
        if (frame.tree==null) return;
        frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        vis.setWedgeHeight(wedgeHeightSlider.getValue()/100.00);
        frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
}
