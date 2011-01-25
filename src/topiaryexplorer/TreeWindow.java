package topiaryexplorer;

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
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * TreeWindow is the window that contains the tree visualization.
 */

public class TreeWindow extends TopiaryWindow implements KeyListener, ActionListener, WindowListener{
    JMenuBar topMenu = new JMenuBar();
    JMenu treeMenu = new JMenu("Options");
    JMenu nodeMenu = new JMenu("Node");
    
    JMenu collapseByMenu = new JMenu("Collapse by");
    JCheckBoxMenuItem externalLabelsMenuItem = new JCheckBoxMenuItem("Tip Labels");
    JCheckBoxMenuItem internalLabelsMenuItem = new JCheckBoxMenuItem("Internal Node Labels");
    
    ButtonGroup treeLayoutGroup = new ButtonGroup();
    
    JPanel toolbars = new JPanel();
    JPanel leftPanel = new JPanel();
    JPanel rightPanel = new JPanel();
    JPanel middlePanel = new JPanel();
    JMenuItem item;
    TreeWindow thisWindow = this;
    MainFrame frame = null;
    TreeAppletHolder treeHolder = null;
    TreeVis tree = new TreeVis();
    JPanel treePanel = new JPanel();
    TreeToolbar treeToolbar = null;
    VerticalTreeToolbar verticalTreeToolbar = null;
    CollapseTreeToolbar collapseTreeToolbar = null;
    TreeEditToolbar treeEditToolbar = null;
    JScrollPane treeEditPane = new JScrollPane();
    Node clickedNode = null;
    JPopupMenu treePopupMenu = new JPopupMenu();
    TipLabelCustomizer tlc = null;
    Set keys = new java.util.HashSet();
    boolean showTips = false;
    String dir_path = ""; //(new File(".")).getCanonicalPath();
    
