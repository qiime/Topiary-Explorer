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
 * MainFrame is the primary JFrame that is displayed.
 */

public class MainFrame extends JFrame {
    
    FileOpenService fos;
    FileSaveService fss;
    ExtendedService es;
    BasicService bs;
    
    String DATABASE_URL = ""; // jdbc:mysql://127.0.0.1/topiarytool
    String DATABASE_UN = "";  // root
    String DATABASE_PW = "";  // desudesu

    FileContents treeFile = null;
    FileContents otuMetadataFile = null;
    FileContents otuSampleMapFile = null;
    FileContents sampleMetadataFile = null;

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
    JTable databaseTable = new JTable(new SparseTableModel());  
    JTable otuMetadataTable = new JTable(new SparseTableModel());
    JTable otuSampleMapTable = new JTable(new SparseTableModel());
    JTable sampleMetadataTable = new JTable(new SparseTableModel());
    JTable colorKeyTable = new JTable();
    JButton back = new JButton("<<");
    JButton showData = new JButton("Show Table");
    JButton setAs = new JButton("Set As...");
    JButton interpolateButton = new JButton("Interpolate");
    NewProjectDialog newProjectChooser = null;
    JLabel databaseStatus = new JLabel("Database not connected.");
    TopiaryMenu mainMenu = new TopiaryMenu(this);
    
    Container toolbarPane = new Container();
    WindowViewToolbar windowToolbar = new WindowViewToolbar(this);
    
    TreeWindow treeWindow = new TreeWindow(this);
    PcoaWindow pcoaWindow = new PcoaWindow(this);
    ConsoleWindow consoleWindow = new ConsoleWindow(this);
    
    DbConnectWindow db_conn = new DbConnectWindow(this);
    DbSearchWindow db_search = new DbSearchWindow(this);

