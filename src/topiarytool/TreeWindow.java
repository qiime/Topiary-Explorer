package topiarytool;

import com.sun.opengl.util.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.media.opengl.*;
import java.sql.*;
import javax.swing.table.*;
import java.io.*;
import javax.jnlp.*;

/**
 * TreeWindow is the window that contains the tree visualization.
 */

public class TreeWindow extends TopiaryWindow implements KeyListener, ActionListener{
    JMenuBar topMenu = new JMenuBar();
    JMenu treeMenu = new JMenu("Options");
    JMenu nodeMenu = new JMenu("Node");
    JMenu lineWidthMenu = new JMenu("Line Width");
    JMenu rotateMenu = new JMenu("Rotate");
    JMenu collapseByMenu = new JMenu("Collapse by");
    ColorByMenu colorBy;// = new ColorByMenu();
    JCheckBoxMenuItem externalLabelsMenuItem = new JCheckBoxMenuItem("Tip Labels...");
    JCheckBoxMenuItem internalLabelsMenuItem = new JCheckBoxMenuItem("Internal Node Labels");
    
    JRadioButtonMenuItem rectangularradiobutton = new JRadioButtonMenuItem("Rectangular");
    JRadioButtonMenuItem triangularradiobutton = new JRadioButtonMenuItem("Triangular");
    JRadioButtonMenuItem radialradiobutton = new JRadioButtonMenuItem("Radial");
    JRadioButtonMenuItem polarradiobutton = new JRadioButtonMenuItem("Polar");
    
    JSlider lineWidthSlider = new JSlider(1, 1000, 20);
    JSlider rotateSlider = new JSlider(0,359,0);
    
    ButtonGroup treeLayoutGroup = new ButtonGroup();
    ButtonGroup lineWidthGroup = new ButtonGroup();
    JRadioButtonMenuItem uniformLineWidthItem = new JRadioButtonMenuItem("Uniform");
    JMenu lineWidthSampleMetadataMenu = new JMenu("Sample Metadata");
    JMenu lineWidthOtuMetadataMenu = new JMenu("OTU Metadata");
    
    JMenuItem item;
    
    MainFrame frame = null;
    TreeAppletHolder treeHolder = null;
    TreeVis tree = new TreeVis();
    JPanel treePanel = new JPanel();
    TreeToolbar treeToolbar = null;
    VerticalTreeToolbar verticalTreeToolbar = null;
    CollapseTreeToolbar collapseTreeToolbar = null;
    TreeOptionsToolbar treeOpsToolbar = null;
    Node clickedNode = null;
    JPopupMenu treePopupMenu = new JPopupMenu();
    TipLabelCustomizer tlc = null;
    Set keys = new java.util.HashSet();
    boolean showTips = false;
    