    /**
    * Class Constructor
    */
	 public TreeWindow(MainFrame _frame) {
         super(_frame);
	     this.setSize(new Dimension(1000,800));
	     frame = _frame;
/*       this.addWindowListener(new WindowAdapter() {
               public void windowClosing(WindowEvent e) {
                 frame.treeWindows.remove(this);
               }
             });*/
         
         tree.setParent(this);         
         try{
         dir_path = (new File(".")).getCanonicalPath();
         }
         catch(IOException e)
         {}
         
/*         colorBranchesBy = new ColorByMenu(frame,this,frame.branchColorPanel,0);*/
/*         colorLabelsBy = new ColorByMenu(frame,this,frame.labelColorPanel,1);*/
         
/*         branchMenu = new BranchMenu(frame, this, "Branches");*/
	     tree.addKeyListener(this);
	     treeHolder = new TreeAppletHolder(tree, this);
	     treeToolbar = new TreeToolbar(this);
	     verticalTreeToolbar = new VerticalTreeToolbar(this);
	     collapseTreeToolbar = new CollapseTreeToolbar(this);
	     treeEditToolbar = new TreeEditToolbar(this, frame);
	     treeEditPane.add(treeEditToolbar);
/*       wedgeToolbar = new WedgeCustomizerToolbar(this);*/
	     
	     Container pane = getContentPane();
         pane.setLayout(new BorderLayout());
         
         tree.addMouseMotionListener(new MouseMotionAdapter() {
 			public void mouseMoved(java.awt.event.MouseEvent evt) {
 			    if(!isActive()) return;
 				Node node = tree.findNode(evt.getX(), evt.getY());
 				String status = "";
 				String prefix = "";
 				if (node != null) {
                  if (node.isLocked())
                    prefix += "(L)";
                  String label = node.getName();
                  if (label.length() > 0) {
                      status += label;
                    } else {
 						status += String.format("Sub-tree: %d leaves", node.getNumberOfLeaves());
                  }
 				} 
 				treeToolbar.setStatus(prefix, status);
 			}
 		});
	     
	     /*//set up the tree pop-up menu
	              item = new JMenuItem("Collapse/Expand");
	              item.addActionListener(new ActionListener() {
	                  public void actionPerformed(ActionEvent e) {
	                      clickedNode.setCollapsed(!clickedNode.isCollapsed());
	                  }
	              });
	              treePopupMenu.add(item);*/
          item = new JMenuItem("Hide");
                item.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        clickedNode.setHidden(true);
                    }
                });
                treePopupMenu.add(item);
          item = new JMenuItem("Lock/Unlock");
              item.addActionListener(new ActionListener() {
                  public void actionPerformed(ActionEvent e) {
                      clickedNode.setLocked(!clickedNode.isLocked());
                  }
              });
              treePopupMenu.add(item);
         item = new JMenuItem("Invert Collapsed Nodes");
          item.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                  clickedNode.setCollapsed(!clickedNode.isCollapsed());
                  for(Node n: tree.getTree().getNodes())
                    {
                        if(clickedNode.getAnscestors().contains(n) || n == clickedNode)
                          continue;
                        n.setCollapsed(!clickedNode.isCollapsed());
                    }
                    
                  for(Node n: clickedNode.getParent().nodes) // siblings
                    {
                        if(n != clickedNode)
                          n.setCollapsed(!clickedNode.isCollapsed());
                    }
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
                    JOptionPane.showMessageDialog(thisWindow,
                        clickedNode.getConsensusLineage(),
                        "Consensus Lineage",
                        JOptionPane.PLAIN_MESSAGE);
                }
            });
            treePopupMenu.add(item);
            item = new JMenuItem("View Subtree in new Window");
              item.addActionListener(new ActionListener() {
                  public void actionPerformed(ActionEvent arg0) {
                      frame.newTreeWindow(TopiaryFunctions.createNewickStringFromTree(clickedNode));
                  }
              });
              treePopupMenu.add(item);
              item = new JMenuItem("Delete");
              item.addActionListener(new ActionListener() {
                  public void actionPerformed(ActionEvent e) {
                      int del = JOptionPane.showConfirmDialog(
                          thisWindow,
                          "Are you sure you want to delete this node?",
                          "Delete Node",
                          JOptionPane.YES_NO_OPTION);
                          if(del == JOptionPane.YES_OPTION)
                          {
                              tree.noLoop();
                            clickedNode.getParent().nodes.remove(clickedNode);
                            for(Node n : clickedNode.getAnscestors())
                                n.setConsensusLineage(n.getConsensusLineageF());
                          }
/*                        tree.resetTree();*/
                        tree.setTree(tree.getTree());
                        tree.loop();
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
         treePanel.add(treeToolbar, BorderLayout.SOUTH);
         treePanel.add(verticalTreeToolbar, BorderLayout.WEST);
         treePanel.add(treeHolder, BorderLayout.CENTER);
         treePanel.add(collapseTreeToolbar, BorderLayout.NORTH);
/*         toolbars.setLayout(new GridLayout(2,1));*/
/*         toolbars.add(treeEditToolbar);*/
/*         toolbars.add(wedgeToolbar);*/
/*         toolbars.setPreferredSize(new Dimension(this.getWidth(),80));*/
/*         wedgeToolbar.setVisible(false);*/
         rightPanel.setLayout(new BorderLayout());
         rightPanel.add(toolbars, BorderLayout.NORTH);
         rightPanel.add(treePanel, BorderLayout.CENTER);
         leftPanel.add(treeEditToolbar, BorderLayout.CENTER);
         treeEditPane.setViewportView(leftPanel);
	     
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
	     
	     
	     //options menu
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
/*         item = new JMenuItem("Prune tree...");
           item.addActionListener(this);
           treeMenu.add(item);*/
         item = new JMenuItem("Show hidden nodes");
         item.addActionListener(this);
         treeMenu.add(item);
         item = new JMenuItem("Set consensus lineage");
         item.addActionListener(this);
         treeMenu.add(item);
         item = new JMenuItem("Recenter");
         item.addActionListener(this);
         treeMenu.add(item);
         /*item = new JMenuItem("Mirror left/right");
                  item.addActionListener(this);
                  treeMenu.add(item);
                  item = new JMenuItem("Mirror up/down");
                  item.addActionListener(this);
                  treeMenu.add(item);*/
         resetCollapseByMenu();
         treeMenu.add(collapseByMenu);


         externalLabelsMenuItem.setSelected(false);
         externalLabelsMenuItem.addActionListener(this);
         internalLabelsMenuItem.setSelected(false);
         internalLabelsMenuItem.addActionListener(this);
/*         treeMenu.add(externalLabelsMenuItem);*/
         treeMenu.add(internalLabelsMenuItem);
	     topMenu.add(treeMenu);
/*       topMenu.add(nodeMenu);*/
/*       topMenu.add(branchMenu);*/
	     
	     setJMenuBar(topMenu);
/*         pane.add(leftPanel, BorderLayout.WEST);*/ 
         pane.add(treeEditPane, BorderLayout.WEST);
	     pane.add(rightPanel, BorderLayout.CENTER);
	     setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	
	public void actionPerformed(ActionEvent e) {
	    if (e.getActionCommand().equals("Save Tree...")) {
             saveTree();
         }    else if (e.getActionCommand().equals("Tip Labels")) {
/*                    setTipLabels(externalLabelsMenuItem.getState());*/
              } else if (e.getActionCommand().equals("Internal Node Labels")) {
                  tree.setDrawInternalNodeLabels(internalLabelsMenuItem.getState());
              } else if (e.getActionCommand().equals("Lock/Unlock")) {
                    if (frame.clickedNode != null) {
                       frame.clickedNode.setLocked(!frame.clickedNode.isLocked());
                    } 
              } else if (e.getActionCommand().equals("Collapse/Expand")) {
                  if (frame.clickedNode != null) {
                     frame.clickedNode.setCollapsed(!frame.clickedNode.isCollapsed());
                  }
              }
              else if (e.getActionCommand().equals("Show hidden nodes")) {
                     for(Node n: tree.getTree().getNodes())
                        n.setHidden(false);
                 }
             else if (e.getActionCommand().equals("Set consensus lineage")) {
                      resetConsensusLineage();
                  }
             else if (e.getActionCommand().equals("Prune tree...")) {
                      //PruneTreeWindow ptw = new PruneTreeWindow(frame, this, true, true);
                      //ptw.setVisible(true);
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
              } else if (e.getActionCommand().equals("Export Tree Image...") && tree.getTree()!= null) {
                 exportTreeImage();
              } else if (e.getActionCommand().equals("Export Tree Screen Capture...") && tree.getTree()!= null)  {
                  tree.noLoop();
     			  exportScreenCapture();
                  if(this.isActive()) tree.loop();
              }
    }
    
    public void windowActivated(WindowEvent e)  {
        tree.loop();
    }
    
    public void windowDeactivated(WindowEvent e)  {
        tree.noLoop();
    }
    
    public void windowClosed(WindowEvent e) {
        frame.treeWindows.remove(this);
        dispose();
    }
    
    public void windowClosing(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e){
        tree.loop();
    }
    
    public void windowIconified(WindowEvent e)  {
        tree.noLoop();
    }
    
    public void windowOpened(WindowEvent e) {
        tree.loop();
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
            verticalTreeToolbar.syncTreeWithZoomSlider();
	    }
	    if(key.getKeyCode() == 45)
	    {
	        treeToolbar.zoomSlider.setValue(treeToolbar.zoomSlider.getValue() - 1);
	        treeToolbar.syncTreeWithZoomSlider();
            verticalTreeToolbar.zoomSlider.setValue(verticalTreeToolbar.zoomSlider.getValue() - 1);
            verticalTreeToolbar.syncTreeWithZoomSlider();
	    }
    }
    
    public void setTreeVals(Node root) {
        for(Node n : root.getNodes())
        {
            n.setDepthO(n.depthF());
            n.setNumberOfLeavesO(n.getNumberOfLeavesF());
        }
    }
	
	/**
    * Loads a new tree from the selected file
    */
	public void loadTree(FileContents inFile) {
	    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	    treeEditToolbar.setStatus("Loading tree...");
	     if (inFile == null) {
	     	 try {
	             inFile = frame.fos.openFileDialog(null,null);
	         } catch (java.io.IOException e) {}
	     }
         if (inFile != null) {
             tree.noLoop();
             Node root = TopiaryFunctions.createTreeFromNewickFile(inFile);
             setTreeVals(root);
             tree.setTree(root);
             //make sure coloring is empty
/*             removeColor();*/
             treeToolbar.setScale();
             verticalTreeToolbar.setScale();             
             tree.loop();
             
             collapseTree();

             this.setVisible(true);
             frame.consoleWindow.update("Done drawing tree. ");
             frame.treeFile = inFile;
             
             treeHolder.syncScrollbarsWithTree();
         }
         treeEditToolbar.setStatus("Done loading tree.");
         this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
    
    public void loadTree() {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        treeEditToolbar.setStatus("Loading tree...");
        FileContents inFile = null;
        try {
             inFile = frame.fos.openFileDialog(null,null);
         } catch (java.io.IOException e) {}
         if (inFile != null) {
              tree.noLoop();
              Node root = TopiaryFunctions.createTreeFromNewickFile(inFile);
               setTreeVals(root);
               tree.setTree(root);
              //make sure coloring is empty
/*              removeColor();*/
              treeToolbar.setScale();
              verticalTreeToolbar.setScale();
              tree.loop();
              collapseTree();

              this.setVisible(true);
              frame.consoleWindow.update("Done drawing tree. ");
              frame.treeFile = inFile;
              
              treeHolder.syncScrollbarsWithTree();
          }
          treeEditToolbar.setStatus("Done drawing tree.");
          this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
    
    public void loadTree(String treeString) {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        treeEditToolbar.setStatus("Loading tree...");
        tree.noLoop();
        Node root = TopiaryFunctions.createTreeFromNewickString(treeString);
        setTreeVals(root);
        tree.setTree(root);
/*        removeColor();*/
        treeToolbar.setScale();
        verticalTreeToolbar.setScale();
        tree.loop();
        collapseTree();
        
        this.setVisible(true);
        treeHolder.syncScrollbarsWithTree();
        frame.consoleWindow.update("Done drawing tree. ");
        treeEditToolbar.setStatus("Done drawing tree.");
        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
    
    /**
    * Saves the current tree in newick format
    */
    public void saveTree() {
        treeEditToolbar.setStatus("Saving tree...");
	    String s = TopiaryFunctions.createNewickStringFromTree(tree.getTree());
        try {
        	FileContents fc = frame.fss.saveFileDialog(null,null,new ByteArrayInputStream(s.getBytes()),null);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error writing to file.", "Error", JOptionPane.ERROR_MESSAGE);
            frame.consoleWindow.update("Error writing to file.");
        }
        treeEditToolbar.setStatus("Done saving tree.");
    }
    
    /**
    * Exports the current tree and coloring as a pdf with
    * supplied dimensions
    */
    public void exportTreeImage() {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        treeEditToolbar.setStatus("Exporting tree...");
        tree.noLoop();
         ExportTreeDialog etd = new ExportTreeDialog(this);
         etd.setVisible(true);
        if(this.isActive()) tree.loop();
        treeEditToolbar.setStatus("Done exporting tree.");
        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
    
    /**
    * Exports the current tree view as screencap
    */
    public void exportScreenCapture() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        Date date = new Date();
        tree.exportScreenCapture(frame.dir_path+"/tree_screencaps/"+dateFormat.format(date)+".png");
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
         double d = tree.getTree().depth();
         for (Node n : tree.getTree().getNodes()) {
             n.setXOffset(d - n.getXOffset());
             n.setTOffset(Math.PI - n.getTOffset());
             }
        tree.setMirrored(!tree.getMirrored());
        tree.setRadialOffsets(tree.getTree());
     }
     
     /**
     * Mirrors the tree vertically
     */
     public void mirrorVert() {
         int d = tree.getTree().getNumberOfLeaves();
         for (Node n : tree.getTree().getNodes()) {
             n.setYOffset(d - n.getYOffset());
             n.setMaximumYOffset(d - n.getMaximumYOffset());
             n.setMinimumYOffset(d - n.getMinimumYOffset());
             n.setTOffset(-n.getTOffset());
         }
/*         tree.setYOffsets(tree.getTree(), 0);*/
         tree.setRadialOffsets(tree.getTree());
     }
	
	 /**
     * Recolors the tree based on selected OTU metadata field
     */
	public void recolorBranchesByOtu() {
	    treeEditToolbar.setStatus("Coloring tree...");
	    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	    ArrayList<Node> ns = tree.getTree().getLeaves();
       //loop over each node
       for (Node n : ns){
               //get the node's name
               String nodeName = n.getName();
               //get the row of the OTU metadata table with this name
               int rowIndex = frame.otuMetadata.getRowNames().indexOf(nodeName);
               if (rowIndex == -1) {
/*                   JOptionPane.showMessageDialog(null, "ERROR: OTU ID "+nodeName+" not found in OTU Metadata Table.", "Error", JOptionPane.ERROR_MESSAGE);*/
/*                   frame.consoleWindow.update("ERROR: OTU ID "+nodeName+" not found in OTU Metadata Table.");*/
                   continue;
               }
               Object category = frame.otuMetadata.getValueAt(rowIndex, frame.branchColorPanel.getColorColumnIndex());
               if (category == null) continue;
               //get the color for this category
               Color c = frame.branchColorPanel.getColorMap().get(category);
               if (c == null) {
                   JOptionPane.showMessageDialog(null, "ERROR: No color specified for category "+category.toString(), "Error", JOptionPane.ERROR_MESSAGE);
/*                   frame.consoleWindow.update("ERROR: No color specified for category "+category.toString());*/
                   continue;
               }
               //set the node to this color
               n.clearBranchColor();
               n.addBranchColor(c, 1.0);
           
       }
       tree.getTree().updateBranchColorFromChildren();
       frame.repaint();
       treeEditToolbar.setStatus("Done coloring tree.");
       this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
    
    /**
    * Recolors tree by selected sample metadata
    */
    public void recolorBranchesBySample() {
        treeEditToolbar.setStatus("Coloring tree...");
        ArrayList<Node> ns = tree.getTree().getLeaves();
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));        
            //loop over each node
             for (Node n : ns) {
                 //get the node's name

                     String nodeName = n.getName();
                     //get the row of the OTU-Sample map with this name
                     int rowIndex = frame.otuSampleMap.getRowNames().indexOf(nodeName);
                     
                     if (rowIndex == -1) {
                        continue;
                     }
                     //get the row
/*                     ArrayList<Object> row = frame.otuSampleMap.getRow(rowIndex);*/
                     HashMap row = frame.otuSampleMap.getRow(rowIndex);
                     n.clearBranchColor();
                    //for each non-zero column value (starting after the ID column)
                     for (Object i : row.keySet()) {
                         Object value = row.get(i);
                         //if it's not an Integer, skip it
                         if (!(value instanceof Integer)) continue;
                         Integer weight = (Integer)value;
                         if (weight == 0) continue;
                         
                         String sampleID = frame.otuSampleMap.getColumnName(((Number)i).intValue());
                         
                         //find the row that has this sampleID                         
                         int sampleRowIndex = frame.sampleMetadata.getRowNames().indexOf(sampleID);
                         
                         if (sampleRowIndex == -1) {
                            continue;
                         }
                         
                         Object color = null;
                         color = frame.sampleMetadata.getValueAt(sampleRowIndex, frame.branchColorPanel.getColorColumnIndex());                                                          
                         if (color == null) continue;
                         n.addBranchColor(frame.branchColorPanel.getColorMap().get(color), weight);
                     }
             }

          tree.getTree().updateBranchColorFromChildren();
          frame.repaint();
         
         treeEditToolbar.setStatus("Done coloring tree.");
         this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
     }
     
     	public void recolorLabelsByOtu() {
     	    treeEditToolbar.setStatus("Coloring tree...");
     	    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
     	    ArrayList<Node> ns = tree.getTree().getLeaves();
            //loop over each node
            for (Node n : ns){
                    //get the node's name
                    String nodeName = n.getName();
                    //get the row of the OTU metadata table with this name
                    int rowIndex = frame.otuMetadata.getRowNames().indexOf(nodeName);
                    if (rowIndex == -1) {
     /*                   JOptionPane.showMessageDialog(null, "ERROR: OTU ID "+nodeName+" not found in OTU Metadata Table.", "Error", JOptionPane.ERROR_MESSAGE);*/
     /*                   frame.consoleWindow.update("ERROR: OTU ID "+nodeName+" not found in OTU Metadata Table.");*/
                        continue;
                    }
                    Object category = frame.otuMetadata.getValueAt(rowIndex, frame.labelColorPanel.getColorColumnIndex());
                    if (category == null) continue;
                    //get the color for this category
                    Color c = frame.labelColorPanel.getColorMap().get(category);
                    if (c == null) {
                        JOptionPane.showMessageDialog(null, "ERROR: No color specified for category "+category.toString(), "Error", JOptionPane.ERROR_MESSAGE);
     /*                   frame.consoleWindow.update("ERROR: No color specified for category "+category.toString());*/
                        continue;
                    }
                    //set the node to this color
                    n.clearLabelColor();
                    n.addLabelColor(c, 1.0);

            }
            tree.getTree().updateLabelColorFromChildren();
            frame.repaint();
            treeEditToolbar.setStatus("Done coloring tree.");
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
         }

         /**
         * Recolors tree by selected sample metadata
         */
         public void recolorLabelsBySample() {
             treeEditToolbar.setStatus("Coloring tree...");
             ArrayList<Node> ns = tree.getTree().getLeaves();
             this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));        
                 //loop over each node
                  for (Node n : ns) {
                      //get the node's name

                          String nodeName = n.getName();
                          //get the row of the OTU-Sample map with this name
                          int rowIndex = frame.otuSampleMap.getRowNames().indexOf(nodeName);

                          if (rowIndex == -1) {
                             continue;
                          }
                          //get the row
     /*                     ArrayList<Object> row = frame.otuSampleMap.getRow(rowIndex);*/
                          HashMap row = frame.otuSampleMap.getRow(rowIndex);
                          n.clearLabelColor();
                         //for each non-zero column value (starting after the ID column)
                          for (Object i : row.keySet()) {
                              Object value = row.get(i);
                              //if it's not an Integer, skip it
                              if (!(value instanceof Integer)) continue;
                              Integer weight = (Integer)value;
                              if (weight == 0) continue;

                              String sampleID = frame.otuSampleMap.getColumnName(((Number)i).intValue());

                              //find the row that has this sampleID                         
                              int sampleRowIndex = frame.sampleMetadata.getRowNames().indexOf(sampleID);

                              if (sampleRowIndex == -1) {
                                 continue;
                              }

                              Object color = null;
                              color = frame.sampleMetadata.getValueAt(sampleRowIndex, frame.labelColorPanel.getColorColumnIndex());                                                          
                              if (color == null) continue;
                              n.addLabelColor(frame.labelColorPanel.getColorMap().get(color), weight);
                          }
                  }

               tree.getTree().updateLabelColorFromChildren();
               frame.repaint();

              treeEditToolbar.setStatus("Done coloring tree.");
              this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
          }
     
     
     public void resetLineWidthsByOtu() {
         this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
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
/*                JOptionPane.showMessageDialog(null, "ERROR: OTU ID "+nodeName+" not found in OTU Metadata Table.", "Error", JOptionPane.ERROR_MESSAGE);*/
                continue;
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
        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
     }

     public void resetLineWidthsBySample() {
         this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
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
/*                JOptionPane.showMessageDialog(null, "ERROR: OTU ID "+nodeName+" not found in OTU-Sample Table.", "Error", JOptionPane.ERROR_MESSAGE);*/
                frame.consoleWindow.update("ERROR: OTU ID "+nodeName+" not found in OTU-Sample Table.");
                continue;
             }
             //get the row
             ArrayList<Object> row = frame.otuSampleMap.getRow2(rowIndex);
             n.clearBranchColor();
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
/*                    System.out.println("ERROR: Sample ID "+sampleID+" not found in Sample Metadata Table.");*/
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
         this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
     }
     
     public void resetConsensusLineage() {
         this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
         ArrayList<String> colNames = frame.otuMetadata.getColumnNames();
         int col = colNames.indexOf("Consensus Lineage");
         if(col == -1)
         {
             this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
             JOptionPane.showMessageDialog(null, "ERROR: No consensus lineage column defined.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
          }
            
         for (Node n : tree.getTree().getLeaves()) {
            String nodeName = n.getName();
            int id = frame.otuMetadata.getRowNames().indexOf(nodeName);
            n.setLineage(""+frame.otuMetadata.getValueAt(id, col));
         }
         for(Node n : tree.getTree().getNodes())
         {
             n.setConsensusLineage(n.getConsensusLineageF());
         }
         this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
     }
     
/*     public void tipLabels() {
         setTipLabels(showTips);
     }*/
     
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
         for(Node n: tree.getTree().getNodes())
             n.setDrawLabel(externalLabelsMenuItem.getState());
         tree.setDrawExternalNodeLabels(externalLabelsMenuItem.getState());
         tree.redraw();
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
          collapseTreeToolbar.setValue(1000);
      }

      /**
      * Collapses all tree branches
      */
      public void collapseTree() {
          collapseTreeToolbar.setValue(0);
      }

      public void collapseTreeByInternalNodeLabels() {
          for (Node n : tree.getTree().getNodes()){
              if (!n.isLeaf() && n.getName().length() > 0) {
                  n.setCollapsed(true);
              }
          }
      }

      public void collapseByValue(String name, double level) {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
 	 	//first, uncollapse the entire tree
 	 	//frame.treeWindow.uncollapseTree();
 	 	uncollapseTree();
        treeEditToolbar.setStatus("Collasping tree...");
 	 	//using the metadata, collapse the tree
 	 	collapseByValueNonRecursive(tree.getTree(), name, level);
 	 	treeEditToolbar.setStatus("Done collapsing tree.");
 	 	this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
 	 }
 	 
 	 public void collapseByValueNonRecursive(Node root, String name, double level) {
 	     for (Node node : root.getNodes()) {
      	 	//if it's a leaf, set metadata
      	 	if (node.isLeaf()) {
      	 		//first, get the metadata
      	 		SparseTable data = frame.otuMetadata.getData();

      	 		//find which column we're looking at
     	 		int col = frame.otuMetadata.getColumnNames().indexOf(name);

     	 		//find out which row we're looking at
     	 		int row = frame.otuMetadata.getRowNames().indexOf(node.getName());

      	 		//set the node's field
      	 		node.userString = data.get(row,col).toString();
      	 	}
      	 	else {
      	 		//String consensus = (String) node.nodes.get(0).userObject;
                ArrayList<String> consensus = new ArrayList<String>();
                ArrayList<Node> tips = node.getLeaves();
      	 		for (int i = 0; i < tips.size(); i++) {
      	 		    consensus.add(tips.get(i).userString);
      	 		}
      	 		
      	 		node.userString = TopiaryFunctions.getConsensus(consensus, level);
      	 		if(node.userString != null)
                    node.setCollapsed(true);
      	 	}
      	 }
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
 	 		int col = frame.otuMetadata.getColumnNames().indexOf(name);

 	 		//find out which row we're looking at
 	 		int row = frame.otuMetadata.getRowNames().indexOf(node.getName());

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
            //reset the branchColorPanel.getColorMap()
            frame.branchColorPanel.setColorMap(new TreeMap<Object, Color>());
            //reset the colorKeyTable
            ((ColorTableModel)frame.branchColorPanel.getColorKeyTable().getModel()).clearTable();
            frame.branchColorPanel.getColorKeyTable().repaint();
            //reset the node colors
            for (Node n : tree.getTree().getLeaves()) {
                n.noBranchColor();
            }
            tree.getTree().updateBranchColorFromChildren();
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
            frame.branchValue = "";
        }
                
        public void colorBranchesByValue(String value) {
            frame.branchValue = value;
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            treeEditToolbar.setStatus("Coloring branches...");
             //get the column that this category is
             int colIndex = frame.currTable.getColumnNames().indexOf(value);
             if (colIndex == -1) {
                 //JOptionPane.showMessageDialog(null, "ERROR: Column "+value+" not found in table.", "Error", JOptionPane.ERROR_MESSAGE);
                 return;
             }
             ArrayList<Object> column = new ArrayList<Object>();
             //get all unique values in this column
               column = frame.currTable.getColumn(colIndex);
             
             while (column.contains(null)) column.remove(null);
              TreeSet<Object> uniqueVals = new TreeSet<Object>(column);
              //set up the branchColorPanel.getColorMap()
              frame.branchColorPanel.setColorMap(new TreeMap<Object, Color>());
              float[] hsbvals = new float[3];
              hsbvals[0] = 0;
              hsbvals[1] = 1;
              hsbvals[2] = 1;
              Color color = new Color(Color.HSBtoRGB(hsbvals[0], hsbvals[1], hsbvals[2]));
              for (Object val : uniqueVals) {
                  frame.branchColorPanel.getColorMap().put(val, new Color(200,200,200));//color);
                  hsbvals[0] += (1.0/uniqueVals.size());
                  color = new Color(Color.HSBtoRGB(hsbvals[0], hsbvals[1], hsbvals[2]));
              }
             
             frame.branchColorPanel.syncColorKeyTable();
             frame.branchColorPanel.setColorColumnIndex(colIndex);
             
             frame.recolorBranches();
             
            //color tree from leaves
             tree.getTree().updateBranchColorFromChildren();
             
             treeEditToolbar.setStatus("Branches colored by "+value);
             this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
          }
          
          
          public void colorLabelsByValue(String value) {
          frame.labelValue = value;
          this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
          treeEditToolbar.setStatus("Coloring labels...");
           //get the column that this category is
           int colIndex = frame.currTable.getColumnNames().indexOf(value);
           if (colIndex == -1) {
               //JOptionPane.showMessageDialog(null, "ERROR: Column "+value+" not found in table.", "Error", JOptionPane.ERROR_MESSAGE);
               return;
           }
           ArrayList<Object> column = new ArrayList<Object>();
           //get all unique values in this column
             column = frame.currTable.getColumn(colIndex);

           while (column.contains(null)) column.remove(null);
            TreeSet<Object> uniqueVals = new TreeSet<Object>(column);
            //set up the branchColorPanel.getColorMap()
            frame.labelColorPanel.setColorMap(new TreeMap<Object, Color>());
            float[] hsbvals = new float[3];
            hsbvals[0] = 0;
            hsbvals[1] = 1;
            hsbvals[2] = 1;
            Color color = new Color(Color.HSBtoRGB(hsbvals[0], hsbvals[1], hsbvals[2]));
            for (Object val : uniqueVals) {
                frame.labelColorPanel.getColorMap().put(val, new Color(200,200,200));//color);
                hsbvals[0] += (1.0/uniqueVals.size());
                color = new Color(Color.HSBtoRGB(hsbvals[0], hsbvals[1], hsbvals[2]));
            }

           frame.labelColorPanel.syncColorKeyTable();
           frame.labelColorPanel.setColorColumnIndex(colIndex);

           frame.recolorLabels();

          //color tree from leaves
           tree.getTree().updateLabelColorFromChildren();

           treeEditToolbar.setStatus("Labels colored by "+value);
           this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
      /**
        * 
        */
         public void syncTreeWithLineWidthSlider() {
             if (tree.getTree() == null) return;
             double value = treeEditToolbar.branchEditPanel.lineWidthSlider.getValue();
             value = value/20.0;
             tree.setLineWidthScale(value);
             tree.redraw();
         }

         /**
         *
         */
         
         
         public void resetLineWidthOtuMenu() {
/*            branchMenu.resetLineWidthOtuMenu();*/
        }
        
        public void resetLineWidthSampleMenu() {
/*               branchMenu.resetLineWidthSampleMenu();*/
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
                     uncollapseTree();
                 }
            });
            collapseByMenu.add(item);
            item = new JMenuItem("Collapse All");
            item.addActionListener(new ActionListener() {
                 public void actionPerformed(ActionEvent e) {
                     collapseTree();
                 }
            });
            collapseByMenu.add(item);
            item = new JMenuItem("Internal Node Labels");
            item.addActionListener(new ActionListener() {
                 public void actionPerformed(ActionEvent e) {
                     collapseTreeByInternalNodeLabels();
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
                                double level = Double.parseDouble(((String)JOptionPane.showInputDialog(
                                                    thisWindow,
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
                                    collapseByValue(value,level);
                                }
                                else
                                    JOptionPane.showMessageDialog(frame,
                                        "Invalid threshold percentage.",
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE);
                                
                            }
                        });
                        collapseByMenu.add(item);
                   }
               }
        }
}