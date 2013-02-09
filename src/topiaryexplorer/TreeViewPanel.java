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
import javax.imageio.ImageIO;
/**
 * <<Class summary>>
 *
 * @author Meg Pirrung &lt;&gt;
 * @version $Rev$
 */
public final class TreeViewPanel extends JPanel {
    TreeWindow frame = null;
    TreeEditToolbar parent = null;
    TreeVis vis = null;
    
    JToggleButton selectButton = new JToggleButton("Select Mode");
    JButton recenterButton = new JButton(new ImageIcon(ImageIO.read(this.getClass().getClassLoader().getResource("recenter.gif"))));
    JButton ladderizeButton = new JButton(new ImageIcon(ImageIO.read(this.getClass().getClassLoader().getResource("ladderize.gif"))));
    JButton subtreeButton = new JButton(new ImageIcon(ImageIO.read(this.getClass().getClassLoader().getResource("new_subtree.gif"))));
    JButton pruneButton = new JButton(new ImageIcon(ImageIO.read(this.getClass().getClassLoader().getResource("prune.gif"))));
    JButton showHiddenButton = new JButton(new ImageIcon(ImageIO.read(this.getClass().getClassLoader().getResource("show_hidden.gif"))));
    JButton setLineageButton = new JButton(new ImageIcon(ImageIO.read(this.getClass().getClassLoader().getResource("consensus_lineage.gif"))));
    JButton collapseByButton = new JButton(new ImageIcon(ImageIO.read(this.getClass().getClassLoader().getResource("collapse_by.gif"))));
    JPopupMenu collapseByMenu = new JPopupMenu();
    JPanel buttonPanel = new JPanel();
    
    JPanel rotatePanel = new JPanel();
    JLabel rotateLabel = new JLabel("Rotate: ");
    JSlider rotateSlider = new JSlider(0,359,0);
    
    ButtonGroup layoutGroup = new ButtonGroup();
    JPanel layoutPanel = new JPanel();
    CollapsablePanel layoutCP = new CollapsablePanel("Layout", layoutPanel, true, true);
    JToggleButton rectButton = new JToggleButton(new ImageIcon(ImageIO.read(this.getClass().getClassLoader().getResource("rectangular.gif"))),true);
    // JToggleButton rectButton = new JToggleButton(new ImageIcon("./src/images/rectangular.gif"),true);
    JToggleButton triButton = new JToggleButton(new ImageIcon(ImageIO.read(this.getClass().getClassLoader().getResource("triangular.gif"))));
    JToggleButton radialButton = new JToggleButton(new ImageIcon(ImageIO.read(this.getClass().getClassLoader().getResource("radial.gif"))));
    JToggleButton polarButton = new JToggleButton(new ImageIcon(ImageIO.read(this.getClass().getClassLoader().getResource("polar.gif"))));
    
    JPanel mirrorPanel = new JPanel();
    CollapsablePanel mirrorCP = new CollapsablePanel("Mirror", mirrorPanel, false, true);
    
    JToggleButton mirrorvertButton = new JToggleButton(new ImageIcon(ImageIO.read(this.getClass().getClassLoader().getResource("mirror_vert.gif"))));
    JToggleButton mirrorhorzButton = new JToggleButton(new ImageIcon(ImageIO.read(this.getClass().getClassLoader().getResource("mirror_horz.gif"))));
    
	JPanel colorsPanel = new JPanel();
	
    JPanel bgColorPanel = new JPanel();
    JLabel bgColorLabel = new JLabel("  Background color");
    JLabel colorLabel = new JLabel("  ");
    JColorChooser colorChooser = new JColorChooser();
    
    JPanel unmappedNodeColorPanel = new JPanel();
    JLabel unmappedNodeColorLabelText = new JLabel("  Unmapped node color");
    JLabel unmappedNodeColorLabel = new JLabel("  ");
    JSlider unmappedTranspSlider = new JSlider(0,255,255);
    
