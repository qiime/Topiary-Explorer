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

/**
 * MainFrame is the primary JFrame that is displayed.
 */

public class MainFrame extends JFrame {
    
    FileOpenService fos;
    FileSaveService fss;
    ExtendedService es;
    BasicService bs;
    String dir_path = "";//(new File(".")).getCanonicalPath();
    
    String DATABASE_URL = ""; // jdbc:mysql://127.0.0.1/topiarytool
    String DATABASE_UN = "";  // root
    String DATABASE_PW = "";  // desudesu

    FileContents treeFile = null;
    FileContents otuMetadataFile = null;
    FileContents otuSampleMapFile = null;
    FileContents sampleMetadataFile = null;

    //INITIALIZE GUI OBJECTS
    JSplitPane splitPane = null;
/*    JPanel colorPanel = new JPanel();*/
    JTabbedPane colorPane = new JTabbedPane(JTabbedPane.TOP);
    JPanel splitPaneMainPanel = new JPanel();
    JPanel databasePanel = new JPanel();
    JPanel databaseBottomPanel = new JPanel();
    JPanel databaseTopPanel = new JPanel();
    JPanel searchPanel = new JPanel();
    JPanel dataPanel = new JPanel();
    JToolBar colorBar = new JToolBar(SwingConstants.VERTICAL);
    JTabbedPane dataPane = new JTabbedPane();
    //JTabbedPane tabbedPane = new JTabbedPane();
    JTabbedPane databaseTabPane = new JTabbedPane();
    JScrollPane databaseScrollPane = new JScrollPane();
    JScrollPane otuMetadataScrollPane = new JScrollPane();
    JScrollPane otuSampleMapScrollPane = new JScrollPane();
    JScrollPane sampleMetadataScrollPane = new JScrollPane();
    /*JScrollPane colorKeyScrollPane = new JScrollPane();  */
    JTable databaseTable = new JTable(new SparseTableModel());  
    JTable otuMetadataTable = new JTable(new SparseTableModel());
    JTable otuSampleMapTable = new JTable(new SparseTableModel());
    JTable sampleMetadataTable = new JTable(new SparseTableModel());
/*    JTable branchColorKeyTable = new JTable();*/
    JButton back = new JButton("<<");
    JButton showData = new JButton("Show Table");
    JButton setAs = new JButton("Set As...");
    JButton interpolateButton = new JButton("Interpolate");
    NewProjectDialog newProjectChooser = null;
    JLabel databaseStatus = new JLabel("Database not connected.");
    TopiaryMenu mainMenu = new TopiaryMenu(this);
    
    Container toolbarPane = new Container();
    WindowViewToolbar windowToolbar = new WindowViewToolbar(this);
    
    ArrayList<TreeWindow> treeWindows = new ArrayList<TreeWindow>();
/*    TreeWindow treeWindow = null;*/
    PcoaWindow pcoaWindow = new PcoaWindow(this);
    ConsoleWindow consoleWindow = new ConsoleWindow(this);
    
    DbConnectWindow db_conn = new DbConnectWindow(this);
    DbSearchWindow db_search = new DbSearchWindow(this);

    //Variables that hold the data tables
    DataTable otuMetadata = new DataTable();
    DataTable sampleMetadata = new DataTable();
    DataTable otuSampleMap = new DataTable();
    DataTable database = new DataTable();


    ColorPanel branchColorPanel;// = null;//new ColorPanel(this);
    ColorPanel labelColorPanel;// = null;
    /*//Holds the current coloring information
        TreeMap<Object, Color> branchColorMap = new TreeMap<Object, Color>();
        TreeMap<Object, Color> nodebranchColorMap = new TreeMap<Object, Color>();*/
    DataTable currTable = null;
    int lineWidthColumnIndex = -1;

    //Holds the currently-clicked node
    Node clickedNode = null;
    String labelValue = "";
    String branchValue = "";

