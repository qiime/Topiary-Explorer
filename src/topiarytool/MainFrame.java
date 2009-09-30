package topiarytool;

import com.sun.opengl.util.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.media.opengl.*;

/**
 * MainFrame is the primary JFrame that is displayed.
 */

public class MainFrame extends JFrame {

    //INITIALIZE GUI OBJECTS
    JSplitPane splitPane = null;
    
    JPanel colorPanel = new JPanel();
    JPanel treePanel = new JPanel();
    JPanel pcoaPanel = new JPanel();
    JPanel splitPaneMainPanel = new JPanel();
    JTabbedPane dataPane = new JTabbedPane();
    JTabbedPane tabbedPane = new JTabbedPane();
    JScrollPane otuMetadataScrollPane = new JScrollPane();
    JScrollPane otuSampleMapScrollPane = new JScrollPane();
    JScrollPane sampleMetadataScrollPane = new JScrollPane();
    JScrollPane colorKeyScrollPane = new JScrollPane();    
    JTable otuMetadataTable = new JTable();
    JTable otuSampleMapTable = new JTable();
    JTable sampleMetadataTable = new JTable();
    JTable colorKeyTable = new JTable();
    TreeToolbar treeToolbar = new TreeToolbar(this);
    JButton interpolateButton = new JButton("Interpolate");
    TreeVis tree = new TreeVis();
    TreeAppletHolder treeHolder = new TreeAppletHolder(tree, this);
    PcoaVis pcoa = new PcoaVis();
    JFileChooser loadDataFileChooser = new JFileChooser();
    JLabel treeStatus = new JLabel("");
    TopiaryMenu mainMenu = new TopiaryMenu(this);
    JPopupMenu treePopupMenu = new JPopupMenu();
    Animator animator = null;

    //Variables that hold the data tables
    DataTable otuMetadata = null;
    DataTable sampleMetadata = null;
    DataTable otuSampleMap = null;

    //Holds the current coloring information
    TreeMap<Object, Color> colorMap = new TreeMap<Object, Color>();
    DataTable currTable = null;
    int colorColumnIndex = -1;

    //Holds the currently-clicked node
    Node clickedNode = null;
    
