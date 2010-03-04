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
 * MainFrame is the primary JFrame that is displayed.
 */

public class MainFrame extends JFrame {
    
    String DATABASE_URL = ""; // jdbc:mysql://127.0.0.1/topiarytool
    String DATABASE_UN = "";  // root
    String DATABASE_PW = "";  // desudesu

    //INITIALIZE GUI OBJECTS
    JSplitPane splitPane = null;
    JPanel colorPanel = new JPanel();
    JPanel splitPaneMainPanel = new JPanel();
    JPanel databasePanel = new JPanel();
    JPanel databaseBottomPanel = new JPanel();
    JPanel databaseTopPanel = new JPanel();
    JPanel searchPanel = new JPanel();
    JPanel dataPanel = new JPanel();
    JTabbedPane dataPane = new JTabbedPane();
    //JTabbedPane tabbedPane = new JTabbedPane();
    JTabbedPane databaseTabPane = new JTabbedPane();
    JScrollPane databaseScrollPane = new JScrollPane();
    JScrollPane otuMetadataScrollPane = new JScrollPane();
    JScrollPane otuSampleMapScrollPane = new JScrollPane();
    JScrollPane sampleMetadataScrollPane = new JScrollPane();
    JScrollPane colorKeyScrollPane = new JScrollPane();  
    JTable databaseTable = new JTable();  
    JTable otuMetadataTable = new JTable();
    JTable otuSampleMapTable = new JTable();
    JTable sampleMetadataTable = new JTable();
    JTable colorKeyTable = new JTable();
    JButton interpolateButton = new JButton("Interpolate");
    JFileChooser loadDataFileChooser = new JFileChooser();
    JLabel databaseStatus = new JLabel("Database not connected.");
    TopiaryMenu mainMenu = new TopiaryMenu(this);
    
    Container toolbarPane = new Container();
    WindowViewToolbar windowToolbar = new WindowViewToolbar(this);
    
    TreeWindow treeWindow = new TreeWindow(this);
    PcoaWindow pcoaWindow = new PcoaWindow(this);
    
    DbConnectWindow db_conn = new DbConnectWindow();
    DbSearchWindow db_search = new DbSearchWindow();

    //Variables that hold the data tables
    DataTable otuMetadata = null;
    DataTable sampleMetadata = null;
    DataTable otuSampleMap = null;
    DataTable database = null;

    //Holds the current coloring information
    TreeMap<Object, Color> colorMap = new TreeMap<Object, Color>();
    DataTable currTable = null;
    int colorColumnIndex = -1;
    int lineWidthColumnIndex = -1;

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
        
        pane.setLayout(new BorderLayout());
        
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
        