    //Variables that hold the data tables
    DataTable otuMetadata = new DataTable();
    DataTable sampleMetadata = new DataTable();
    DataTable otuSampleMap = new DataTable();
    DataTable database = new DataTable();

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
        databaseTopPanel.setLayout(new GridLayout(1,5));
        back.setEnabled(false);
        back.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                showAllTables();
            }
        });
        databaseTopPanel.add(back);
        databaseTopPanel.add(new JLabel(""));
        databaseTopPanel.add(new JLabel(""));
        showData.setEnabled(false);
        showData.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                showSelectedTable();
            }
        });
        
        databaseTopPanel.add(showData);
        setAs.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                setDatabaseResultsAs();
            }
        });
        setAs.setEnabled(false);
        databaseTopPanel.add(setAs);
        databasePanel.add(databaseTopPanel, BorderLayout.NORTH);
        
        databaseScrollPane = new JScrollPane(databaseTable);
        databasePanel.add(databaseScrollPane, BorderLayout.CENTER);
        databaseBottomPanel.setLayout(new BorderLayout());
        databaseBottomPanel.add(databaseStatus, BorderLayout.SOUTH);
        databaseBottomPanel.add(db_conn, BorderLayout.CENTER);

        databaseTabPane.addTab("Connect", databaseBottomPanel);
        databaseTabPane.addTab("Search", db_search);
        
        databaseTabPane.setEnabledAt(1, false);

        databaseTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        databaseTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        databaseTable.setCellSelectionEnabled(true);

        //databasePane.addTab("Database", dataPanel);
        databasePanel.add(databaseTabPane, BorderLayout.SOUTH);
        dataPane.addTab("Database", databasePanel);
        
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
        dataPane.setSelectedIndex(0);
        
        dataPanel.setLayout(new BorderLayout());
        dataPanel.add(dataPane, BorderLayout.CENTER);

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
        
        //Set up the jnlp services
        try { 
        	fos = (FileOpenService)ServiceManager.lookup("javax.jnlp.FileOpenService");
    	} catch (UnavailableServiceException e) { fos=null; }
    	try { 
        	fss = (FileSaveService)ServiceManager.lookup("javax.jnlp.FileSaveService");
    	} catch (UnavailableServiceException e) { fss=null; }
    	try { 
        	es = (ExtendedService)ServiceManager.lookup("javax.jnlp.ExtendedService");
    	} catch (UnavailableServiceException e) { es=null; }   
    	try { 
        	bs = (BasicService)ServiceManager.lookup("javax.jnlp.BasicService");
    	} catch (UnavailableServiceException e) { bs=null; }   
     }
     
     public void resetDatabaseTable() {
         database = new DataTable(db_conn.c);
         SparseTableModel model = new SparseTableModel(database.getData(),
   		 	database.getColumnNames());
   		 TableSorter sorter = new TableSorter(model, databaseTable.getTableHeader());
   		 databaseTable.setModel(sorter);
     }
     
     public void showAllTables() {
         db_conn.c.getAvailableTables();
         resetDatabaseTable();
  		 back.setEnabled(false);
  		 setAs.setEnabled(false);
  		 showData.setEnabled(true);
     }
     
     public void setDatabaseResultsAs() {
         Object[] possibilities = {"Otu Sample Map", "Otu Metadata", "Sample Metadata"};
         String tableName = (String)JOptionPane.showInputDialog(
                             this,
                             "Use database results in which table?",
                             "Customized Dialog",
                             JOptionPane.PLAIN_MESSAGE,
                             null,
                             possibilities,
                             possibilities[1]);

         //If a string was returned, say so.
         if (tableName != null) {
             SparseTableModel model = null;
             TableSorter sorter = null;
             switch(tableName.charAt(4))
             {
                 case 'S':
                    otuSampleMap = new DataTable(database.toStrings());
                  
                    model = new SparseTableModel(otuSampleMap.getData(),
					 otuSampleMap.getColumnNames());
					 sorter = new TableSorter(model, otuSampleMapTable.getTableHeader());
					 otuSampleMapTable.setModel(sorter);
					 dataPane.setSelectedIndex(2);
                    break;
                 case 'M':
                  otuMetadata = new DataTable(database.toStrings());

				     model = new SparseTableModel(otuMetadata.getData(),
					 otuMetadata.getColumnNames());
					 sorter = new TableSorter(model, otuMetadataTable.getTableHeader());
					 otuMetadataTable.setModel(sorter);
					 mainMenu.resetColorByOtuMenu();
                      mainMenu.resetLineWidthOtuMenu();
                      mainMenu.resetCollapseByMenu();
                      dataPane.setSelectedIndex(1);
                    break;
                 case 'l':
                 sampleMetadata = new DataTable(database.toStrings());
                  
                  model = new SparseTableModel(sampleMetadata.getData(),
					 sampleMetadata.getColumnNames());
					  sorter = new TableSorter(model, sampleMetadataTable.getTableHeader());
					 sampleMetadataTable.setModel(sorter);
					 mainMenu.resetColorBySampleMenu();
                      mainMenu.resetLineWidthSampleMenu();
                      dataPane.setSelectedIndex(3);
                    break;
                 default:
             }
             back.setEnabled(true);
             showData.setEnabled(false);
         }
     }
     
     public void showSelectedTable() {
         String tableName = "";
         int rowIndexStart = databaseTable.getSelectedRow();
          if(rowIndexStart != -1)
          {
              int rowIndexEnd = databaseTable.getSelectionModel().getMaxSelectionIndex();
              int colIndexStart = databaseTable.getSelectedColumn();
              int colIndexEnd = databaseTable.getColumnModel().getSelectionModel().getMaxSelectionIndex();
              String[] headers = database.getColumnNames().toArray(new String[0]);
              String temp = "";
              ArrayList<String> ops = new ArrayList<String>();
              // Check each cell in the range
              for (int r=rowIndexStart; r<=rowIndexEnd; r++) {
                  for (int c=colIndexStart; c<=colIndexEnd; c++) {
                      if (databaseTable.isCellSelected(r, c)) {
                          // cell is selected
                          tableName = databaseTable.getValueAt(r,c).toString();
                      }
                  }
              }
              if(db_conn.c.getDataFromTable(tableName))
              {
                 resetDatabaseTable();
           		 setAs.setEnabled(true);
           		 back.setEnabled(true);
           		 showData.setEnabled(false);
       		 }
       		 else
       		 {
       		     JOptionPane.showMessageDialog(null, "ERROR: problem loading data from table("+tableName+").", "Error", JOptionPane.ERROR_MESSAGE);
       		 }
         }
         else
         {
             JOptionPane.showMessageDialog(null, "ERROR: no table selected.", "Error", JOptionPane.ERROR_MESSAGE);
         }
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