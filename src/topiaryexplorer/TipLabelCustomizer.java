package topiaryexplorer;

import java.applet.*;
import java.lang.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.border.*;

/**
 *
 * @author megumi
 */
public class TipLabelCustomizer extends JFrame {
    MainFrame frame = null;
    TreeWindow treeWindow = null;
    JLabel title = new JLabel("Choose metadata columns to include in node labels:");
    JPanel mainPanel = new JPanel();
    JPanel delimPanel = new JPanel();
/*    JPanel buttonPanel = new JPanel();
    JPanel selectionPanel = new JPanel();*/
    JButton okButton = new JButton("Set Labels");
    JButton cancelButton = new JButton("Cancel");
    JLabel selectlabel = new JLabel("Select:");
    JButton allclrButton = new JButton("Select All");
    JButton invertButton = new JButton("Invert Selection");
    JScrollPane selectionPane = new JScrollPane();
    JLabel searchLabel = new JLabel("Set tip labels with select options");
    JCheckBox item = new JCheckBox();
    ArrayList<JCheckBox> itemlist = new ArrayList<JCheckBox>();
    JPanel optionspanel = new JPanel();
    JLabel delimlabel = new JLabel("Separator:");
    JTextField delim = new JTextField(", ", 5);
    JScrollBar scroller = new JScrollBar();
    JScrollPane optionsPane = new JScrollPane();
    JList optionsTable = new JList();
    String[] metaTypes = {"OTU Metadata", "Sample Metadata"};
    JComboBox metaCombo = new JComboBox();
    Vector<Object> cheader = new Vector<Object>();
    Vector<Object> vals = new Vector<Object>();
    
    public TipLabelCustomizer(MainFrame _frame, TreeWindow _treeWindow, boolean otuMeta, boolean sampleMeta) {
        frame = _frame;
        treeWindow = _treeWindow;
        this.setSize(new Dimension(400,200));
        this.setLocation(frame.getWidth()/2, frame.getHeight()/2);
        this.setTitle("Choose metadata columns to include in node labels");
        this.setLayout(new BorderLayout());
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new TitledBorder("Set Tip Labels As"));
        
        if(otuMeta)
            metaCombo.addItem(metaTypes[0]);
        /*if(sampleMeta)
                    metaCombo.addItem(metaTypes[1]);*/
/*        mainPanel.add(metaCombo, BorderLayout.NORTH);*/

        
        optionsTable = new JList(frame.otuMetadata.getColumnNames().toArray());
        optionsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        optionsPane = new JScrollPane(optionsTable);
        
        mainPanel.add(optionsPane, BorderLayout.CENTER);
        
        optionspanel.setLayout(new GridLayout(4,1));
/*        optionspanel.add(selectlabel);*/
        allclrButton.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                   allclrbuttonpressed();
               }
        });
        invertButton.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                  invbuttonpressed();
              }
        });
        optionspanel.add(allclrButton);
        optionspanel.add(invertButton);
/*        optionspanel.add(new JLabel(""));*/
        delimPanel.add(delimlabel);
        delimPanel.add(delim);
        optionspanel.add(delimPanel);
/*        optionspanel.add(delim);*/
/*        optionspanel.add(new JLabel(""));*/
/*        optionspanel.add(new JLabel(""));*/
        okButton.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                  okbuttonpressed();
              }
        });
/*        optionspanel.add(okButton);*/
        mainPanel.add(optionspanel, BorderLayout.WEST);
        this.add(mainPanel, BorderLayout.CENTER);
        
        this.add(okButton, BorderLayout.SOUTH);
    }
    
    public void okbuttonpressed() {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        ArrayList<Object> ops = new ArrayList(Arrays.asList(optionsTable.getSelectedValues()));
        
        for(Node n :treeWindow.tree.getTree().getNodes()) {
            n.setDrawLabel(true);
            String name = "";
            ArrayList<String> vals = new ArrayList<String>();
            for(Object o : ops)
            {
                o = (String)o;
                int colIndex = 0;
                for (String val : frame.otuMetadata.getColumnNames()) {
                    if (val.equals(o)) {
                        break;
                    }
                    colIndex++;
                }
                int rowIndex = frame.otuMetadata.getRowNames().indexOf(n.getName());
                if (rowIndex == -1) {
/*                    JOptionPane.showMessageDialog(null, "ERROR: OTU ID "+n.getName()+" not found in OTU Metadata Table.", "Error", JOptionPane.ERROR_MESSAGE);*/
                    continue;
                }
                Object category = frame.otuMetadata.getValueAt(rowIndex, colIndex);
                if(category != null)
                    vals.add(category.toString());
            }
            if(vals.size() != 0){
                for(int i = 0; i < vals.size()-1; i++)
                    name += vals.get(i) + delim.getText();
                name += vals.get(vals.size()-1);
                n.setLabel(name);
            }
        }
        treeWindow.tree.checkBounds();
        treeWindow.tree.redraw();
        this.hide();
        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
    
    public void allclrbuttonpressed() {
        if(allclrButton.getText() == "Select All")
        {
            optionsTable.setSelectionInterval(0,frame.otuMetadata.getColumnNames().size());
            allclrButton.setText("Clear Selection");
        }
        else
        {
            optionsTable.clearSelection();
            allclrButton.setText("Select All");
        }
    }
    
    public void invbuttonpressed() {
         int[] selected = optionsTable.getSelectedIndices();
         optionsTable.setSelectionInterval(0,frame.otuMetadata.getColumnNames().size());
         for(int i : selected)
             optionsTable.removeSelectionInterval(i,i);         
    }

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
/*                new TipLabelCustomizer().setVisible(true);*/
            }
        });
    }

}