    /**
    * Class Constructor
    */
	 public TreeWindow(MainFrame _frame) {
	     super(_frame);
	     this.setSize(new Dimension(900,700));
         frame = _frame;
         colorBy = new ColorByMenu(frame, this);
	     tree.addKeyListener(this);
	     treeHolder = new TreeAppletHolder(tree, this);
	     treeToolbar = new TreeToolbar(this);
	     verticalTreeToolbar = new VerticalTreeToolbar(this);
	     collapseTreeToolbar = new CollapseTreeToolbar(this);
	     treeOpsToolbar = new TreeOptionsToolbar(this, frame);
	     
	     Container pane = getContentPane();
         pane.setLayout(new BorderLayout());
         
         tree.addMouseMotionListener(new MouseMotionAdapter() {
 			public void mouseMoved(java.awt.event.MouseEvent evt) {
 				Node node = tree.findNode(evt.getX(), evt.getY());
 				if (node != null) {
 					if (node.isLeaf()) {
 						treeOpsToolbar.setStatus(String.format("Leaf (OTU): %s", node.getName()));
 					} else {
 						treeOpsToolbar.setStatus(String.format("Sub-tree: %d leaves", node.getNumberOfLeaves()));
 					}
 				} else {
 						treeOpsToolbar.setStatus(" ");
 				}
 			}
 		});
	     
	     //set up the tree pop-up menu
         item = new JMenuItem("Collapse/Expand");
         item.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
                 clickedNode.setCollapsed(!clickedNode.isCollapsed());
             }
         });
         treePopupMenu.add(item);
         item = new JMenuItem("Rotate (Swap Children)");
         item.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent arg0) {
                 clickedNode.rotate();
                 tree.setYOffsets(tree.getTree(), 0);
             }
         });
         treePopupMenu.add(item);
         item = new JMenuItem("Toggle Pie Chart");
         item.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent arg0) {
                 clickedNode.setDrawPie(!clickedNode.getDrawPie());
             }
         });
         treePopupMenu.add(item);
         item = new JMenuItem("Toggle Node Label");
          item.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent arg0) {
                  clickedNode.setDrawLabel(!clickedNode.getDrawLabel());
              }
          });
          treePopupMenu.add(item);
          item = new JMenuItem("Consensus Lineage");
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    System.out.println(clickedNode.getConsensusLineage());
                }
            });
            treePopupMenu.add(item);

         tree.addMouseListener(new java.awt.event.MouseAdapter() {
 			public void mousePressed(java.awt.event.MouseEvent evt) {
 				clickedNode = tree.findNode(evt.getX(), evt.getY());
 				if (evt.isPopupTrigger() && clickedNode != null) {
 					treePopupMenu.show(tree, evt.getX(), evt.getY());
 				}
 			}
 		});

         tree.addChangeListener(new ChangeListener() {

             public void stateChanged(ChangeEvent e) {
                 treeToolbar.syncZoomSliderWithTree();
                 verticalTreeToolbar.syncZoomSliderWithTree();
             }
         });
         
         treePanel.setLayout(new BorderLayout());
         treePanel.add(treeToolbar, BorderLayout.PAGE_END);
         treePanel.add(verticalTreeToolbar, BorderLayout.LINE_START);
         treePanel.add(treeHolder, BorderLayout.CENTER);
         treePanel.add(collapseTreeToolbar, BorderLayout.PAGE_START);
	     pane.add(treeOpsToolbar, BorderLayout.NORTH);
	     
	     //set up the "node" submenus
         item = new JMenuItem("Collapse/Expand");
         item.addActionListener(this);
         nodeMenu.add(item);
         item = new JMenuItem("Collapse/Expand All Children");
         item.addActionListener(this);
         nodeMenu.add(item);
         item = new JMenuItem("Rotate (Swap Children)");
         item.addActionListener(this);
         nodeMenu.add(item);
         item = new JCheckBoxMenuItem("Pie Chart");
         item.addActionListener(this);
         nodeMenu.add(item);
         item = new JCheckBoxMenuItem("Node Label");
         item.addActionListener(this);
         nodeMenu.add(item);
	     
	     item = new JMenuItem("Save Tree...");
         item.addActionListener(this);
         treeMenu.add(item);
         treeMenu.add(new JSeparator());
         item = new JMenuItem("Export Tree Image...");
         item.addActionListener(this);
         treeMenu.add(item);
         item = new JMenuItem("Export Tree Screen Capture...");
         item.addActionListener(this);
         treeMenu.add(item);
         treeMenu.add(new JSeparator());
	     
	     //set up the "tree" submenus
	     JMenuItem item;
         item = new JMenuItem("Beautify");
         item.addActionListener(this);
         treeMenu.add(item);
         item = new JMenuItem("Recenter");
         item.addActionListener(this);
         treeMenu.add(item);
         item = new JMenuItem("Mirror left/right");
         item.addActionListener(this);
         treeMenu.add(item);
         item = new JMenuItem("Mirror up/down");
         item.addActionListener(this);
         treeMenu.add(item);
         resetCollapseByMenu();
         treeMenu.add(collapseByMenu);
         JMenu sortBy = new JMenu("Sort by");
         item = new JMenuItem("Number of OTUs");
         item.addActionListener(this);
         sortBy.add(item);
         item = new JMenuItem("Number of immediate children");
         item.addActionListener(this);
         sortBy.add(item);
         treeMenu.add(sortBy);


         JMenu layout = new JMenu("Layout");
         rectangularradiobutton.setSelected(true);
         rectangularradiobutton.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
                 tree.setTreeLayout("Rectangular");
                 rotateMenu.setEnabled(false);
             }
         });
         treeLayoutGroup.add(rectangularradiobutton);
         layout.add(rectangularradiobutton);

         triangularradiobutton.setSelected(true);
         triangularradiobutton.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
                 tree.setTreeLayout("Triangular");
                 rotateMenu.setEnabled(false);
             }
         });
         treeLayoutGroup.add(triangularradiobutton);
         layout.add(triangularradiobutton);


         radialradiobutton = new JRadioButtonMenuItem("Radial");
         radialradiobutton.setSelected(true);
         radialradiobutton.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
                 tree.setTreeLayout("Radial");
                 rotateMenu.setEnabled(true);
             }
         });
         treeLayoutGroup.add(radialradiobutton);
         layout.add(radialradiobutton);

         polarradiobutton.setSelected(true);
         polarradiobutton.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
                 tree.setTreeLayout("Polar");
                 rotateMenu.setEnabled(true);
             }
         });
         treeLayoutGroup.add(polarradiobutton);
         layout.add(polarradiobutton);


         treeMenu.add(layout);  

         lineWidthMenu.add(lineWidthOtuMetadataMenu);
         lineWidthMenu.add(lineWidthSampleMetadataMenu);
         lineWidthGroup.add(uniformLineWidthItem);
         uniformLineWidthItem.setSelected(true);
         uniformLineWidthItem.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
                 for (Node n : tree.getTree().getNodes()) {
                     n.setLineWidth(1);
                 }
                 syncTreeWithLineWidthSlider();
             }
         });
         lineWidthMenu.add(uniformLineWidthItem);
         lineWidthSlider.addChangeListener(new ChangeListener() {
             public void stateChanged(ChangeEvent e) {
                 if (lineWidthSlider.getValueIsAdjusting()){
                     syncTreeWithLineWidthSlider();
                 }
             }
         });
         lineWidthMenu.add(lineWidthSlider);
         treeMenu.add(lineWidthMenu);

         rotateSlider.addChangeListener(new ChangeListener() {
         	public void stateChanged(ChangeEvent e) {
         		if (rotateSlider.getValueIsAdjusting()) {
         			syncTreeWithRotateSlider();
         		}
         	}
         });
         rotateMenu.add(rotateSlider);
         rotateMenu.setEnabled(false);
         treeMenu.add(rotateMenu);


         item = new JMenuItem("Background Color...");        
         item.addActionListener(new ActionListener() {        
             public void actionPerformed(ActionEvent e) {
                 JColorChooser colorChooser = new JColorChooser();
                 Color c = colorChooser.showDialog(frame, "Pick a Color", tree.getBackgroundColor());
                 tree.setBackgroundColor(c);
             }
         });
         treeMenu.add(item);

         externalLabelsMenuItem.setSelected(false);
         externalLabelsMenuItem.addActionListener(this);
         internalLabelsMenuItem.setSelected(false);
         internalLabelsMenuItem.addActionListener(this);
         treeMenu.add(externalLabelsMenuItem);
         treeMenu.add(internalLabelsMenuItem);
	     topMenu.add(treeMenu);
	     topMenu.add(nodeMenu);
	     topMenu.add(colorBy);
	     
	     setJMenuBar(topMenu);
	     pane.add(treePanel, BorderLayout.CENTER);
	}
	
	public void actionPerformed(ActionEvent e) {
	    if (e.getActionCommand().equals("Save Tree...")) {
             saveTree();
         }    else if (e.getActionCommand().equals("Tip Labels...")) {
                  setTipLabels(externalLabelsMenuItem.getState());
              } else if (e.getActionCommand().equals("Internal Node Labels")) {
                  tree.setDrawInternalNodeLabels(internalLabelsMenuItem.getState());
              } else if (e.getActionCommand().equals("Collapse/Expand")) {
                  if (frame.clickedNode != null) {
                     frame.clickedNode.setCollapsed(!frame.clickedNode.isCollapsed());
                  }
              }
              else if (e.getActionCommand().equals("Beautify")) {
                   tree.getTree().sortByBranchLength();
                   tree.setYOffsets(tree.getTree(), 0);
                   tree.setTOffsets(tree.getTree(), 0);
                   tree.setROffsets(tree.getTree(), 0);
                   tree.setRadialOffsets(tree.getTree());
               }
               else if (e.getActionCommand().equals("Collapse/Expand All Children")) {
                   if (frame.clickedNode != null) {
                      ArrayList<Node> children = frame.clickedNode.getNodes();
                      for(Node n: children)
                          {n.setCollapsed(!n.isCollapsed());}
                      frame.clickedNode.setCollapsed(!frame.clickedNode.isCollapsed());
                   }
              } else if (e.getActionCommand().equals("Rotate (Swap Children)")) {
                  if (frame.clickedNode != null) {
                     frame.clickedNode.rotate();
                     tree.setYOffsets(tree.getTree(), 0);
                  }
              } else if (e.getActionCommand().equals("Pie Chart")) {
                  if (frame.clickedNode != null) {
                     frame.clickedNode.setDrawPie(!frame.clickedNode.getDrawPie());
                     }
             } else if (e.getActionCommand().equals("Node Label")) {
                       if (frame.clickedNode != null) {
                          frame.clickedNode.setDrawLabel(!frame.clickedNode.getDrawLabel());
                       }
              } else if (e.getActionCommand().equals("Recenter")) {
                  recenter();
              } else if (e.getActionCommand().equals("Mirror left/right")) {
                 mirrorHorz();
              } else if (e.getActionCommand().equals("Mirror up/down")) {
                  mirrorVert();
              } else if (e.getActionCommand().equals("Number of OTUs")) {
                  tree.getTree().sortByNumberOfOtus();
                  tree.setYOffsets(tree.getTree(), 0);
                  tree.setTOffsets(tree.getTree(), 0);
                  tree.setROffsets(tree.getTree(), 0);
                  tree.setRadialOffsets(tree.getTree());
              } else if (e.getActionCommand().equals("Number of immediate children")) {
                  tree.getTree().sortByNumberOfChildren();
                  tree.setYOffsets(tree.getTree(), 0);
                  tree.setTOffsets(tree.getTree(), 0);
                  tree.setROffsets(tree.getTree(), 0);
                  tree.setRadialOffsets(tree.getTree());
              } else if (e.getActionCommand().equals("Export Tree Image...") && tree.getTree()!= null) {
                 exportTreeImage();
              } else if (e.getActionCommand().equals("Export Tree Screen Capture...") && tree.getTree()!= null)  {
                  tree.noLoop();
                  try {
                  	 byte[] b = new byte[0];
                      FileContents fc = frame.fss.saveFileDialog(null,null,new ByteArrayInputStream(b),null);
     			 	 tree.exportScreenCapture(fc);
     			 } catch(IOException ex){}
                  tree.loop();
              }
    }
	
	public void keyTyped(KeyEvent key) {
	}
	
	public void keyReleased(KeyEvent key) {
       }

    public void keyPressed(KeyEvent key) {
	    if(key.getKeyCode() == 61)
	    {
	        treeToolbar.zoomSlider.setValue(treeToolbar.zoomSlider.getValue() + 1);
	        treeToolbar.syncTreeWithZoomSlider();
            verticalTreeToolbar.zoomSlider.setValue(verticalTreeToolbar.zoomSlider.getValue() + 1);
            tree.changeFontSize(Math.min(tree.getFontSize()+1,32));
            verticalTreeToolbar.syncTreeWithZoomSlider();
	    }
	    if(key.getKeyCode() == 45)
	    {
	        treeToolbar.zoomSlider.setValue(treeToolbar.zoomSlider.getValue() - 1);
	        treeToolbar.syncTreeWithZoomSlider();
            verticalTreeToolbar.zoomSlider.setValue(verticalTreeToolbar.zoomSlider.getValue() - 1);
            tree.changeFontSize(Math.max(tree.getFontSize()-1,1));
            verticalTreeToolbar.syncTreeWithZoomSlider();
	    }
    }
	
	/**
    * Loads a new tree from the selected file
    */
	public void loadTree(FileContents inFile) {
	     if (inFile == null) {
	     	 try {
	             inFile = frame.fos.openFileDialog(null,null);
	         } catch (java.io.IOException e) {}
	     }
         if (inFile != null) {
             tree.noLoop();
             tree.setTree(TopiaryFunctions.createTreeFromNewickFile(inFile));
             //make sure coloring is empty
             removeColor();
             treeToolbar.zoomSlider.setValue(0);
             tree.loop();
             collapseTree();
             
/*             treeMenu.setEnabled(true);
             nodeMenu.setEnabled(true);*/

             colorBy.setEnabled(true);
             this.setVisible(true);
             resetConsensusLineage();
             System.out.println("Done drawing tree.");
             frame.consoleWindow.update("Done drawing tree. ");
             frame.treeFile = inFile;
             
             treeHolder.syncScrollbarsWithTree();
         }
    }
    
    public void loadTree() {
        FileContents inFile = null;
        try {
             inFile = frame.fos.openFileDialog(null,null);
         } catch (java.io.IOException e) {}
         if (inFile != null) {
              tree.noLoop();
              tree.setTree(TopiaryFunctions.createTreeFromNewickFile(inFile));
              //make sure coloring is empty
              removeColor();
              treeToolbar.zoomSlider.setValue(0);
              tree.loop();
              collapseTree();

/*              frame.mainMenu.treeMenu.setEnabled(true);
              frame.mainMenu.nodeMenu.setEnabled(true);*/

              colorBy.setEnabled(true);
              this.setVisible(true);
              resetConsensusLineage();
              System.out.println("Done drawing tree.");
              frame.consoleWindow.update("Done drawing tree. ");
              frame.treeFile = inFile;
              
              treeHolder.syncScrollbarsWithTree();
          }
    }
    
    public void loadTree(String treeString) {
        tree.noLoop();
        tree.setTree(TopiaryFunctions.createTreeFromNewickString(treeString));
        removeColor();
        treeToolbar.zoomSlider.setValue(0);
        tree.loop();
        collapseTree();
        
/*        frame.mainMenu.treeMenu.setEnabled(true);
        frame.mainMenu.nodeMenu.setEnabled(true);*/

        colorBy.setEnabled(true);
        this.setVisible(true);
        resetConsensusLineage();
        System.out.println("Done drawing tree.");
        frame.consoleWindow.update("Done drawing tree. ");
    }
    
    /**
    * Saves the current tree in newick format
    */
    public void saveTree() {
	    String s = TopiaryFunctions.createNewickStringFromTree(tree.getTree());
        try {
        	FileContents fc = frame.fss.saveFileDialog(null,null,new ByteArrayInputStream(s.getBytes()),null);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error writing to file.", "Error", JOptionPane.ERROR_MESSAGE);
            frame.consoleWindow.update("Error writing to file.");
        }
    }
    
    /**
    * Exports the current tree and coloring as a pdf with
    * supplied dimensions
    */
    public void exportTreeImage() {
        tree.noLoop();
         //Determine PDF dimensions
         PDFDimensionsDialog p = new PDFDimensionsDialog(frame);
         p.pack();
         p.setVisible(true);
         double dims[] = p.dims;
         if (dims[0]!=0 || dims[1]!=0) {
         	try {
         		byte[] b = new byte[0];
            	tree.exportTreeImage(frame.fss.saveFileDialog(null,null,new ByteArrayInputStream(b),null), dims);
           	} catch(IOException ex) {};
		}
        tree.loop();
    }
    
    /**
    * Recenters the tree in the treeview window
    */
    public void recenter() {
        tree.resetTreeX();
        tree.resetTreeY();
        treeToolbar.syncZoomSliderWithTree();
    }
	
	/**
    * Mirrors the tree horizontally
    */
	public void mirrorHorz() {
         // reset wedge slider
         for (Node n : tree.getTree().getNodes()) {
             n.setXOffset(tree.getTree().depth() - n.getXOffset());
             n.setTOffset(Math.PI - n.getTOffset());
             }
        tree.setRadialOffsets(tree.getTree());
     }
     
     /**
     * Mirrors the tree vertically
     */
     public void mirrorVert() {
         for (Node n : tree.getTree().getNodes()) {
             n.setYOffset(tree.getTree().getNumberOfLeaves() - n.getYOffset());
             n.setTOffset(-n.getTOffset());
         }
         tree.setRadialOffsets(tree.getTree());
     }
	
	 /**
     * Recolors the tree based on selected OTU metadata field
     */
	public void recolorTreeByOtu() {
       //loop over each node
       for (Node n : tree.getTree().getLeaves()){
               //get the node's name
               String nodeName = n.getName();
               //get the row of the OTU metadata table with this name
               int rowIndex = -1;
               for (int i = 0; i < frame.otuMetadata.getData().maxRow(); i++) {
                   String val = (String)frame.otuMetadata.getData().get(i,0);
                   if (val.equals(nodeName)) {
                       rowIndex = i;
                       break;
                   }
               }
               if (rowIndex == -1) {
                   JOptionPane.showMessageDialog(null, "ERROR: OTU ID "+nodeName+" not found in OTU Metadata Table.", "Error", JOptionPane.ERROR_MESSAGE);
                   frame.consoleWindow.update("ERROR: OTU ID "+nodeName+" not found in OTU Metadata Table.");
                   return;
               }
               Object category = frame.otuMetadata.getValueAt(rowIndex, frame.colorColumnIndex);
               if (category == null) continue;
               //get the color for this category
               Color c = frame.colorMap.get(category);
               if (c == null) {
                   JOptionPane.showMessageDialog(null, "ERROR: No color specified for category "+category.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                   frame.consoleWindow.update("ERROR: No color specified for category "+category.toString());
                   return;
               }
               //set the node to this color
               n.clearColor();
               n.addColor(c, 1.0);
           
       }
       tree.getTree().updateColorFromChildren();
       frame.repaint();
    }
    
    /**
    * Recolors tree by selected sample metadata
    */
    public void recolorTreeBySample() {
         //loop over each node
         for (Node n : tree.getTree().getLeaves()) {
             //get the node's name
             
                 String nodeName = n.getName();
                 //get the row of the OTU-Sample map with this name
                 int rowIndex = -1;
                 for (int i = 0; i < frame.otuSampleMap.getData().maxRow(); i++) {
                    String val = (String)frame.otuSampleMap.getData().get(i,0);
                    if (val.equals(nodeName)) {
                        rowIndex = i;
                        break;
                    }
                 }
                 if (rowIndex == -1) {
                    JOptionPane.showMessageDialog(null, "ERROR: OTU ID "+nodeName+" not found in OTU-Sample Table.", "Error", JOptionPane.ERROR_MESSAGE);
                    frame.consoleWindow.update("ERROR: OTU ID "+nodeName+" not found in OTU-Sample Table.");
                    return;
                 }
                 //get the row
                 ArrayList<Object> row = frame.otuSampleMap.getRow(rowIndex);
                 n.clearColor();
                 //for each non-zero column value (starting after the ID column)
                 for (int i = 1; i < row.size(); i++) {
                     Object value = row.get(i);
                     //if it's not an Integer, skip it
                     if (!(value instanceof Integer)) continue;
                     Integer weight = (Integer)value;
                     if (weight == 0) continue;
                     String sampleID = frame.otuSampleMap.getColumnName(i);
                     //find the row that has this sampleID
                     int sampleRowIndex = -1;
                     for (int j = 0; j < frame.sampleMetadata.getData().maxRow(); j++) {
                        if (frame.sampleMetadata.getData().get(j,0).equals(sampleID)) {
                            sampleRowIndex = j;
                            break;
                        }
                     }
                     if (sampleRowIndex == -1) {
                        //JOptionPane.showMessageDialog(null, "ERROR: Sample ID "+sampleID+" not found in Sample Metadata Table.", "Error", JOptionPane.ERROR_MESSAGE);
                        //return;
                        System.out.println("ERROR: Sample ID "+sampleID+" not found in Sample Metadata Table.");
                        frame.consoleWindow.update("ERROR: Sample ID "+sampleID+" not found in Sample Metadata Table.");
                        continue;
                     }
                     Object val = frame.sampleMetadata.getValueAt(sampleRowIndex, frame.colorColumnIndex);
                     if (val == null) continue;
                     n.addColor(frame.colorMap.get(val), weight);
                 
             }
         }
         tree.getTree().updateColorFromChildren();
         frame.repaint();
     }
     
     
     public void resetLineWidthsByOtu() {
        //loop over each node
        for (Node n : tree.getTree().getLeaves()){
            //get the node's name
            String nodeName = n.getName();
            //get the row of the OTU metadata table with this name
            int rowIndex = -1;
            Object nodeNameObj = TopiaryFunctions.objectify(nodeName);
            for (int i = 0; i < frame.otuMetadata.getData().maxRow(); i++) {
                if (frame.otuMetadata.getData().get(i,0).equals(nodeNameObj)) {
                    rowIndex = i;
                    break;
                }
            }
            if (rowIndex == -1) {
                JOptionPane.showMessageDialog(null, "ERROR: OTU ID "+nodeName+" not found in OTU Metadata Table.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            double linevalue = 0;
            Object category = frame.otuMetadata.getValueAt(rowIndex, frame.lineWidthColumnIndex);
            if (category == null) continue;

            if (String.class.isInstance(category)) {
                JOptionPane.showMessageDialog(null, "ERROR: OTU ID "+nodeName+" is not all numerical data and cannot be used for line widths.", "Error", JOptionPane.ERROR_MESSAGE);
                frame.consoleWindow.update("ERROR: OTU ID "+nodeName+" is not all numerical data and cannot be used for line widths.");
                return;
            } else if (Integer.class.isInstance(category)) {
                linevalue = (double) ( ((Integer)category).intValue());
            } else { //double
                linevalue = ((Double)category).doubleValue();;
            }      
           
            //set the node to this line width
            n.setLineWidth(linevalue);
        }
        tree.getTree().updateLineWidthsFromChildren();
     }

     public void resetLineWidthsBySample() {
         //loop over each node
         for (Node n : tree.getTree().getLeaves()) {
             //get the node's name
             String nodeName = n.getName();
             //get the row of the OTU-Sample map with this name
             int rowIndex = -1;
             Object nodeNameObj = TopiaryFunctions.objectify(nodeName);
             for (int i = 0; i < frame.otuSampleMap.getData().maxRow(); i++) {
                if (frame.otuSampleMap.getData().get(i,0).equals(nodeNameObj)) {
                    rowIndex = i;
                    break;
                }
             }
             if (rowIndex == -1) {
                JOptionPane.showMessageDialog(null, "ERROR: OTU ID "+nodeName+" not found in OTU-Sample Table.", "Error", JOptionPane.ERROR_MESSAGE);
                frame.consoleWindow.update("ERROR: OTU ID "+nodeName+" not found in OTU-Sample Table.");
                return;
             }
             //get the row
             ArrayList<Object> row = frame.otuSampleMap.getRow(rowIndex);
             n.clearColor();
             //for each non-zero column value (starting after the ID column)
             for (int i = 1; i < row.size(); i++) {
                 Object value = row.get(i);
                 //if it's not an Integer, skip it
                 if (!(value instanceof Integer)) continue;
                 Integer weight = (Integer)value;
                 if (weight == 0) continue;
                 String sampleID = frame.otuSampleMap.getColumnName(i);
                 //find the row that has this sampleID
                 int sampleRowIndex = -1;
                 Object sampleIDObj = TopiaryFunctions.objectify(sampleID);
                 for (int j = 0; j < frame.sampleMetadata.getData().maxRow(); j++) {
                    if (frame.sampleMetadata.getData().get(j,0).equals(sampleIDObj)) {
                        sampleRowIndex = j;
                        break;
                    }
                 }
                 if (sampleRowIndex == -1) {
                    //JOptionPane.showMessageDialog(null, "ERROR: Sample ID "+sampleID+" not found in Sample Metadata Table.", "Error", JOptionPane.ERROR_MESSAGE);
                    //return;
                    System.out.println("ERROR: Sample ID "+sampleID+" not found in Sample Metadata Table.");
                    frame.consoleWindow.update("ERROR: Sample ID "+sampleID+" not found in Sample Metadata Table.");
                    continue;
                 }
                 double linevalue = 0;
                 Object category = frame.sampleMetadata.getValueAt(sampleRowIndex, frame.lineWidthColumnIndex);
                 if (category == null) continue;
                 if (String.class.isInstance(category)) {
                     JOptionPane.showMessageDialog(null, "ERROR: Sample ID "+category+" is not all numerical data and cannot be used for line widths.", "Error", JOptionPane.ERROR_MESSAGE);
                     frame.consoleWindow.update("ERROR: Sample ID "+category+" is not all numerical data and cannot be used for line widths.");
                     return;
                 } else if (Integer.class.isInstance(category)) {
                     linevalue = (double) ( ((Integer)category).intValue());
                 } else { //double
                     linevalue = ((Double)category).doubleValue();
                 }      
           
                 //set the node to this line width
                 n.setLineWidth(linevalue);
             }
         }
         tree.getTree().updateLineWidthsFromChildren();
     }
     
     public void resetConsensusLineage() {
         ArrayList<String> colNames = frame.otuMetadata.getColumnNames();
         int col = colNames.indexOf("Consensus Lineage");
         if(col == -1)
            return;
            
         for (Node n : tree.getTree().getLeaves()) {
            String nodeName = n.getName();
            for(int i = 0; i < frame.otuMetadata.getData().maxRow(); i++)
            {
                if(frame.otuMetadata.getValueAt(i,0).equals(nodeName))
                    n.setLineage(""+frame.otuMetadata.getValueAt(i, col));
            }
         }
         //tree.getTree().setConsensusLineage();
     }
     
     public void tipLabels() {
         setTipLabels(showTips);
     }
     
     /**
     * Resets tipLabelCustomizer object based on new OTU metadata
     */
     public void resetTipLabelCustomizer(boolean state) {
         if(tlc != null) {tlc.dispose();}
         tlc = new TipLabelCustomizer(frame, this, (frame.otuMetadata != null), (frame.sampleMetadata != null));
         tlc.setVisible(state);
     }
     
     /**
     * Sets tip labels based on user selected metadata values
     */
     public void setTipLabels(boolean state) {
         if(frame.otuMetadata == null)
         {
             JOptionPane.showMessageDialog(null, "No OTU metadata to use in tip labels.", "Error", JOptionPane.ERROR_MESSAGE);
            frame.consoleWindow.update("No OTU metadata to use in tip labels.");
         }
         else
         {
             externalLabelsMenuItem.setState(state);
             if(tlc == null) {resetTipLabelCustomizer(state);}
             tlc.setVisible(state);
        }
        tree.setDrawExternalNodeLabels(state);
        frame.repaint();
     }

      public void setLineWidthByValue(String value) {
         //get the column that this category is
         int colIndex = frame.currTable.getColumnNames().indexOf(value);
         if (colIndex == -1) {
             //JOptionPane.showMessageDialog(null, "ERROR: Column "+value+" not found in table.", "Error", JOptionPane.ERROR_MESSAGE);

             return;
         }
         frame.lineWidthColumnIndex = colIndex;
         frame.resetLineWidths();
         tree.getTree().updateLineWidthsFromChildren();
      }
      
      /**
      * Uncollapses all tree branches
      */
      public void uncollapseTree(){
         for (Node n : tree.getTree().getNodes()) {
             n.setCollapsed(false);
         }
      }

      /**
      * Collapses all tree branches
      */
      public void collapseTree() {
         for (Node n : tree.getTree().getNodes()) {
             n.setCollapsed(true);
         }
         tree.getTree().setCollapsed(false);
      }

      public void collapseTreeByInternalNodeLabels() {
          for (Node n : tree.getTree().getNodes()){
              if (!n.isLeaf() && n.getName().length() > 0) {
                  n.setCollapsed(true);
              }
          }
      }

      public void collapseByValue(String name) {

 	 	//first, uncollapse the entire tree
 	 	uncollapseTree();

 	 	//using the metadata, collapse the tree
 	 	collapseByValueRecursive(tree.getTree(), name);

 	 }

 	 public void collapseByValueRecursive(Node node, String name) {
 	 	for (int i = 0; i < node.nodes.size(); i++) {
 	 		collapseByValueRecursive(node.nodes.get(i), name);
 	 	}
 	 	//if it's a leaf, set metadata
 	 	if (node.isLeaf()) {
 	 		//first, get the metadata
 	 		SparseTable data = frame.otuMetadata.getData();

 	 		//find which column we're looking at
 	 		int col;
 	 		for (col = 0; col < data.maxCol(); col++) {
 	 			if (frame.otuMetadata.getColumnName(col).equals(name)) { break; }
 	 		}

 	 		//find out which row we're looking at
 	 		int row;
 	 		for (row = 0; row < data.maxRow(); row++) {
 	 			if ( ( data.get(row,0).toString()).equals(node.getName())) { break; }
 	 		}

 	 		//set the node's field
 	 		node.userObject = (Object) data.get(row,col).toString();
 	 	}
 	 	else {
 	 		String consensus = (String) node.nodes.get(0).userObject;

 	 		for (int i = 0; i < node.nodes.size(); i++) {
 	 			if (!((String) node.nodes.get(i).userObject).equals(consensus)) {
 	 				consensus = "none";
 	 				break;
 	 			}
 	 		}
 	 		node.userObject = (Object) consensus;

 	 		if (!consensus.equals("none")) {
 	 			node.setCollapsed(true);
 	 		}
 	 	}

 	 }

      public void removeColor() {
                    //reset the colorMap
                    frame.colorMap = new TreeMap<Object, Color>();
                    //reset the colorKeyTable
                    ((ColorTableModel)frame.colorKeyTable.getModel()).clearTable();
                    frame.colorKeyTable.repaint();
                    //reset the node colors
                    //if (tree.getTree() != null) {
                        for (Node n : tree.getTree().getLeaves()) {
                            n.noColor();
                        }
                        tree.getTree().updateColorFromChildren();
                    //}
                    this.repaint();

                    //reset the pcoa vertex colors
                    if (frame.pcoaWindow.pcoa.sampleData != null) {
                        for (VertexData v : frame.pcoaWindow.pcoa.sampleData) {
                            v.clearColor();
                        }
                    }
                    if (frame.pcoaWindow.pcoa.spData != null) {
                        for (VertexData v : frame.pcoaWindow.pcoa.spData) {
                            v.clearColor();
                        }
                    }

                }
                
                public void colorByValue(String value) {

                     //get the column that this category is
                     int colIndex = frame.currTable.getColumnNames().indexOf(value);
                     if (colIndex == -1) {
                         //JOptionPane.showMessageDialog(null, "ERROR: Column "+value+" not found in table.", "Error", JOptionPane.ERROR_MESSAGE);

                         return;
                     }

                     //get all unique values in this column
                     ArrayList<Object> column = frame.currTable.getColumn(colIndex);
                     while (column.contains(null)) column.remove(null);
                     TreeSet<Object> uniqueVals = new TreeSet<Object>(column);
                     //set up the colorMap
                     frame.colorMap = new TreeMap<Object, Color>();
                     float[] hsbvals = new float[3];
                     hsbvals[0] = 0;
                     hsbvals[1] = 1;
                     hsbvals[2] = 1;
                     Color color = new Color(Color.HSBtoRGB(hsbvals[0], hsbvals[1], hsbvals[2]));
                     for (Object val : uniqueVals) {
                         frame.colorMap.put(val, color);
                         hsbvals[0] += (1.0/uniqueVals.size());
                         color = new Color(Color.HSBtoRGB(hsbvals[0], hsbvals[1], hsbvals[2]));
                     }
                     frame.syncColorKeyTable();

                     frame.colorColumnIndex =  colIndex;
                     frame.recolor();
                     //color tree from leaves
                     tree.getTree().updateColorFromChildren();
                  }
      /**
        * 
        */
         public void syncTreeWithLineWidthSlider() {
             if (tree.getTree() == null) return;
             double value = lineWidthSlider.getValue();
             value = value/20.0;
             tree.setLineWidthScale(value);
             tree.redraw();
         }

         /**
         *
         */
         public void syncTreeWithRotateSlider() {
         	if (tree.getTree() == null) return;
         	double value = rotateSlider.getValue();
         	tree.setRotate(value);
         	tree.redraw();
         }
         
         public void resetLineWidthOtuMenu() {
            uniformLineWidthItem.setSelected(true);
            lineWidthOtuMetadataMenu.removeAll();
            ArrayList<String> data = frame.otuMetadata.getColumnNames();
            //start at 1 to skip ID column
            for (int i = 1; i < data.size(); i++) {
                 String value = data.get(i);
                 item = new JRadioButtonMenuItem(value);
                 lineWidthGroup.add(item);
                 item.addActionListener(new ActionListener() {
                     public void actionPerformed(ActionEvent e) {
                         //get the category to color by
                         String value = e.getActionCommand();
                         System.out.println("*");
                         frame.currTable = frame.otuMetadata;
                         frame.treeWindow.setLineWidthByValue(value);
                     }
                 });
                 lineWidthOtuMetadataMenu.add(item);
            }
        }
        
        public void resetLineWidthSampleMenu() {
               uniformLineWidthItem.setSelected(true);
               lineWidthSampleMetadataMenu.removeAll();
               ArrayList<String> data = frame.sampleMetadata.getColumnNames();
               //start at 1 to skip ID column
               for (int i = 1; i < data.size(); i++) {
                    String value = data.get(i);
                    JRadioButtonMenuItem item = new JRadioButtonMenuItem(value);
                    item.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            //get the category to color by
                            String value = e.getActionCommand();
                            frame.currTable = frame.sampleMetadata;
                            frame.treeWindow.setLineWidthByValue(value);
                        }
                    });
                    lineWidthGroup.add(item);
                    lineWidthSampleMetadataMenu.add(item);
               }
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
                     frame.treeWindow.uncollapseTree();
                 }
            });
            collapseByMenu.add(item);
            item = new JMenuItem("Collapse All");
            item.addActionListener(new ActionListener() {
                 public void actionPerformed(ActionEvent e) {
                     frame.treeWindow.collapseTree();
                 }
            });
            collapseByMenu.add(item);
            item = new JMenuItem("Internal Node Labels");
            item.addActionListener(new ActionListener() {
                 public void actionPerformed(ActionEvent e) {
                     frame.treeWindow.collapseTreeByInternalNodeLabels();
                 }
            });
            collapseByMenu.add(item);
            collapseByMenu.add(new JSeparator());

            if (frame.otuMetadata != null) {
                   ArrayList<String> data = frame.otuMetadata.getColumnNames();
                   //start at 1 to skip ID column
                   for (int i = 1; i < data.size(); i++) {
                        String value = data.get(i);
                        item = new JMenuItem(value);
                        item.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                //get the category to color by
                                String value = e.getActionCommand();
                                frame.treeWindow.collapseByValue(value);
                            }
                        });
                        collapseByMenu.add(item);
                   }
               }
        }
}