	// {{{ TreeViewPanel constructor
    /**
     * 
     */
    public TreeViewPanel(TreeWindow _frame, TreeEditToolbar _parent) throws IOException{
        super();
        frame = _frame;
        parent = _parent;
        vis = frame.tree;
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        buttonPanel.setLayout(new GridLayout(2,4));
        
        selectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                 frame.changeMode(selectButton.isSelected());
            }
        });
        // buttonPanel.add(selectButton);
        recenterButton.setToolTipText("Recenter");
        recenterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                 frame.recenter();
            }
        });
        buttonPanel.add(recenterButton);
        
		ladderizeButton.setToolTipText("Ladderize");
        ladderizeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                 frame.ladderize();
            }
        });
        buttonPanel.add(ladderizeButton);
        
		subtreeButton.setToolTipText("View Subtree...");
        subtreeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                 viewSubtree();
                }
                catch(IOException ex) {
                    System.out.println("Could not open icons");
                }
            }
        });
        buttonPanel.add(subtreeButton);
        
		pruneButton.setToolTipText("Prune Tree...");
        pruneButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                 pruneTree();
            }
        });
        buttonPanel.add(pruneButton);
        
		showHiddenButton.setToolTipText("Show Hidden Nodes");
        showHiddenButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for(Node n: vis.getTree().getNodes())
                    n.setHidden(false);
                vis.redraw();
            }
        });
        buttonPanel.add(showHiddenButton);
        
		setLineageButton.setToolTipText("Set Consensus Lineage...");
        setLineageButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.resetConsensusLineage();
            }
        });
        buttonPanel.add(setLineageButton);
		
        resetCollapseByMenu();
		collapseByButton.setToolTipText("Collapse By...");
        collapseByButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                collapseByMenu.show(collapseByButton, collapseByButton.getX()-60, collapseByButton.getY()-5);
            }
        });
        buttonPanel.add(collapseByButton);
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
        
        rectButton.setToolTipText("Rectangular");
        rectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.collapseTreeToolbar.sliderEnabled(true);
                vis.setTreeLayout("Rectangular");
                rotateSlider.setValue(0);
                syncTreeWithRotateSlider();
                rotatePanel.setVisible(false);
                frame.lockButton.setSelected(false);
                frame.lockButton.setEnabled(true);
                layoutChanged();
            }
        });

        triButton.setToolTipText("Triangular");
        triButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.collapseTreeToolbar.sliderEnabled(true);
                vis.setTreeLayout("Triangular");
                rotateSlider.setValue(0);
                syncTreeWithRotateSlider();
                rotatePanel.setVisible(false);
                frame.lockButton.setSelected(false);
                frame.lockButton.setEnabled(true);
                layoutChanged();
            }
        });

        radialButton.setToolTipText("Radial");
        radialButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.collapseTreeToolbar.sliderEnabled(true);
                parent.treeStatus.setText("Calculating Offsets...");
                // vis.setTOffsets(vis.getTree(), 0);
                vis.setTOffsets();
                // vis.setTOffsets(vis.getTree(), 2*Math.PI, 0);
                vis.setROffsets(vis.getTree(), 0);
                // vis.setROffsets(vis.getTree());
                vis.setRadialOffsets(vis.getTree());
                vis.setTreeLayout("Radial");
                rotatePanel.setVisible(true);
                layoutChanged();
                frame.lockButton.setSelected(true);
                frame.lockButton.setEnabled(false);
                parent.treeStatus.setText("Done drawing tree.");
            }
        });

        polarButton.setToolTipText("Polar");
        polarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.collapseTreeToolbar.setMax();
                frame.collapseTreeToolbar.sliderEnabled(false);
