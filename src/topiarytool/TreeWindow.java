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

/**
 * TreeWindow is the window that contains the tree visualization.
 */

public class TreeWindow extends JFrame {
    MainFrame frame = null;
    TreeAppletHolder treeHolder = null;
    TreeVis tree = new TreeVis();
    JPanel treePanel = new JPanel();
    TreeToolbar treeToolbar = null;
    VerticalTreeToolbar verticalTreeToolbar = null;
    CollapseTreeToolbar collapseTreeToolbar = null;
    TreeOptionsToolbar treeOpsToolbar = null;
    Node clickedNode = null;
    JLabel treeStatus = new JLabel("");
    JPopupMenu treePopupMenu = new JPopupMenu();
/*    TreeMap<Object, Color> colorMap = null;*/
    TipLabelCustomizer tlc = null;
    
	 public TreeWindow(MainFrame _frame) {
	     this.setSize(new Dimension(900,700));
	     frame = _frame;
	     treeHolder = new TreeAppletHolder(tree, this);
	     treeToolbar = new TreeToolbar(this);
	     verticalTreeToolbar = new VerticalTreeToolbar(this);
	     collapseTreeToolbar = new CollapseTreeToolbar(this);
	     treeOpsToolbar = new TreeOptionsToolbar(this);
/*       colorMap = frame.colorMap;*/
	     
	     Container pane = getContentPane();
         pane.setLayout(new BorderLayout());
         
         tree.addMouseMotionListener(new MouseMotionAdapter() {
 			public void mouseMoved(java.awt.event.MouseEvent evt) {
 				Node node = tree.findNode(evt.getX(), evt.getY());
 				if (node != null) {
 					if (node.isLeaf()) {
 						treeStatus.setText(String.format("Leaf (OTU): %s", node.getName()));
 					} else {
 						treeStatus.setText(String.format("Sub-tree: %,d leaves", node.getNumberOfLeaves()));
 					}
 				} else {
 						treeStatus.setText(" ");
 				}
 			}
 		});
	     
	     //set up the tree pop-up menu
         JMenuItem item = new JMenuItem("Collapse/Expand");
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
         treePanel.add(treeToolbar, BorderLayout.PAGE_START);
         treePanel.add(verticalTreeToolbar, BorderLayout.LINE_START);
         treePanel.add(treeHolder, BorderLayout.CENTER);
         treePanel.add(collapseTreeToolbar, BorderLayout.PAGE_END);
	     pane.add(treeOpsToolbar, BorderLayout.NORTH);
	     pane.add(treePanel, BorderLayout.CENTER);
	}
	
	public void mirrorHorz() {
         // reset wedge slider
         for (Node n : tree.getTree().getNodes()) {
             n.setXOffset(tree.getTree().depth() - n.getXOffset());
             n.setTOffset(Math.PI - n.getTOffset());
             }
        tree.setRadialOffsets(tree.getTree());
     }
     
     public void mirrorVert() {
         for (Node n : tree.getTree().getNodes()) {
             n.setYOffset(tree.getTree().getNumberOfLeaves() - n.getYOffset());
             n.setTOffset(-n.getTOffset());
         }
         tree.setRadialOffsets(tree.getTree());
     }
	
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
               return;
           }
           Object category = frame.otuMetadata.getValueAt(rowIndex, frame.colorColumnIndex);
           if (category == null) continue;
           //get the color for this category
           Color c = frame.colorMap.get(category);
           if (c == null) {
               JOptionPane.showMessageDialog(null, "ERROR: No color specified for category "+category.toString(), "Error", JOptionPane.ERROR_MESSAGE);
               return;
           }
           //set the node to this color
           n.clearColor();
           n.addColor(c, 1.0);
       }
       tree.getTree().updateColorFromChildren();
       frame.repaint();
    }
    
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
     }
     
     public void resetTipLabelCustomizer(boolean state) {
         if(tlc != null) {tlc.dispose();}
         tlc = new TipLabelCustomizer(frame, this, (frame.otuMetadata != null), (frame.sampleMetadata != null));
         tlc.setVisible(state);
     }
     
     public void setTipLabels(boolean state) {
         if(tlc == null) {resetTipLabelCustomizer(state);}
         tlc.setVisible(state);
         tree.setDrawExternalNodeLabels(state);
         frame.repaint();
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

      public void uncollapseTree(){
         for (Node n : tree.getTree().getNodes()) {
             n.setCollapsed(false);
         }
      }

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
          if (tree.getTree() != null) {
              for (Node n : tree.getTree().getLeaves()) {
                  n.clearColor();
              }
              tree.getTree().updateColorFromChildren();
          }

          //reset the pcoa vertex colors
          if (frame.pcoa.sampleData != null) {
              for (VertexData v : frame.pcoa.sampleData) {
                  v.clearColor();
              }
          }
          if (frame.pcoa.spData != null) {
              for (VertexData v : frame.pcoa.spData) {
                  v.clearColor();
              }
          }

      }
}