    /**
     * Default constructor.  Sets up the GUI.
     */
     public MainFrame() {
        super("TopiaryExplorer");
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //get content pane
        Container pane = getContentPane();
        
        pane.setLayout(new BorderLayout());
        
        try{
        dir_path = (new File(".")).getCanonicalPath();
        }
        catch(IOException e)
        {}
        
        //set up the menu bar
        setJMenuBar(mainMenu);

        //set up the color panel
        interpolateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                ((ColorPanel)colorPane.getSelectedComponent()).interpolateColors();
            }
        });
        interpolateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        colorPane.setPreferredSize(new Dimension(220,600));
        branchColorPanel = new ColorPanel(this, 0);
        labelColorPanel = new ColorPanel(this, 1);
        colorPane.add("Branches", branchColorPanel);
        colorPane.add("Labels", labelColorPanel);
        colorBar.add(new JLabel("Color Key"));
        colorBar.add(colorPane);
        colorBar.add(interpolateButton);
        
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
        otuMetadataTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        otuMetadataTable.setCellSelectionEnabled(true);
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
/*        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, colorBar, dataPanel);*/
        pane.add(dataPanel, BorderLayout.CENTER);
        pane.add(colorBar, BorderLayout.WEST);
        
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
    	
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
     }
     
     public void newTreeWindow() {
         setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
         TreeWindow tempTreeWindow = new TreeWindow(this);
         tempTreeWindow.loadTree();
         treeWindows.add(tempTreeWindow);
         tempTreeWindow.setTitle("Tree "+treeWindows.size());
         resetOtuMenus();
         resetSampleMenus();
         setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
     }
     
     public void newTreeWindow(String treeString) {
          setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
          TreeWindow tempTreeWindow = new TreeWindow(this);
          tempTreeWindow.loadTree(treeString);
          treeWindows.add(tempTreeWindow);
          tempTreeWindow.setTitle("Tree "+treeWindows.size());
          resetOtuMenus();
          resetSampleMenus();
          
          if(labelValue != "")
            recolorLabels();
            
          if(branchValue != "")
            recolorBranches();
          
          setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      }
      
      public void newTreeWindow(FileContents treeFile) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            TreeWindow tempTreeWindow = new TreeWindow(this);
            tempTreeWindow.loadTree(treeFile);
            treeWindows.add(tempTreeWindow);
            tempTreeWindow.setTitle("Tree "+treeWindows.size());
            resetOtuMenus();
            resetSampleMenus();
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
        
    public void setOtuMetadata(ArrayList<String> data) {
        otuMetadata = new DataTable(data);

	     SparseTableModel model = new SparseTableModel(otuMetadata.getData(),
		 otuMetadata.getColumnNames());
		 TableSorter sorter = new TableSorter(model, otuMetadataTable.getTableHeader());
		 otuMetadataTable.setModel(sorter);
         
/*         frame.consoleWindow.update("Loaded OTU metadata.");*/
         resetOtuMenus();
         dataPane.setSelectedIndex(1);
    }
    
    public void setOtuSampleMap(ArrayList<String> data) {
         otuSampleMap = new DataTable(data);
         
         SparseTableModel model = new SparseTableModel(otuSampleMap.getData(),
		 otuSampleMap.getColumnNames());
		 TableSorter sorter = new TableSorter(model, otuSampleMapTable.getTableHeader());
		 otuSampleMapTable.setModel(sorter);
		 
/*         consoleWindow.update("Loaded OTU to sample map");*/
         dataPane.setSelectedIndex(2);
    }
    
    public void setSampleMetadata(ArrayList<String> data) {
        sampleMetadata = new DataTable(data);
         
         SparseTableModel model = new SparseTableModel(sampleMetadata.getData(),
		 sampleMetadata.getColumnNames());
		 TableSorter sorter = new TableSorter(model, sampleMetadataTable.getTableHeader());
		 sampleMetadataTable.setModel(sorter);
		 
/*         frame.consoleWindow.update("Loaded sample metadata.");*/
         resetSampleMenus();
         dataPane.setSelectedIndex(3);
    }
     
     public void resetOtuMenus() {
         for(TreeWindow w : treeWindows)
         {
              w.resetLineWidthOtuMenu();
              w.resetCollapseByMenu();
              w.branchMenu.colorBy.resetColorByOtuMenu();
              w.treeEditToolbar.nodeEditPanel.colorByMenu.resetColorByOtuMenu();
          }
     }
     
     public void resetSampleMenus() {
          for(TreeWindow w : treeWindows)
          {
               w.resetLineWidthSampleMenu();
               w.resetCollapseByMenu();
               w.branchMenu.colorBy.resetColorBySampleMenu();
               w.treeEditToolbar.nodeEditPanel.colorByMenu.resetColorBySampleMenu();
               w.resetLineWidthSampleMenu();
           }
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
/*                   resetColorByOtuMenus();*/
                     resetOtuMenus();
                     dataPane.setSelectedIndex(1);
                     break;
                 case 'l':
                     sampleMetadata = new DataTable(database.toStrings());
                  
                     model = new SparseTableModel(sampleMetadata.getData(),
    				 sampleMetadata.getColumnNames());
    				 sorter = new TableSorter(model, sampleMetadataTable.getTableHeader());
    				 sampleMetadataTable.setModel(sorter);
                     resetSampleMenus();
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
     
     public void resetLineWidths() {
         if (currTable != null && currTable == otuMetadata) {
             for(int i = 0; i < treeWindows.size(); i++)
                 treeWindows.get(i).resetLineWidthsByOtu();
         } else if (currTable != null && currTable == sampleMetadata) {
             for(int i = 0; i < treeWindows.size(); i++)
                  treeWindows.get(i).resetLineWidthsByOtu();
         } else {
             //it's null; don't do anything
         }     
     }

     public void recolorBranches() {
         this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
         if (currTable != null && currTable == otuMetadata) {
             for(int i = 0; i < treeWindows.size(); i++)
                  treeWindows.get(i).recolorBranchesByOtu();
         } else if (currTable != null && currTable == sampleMetadata) {
             for(int i = 0; i < treeWindows.size(); i++)
                  treeWindows.get(i).recolorBranchesBySample();
         } else {
             //it's null; don't do anything
         }
         this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
     }
     
     public void recolorLabels() {
          this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
          if (currTable != null && currTable == otuMetadata) {
              for(int i = 0; i < treeWindows.size(); i++)
                   treeWindows.get(i).recolorLabelsByOtu();
              pcoaWindow.recolorPcoaByOtu();
          } else if (currTable != null && currTable == sampleMetadata) {
              for(int i = 0; i < treeWindows.size(); i++)
                   treeWindows.get(i).recolorLabelsBySample();
              pcoaWindow.recolorPcoaBySample();
          } else {
              //it's null; don't do anything
          }
          this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      }
}