/*                int size = vis.getTree().getNumberOfLeaves();
                if(size > 8000)
                {
                    if(JOptionPane.showConfirmDialog(null, 
                    "Viewing the polar layout of trees with more\n"
                    +"than 8000 nodes is not advised.",
                    "Warning",JOptionPane.OK_CANCEL_OPTION, 
                    JOptionPane.WARNING_MESSAGE) 
                    == JOptionPane.CANCEL_OPTION)
                        return;
                }*/
                parent.treeStatus.setText("Calculating Offsets...");
                vis.setRadialOffsets(vis.getTree());
                vis.setTOffsets(vis.getTree(), 0);
                vis.setROffsets(vis.getTree(), 0);
                vis.setTreeLayout("Polar");
                rotatePanel.setVisible(true);
                layoutChanged();
                frame.lockButton.setSelected(true);
                frame.lockButton.setEnabled(false);
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
        
        layoutPanel.setLayout(new BoxLayout(layoutPanel, BoxLayout.Y_AXIS));
        layoutPanel.add(buttonPanel);
        layoutPanel.add(rotatePanel);
        
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

        // colorsPanel.setLayout(new GridLayout(2,1));
		// colorsPanel.setAlignmentY(Component.LEFT_ALIGNMENT);

        colorLabel.setPreferredSize(new Dimension(20,20));
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
                 vis.redraw();
            }
            
            public void mouseExited(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {}
        }); 
        colorLabel.setToolTipText("Change background color...");
		bgColorPanel.setLayout(new BorderLayout());
        bgColorPanel.add(bgColorLabel,BorderLayout.CENTER);
        bgColorPanel.add(colorLabel,BorderLayout.WEST);
        add(bgColorPanel);
        
        unmappedNodeColorLabel.setPreferredSize(new Dimension(20,20));
        unmappedNodeColorLabel.setOpaque(true);
        unmappedNodeColorLabel.setBorder(LineBorder.createGrayLineBorder());
        unmappedNodeColorLabel.setBackground(Color.BLACK);
        unmappedNodeColorLabel.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                Color newColor = JColorChooser.showDialog(
                                     TreeViewPanel.this,
                                     "Choose Background Color",
                                     unmappedNodeColorLabel.getBackground());
                 if(newColor != null)
                 {
                     unmappedNodeColorLabel.setBackground(newColor);
                     frame.setUnmappedNodeColor(newColor);
                     frame.recolorBranches();
                 }
                 vis.redraw();
            }
            
            public void mouseExited(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {}
        }); 
        unmappedNodeColorLabel.setToolTipText("Change unmapped node color");
		unmappedNodeColorPanel.setLayout(new BorderLayout());
        unmappedNodeColorPanel.add(unmappedNodeColorLabelText, BorderLayout.CENTER);
        unmappedNodeColorPanel.add(unmappedNodeColorLabel, BorderLayout.WEST);
		// unmappedNodeColorPanel.setAlignmentY(Component.LEFT_ALIGNMENT);
        add(unmappedNodeColorPanel);
        
		// add(colorsPanel);

        unmappedTranspSlider.addChangeListener(new ChangeListener() {
         	public void stateChanged(ChangeEvent e) {
         		if (unmappedTranspSlider.getValueIsAdjusting()) {
         		    Color newColor = new Color(unmappedNodeColorLabel.getBackground().getRed(),
      unmappedNodeColorLabel.getBackground().getGreen(),
      unmappedNodeColorLabel.getBackground().getBlue(),
      unmappedTranspSlider.getValue());
         			frame.setUnmappedNodeColor(newColor);
         			frame.recolorBranches();
         		}
         	}
         });
        unmappedTranspSlider.setPreferredSize(new Dimension(120,20));
        unmappedTranspSlider.setToolTipText("Unmapped node transparancy");
        unmappedNodeColorPanel.add(unmappedTranspSlider, BorderLayout.SOUTH);
