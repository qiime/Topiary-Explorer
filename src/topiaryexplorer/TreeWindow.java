package topiaryexplorer;

// import com.sun.opengl.util.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
// import javax.media.opengl.*;
import java.sql.*;
import javax.swing.table.*;
import java.io.*;
import javax.jnlp.*;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.imageio.ImageIO;
import java.lang.Number;
/**
 * TreeWindow is the window that contains the tree visualization.
 */

public class TreeWindow extends TopiaryWindow implements KeyListener, ActionListener, WindowListener{
    JMenuBar topMenu = new JMenuBar();
    JMenu treeMenu = new JMenu("File");

    JPanel leftPanel = new JPanel();
    JPanel rightPanel = new JPanel();

    JMenuItem item;
    TreeWindow thisWindow = this;
    MainFrame frame = null;
    TreeAppletHolder treeHolder = null;
    TreeVis tree = new TreeVis();
    JPanel treePanel = new JPanel();
    JToggleButton lockButton = new JToggleButton();

    Boolean zoomLocked = false;
    TreeToolbar treeToolbar = null;
    JPanel bottomPanel = new JPanel();
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
       this.addWindowListener(new WindowAdapter() {
               public void windowClosing(WindowEvent e) {
                 frame.treeWindows.remove(this);
               }
             });
         
         tree.setParent(this);         
         try{
         dir_path = (new File(".")).getCanonicalPath();
         }
         catch(IOException e)
         {}
         
	     tree.addKeyListener(this);
	     treeHolder = new TreeAppletHolder(tree, this);
	     treeToolbar = new TreeToolbar(this);
	     verticalTreeToolbar = new VerticalTreeToolbar(this);
	     collapseTreeToolbar = new CollapseTreeToolbar(this);
	     treeEditToolbar = new TreeEditToolbar(this, frame);
	     treeEditPane.add(treeEditToolbar);
	     
	     try{
         lockButton = new JToggleButton(new ImageIcon(ImageIO.read(this.getClass().getClassLoader().getResource("unlock.gif"))), false);
         lockButton.setSelectedIcon(new ImageIcon(ImageIO.read(this.getClass().getClassLoader().getResource("lock.gif"))));
         }
         catch(IOException e)
         {}
         
	     
	     Container pane = getContentPane();
         pane.setLayout(new BorderLayout());
         
         addComponentListener(new java.awt.event.ComponentAdapter() {
 			public void componentResized(ComponentEvent e) {
 				tree.checkBounds();
 				tree.redraw();

                collapseTreeToolbar.resetLayout();
 				treeHolder.syncScrollbarsWithTree();
 			}
 		});
         