    /**
     * Default constructor.  Sets up the GUI.
     */
     public MainFrame() {
        super("TopiaryTool");

        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //get content pane
        Container pane = getContentPane();
        
        //set up the menu bar
        setJMenuBar(mainMenu);

        //set up the color panel
        colorPanel.setLayout(new BoxLayout(colorPanel, BoxLayout.PAGE_AXIS));
        JLabel label = new JLabel("Color Key");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        colorPanel.add(label, BorderLayout.NORTH);
        colorKeyTable.setModel(new ColorTableModel());
        colorKeyScrollPane = new JScrollPane(colorKeyTable);
        colorKeyScrollPane.setPreferredSize(new Dimension(200,600));
        colorPanel.add(colorKeyScrollPane);
        interpolateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                interpolateColors();
            }
        });
        interpolateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        colorPanel.add(interpolateButton);
        colorPanel.setPreferredSize(new Dimension(200,600));
        
        otuMetadataTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        //otuMetadataTable.setAutoCreateRowSorter(true);
        otuMetadataScrollPane = new JScrollPane(otuMetadataTable);
        dataPane.addTab("OTU Metadata", otuMetadataScrollPane);

        otuSampleMapTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        //otuSampleMapTable.setAutoCreateRowSorter(true);
        otuSampleMapScrollPane = new JScrollPane(otuSampleMapTable);
        dataPane.addTab("OTU-Sample Map", otuSampleMapScrollPane);

        sampleMetadataTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        //sampleMetadataTable.setAutoCreateRowSorter(true);
        sampleMetadataScrollPane = new JScrollPane(sampleMetadataTable);
        dataPane.addTab("Sample Metadata", sampleMetadataScrollPane);
        
        //set up the tree panel

        tree.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(java.awt.event.MouseEvent evt) {
				Node node = tree.findNode(evt.getX(), evt.getY());
				if (node != null) {
					if (node.isLeaf()) {
						treeStatus.setText(String.format("Leaf (OTU): %s", node.getLabel()));
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
            }
        });


        treePanel.setLayout(new BorderLayout());
        treePanel.add(treeToolbar, BorderLayout.PAGE_START);
        treePanel.add(treeHolder, BorderLayout.CENTER);
        treePanel.add(treeStatus, BorderLayout.PAGE_END);

        pcoaPanel.setLayout(new BorderLayout());
        GLCanvas canvas = new GLCanvas();
        canvas.addGLEventListener(pcoa);
        animator = new FPSAnimator(canvas, 30);
        pcoaPanel.add(canvas, BorderLayout.CENTER);
        animator.start();
        

        
        //set up the main panel
        tabbedPane.addTab("Data", dataPane);
        tabbedPane.addTab("Tree", treePanel);
        tabbedPane.addTab("PCoA", pcoaPanel);

        
        //add them to the split pane
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, colorPanel, tabbedPane);
        
        pane.add(splitPane);
        
        //the following is required to make sure th GLContext is created, or else resizing the 
        //window will result in program freezes
        tabbedPane.setSelectedIndex(2);
     }

     /**
      * Syncs the colorKeyTable with colorMap
      */
     public void syncColorKeyTable() {
         //data is: name, color, selected
         ArrayList<ArrayList<Object>> data = new ArrayList<ArrayList<Object>>();
         for (Object key : colorMap.keySet()) {
             ArrayList<Object> newRow = new ArrayList<Object>();
             newRow.add(key);
             newRow.add(colorMap.get(key));
             newRow.add(new Boolean(false));
             data.add(newRow);
         }

         ArrayList<String> colNames = new ArrayList<String>();
         colNames.add("Category");
         colNames.add("Color");
         colNames.add("");

         colorKeyTable.setModel(new ColorTableModel(data, colNames));
         colorKeyTable.setDefaultRenderer(Color.class, new ColorRenderer(true));
         colorKeyTable.setDefaultEditor(Color.class, new ColorEditor());
         colorKeyTable.setSelectionBackground(new Color(255,255,255));
         colorKeyTable.setSelectionForeground(new Color(0,0,0));

         colorKeyTable.getColumnModel().getColumn(1).setPreferredWidth(20);
         colorKeyTable.getColumnModel().getColumn(2).setPreferredWidth(20);
         colorKeyTable.setDragEnabled(true);

         colorKeyTable.getModel().addTableModelListener(new TableModelListener() {
             public void tableChanged(TableModelEvent e) {
                 int row = e.getFirstRow();
                 int column = e.getColumn();
                 ColorTableModel model = (ColorTableModel)e.getSource();
                 Object value = model.getValueAt(row, 0);
                 Object data = model.getValueAt(row, column);
                 if (column == 2) {
                     model.setValueAt(data, row, column);
                 } else if (column == 1) {
                     //update the color map
                     colorMap.remove(value);
                     colorMap.put(value, (Color)data);
                     recolor();
                 }
             }
         });
     }

     /**
      * Syncs the colorMap with the colorKeyTable
      */
     public void syncColorMap() {
        colorMap.clear();
        for (ArrayList<Object> row : ((ColorTableModel)colorKeyTable.getModel()).getData()) {
            colorMap.put(row.get(0), (Color)row.get(1));
        }
     }

     public void recolor() {
         if (currTable != null && currTable == otuMetadata) {
             recolorTreeByOtu();
             recolorPcoaByOtu();
         } else if (currTable != null && currTable == sampleMetadata) {
             recolorTreeBySample();
             recolorPcoaBySample();
         } else {
             //it's null; don't do anything
         }
     }
     public void recolorPcoaByOtu() {
        if (pcoa.spData == null) return;
        //loop over each sample vertex
        for (VertexData v : pcoa.sampleData) {
            v.groupColor = new ArrayList<Color>();
            v.groupFraction = new ArrayList<Double>();
            String sampleID = v.label;
            //find the column of the otu-sample map with this ID
            int colIndex = otuSampleMap.getColumnNames().indexOf(sampleID);
            //get this column of the table
            ArrayList<Object> colData = otuSampleMap.getColumn(colIndex);
            //for each non-zero row value
            for (int i = 0; i < colData.size(); i++) {
                Object value = colData.get(i);
                //if it's not an Integer, skip it
                if (!(value instanceof Integer)) continue;
                Integer weight = (Integer)value;
                if (weight == 0) continue;
                Object otuID = otuSampleMap.getValueAt(i,0);
                //find the row that has this otuID
                int otuRowIndex = -1;
                for (int j = 0; j < otuMetadata.getData().size(); j++) {
                   if (otuMetadata.getData().get(j).get(0).equals(otuID)) {
                       otuRowIndex = j;
                       break;
                   }
                }
                if (otuRowIndex == -1) {
                   JOptionPane.showMessageDialog(null, "ERROR: OTU ID "+otuID+" not found in OTU Metadata Table.", "Error", JOptionPane.ERROR_MESSAGE);
                   return;
                }
                Object val = otuMetadata.getValueAt(otuRowIndex, colorColumnIndex);
                if (val == null) continue;
                v.groupColor.add(colorMap.get(val));
                v.groupFraction.add(new Double(weight.intValue()));
            }
            v.mergeColors();
        }
     }

     public void recolorPcoaBySample() {
         if (pcoa.sampleData == null) return;
         //loop over each sample vertex
         for (VertexData v : pcoa.sampleData) {
             //get the sampleID
             String sampleID = v.label;
             //find the row of the sample metadata table with this ID
             int rowIndex = -1;
             Object sampleIDObj = TopiaryFunctions.objectify(sampleID);
             for (int i = 0; i < sampleMetadata.getData().size(); i++) {
                 ArrayList<Object> row = sampleMetadata.getData().get(i);
                 if (row.get(0).equals(sampleIDObj)) {
                     rowIndex = i;
                     break;
                 }
             }
             if (rowIndex == -1) {
                JOptionPane.showMessageDialog(null, "ERROR: Sample ID "+sampleID+" not found in Sample Metadata Table.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
             }
             Object category = sampleMetadata.getValueAt(rowIndex, colorColumnIndex);
             if (category == null) continue;
             //get the color for this category
             Color c = colorMap.get(category);
             if (c == null) {
                JOptionPane.showMessageDialog(null, "ERROR: No color specified for category "+category.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
             }
             v.groupColor = new ArrayList<Color>();
             v.groupFraction = new ArrayList<Double>();
             v.groupColor.add(c);
             v.groupFraction.add(1.0);
         }

     }
     
     public void recolorTreeByOtu() {
        //loop over each node
        for (Node n : tree.getTree().getLeaves()){
            //get the node's name
            String nodeName = n.getLabel();
            //get the row of the OTU metadata table with this name
            int rowIndex = -1;
            Object nodeNameObj = TopiaryFunctions.objectify(nodeName);
            for (int i = 0; i < otuMetadata.getData().size(); i++) {
                ArrayList<Object> row = otuMetadata.getData().get(i);
                if (row.get(0).equals(nodeNameObj)) {
                    rowIndex = i;
                    break;
                }
            }
            if (rowIndex == -1) {
                JOptionPane.showMessageDialog(null, "ERROR: OTU ID "+nodeName+" not found in OTU Metadata Table.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Object category = otuMetadata.getValueAt(rowIndex, colorColumnIndex);
            if (category == null) continue;
            //get the color for this category
            Color c = colorMap.get(category);
            if (c == null) {
                JOptionPane.showMessageDialog(null, "ERROR: No color specified for category "+category.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            //set the node to this color
            n.clearColor();
            n.addColor(c, 1.0);
        }
        tree.getTree().updateColorFromChildren();
     }

     public void recolorTreeBySample() {
         //loop over each node
         for (Node n : tree.getTree().getLeaves()) {
             //get the node's name
             String nodeName = n.getLabel();
             //get the row of the OTU-Sample map with this name
             int rowIndex = -1;
             Object nodeNameObj = TopiaryFunctions.objectify(nodeName);
             for (int i = 0; i < otuSampleMap.getData().size(); i++) {
                if (otuSampleMap.getData().get(i).get(0).equals(nodeNameObj)) {
                    rowIndex = i;
                    break;
                }
             }
             if (rowIndex == -1) {
                JOptionPane.showMessageDialog(null, "ERROR: OTU ID "+nodeName+" not found in OTU-Sample Table.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
             }
             //get the row
             ArrayList<Object> row = otuSampleMap.getData().get(rowIndex);
             n.clearColor();
             //for each non-zero column value (starting after the ID column)
             for (int i = 1; i < row.size(); i++) {
                 Object value = row.get(i);
                 //if it's not an Integer, skip it
                 if (!(value instanceof Integer)) continue;
                 Integer weight = (Integer)value;
                 if (weight == 0) continue;
                 String sampleID = otuSampleMap.getColumnName(i);
                 //find the row that has this sampleID
                 int sampleRowIndex = -1;
                 Object sampleIDObj = TopiaryFunctions.objectify(sampleID);
                 for (int j = 0; j < sampleMetadata.getData().size(); j++) {
                    if (sampleMetadata.getData().get(j).get(0).equals(sampleIDObj)) {
                        sampleRowIndex = j;
                        break;
                    }
                 }
                 if (sampleRowIndex == -1) {
                    JOptionPane.showMessageDialog(null, "ERROR: Sample ID "+sampleID+" not found in Sample Metadata Table.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                 }
                 Object val = sampleMetadata.getValueAt(sampleRowIndex, colorColumnIndex);
                 if (val == null) continue;
                 n.addColor(colorMap.get(val), weight);
             }
         }
         tree.getTree().updateColorFromChildren();
     }

     public void colorByValue(String value) {

        //get the column that this category is
        int colIndex = currTable.getColumnNames().indexOf(value);
        if (colIndex == -1) {
            JOptionPane.showMessageDialog(null, "ERROR: Column "+value+" not found in table.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //get all unique values in this column
        ArrayList<Object> column = currTable.getColumn(colIndex);
        while (column.contains(null)) column.remove(null);
        TreeSet<Object> uniqueVals = new TreeSet<Object>(column);
        //set up the colorMap
        colorMap = new TreeMap<Object, Color>();
        float[] hsbvals = new float[3];
        hsbvals[0] = 0;
        hsbvals[1] = 1;
        hsbvals[2] = 1;
        Color color = new Color(Color.HSBtoRGB(hsbvals[0], hsbvals[1], hsbvals[2]));
        for (Object val : uniqueVals) {
            colorMap.put(val, color);
            hsbvals[0] += (1.0/uniqueVals.size());
            color = new Color(Color.HSBtoRGB(hsbvals[0], hsbvals[1], hsbvals[2]));
        }
        syncColorKeyTable();

        colorColumnIndex =  colIndex;
        recolor();
        //color tree from leaves
        tree.getTree().updateColorFromChildren();
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
     }

     public void collapseTreeByInternalNodeLabels() {
         for (Node n : tree.getTree().getNodes()){
             if (!n.isLeaf() && n.getLabel().length() > 0) {
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
	 		ArrayList<ArrayList<Object>> rows = otuMetadata.getData();

	 		//find which column we're looking at
	 		int col;
	 		for (col = 0; col < rows.get(0).size(); col++) {
	 			if (otuMetadata.getColumnName(col).equals(name)) { break; }
	 		}

	 		//find out which row we're looking at
	 		int row;
	 		for (row = 0; row < rows.size(); row++) {
	 			if ( ( rows.get(row).get(0).toString()).equals(node.getLabel())) { break; }
	 		}

	 		//set the node's field
	 		node.userObject = (Object) rows.get(row).get(col).toString();
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
         colorMap = new TreeMap<Object, Color>();
         //reset the colorKeyTable
         ((ColorTableModel)colorKeyTable.getModel()).clearTable();
         colorKeyTable.repaint();
         //reset the node colors
         if (tree.getTree() != null) {
             for (Node n : tree.getTree().getLeaves()) {
                 n.clearColor();
             }
             tree.getTree().updateColorFromChildren();
         }

         //reset the pcoa vertex colors
         if (pcoa.sampleData != null) {
             for (VertexData v : pcoa.sampleData) {
                 v.clearColor();
             }
         }
         if (pcoa.spData != null) {
             for (VertexData v : pcoa.spData) {
                 v.clearColor();
             }
         }
        
     }

     public void interpolateColors() {
		ArrayList<ArrayList<Object>> data = ((ColorTableModel)colorKeyTable.getModel()).getData();

		int first = -1;
		int second = -1;
		//find the first color
		for (int i = 0; i < data.size(); i++) {
			if ((Boolean) data.get(i).get(2) == true) {
				second = i;
				break;
			}
		}
		//now, keep moving down the list and interpolating until we reach the end
		while (true) {
			//switch the second color to the first
			first = second;
			second = -1;
			//find the next color
			for (int i = first+1; i < data.size(); i++) {
				if ((Boolean) data.get(i).get(2) == true) {
				second = i;
				break;
				}
			}
			//have we reached the end?
			if (second == -1) {
				((ColorTableModel) colorKeyTable.getModel()).setData(data);
                colorKeyTable.repaint();
                syncColorMap();
                recolor();
                return;
			}
			//interpolate
			Color firstColor = (Color) data.get(first).get(1);
			Color secondColor = (Color) data.get(second).get(1);
			for (int i = first+1; i < second; i++) {
				//here's the interpolation
				float frac = (i-first)*(1.0f/(second-first));
				Color c = new Color((1-frac)*firstColor.getRed()/255.0f + frac*secondColor.getRed()/255.0f,
					(1-frac)*firstColor.getGreen()/255.0f + frac*secondColor.getGreen()/255.0f,
					(1-frac)*firstColor.getBlue()/255.0f + frac*secondColor.getBlue()/255.0f);
                data.get(i).set(1, c);
			}
			
		}
     }




}