/*        add(rotatePanel);*/
        add(layoutCP);
        add(mirrorCP);
    }
	// }}}
	
	public void viewSubtree() throws IOException{
	    ViewSubtreeDialog v = new ViewSubtreeDialog(frame);
	}
	
	public void pruneTree() {
	    PruneTreeDialog d = new PruneTreeDialog(frame.frame, frame, 
	        (frame.frame.otuMetadata.getColumnCount() > 0),
	        (frame.frame.sampleMetadata.getColumnCount() > 0 && 
	        frame.frame.otuSampleMap.getColumnCount() > 0));
	}
	
	public void layoutChanged() {
	    frame.treeToolbar.setScale();
	    frame.verticalTreeToolbar.setScale();
        frame.recenter();
	    vis.checkBounds();
	    vis.redraw();
	    validate();
	}
	
	public void syncTreeWithRotateSlider() {
     	if (vis.getTree() == null) return;
     	double value = rotateSlider.getValue();
     	vis.setRotate(value);
     	vis.redraw();
     }
     
     /**
      * Resets collapse by menu when new OTU or Sample metadata
      * files are loaded
      */
     public void resetCollapseByMenu() {
         //NOTE: can only collapse on OTU metadata
         collapseByMenu.removeAll();
         JMenuItem item = new JMenuItem("Uncollapse All");
         item.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                  frame.uncollapseTree();
              }
         });
         collapseByMenu.add(item);
         item = new JMenuItem("Collapse All");
         item.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                  frame.collapseTree();
              }
         });
         collapseByMenu.add(item);
         item = new JMenuItem("Node Labels");
         item.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                  String value = e.getActionCommand();
                  double level = Double.parseDouble(((String)JOptionPane.showInputDialog(
                             frame,
                             "Enter percent threshold to collapse by:\n"+
                             "Use 100(%) for completely homogeneous\n"+
                             "collapsing",
                             "Collapse by "+value,
                             JOptionPane.PLAIN_MESSAGE,
                             null,
                             null,
                             "90")))/100;
         if(level > 0 && level <= 1)
         {
             frame.collapseTreeByNodeLabels(level);
         }
         else
             JOptionPane.showMessageDialog(frame,
                 "Invalid threshold percentage.",
                 "Error",
                 JOptionPane.ERROR_MESSAGE);
                  
              }
         });
         collapseByMenu.add(item);
         item = new JMenuItem("Consensus Lineage");
         item.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                  String value = e.getActionCommand();
                  double level = Double.parseDouble(((String)JOptionPane.showInputDialog(
                             frame,
                             "Enter percent threshold to collapse by:\n"+
                             "Use 100(%) for completely homogeneous\n"+
                             "collapsing",
                             "Collapse by "+value,
                             JOptionPane.PLAIN_MESSAGE,
                             null,
                             null,
                             "90")))/100;
         if(level > 0 && level <= 1)
         {
            frame.collapseTreeByConsensusLineage(level);
         }
         else
             JOptionPane.showMessageDialog(frame,
                 "Invalid threshold percentage.",
                 "Error",
                 JOptionPane.ERROR_MESSAGE);
                  
              }
         });
         item.setEnabled(false);
         collapseByMenu.add(item);
         collapseByMenu.add(new JSeparator());
         // 
         // item = new JMenuItem("External Node Labels");
         //     item.addActionListener(new ActionListener() {
         //         public void actionPerformed(ActionEvent e) {
         //             collapseItemClicked(e);
         //         }
         //     });
         //     collapseByMenu.add(item);

         if (frame.frame.otuMetadata != null) {             
                ArrayList<String> data = frame.frame.otuMetadata.getColumnNames();
                //start at 1 to skip ID column
                for (int i = 1; i < data.size(); i++) {
                     String value = data.get(i);
                     item = new JMenuItem(value);
                     item.addActionListener(new ActionListener() {
                         public void actionPerformed(ActionEvent e) {
                             collapseItemClicked(e);
                         }
                     });
                     collapseByMenu.add(item);
                }
            }
     }
     
     public void collapseItemClicked(ActionEvent e) {
         //get the category to color by
         String value = e.getActionCommand();
         double level = Double.parseDouble(((String)JOptionPane.showInputDialog(
                             frame,
                             "Enter percent threshold to collapse by:\n"+
                             "Use 100(%) for completely homogeneous\n"+
                             "collapsing",
                             "Collapse by "+value,
                             JOptionPane.PLAIN_MESSAGE,
                             null,
                             null,
                             "90")))/100;
         if(level > 0 && level <= 1)
         {
             frame.collapseByValue(value,level);
         }
         else
             JOptionPane.showMessageDialog(frame,
                 "Invalid threshold percentage.",
                 "Error",
                 JOptionPane.ERROR_MESSAGE);
     }
}
