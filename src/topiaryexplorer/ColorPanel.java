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
    private String currentValue = "";
    
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
	
	String getCurrentValue() { return currentValue; }
	
	void setCurrentValue(String s) { currentValue = s; }
	
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
         colNames.add(currentValue);
         // currentValue = "Category";
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
 			
 			if(firstColor.equals(secondColor))
 			{
 			    for (int i = first+1; i < second; i++) 
 			        sorterModel.setValueAt(firstColor, i, 1);
		        
 			}else
 			{
 			    float frac;
 			    float one_minus_frac;
 			    float inv_sec_first = 1.0f/(second - first);
 			    
                float first_red_adj = firstColor.getRed() / 255.0f;
                float first_green_adj = firstColor.getGreen() / 255.0f;
                float first_blue_adj = firstColor.getBlue() / 255.0f;
                
                float second_red_adj = secondColor.getRed() / 255.0f;
                float second_green_adj = secondColor.getGreen() / 255.0f;
                float second_blue_adj = secondColor.getBlue() / 255.0f;
    
     			for (int i = first+1; i < second; i++) {
     				//here's the interpolation
     				frac = (i-first)*inv_sec_first;
                    one_minus_frac = 1 - frac;
                    Color c = new Color(one_minus_frac*first_red_adj+ frac*second_red_adj,
one_minus_frac*first_green_adj +
frac*second_green_adj,
one_minus_frac*first_blue_adj + frac*second_blue_adj);
                     sorterModel.setValueAt(c, i, 1);
                     // colorKeyTable.repaint();
     			}
		    }
 		}
      // setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      }

}
