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
    
    JButton recenterButton = new JButton("Recenter");
    JButton ladderizeButton = new JButton("Ladderize");
    JButton pruneButton = new JButton("Prune Tree");
    JButton showHiddenButton = new JButton("Show Hidden Nodes");
    JButton setLineageButton = new JButton("Set Consensus Lineage");
    JPanel buttonPanel = new JPanel();
    
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
        
        buttonPanel.setLayout(new GridLayout(5,1));
        
        recenterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                 frame.recenter();
            }
        });
        buttonPanel.add(recenterButton);
        
        ladderizeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                 frame.ladderize();
            }
        });
        buttonPanel.add(ladderizeButton);
        
        pruneButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                vis.noLoop();
                 double total = vis.getTree().depth();
                 double perc = .01;
                 while(vis.getTree().getNumberOfLeaves() > 30000)
                 {
                      for(Node n: vis.getTree().getLeaves())
                        {
                            n.prune(total, perc);
                        }
                        perc += .01;
                        frame.setTreeVals(vis.getTree());
                        vis.setTree(vis.getTree());
                }
                vis.loop();
            }
        });
        buttonPanel.add(pruneButton);
        
        showHiddenButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for(Node n: vis.getTree().getNodes())
                    n.setHidden(false);
            }
        });
        buttonPanel.add(showHiddenButton);
        
        setLineageButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.resetConsensusLineage();
            }
        });
        buttonPanel.add(setLineageButton);
        
        add(buttonPanel);
        
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
         rotatePanel.setVisible(false);
         rotateSlider.setEnabled(false);
        
        rectButton.setToolTipText("Rectangular");
        rectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                vis.setTreeLayout("Rectangular");
                rotateSlider.setValue(0);
                syncTreeWithRotateSlider();
                rotatePanel.setVisible(false);
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
                rotatePanel.setVisible(false);
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
                rotatePanel.setVisible(true);
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
                rotatePanel.setVisible(true);
                rotateSlider.setEnabled(true);
                layoutChanged();
                parent.treeStatus.setText("Done drawing tree.");
            }
        });
        layoutGroup.add(rectButton);
        layoutGroup.add(triButton);
        layoutGroup.add(radialButton);
        layoutGroup.add(polarButton);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1,4));
        buttonPanel.add(rectButton);
        buttonPanel.add(triButton);
        buttonPanel.add(radialButton);
        buttonPanel.add(polarButton);
        
        layoutPanel.setLayout(new GridLayout(2,1));
        layoutPanel.add(buttonPanel);
        layoutPanel.add(rotatePanel);
/*        layoutPanel
        layoutPanel.
        layoutPanel.
        layoutPanel.*/
        
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
/*        add(rotatePanel);*/
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
