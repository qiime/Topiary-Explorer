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
public final class TreeViewPanel extends JPanel{
    TreeWindow frame = null;
    TreeEditToolbar parent = null;
    TreeVis vis = null;
    
    JPanel rotatePanel = new JPanel();
    JLabel rotateLabel = new JLabel("Rotate: ");
    JSlider rotateSlider = new JSlider(0,359,0);
    
    ButtonGroup layoutGroup = new ButtonGroup();
    JPanel layoutPanel = new JPanel();
    CollapsablePanel layoutCP = new CollapsablePanel("Layout", layoutPanel, true, true);
    JToggleButton rectButton = new JToggleButton(new ImageIcon("./src/images/rectangular.gif"),true);
    JToggleButton triButton = new JToggleButton(new ImageIcon("./src/images/triangular.gif"));
    JToggleButton radialButton = new JToggleButton(new ImageIcon("./src/images/radial.gif"));
    JToggleButton polarButton = new JToggleButton(new ImageIcon("./src/images/polar.gif"));
    
    JPanel mirrorPanel = new JPanel();
    CollapsablePanel mirrorCP = new CollapsablePanel("Mirror", mirrorPanel, false, true);
    
    JToggleButton mirrorvertButton = new JToggleButton(new ImageIcon("./src/images/mirror_vert.gif"));
    JToggleButton mirrorhorzButton = new JToggleButton(new ImageIcon("./src/images/mirror_horz.gif"));
    
    JPanel bgColorPanel = new JPanel();
    JLabel bgColorLabel = new JLabel("Background Color: ");
    JLabel colorLabel = new JLabel("  ");
    JColorChooser colorChooser = new JColorChooser();
    
	// {{{ TreeViewPanel constructor
    /**
     * 
     */
    public TreeViewPanel(TreeWindow _frame, TreeEditToolbar _parent) {
        super();
        frame = _frame;
        parent = _parent;
        vis = frame.tree;
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        rotateSlider.addChangeListener(new ChangeListener() {
         	public void stateChanged(ChangeEvent e) {
         		if (rotateSlider.getValueIsAdjusting()) {
         			syncTreeWithRotateSlider();
         		}
         	}
         });
         rotateSlider.setPreferredSize(new Dimension(120,20));
         rotatePanel.add(rotateLabel);
         rotatePanel.add(rotateSlider);
         rotateSlider.setEnabled(false);
        
        rectButton.setToolTipText("Rectangular");
        rectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                vis.setTreeLayout("Rectangular");
                rotateSlider.setValue(0);
                syncTreeWithRotateSlider();
                rotateSlider.setEnabled(false);
                layoutChanged();
            }
        });

        triButton.setToolTipText("Triangular");
        triButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                vis.setTreeLayout("Triangular");
                rotateSlider.setValue(0);
                syncTreeWithRotateSlider();
                rotateSlider.setEnabled(false);
                layoutChanged();
            }
        });

        radialButton.setToolTipText("Radial");
        radialButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                parent.treeStatus.setText("Calculating Offsets...");
                vis.noLoop();
                vis.setRadialOffsets(vis.getTree());
                vis.setTOffsets(vis.getTree(), 0);
                vis.setROffsets(vis.getTree(), 0);
                vis.loop();
                vis.setTreeLayout("Radial");
                rotateSlider.setEnabled(true);
                layoutChanged();
                parent.treeStatus.setText("Done drawing tree.");
            }
        });

        polarButton.setToolTipText("Polar");
        polarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int size = vis.getTree().getNumberOfLeaves();
                if(size > 8000)
                {
                    if(JOptionPane.showConfirmDialog(null, 
                    "Viewing the polar layout of trees with more\n"
                    +"than 8000 nodes is not advised.",
                    "Warning",JOptionPane.OK_CANCEL_OPTION, 
                    JOptionPane.WARNING_MESSAGE) 
                    == JOptionPane.CANCEL_OPTION)
                        return;
                }
                parent.treeStatus.setText("Calculating Offsets...");
                vis.noLoop();
                vis.setRadialOffsets(vis.getTree());
                vis.setTOffsets(vis.getTree(), 0);
                vis.setROffsets(vis.getTree(), 0);
                vis.loop();
                vis.setTreeLayout("Polar");
                rotateSlider.setEnabled(true);
                layoutChanged();
                parent.treeStatus.setText("Done drawing tree.");
            }
        });
        layoutGroup.add(rectButton);
        layoutGroup.add(triButton);
        layoutGroup.add(radialButton);
        layoutGroup.add(polarButton);
        
        layoutPanel.setLayout(new GridLayout(1,4));
        layoutPanel.add(rectButton);
        layoutPanel.add(triButton);
        layoutPanel.add(radialButton);
        layoutPanel.add(polarButton);
        
        mirrorvertButton.setToolTipText("Flip Vertical");
        mirrorvertButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.mirrorVert();
            }
        });
        
        mirrorhorzButton.setToolTipText("Flip Horizontal");
        mirrorhorzButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.mirrorHorz();
            }
        });
        mirrorPanel.setLayout(new GridLayout(1,2));
        mirrorPanel.add(mirrorvertButton);
        mirrorPanel.add(mirrorhorzButton);

        
        
        colorLabel.setPreferredSize(new Dimension(30,20));
        colorLabel.setOpaque(true);
        colorLabel.setBorder(LineBorder.createGrayLineBorder());
        colorLabel.setBackground(Color.WHITE);
        colorLabel.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                Color newColor = JColorChooser.showDialog(
                                     TreeViewPanel.this,
                                     "Choose Background Color",
                                     colorLabel.getBackground());
                 if(newColor != null)
                 {
                     colorLabel.setBackground(newColor);
                     vis.setBackgroundColor(newColor);
                 }
            }
            
            public void mouseExited(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {}
        }); 
        colorLabel.setToolTipText("Change background color...");
        bgColorPanel.add(bgColorLabel);
        bgColorPanel.add(colorLabel);
        add(bgColorPanel);
        add(rotatePanel);
        add(layoutCP);
        add(mirrorCP);
    }
	// }}}
	
	public void layoutChanged() {
	    frame.treeToolbar.setScale();
	    frame.verticalTreeToolbar.setScale();
	}
	
	public void syncTreeWithRotateSlider() {
     	if (vis.getTree() == null) return;
     	double value = rotateSlider.getValue();
     	vis.setRotate(value);
     	vis.redraw();
     }
}