        databasePanel.setLayout(new BorderLayout());
        databaseScrollPane = new JScrollPane(databaseTable);
        databasePanel.add(databaseScrollPane, BorderLayout.CENTER);
        databaseBottomPanel.setLayout(new GridLayout(1,2));
        //databaseBottomPanel.add(databaseStatus);
        db_conn.connect_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                connectButtonPressed();
            }
        });
        databaseBottomPanel.add(db_conn);

        databaseTabPane.addTab("Connect", databaseBottomPanel);
        databaseTabPane.addTab("Search", db_search);
        
        db_search.searchButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e){
               searchButtonPressed();
           }
        });
        db_search.resetButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e){
               resetButtonPressed();
           } 
        });
        
        databaseTabPane.setEnabledAt(1, false);

        databaseTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        dataPane.addTab("Database", databasePanel);
        //dataPane.add(databaseTabPane, BorderLayout.SOUTH);
        
        otuMetadataTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        //otuMetadataTable.setAutoCreateRowSorter(true);
        otuMetadataScrollPane = new JScrollPane(otuMetadataTable);
        dataPane.addTab("OTU Metadata", otuMetadataScrollPane);

        otuSampleMapTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        //otuSampleMapTable.setAutoCreateRowSorter(true);
        otuSampleMapScrollPane = new JScrollPane(otuSampleMapTable);
        dataPane.addTab("OTU-Sample Map", otuSampleMapScrollPane);

        sampleMetadataTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        //sampleMetadataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sampleMetadataTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        sampleMetadataTable.setCellSelectionEnabled(true);
        //sampleMetadataTable.setAutoCreateRowSorter(true);
        sampleMetadataScrollPane = new JScrollPane(sampleMetadataTable);
        dataPane.addTab("Sample Metadata", sampleMetadataScrollPane);
        dataPane.setSelectedIndex(1);
        dataPane.setEnabledAt(0, false);
        
        dataPanel.setLayout(new BorderLayout());
        dataPanel.add(dataPane, BorderLayout.CENTER);
        //databaseTabPane.setEnabled(false);
        //dataPanel.add(databaseTabPane, BorderLayout.SOUTH);
        //tabbedPane.addTab("Data", dataPanel);

        //set up toolbar area
        toolbarPane.setLayout(new FlowLayout());
        toolbarPane.add(windowToolbar);
        
        pane.add(toolbarPane, BorderLayout.NORTH);
        
        //add them to the split pane
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, colorPanel, dataPanel);
        pane.add(splitPane, BorderLayout.CENTER);
        
        //the following is required to make sure th GLContext is created, or else resizing the 
        //window will result in program freezes
        //tabbedPane.setSelectedIndex(0);
     }
     
     public void searchButtonPressed() {
         int rowIndexStart = sampleMetadataTable.getSelectedRow();
         if(rowIndexStart != -1)
         {
             int rowIndexEnd = sampleMetadataTable.getSelectionModel().getMaxSelectionIndex();
             int colIndexStart = sampleMetadataTable.getSelectedColumn();
             int colIndexEnd = sampleMetadataTable.getColumnModel().getSelectionModel().getMaxSelectionIndex();
             String[] headers = sampleMetadata.getColumnNames().toArray(new String[0]);
             String temp = "";
             ArrayList<String> ops = new ArrayList<String>();
             // Check each cell in the range
             for (int r=rowIndexStart; r<=rowIndexEnd; r++) {
                 for (int c=colIndexStart; c<=colIndexEnd; c++) {
                     if (sampleMetadataTable.isCellSelected(r, c)) {
                         // cell is selected
                         temp = "";
                         temp += headers[c] + " = ";
                         temp += "\'" + sampleMetadataTable.getValueAt(r,c).toString() + "\'";
                         ops.add(temp);
                     }
                 }
             }

             Set<String> setOps = new HashSet<String>(ops);
             String[] setOpsarry = new String[setOps.size()];
             setOps.toArray(setOpsarry);
         
             Boolean useor = true;
             if(db_search.andRadioButton.isSelected() == true)
                 useor = false;

             db_conn.c.setData(setOpsarry, useor);
             getMetadataFromConn();
         }
         else
             JOptionPane.showMessageDialog(null, "ERROR: no metadata columns are selected.", "Error", JOptionPane.ERROR_MESSAGE);
      }
     
     public void getMetadataFromConn() {
         treeWindow.tree.noLoop();
         //tabbedPane.setSelectedIndex(0);
         dataPane.setSelectedIndex(3);
         sampleMetadata = new DataTable(db_conn.c);
         sampleMetadataTable.setModel(new SparseTableModel(sampleMetadata.getData(),
         sampleMetadata.getColumnNames()){
         //make it so the user can't edit the cells manually
         public boolean isCellEditable(int rowIndex, int colIndex) {
             return false;
             }
         });
         if (currTable == otuMetadata) {
             treeWindow.removeColor();
         }
         sampleMetadataTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
         //sampleMetadataScrollPane = new JScrollPane(sampleMetadataTable);
         mainMenu.resetColorBySampleMenu();
         treeWindow.tree.loop();
     }
     
     public void setDatabaseTables() {
          //tabbedPane.setSelectedIndex(0);
          dataPane.setSelectedIndex(0);
          database = new DataTable(db_conn.c);
          databaseTable.setModel(new SparseTableModel(sampleMetadata.getData(),
          database.getColumnNames()){
          //make it so the user can't edit the cells manually
          public boolean isCellEditable(int rowIndex, int colIndex) {
              return false;
              }
          });
      }
     
     public void resetButtonPressed() {
         db_conn.c.reset();
         db_conn.c.setData();
         getMetadataFromConn();
     }
     
     public void connectButtonPressed() {
         if(db_conn.connect_button.getText() == "Connect")
         {
             if(connectToDB())
             {
                 db_conn.db_name_field.disable();
                 db_conn.un_field.disable();
                 db_conn.pw_field.disable();
                 db_conn.connect_button.setText("Disconnect");
                 resetButtonPressed();
                 databaseTabPane.setEnabledAt(1, true);
             }
             else
                 JOptionPane.showMessageDialog(null, "ERROR: could not connect to database.", "Error", JOptionPane.ERROR_MESSAGE);
             
         }
         else
         {
             db_conn.c.close_connection();
             db_conn.db_name_field.enable();
             db_conn.un_field.enable();
             db_conn.pw_field.enable();
             db_conn.connect_button.setText("Connect");
             databaseTabPane.setEnabledAt(1, false);
         }
     }

     public Boolean connectToDB() {
         String url = db_conn.db_name_field.getText();
         String un = db_conn.un_field.getText();
         String pw = db_conn.pw_field.getText();

        db_conn.c = new mysqlConnect(un,pw,url);
        Boolean success = db_conn.c.makeConnection();
        if(success)
            db_conn.c.setData();
        
        return success;
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
     
     public void resetLineWidths() {
         if (currTable != null && currTable == otuMetadata) {
             treeWindow.resetLineWidthsByOtu();
         } else if (currTable != null && currTable == sampleMetadata) {
             treeWindow.resetLineWidthsBySample();
         } else {
             //it's null; don't do anything
         }     
     }

     public void recolor() {
         if (currTable != null && currTable == otuMetadata) {
             treeWindow.recolorTreeByOtu();
             pcoaWindow.recolorPcoaByOtu();
         } else if (currTable != null && currTable == sampleMetadata) {
             treeWindow.recolorTreeBySample();
             pcoaWindow.recolorPcoaBySample();
         } else {
             //it's null; don't do anything
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