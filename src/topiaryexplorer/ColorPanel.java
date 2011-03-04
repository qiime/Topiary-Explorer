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
 * Panel that coordinates the color key table.
 *
 * @author Meg Pirrung &lt;&gt;
 * @version $Rev$
 **/
public class ColorPanel extends JPanel{
    private MainFrame frame = null;
    private JScrollPane colorKeyScrollPane = new JScrollPane();  
    private JTable colorKeyTable = new JTable();
    private int elementType = 0;
    
    //Holds the current coloring information
    private HashMap colorMap = new HashMap();
    private int colorColumnIndex = -1;
    
	// {{{ ColorPanel constructor
    /**
     * Creates a color panel of a certain element type with a reference to the main frame.
     */
    public ColorPanel(MainFrame _frame, int _elementType) {
        super();
        frame = _frame;
        elementType = _elementType;

	    ColorTableModel model = new ColorTableModel();
		TableSorter sorter = new TableSorter(model, colorKeyTable.getTableHeader());
        
        
        colorKeyTable.setModel(sorter);
        colorKeyTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        colorKeyTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        colorKeyTable.setCellSelectionEnabled(true);
        colorKeyScrollPane = new JScrollPane(colorKeyTable);
        this.setPreferredSize(new Dimension(270,600));
        colorKeyScrollPane.setPreferredSize(new Dimension(250,580));
        add(colorKeyScrollPane);
    }
	// }}}
	/**
	* Returns the current mapping for the color blocks
	* and their associated metadata values.
	**/
	HashMap getColorMap(){ return colorMap; }
	/**
	* Sets the colormap.
	**/
	void setColorMap(HashMap c) { colorMap = c; }
	/**
	* Gets the colorKeyTable corresponding to this color panel.
	* @return the current color key table.
	**/
	JTable getColorKeyTable() { return colorKeyTable; }
	/**
	* Gets the index of the currently selected value in the color map.
	* @return the index of the currently selected value.
	**/
	int getColorColumnIndex() { return colorColumnIndex; }
	/**
	* Sets the index of the currently selected value
	**/
	void setColorColumnIndex(int i) { colorColumnIndex = i; }
	
    /**
    * Syncs the colorKeyTable with ColorMap
    **/
     void syncColorKeyTable() {
         // data is: name, color, selected
         ArrayList<ArrayList<Object>> data = new ArrayList<ArrayList<Object>>();
         // set up the rows of the color map
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

         ColorTableModel model = new ColorTableModel(data, colNames);
 		 TableSorter sorter = new TableSorter(model, colorKeyTable.getTableHeader());

         colorKeyTable.setModel(sorter);
         colorKeyTable.setDefaultRenderer(Color.class, new ColorRenderer(true));
         colorKeyTable.setDefaultEditor(Color.class, new ColorEditor());
         colorKeyTable.setSelectionBackground(new Color(255,255,255));
         colorKeyTable.setSelectionForeground(new Color(0,0,0));
         colorKeyTable.getColumnModel().getColumn(0).setPreferredWidth(190);
         colorKeyTable.getColumnModel().getColumn(1).setPreferredWidth(20);
         colorKeyTable.getColumnModel().getColumn(2).setPreferredWidth(20);
         colorKeyTable.setDragEnabled(true);

         ((TableSorter)colorKeyTable.getModel()).getTableModel().addTableModelListener(new TableModelListener() {
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
                     if(elementType == 0)
                         frame.recolorBranches();
                     else if(elementType == 1)
                         frame.recolorLabels();
                }
            }
        });
     }

     /**
      * Syncs the ColorMap with the colorKeyTable
      */
     void syncColorMap() {
        colorMap.clear();
        for (ArrayList<Object> row : ((ColorTableModel)((TableSorter)colorKeyTable.getModel()).getTableModel()).getData()) {
            colorMap.put(row.get(0), (Color)row.get(1));
        }
     }
     
     /**
     * Create a gradient using the colors selected in the colormap
     **/
     void interpolateColors() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
/*      ArrayList<ArrayList<Object>> data = ((ColorTableModel)((TableSorter)colorKeyTable.getModel()).getTableModel()).getData();*/
        TableSorter sorterModel = (TableSorter)colorKeyTable.getModel();
 		int first = -1;
 		int second = -1;
 		//find the first color
 		for (int i = 0; i < sorterModel.getRowCount(); i++) {
 			if ((Boolean) sorterModel.getValueAt(i,2) == true) {
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
 			for (int i = first+1; i < sorterModel.getRowCount(); i++) {
 				if ((Boolean) sorterModel.getValueAt(i,2) == true) {
 				second = i;
 				break;
 				}
 			}
 			//have we reached the end?
 			if (second == -1) {
/*              ((ColorTableModel)((TableSorter)colorKeyTable.getModel()).getTableModel()).setData(data);*/
                 colorKeyTable.repaint();
                 syncColorMap();
                 if(elementType == 0)
                      frame.recolorBranches();
                  else if(elementType == 1)
                      frame.recolorLabels();
                 setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                 return;
 			}
 			//interpolate
 			Color firstColor = (Color) sorterModel.getValueAt(first,1);
 			Color secondColor = (Color) sorterModel.getValueAt(second,1);
/*          Color firstColor = (Color) data.get(first).get(1);*/
/*          Color secondColor = (Color) data.get(second).get(1);*/
 			for (int i = first+1; i < second; i++) {
 				//here's the interpolation
 				float frac = (i-first)*(1.0f/(second-first));
 				Color c = new Color((1-frac)*firstColor.getRed()/255.0f + frac*secondColor.getRed()/255.0f,
 					(1-frac)*firstColor.getGreen()/255.0f + frac*secondColor.getGreen()/255.0f,
 					(1-frac)*firstColor.getBlue()/255.0f + frac*secondColor.getBlue()/255.0f);
/*                 data.get(i).set(1, c);*/
                 sorterModel.setValueAt(c, i, 1);
 			}
 		}
      }

}