         tree.addMouseMotionListener(new MouseMotionAdapter() {
             public void mouseMoved(java.awt.event.MouseEvent evt) {
                 if(!isActive()) return;
                 Node node = tree.findNode(evt.getX(), evt.getY());
                 if (node != null) {
                  setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));  
                 }
                 else
                 {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                  }
             }
         });
         
         
          item = new JMenuItem("Find Node in Metadata");
           item.addActionListener(this);
           treePopupMenu.add(item);
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

         item = new JMenuItem("Rotate (Swap Children)");
         item.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent arg0) {
                 clickedNode.rotate();
                 tree.setYOffsets(tree.getTree(), 0);
                 tree.redraw();
             }
         });
         treePopupMenu.add(item);
         item = new JMenuItem("Toggle Pie Chart");
         item.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent arg0) {
                 clickedNode.setDrawPie(!clickedNode.getDrawPie());
                 tree.redraw();
             }
         });
         item.setEnabled(false);
         treePopupMenu.add(item);
         item = new JMenuItem("Toggle Node Label");
          item.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent arg0) {
                  clickedNode.setDrawLabel(!clickedNode.getDrawLabel());
                  tree.redraw();
              }
          });
          item.setEnabled(false);
          treePopupMenu.add(item);
          item = new JMenuItem("Reset Node Label Position");
          item.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent arg0) {
                  clickedNode.setLabelYOffset(0.0);
                  clickedNode.setLabelXOffset(0.0);
                  tree.redraw();
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
            item.setEnabled(false);
            treePopupMenu.add(item);
            item = new JMenuItem("View Subtree in new Window");
              item.addActionListener(new ActionListener() {
                  public void actionPerformed(ActionEvent arg0) { frame.newTreeWindow(TopiaryFunctions.createNewickStringFromTree(clickedNode), "Subtree-"+clickedNode.getLabel());
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
                            clickedNode.prune(true);
                            tree.getTree().prune();
                          }
                        tree.setTree(tree.getTree());
                        setTreeVals(tree.getTree());
                        tree.redraw();
                  }
              });
              treePopupMenu.add(item);

         tree.addMouseListener(new java.awt.event.MouseAdapter() {
 			public void mousePressed(java.awt.event.MouseEvent evt) {
 				clickedNode = tree.findNode(evt.getX(), evt.getY());
 				if (evt.isPopupTrigger() && clickedNode != null) {
 				    treePopupMenu.getComponent(0).setEnabled(clickedNode.isLeaf());
 				    treePopupMenu.getComponent(2).setEnabled(!clickedNode.isLeaf());
 				    treePopupMenu.getComponent(3).setEnabled(!clickedNode.isLeaf());
 				    treePopupMenu.getComponent(4).setEnabled(!clickedNode.isLeaf());
 				    treePopupMenu.getComponent(5).setEnabled(!clickedNode.isLeaf());
 				    treePopupMenu.getComponent(8).setEnabled(!clickedNode.isLeaf());
 					treePopupMenu.show(tree, evt.getX(), evt.getY());
 				}
 			}
 			public void mouseReleased(java.awt.event.MouseEvent evt){
 				clickedNode = tree.findNode(evt.getX(), evt.getY());
 				if (evt.isPopupTrigger() && clickedNode != null) {
 				    treePopupMenu.getComponent(0).setEnabled(clickedNode.isLeaf());
 				    treePopupMenu.getComponent(2).setEnabled(!clickedNode.isLeaf());
 				    treePopupMenu.getComponent(3).setEnabled(!clickedNode.isLeaf());
 				    treePopupMenu.getComponent(4).setEnabled(!clickedNode.isLeaf());
 				    treePopupMenu.getComponent(5).setEnabled(!clickedNode.isLeaf());
 				    treePopupMenu.getComponent(8).setEnabled(!clickedNode.isLeaf());
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
         bottomPanel.setLayout(new BorderLayout());
         lockButton.setToolTipText("Lock zooming");
         lockButton.addChangeListener(new ChangeListener() {
               public void stateChanged(ChangeEvent e) {
                   zoomLocked = lockButton.isSelected();
                   verticalTreeToolbar.sliderEnabled(!lockButton.isSelected());
               }
           });
         bottomPanel.add(lockButton, BorderLayout.WEST);
         bottomPanel.add(treeToolbar, BorderLayout.CENTER);
         treePanel.add(bottomPanel, BorderLayout.SOUTH);
         treePanel.add(verticalTreeToolbar, BorderLayout.WEST);
         treePanel.add(treeHolder, BorderLayout.CENTER);
         treePanel.add(collapseTreeToolbar, BorderLayout.NORTH);
         rightPanel.setLayout(new BorderLayout());
         rightPanel.add(treePanel, BorderLayout.CENTER);
         leftPanel.add(treeEditToolbar);
         treeEditPane.setViewportView(leftPanel);
	     
	     
	     //options menu
	     item = new JMenuItem("Save Newick String...");
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
         item = new JMenuItem("Close");
         item.addActionListener(this);
         treeMenu.add(item);
	     
	     //set up the "tree" submenus
	     topMenu.add(treeMenu);
	     
	     setJMenuBar(topMenu);
         pane.add(treeEditPane, BorderLayout.WEST);
	     pane.add(rightPanel, BorderLayout.CENTER);
	     setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	
	public void resetCollapseByMenu() {
	    treeEditToolbar.treeViewPanel.resetCollapseByMenu();
	}
	
	public void actionPerformed(ActionEvent e) {
	    if (e.getActionCommand().equals("Save Tree...")) {
             saveTree();
         }    else if (e.getActionCommand().equals("Lock/Unlock")) {
                    if (frame.clickedNode != null) {
                       frame.clickedNode.setLocked(!frame.clickedNode.isLocked());
                    } 
              } else if (e.getActionCommand().equals("Collapse/Expand")) {
                  if (frame.clickedNode != null) {
                     frame.clickedNode.setCollapsed(!frame.clickedNode.isCollapsed());
                  }
              }
            else if (e.getActionCommand().equals("Find Node in Metadata")) {
                if(frame.otuMetadata == null)
                {
                    return;
                }
                if (clickedNode != null && clickedNode.isLeaf()) {
                    frame.otuMetadataTable.scrollRectToVisible(frame.otuMetadataTable.getCellRect(frame.otuMetadata.getRowNames().indexOf(clickedNode.getName()),0,true));
                   frame.otuMetadataTable.changeSelection(frame.otuMetadata.getRowNames().indexOf(clickedNode.getName()),0,true,false);
                    frame.dataPane.setSelectedIndex(1);
                    frame.otuMetadataTable.requestFocus();
                    frame.setVisible(true);
                    frame.repaint();
               }
              }else if (e.getActionCommand().equals("Collapse/Expand All Children"))    
              {
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
              } else if (e.getActionCommand().equals("Mirror left/right")) {
                 mirrorHorz();
              } else if (e.getActionCommand().equals("Mirror up/down")) {
                  mirrorVert();
              } else if (e.getActionCommand().equals("Export Tree Image...") && tree.getTree()!= null) {
                 exportTreeImage();
              } else if (e.getActionCommand().equals("Export Tree Screen Capture...") && tree.getTree()!= null)  {
                  tree.noLoop();
     			  exportScreenCapture();
                  if(this.isActive()) tree.redraw();
              } else if (e.getActionCommand().equals("Close"))  {
                  windowClosed();
              }
    }
    
    public void windowActivated(WindowEvent e)  {
        tree.redraw();
    }
    
    public void windowDeactivated(WindowEvent e)  {
    }
    
    public void windowClosed(WindowEvent e) {
        frame.treeWindows.remove(this);
        dispose();
    }
    
    public void windowClosed() {
        frame.treeWindows.remove(this);
        dispose();
    }
    
    public void windowClosing(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e){
        tree.redraw();
    }
    
    public void windowIconified(WindowEvent e)  {
    }
    
    public void windowOpened(WindowEvent e) {
        tree.redraw();
    }
	
	public void keyTyped(KeyEvent key) {
	}
	
	public void keyReleased(KeyEvent key) {
       }

    public void keyPressed(KeyEvent key) {
	    if(key.getKeyCode() == 61)
	        zoomIn();
	    if(key.getKeyCode() == 45)
	        zoomOut();
    }
    
    public void zoomIn() {
        treeToolbar.zoomIn();
        verticalTreeToolbar.zoomIn();
    }
    
    public void zoomOut() {
        treeToolbar.zoomOut();
        verticalTreeToolbar.zoomOut();
    }
    
    public void syncTreeWithZoomSliders() {
        treeToolbar.syncTreeWithZoomSlider();
        verticalTreeToolbar.syncTreeWithZoomSlider();
    }
    
    public void setTreeVals(Node root) {
        for(Node n : root.getNodes())
        {
            n.setDepth(n.depthF());
            n.setNumberOfLeaves(n.getLeaves().size());
        }
        treeEditToolbar.summaryPanel.setTree(root);
        treeEditToolbar.summaryPanel.updateTable();
    }
	
	public void ladderize() {
        tree.getTree().ladderize();
        tree.setTree(tree.getTree());
	    tree.redraw();
	}
	
	/**
    * Loads a new tree from the selected file
    */
	public boolean loadTree(FileContents inFile) {
        // frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	     if (inFile == null) {
	     	 try {
	             inFile = frame.fos.openFileDialog(null,null);
	         } catch (IOException e) {
                 // frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	         }
	     }
         if (inFile != null) {
             Node root = TopiaryFunctions.createTreeFromNewickFile(inFile);
             setTreeVals(root);
             tree.setTree(root);
             treeToolbar.setScale();
             verticalTreeToolbar.setScale();             
             tree.redraw();
             
             collapseTree();
             // frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
             this.setVisible(true);
             frame.treeFile = inFile;
             treeHolder.syncScrollbarsWithTree();
             
             return true;
         }
         else
         {
             // frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
             return false;
         }
    }
    
    public String loadTree() {
        // frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        FileContents inFile = null;
        try {
             inFile = frame.fos.openFileDialog(null,null);
         } catch (IOException e) {
             // frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
         }
         
         if (inFile != null) {
              Node root = TopiaryFunctions.createTreeFromNewickFile(inFile);
               setTreeVals(root);
               tree.setTree(root);
              treeToolbar.setScale();
              verticalTreeToolbar.setScale();
              tree.redraw();
              collapseTree();

              // frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
              this.setVisible(true);
              frame.treeFile = inFile;
              
              treeHolder.syncScrollbarsWithTree();
              String treeName = "";
              try {
                  treeName = inFile.getName();
              }
              catch(IOException e) {
              }
              return treeName;
          }
          else
          {
              // frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
              return null;
          }
    }
    
    public void loadTree(String treeString) {
        // frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        tree.noLoop();
        Node root = TopiaryFunctions.createTreeFromNewickString(treeString);
        setTreeVals(root);
        tree.setTree(root);
        treeToolbar.setScale();
        verticalTreeToolbar.setScale();
        tree.redraw();
        collapseTree();
        
        this.setVisible(true);
        treeHolder.syncScrollbarsWithTree();
        // frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
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
        }
    }
    
    /**
    * Exports the current tree and coloring as a pdf with
    * supplied dimensions
    */
    public void exportTreeImage() {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        tree.noLoop();
         ExportTreeDialog etd = new ExportTreeDialog(this);
         etd.setVisible(true);
        if(this.isActive()) tree.redraw();
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
    
    public void changeMode(boolean selectMode) {
        tree.setSelectMode(selectMode);
    }
    
    /**
    * Recenters the tree in the treeview window
    */
    public void recenter() {
        tree.resetTreeX();
        tree.resetTreeY();
        tree.redraw();
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
        tree.redraw();
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
         tree.setRadialOffsets(tree.getTree());
         tree.redraw();
     }
     
     public void pruneTreeByBranchLength(double cutoff) {
         double total = tree.getTree().depth();
         // send cutoff percentage of branch length to prune method
         for(Node n: tree.getTree().getLeaves())
             n.prune(total, cutoff);
         tree.getTree().prune();
         tree.setTree(tree.getTree());
         setTreeVals(tree.getTree());
     }
     
     public void pruneTreeByNumNodes(int cutoff) {
         double total = tree.getTree().depth();
          double perc = .01;
          //while the tree still has too many leaves
          while(tree.getTree().getNumberOfLeaves() > cutoff)
          {
              // send leaves that are the smallest percentage
              // of branch length first
               for(Node n: tree.getTree().getLeaves())
                 n.prune(total, perc);

               perc += .01;
               tree.getTree().prune();
               tree.setTree(tree.getTree());
               setTreeVals(tree.getTree());
         }
     }
	
	public void pruneTreeByOtu(String category, Object value) {
	    int colIndex = frame.otuMetadata.getColumnIndex(category);
	    ArrayList<Node> ns = tree.getTree().getLeaves();
       //loop over each node
       for (Node n : ns){
               //get the node's name
               String nodeName = n.getName();
               //get the row of the OTU metadata table with this name
               int rowIndex = frame.otuMetadata.getRowNames().indexOf(nodeName);
               if (rowIndex == -1) {
                   continue;
               }
               // if this tip is one of the tips set to prune, prune it
               if(frame.otuMetadata.getValueAt(rowIndex, colIndex).equals(value))
               {
                 n.getParent().clearBranchColor();
                 n.prune(true);
               }
       }
       tree.getTree().prune();
       setTreeVals(tree.getTree());
       tree.redraw();
	}
	
	public void pruneTreeBySample(String category, Object value) {
	    ArrayList<Node> ns = tree.getTree().getLeaves();        
	    int colIndex = frame.sampleMetadata.getColumnIndex(category);
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
                 HashMap row = frame.otuSampleMap.getRow(rowIndex);
                //for each non-zero column value (starting after the ID column)
                 for (Object i : row.keySet()) {
                     Object v = row.get(i);
                     // if it's not an Integer, skip it
                     if (!(v instanceof Integer)) continue;
                     Integer weight = (Integer)v;
                     if (weight == 0) continue;
                     
                     String sampleID = frame.otuSampleMap.getColumnName(((Number)i).intValue());
                     
                     //find the row that has this sampleID                         
                     int sampleRowIndex = frame.sampleMetadata.getRowNames().indexOf(sampleID);
                     
                     // if the sample is not found, continue
                     if (sampleRowIndex == -1)
                        continue;
                     
                     // if the value is null continue
                     if(frame.sampleMetadata.getValueAt(sampleRowIndex, colIndex) == null)
                        continue;
                     
                     // if the value is equal to the value we want to prune by
                     if(frame.sampleMetadata.getValueAt(sampleRowIndex, colIndex).equals(value))                                                          
                     {
                         // add it to prune vals
                         n.getParent().clearBranchColor();
                         n.prune(true);
                     }
                     else
                     {
                         // else add that this val shouldnt be pruned
                         n.prune(false);
                     }
                 }
        }
        // tree.getTree().prune();
        tree.redraw();
	}
	
	public void recolorBranches() {
	    if (frame.currTable != null && frame.currTable == frame.otuMetadata) {
                recolorBranchesByOtu();
                treeEditToolbar.summaryPanel.treeColored();
         } else if (frame.currTable != null && frame.currTable == frame.sampleMetadata) {
                recolorBranchesBySample();
                treeEditToolbar.summaryPanel.treeColored();
         } else {
             //it's null; don't do anything
         }
	}
	
	 /**
     * Recolors the tree based on selected OTU metadata field
     */
	public void recolorBranchesByOtu() {
	    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	    ArrayList<Node> ns = tree.getTree().getLeaves();
       //loop over each node
       for (Node n : ns){
               //get the node's name
               String nodeName = n.getName();
               //get the row of the OTU metadata table with this name
               int rowIndex = frame.otuMetadata.getRowNames().indexOf(nodeName);
               // tip name not in otu metadata
               if (rowIndex == -1) {
                   continue;
               }
               
               Object category = frame.otuMetadata.getValueAt(rowIndex, frame.branchColorPanel.getColorColumnIndex());
               if (category == null) continue;
               //get the color for this category
               Color c = (Color)frame.branchColorPanel.getColorMap().get(category);
               if (c == null) {
                   JOptionPane.showMessageDialog(null, "ERROR: No color specified for category "+category.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                   continue;
               }
               //set the node to this color
               n.clearBranchColor();
               n.addBranchColor(c, 1.0);
               n.addBranchValue(category);
       }
       tree.getTree().updateBranchColorFromChildren();
       frame.repaint();
       this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
    
	public void recolorLabelsByOtu() {
 	    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
 	    ArrayList<Node> ns = tree.getTree().getLeaves();
        //loop over each node
        for (Node n : ns){
                //get the node's name
                String nodeName = n.getName();
                //get the row of the OTU metadata table with this name
                int rowIndex = frame.otuMetadata.getRowNames().indexOf(nodeName);
                //node not found in otu metadata
                if (rowIndex == -1) {
                    continue;
                }
                Object category = frame.otuMetadata.getValueAt(rowIndex, frame.labelColorPanel.getColorColumnIndex());
                if (category == null) continue;
                //get the color for this category
                Color c = (Color)frame.labelColorPanel.getColorMap().get(category);
                if (c == null) {
                    JOptionPane.showMessageDialog(null, "ERROR: No color specified for category "+category.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                    continue;
                }
                //set the node to this color
                n.clearLabelColor();
                n.addLabelColor(c, 1.0);
                n.addLabelValue(category);
        }
        tree.getTree().updateLabelColorFromChildren();
        frame.repaint();
        // tree.redraw();
        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
     }

    
    /**
    * Recolors tree by selected sample metadata
    */
    public void recolorBranchesBySample() {
        boolean weighted = treeEditToolbar.branchEditPanel.weightedColoringMenuItem.isSelected();
        ArrayList<Node> ns = tree.getTree().getLeaves();
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));        
            //loop over each node
             for (Node n : ns) {
                 //get the node's name

                     String nodeName = n.getName();
                     //get the row of the OTU-Sample map with this name
                     int rowIndex = frame.otuSampleMap.getRowNames().indexOf(nodeName);
                     // System.out.println("nodename:"+nodeName+", index:"+rowIndex);
                     
                     // node name does not exist in sample-tip map
                     if (rowIndex == -1) {
                         n.addBranchColor(new Color(0),  1.0);
                         // n.addBranchColor(new Color(0),  1.0);
                         n.addBranchValue("Nodes not in mapping");
                        continue;
                     }
                     
                     //get the row
                     HashMap row = frame.otuSampleMap.getRow(rowIndex);
                     n.clearBranchColor();
                    //for each non-zero column value in the mapping(starting after the ID column)
                     for (Object i : row.keySet()) //for all samples
                     {
                         // if(i == 0) //skip otu id column
                            // continue;
                         Object w = row.get(i); // get the OTU count
                         //if it's not a Number, skip it
                         if (!(w instanceof Number)) continue;
                         Integer weight = 0;
                         try {
                             weight = (Integer)w;
                         } catch ( ClassCastException e) {
                             weight = ((Double)w).intValue();
                         }
                         if (weight == 0) continue; // this sample doesn't contain this OTU
                         
                         if(!weighted)
                            weight = 1;
                         
                         String sampleID = frame.otuSampleMap.getColumnName(((Number)i).intValue());
                         // System.out.println("sampleID:"+sampleID);
                         //The sampleID is taken from the columnames of the
                         //sample-tip map and one of the headers is otu id
                         if(sampleID.equals("OTU ID"))
                            continue;
                         
                         //find the row that has this sampleID                         
                         int sampleRowIndex = frame.sampleMetadata.getRowNames().indexOf(sampleID);
                         // for(String s : frame.sampleMetadata.getRowNames())
                            // System.out.println(s);
                         
                         // this sample is not in the metadata but contains the current OTU, it will not be colored
                         if (sampleRowIndex == -1) {
                             n.addBranchColor(new Color(0),  weight);
                             n.addBranchValue("Nodes in samples without metadata");
                            continue;
                         }
                         
                         Object value = null;
                         value = frame.sampleMetadata.getValueAt(sampleRowIndex, frame.branchColorPanel.getColorColumnIndex());                                                          
                         if (value == null) continue;
                         n.addBranchColor((Color)frame.branchColorPanel.getColorMap().get(value), weight);
                         n.addBranchValue(value);
                     }
                     // no samples contain this OTU
                     if(n.getGroupBranchColor().size() == 0)
                     {
                         n.addBranchColor(new Color(0),  1);
                         n.addBranchValue("Nodes not found in any sample");
                     }
             }

         tree.getTree().updateBranchColorFromChildren();
         frame.repaint();
         // tree.redraw();
         this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
     }
     
     
         /**
         * Recolors tree by selected sample metadata
         */
         public void recolorLabelsBySample() {
             ArrayList<Node> ns = tree.getTree().getLeaves();
             this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));        
                 //loop over each node
                  for (Node n : ns) {
                      //get the node's name

                          String nodeName = n.getName();
                          //get the row of the OTU-Sample map with this name
                          int rowIndex = frame.otuSampleMap.getRowNames().indexOf(nodeName);

                          if (rowIndex == -1) {
                             n.addLabelColor(new Color(0),  1);
                             n.addLabelValue("Nodes not found in mapping");
                             continue;
                          }
                          //get the row
                          HashMap row = frame.otuSampleMap.getRow(rowIndex);
                          n.clearLabelColor();
                         //for each non-zero column value (starting after the ID column)
                     for (Object i : row.keySet()) //for all samples
                     {
                         Object w = row.get(i); // get the OTU count
                         //if it's not an Integer, skip it
                         if (!(w instanceof Integer)) continue;
                         Integer weight = (Integer)w;
                         if (weight == 0) continue; // this sample doesn't contain this OTU
                         
                         String sampleID = frame.otuSampleMap.getColumnName(((Number)i).intValue());
                         //The sampleID is taken from the columnames of the
                         //sample-tip map and one of the headers is otu id
                         if(sampleID.equals("OTU ID"))
                            continue;
                         
                         //find the row that has this sampleID                         
                         int sampleRowIndex = frame.sampleMetadata.getRowNames().indexOf(sampleID);
                         
                         // this sample is not in the metadata but contains the current OTU, it will not be colored
                         if (sampleRowIndex == -1) {
                             n.addLabelColor(new Color(0),  weight);
                             n.addLabelValue("Nodes in samples without metadata");
                            continue;
                         }
                         
                         Object value = null;
                         value = frame.sampleMetadata.getValueAt(sampleRowIndex, frame.labelColorPanel.getColorColumnIndex());                                                          
                         if (value == null) continue;
                         n.addLabelColor((Color)frame.labelColorPanel.getColorMap().get(value), weight);
                         n.addLabelValue(value);
                     }
                     // no samples contain this OTU
                     if(n.getGroupLabelColor().size() == 0)
                     {
                         n.addLabelColor(new Color(0),  n.getBranchLength());
                         n.addLabelValue("Nodes not found in any sample");
                     }
                  }

               tree.getTree().updateLabelColorFromChildren();
               frame.repaint();
               // tree.redraw();
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
                continue;
            }
            double linevalue = 0;
            Object category = frame.otuMetadata.getValueAt(rowIndex, frame.lineWidthColumnIndex);
            if (category == null) continue;

            if (String.class.isInstance(category)) {
                JOptionPane.showMessageDialog(null, "ERROR: OTU ID "+nodeName+" is not all numerical data and cannot be used for line widths.", "Error", JOptionPane.ERROR_MESSAGE);
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
        tree.redraw();
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
                    continue;
                 }
                 double linevalue = 0;
                 Object category = frame.sampleMetadata.getValueAt(sampleRowIndex, frame.lineWidthColumnIndex);
                 if (category == null) continue;
                 if (String.class.isInstance(category)) {
                     JOptionPane.showMessageDialog(null, "ERROR: Sample ID "+category+" is not all numerical data and cannot be used for line widths.", "Error", JOptionPane.ERROR_MESSAGE);
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
         tree.redraw();
         this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
     }
     
     public void resetConsensusLineage() {
         this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
         
         if(frame.otuMetadata != null) {
                    ConsensusLineageDialog cld = new ConsensusLineageDialog(frame, this);
                    treePopupMenu.getComponent(6).setEnabled(true);
                    treeEditToolbar.treeViewPanel.collapseByMenu.getComponent(3).setEnabled(true);
                }
                else
                JOptionPane.showMessageDialog(this,
                                     "Consensus lineage cannot be set without Tip metadata.",
                                     "Error",
                                     JOptionPane.ERROR_MESSAGE); 
         
         this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
     }
     
     /**
     * Resets tipLabelCustomizer object based on new OTU metadata
     */
     public void resetTipLabelCustomizer(boolean state) {
         if(tlc != null) {tlc.dispose();}
         tlc = new TipLabelCustomizer(frame, this, (frame.otuMetadata != null),
          (frame.sampleMetadata != null));
         tlc.setVisible(state);
     }
     
     /**
     * Sets tip labels based on user selected metadata values
     */
     public void setTipLabels(boolean state) {
         for(Node n: tree.getTree().getNodes())
             n.setDrawLabel(treeEditToolbar.nodeEditPanel.nodeLabelCheckBox.isSelected());
         tree.setDrawExternalNodeLabels(treeEditToolbar.nodeEditPanel.nodeLabelCheckBox.isSelected());
         tree.redraw();
     }

      public void setLineWidthByValue(String value) {
         //get the column that this category is
         int colIndex = frame.currTable.getColumnNames().indexOf(value);
         if (colIndex == -1) {
             return;
         }
         frame.lineWidthColumnIndex = colIndex;
         frame.resetLineWidths();
         tree.redraw();
         tree.getTree().updateLineWidthsFromChildren();
      }
      
      /**
      * Uncollapses all tree branches
      */
      public void uncollapseTree(){
          for(Node n: tree.getTree().getNodes())
            n.setCollapsed(false);
      }

      /**
      * Collapses all tree branches
      */
      public void collapseTree() {
          collapseTreeToolbar.setValue(0);
      }

      public void collapseTreeByNodeLabels(double level) {
          for (Node node : tree.getTree().getNodes()) {
      	 	//if it's a leaf, set metadata
      	 	if (node.isLeaf()) {
      	 		//set the node's field
      	 		node.userString = node.getLabel();
      	 	}
      	 	else {
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
      
      public void collapseTreeByConsensusLineage(double level) {
          for (Node node : tree.getTree().getNodes()) {
      	 	//if it's a leaf, set metadata
      	 	if (node.isLeaf()) {
      	 		//set the node's field
      	 		node.userString = node.getConsensusLineage();
      	 	}
      	 	else {
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

      public void collapseByValue(String name, double level) {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
 	 	//first, uncollapse the entire tree
 	 	//frame.treeWindow.uncollapseTree();
 	 	uncollapseTree();
 	 	//using the metadata, collapse the tree
 	 	collapseByValueNonRecursive(tree.getTree(), name, level);
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
            //reset the node colors
            for (Node n : tree.getTree().getLeaves()) {
                n.noBranchColor();
            }
            tree.getTree().updateBranchColorFromChildren();

            this.repaint();

            frame.branchValue = "";
            tree.redraw();
        }
                
        public void colorBranchesByValue(String value) {
            frame.branchValue = value;
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            treeEditToolbar.branchEditPanel.colorBy.setEnabled(false);
             //get the column that this category is
             int colIndex = frame.currTable.getColumnNames().indexOf(value);
             if (colIndex == -1) {
                 return;
             }
             ArrayList<Object> column = new ArrayList<Object>();
             column = frame.currTable.getColumn(colIndex);
             //remove nulls
             while (column.contains(null)) column.remove(null);
              
             //get all unique values in this column
             ArrayList newcol = new ArrayList<Object>();
             for(Object o : column)
             {
                 if(newcol.contains(o))
                    continue;
                newcol.add(o);
             }
              
             frame.branchColorPanel.setColorMap(new HashMap());
             double hue = 0;
             Color color = new Color(0);
              
             //see if this column contains numeric values
             Class c = TopiaryFunctions.getColumnClass(column);
             
             if(c.isInstance(Number.class))
             {            
                 ArrayList<Double> numerics = new ArrayList<Double>();
                 for(Object o : newcol)
                 {
                     try {
                      numerics.add((Double)o);
                    }
                    catch(ClassCastException e) {
                        numerics.add(Double.parseDouble(o.toString()));
                    }
                  }
                 Collections.sort(numerics);
                 double min = numerics.get(0);
                 double max = numerics.get(numerics.size()-1);
                 double diff = max-min;
                 for (Object val : newcol) {
                     try {
                         hue = ((Float)val-min)*(1.0f/diff);
                    }
                    catch(ClassCastException e) {
                        hue = (Float.parseFloat(val.toString())-min)*(1.0f/diff);
                        
                    }
                     color = new Color(Color.HSBtoRGB((float)hue*.66f, 1, 1));
                     frame.branchColorPanel.getColorMap().put(val, color);                      
                  }
                 
             }
             else {
                 Collections.sort(newcol);
                  for (Object val : newcol) {
                      hue += (1.0/newcol.size());
                      color = new Color(Color.HSBtoRGB((float)hue, 1, 1));
                      frame.branchColorPanel.getColorMap().put(val, color);
                  }
              }
             
             frame.branchColorPanel.syncColorKeyTable();
             frame.branchColorPanel.setColorColumnIndex(colIndex);
             
             frame.recolorBranches();
             
            //color tree from leaves
             tree.getTree().updateBranchColorFromChildren();
             tree.redraw();
             treeEditToolbar.branchEditPanel.colorBy.setEnabled(true);
             treeEditToolbar.summaryPanel.treeColored();
             this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
          }
          
          
          public void colorLabelsByValue(String value) {
          frame.labelValue = value;
          this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
          treeEditToolbar.nodeEditPanel.colorByButton.setEnabled(false);
           //get the column that this category is
           int colIndex = frame.currTable.getColumnNames().indexOf(value);
           if (colIndex == -1) {
               return;
           }
           ArrayList<Object> column = new ArrayList<Object>();
           //get all unique values in this column
             column = frame.currTable.getColumn(colIndex);

           while (column.contains(null)) column.remove(null);
            ArrayList<Object> uniqueVals = new ArrayList<Object>(column);
            //set up the branchColorPanel.getColorMap()
            frame.labelColorPanel.setColorMap(new HashMap());
            float hue = 0;
            Color color = new Color(Color.HSBtoRGB(hue, 1, 1));
            for (Object val : uniqueVals) {
                frame.labelColorPanel.getColorMap().put(val, color);
                hue += (1.0/uniqueVals.size());
                color = new Color(Color.HSBtoRGB(hue, 1, 1));
            }

           frame.labelColorPanel.syncColorKeyTable();
           frame.labelColorPanel.setColorColumnIndex(colIndex);

           frame.recolorLabels();

          //color tree from leaves
           tree.getTree().updateLabelColorFromChildren();
           treeEditToolbar.nodeEditPanel.colorByButton.setEnabled(true);
           this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
           tree.redraw();
        }
      /**
        * 
        */
         public void syncTreeWithLineWidthSlider() {
             if (tree.getTree() == null) return;
             double value = treeEditToolbar.branchEditPanel.lineWidthSlider.getValue();
             value = value/20.0;
             tree.setLineWidthScale((float)value);
             tree.redraw();
         }
         
         public void resetLineWidthOtuMenu() {
/*            branchMenu.resetLineWidthOtuMenu();*/
        }
        
        public void resetLineWidthSampleMenu() {
/*               branchMenu.resetLineWidthSampleMenu();*/
           }        
        
}