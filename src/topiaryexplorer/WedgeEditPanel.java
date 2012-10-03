package topiaryexplorer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.event.*;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicArrowButton;

/**
 * <<Class summary>>
 *
 * @author Meg Pirrung &lt;&gt;
 * @version $Rev$
 */
public class WedgeEditPanel extends JPanel{
    TreeWindow frame = null;
    TreeVis vis = null;
    JLabel wedgeLabel = new JLabel("Customize wedges:");
    
    JPanel labelPanel = new JPanel();
    CollapsablePanel labelPanelCP = new CollapsablePanel("Labels",labelPanel, false, true);
    
    JPanel heightPanel = new JPanel();
    JLabel heightLabel = new JLabel("Height  ");
    JSlider wedgeHeightSlider = new JSlider(0,101,100);

    JButton resetPosition = new JButton("Reset Positions");
    JPanel pPanel = new JPanel();
    
    String[] fntFaces = {"SansSerif","Serif","Courier"};//PFont.list();
    JComboBox fntFace = new JComboBox(fntFaces);
    // JLabel fontSizeLabel = new JLabel("Size: ");
    JTextField fntSize = new JTextField("",3);
    // JLabel ptLabel = new JLabel("pt");
    BasicArrowButton fntIncButton = new BasicArrowButton(SwingConstants.NORTH);
    BasicArrowButton fntDecButton = new BasicArrowButton(SwingConstants.SOUTH);
    JPanel fntPanel = new JPanel();
    JPanel fontSizePanel = new JPanel();
    JPanel fontColorPanel = new JPanel();
    JButton colorButton = new JButton("Color");
    JLabel colorLabel = new JLabel("  ");
    JColorChooser colorChooser = new JColorChooser();
	JPanel fntButtonPanel = new JPanel();
    
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
        // setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setLayout(new BorderLayout());

        heightPanel.add(heightLabel);
        wedgeHeightSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (wedgeHeightSlider.getValueIsAdjusting()){
                    syncTreeWithWedgeSlider();
                    vis.redraw();
                }
            }
        });
        wedgeHeightSlider.setToolTipText("Slide to change the size of wedges");
        wedgeHeightSlider.setPreferredSize(new Dimension(120,20));
        heightPanel.add(wedgeHeightSlider);
        add(heightPanel, BorderLayout.NORTH);
        
        wedgeLabelCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                vis.setDrawWedgeLabels(wedgeLabelCheckBox.isSelected());
                labelPanelCP.setVisible(wedgeLabelCheckBox.isSelected());
/*                vis.redraw();*/
            }
        });
        holder.add(wedgeLabelCheckBox);
        holder.add(new JLabel());
        add(holder, BorderLayout.WEST);
        
        // labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
        labelPanel.setLayout(new BorderLayout());
        
		resetPosition.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for(Node n : vis.getTree().getNodes()) {
                    n.setLabelYOffset(0.0);
                    n.setLabelXOffset(0.0);
                }
                vis.redraw();
            }
        });

        // resetPosition.setMinimumSize(new Dimension(188, 20));
        // pPanel.add(resetPosition);     
        labelPanel.add(resetPosition, BorderLayout.NORTH);
        
        fntFace.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               vis.setWedgeFontFace((String)fntFace.getSelectedItem());
               vis.redraw();
/*               System.out.println((String)fntFace.getSelectedItem());*/
            }
        });
/*        holder2.add(new JLabel("Font Face: "));*/
        labelPanel.add(fntFace, BorderLayout.CENTER);
        
/*        labelPanel.add(fontSizeLabel);*/
		fontSizePanel.setLayout(new BorderLayout());
        // fontSizePanel.add(fontSizeLabel);
		fntButtonPanel.setLayout(new GridLayout(2,1));
        fntSize.setToolTipText("Change the font size of wedge labels");
        fntSize.setText(""+(int)vis.getWedgeFontSize());
        fntSize.setMaximumSize(new Dimension(20,20));
		fntSize.setFont(new Font("Helvetica",0,10));
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
        fontSizePanel.add(fntSize, BorderLayout.CENTER);
        fntIncButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                vis.setWedgeFontSize(vis.getWedgeFontSize()+1);
                fntSize.setText(""+(int)vis.getWedgeFontSize());
            }
        });
		fntButtonPanel.add(fntIncButton);
        fntDecButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                vis.setWedgeFontSize(vis.getWedgeFontSize()-1);
                fntSize.setText(""+(int)vis.getWedgeFontSize());
            }
        });
		fntButtonPanel.add(fntDecButton);
        fontSizePanel.add(fntButtonPanel, BorderLayout.EAST);
        labelPanel.add(fontSizePanel, BorderLayout.EAST);
        colorLabel.setPreferredSize(new Dimension(20,20));
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
        colorLabel.setToolTipText("Change the wedge label font color");
        labelPanel.add(colorLabel, BorderLayout.WEST);

        labelPanelCP.setVisible(wedgeLabelCheckBox.isSelected());
        add(labelPanelCP, BorderLayout.SOUTH);
    }
	// }}}
	
	public void syncTreeWithWedgeSlider() {
        if (frame.tree==null) return;
        frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        vis.setWedgeHeight(wedgeHeightSlider.getValue()/100.00);
        frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
}
