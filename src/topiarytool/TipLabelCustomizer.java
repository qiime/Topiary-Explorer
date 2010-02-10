package topiarytool;

import java.applet.*;
import java.lang.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

/**
 *
 * @author megumi
 */
public class TipLabelCustomizer extends JFrame {
    MainFrame frame = null;
    JLabel title = new JLabel("Choose metadata columns to include in node labels:");
    JPanel mainPanel = new JPanel();
    JPanel buttonPanel = new JPanel();
    JPanel selectionPanel = new JPanel();
    JButton okButton = new JButton("OK");
    JButton cancelButton = new JButton("Cancel");
    JLabel selectlabel = new JLabel("Select:");
    JButton allclrButton = new JButton("ALL");
    JButton invertButton = new JButton("INV");
    JScrollPane selectionPane = new JScrollPane();
    JLabel searchLabel = new JLabel("Set tip labels with select options");
    JCheckBox item = new JCheckBox();
    ArrayList<JCheckBox> itemlist = new ArrayList<JCheckBox>();
    JPanel optionspanel = new JPanel();
    JLabel delimlabel = new JLabel("Delimter:");
    JTextArea delim = new JTextArea(",    ");
    JScrollBar scroller = new JScrollBar();
    
    public TipLabelCustomizer(MainFrame _frame) {
        frame = _frame;
        this.setSize(new Dimension(400,300));
        this.setTitle("Choose metadata columns to include in node labels");
        mainPanel.setLayout(new BorderLayout());
        ArrayList<String> columns = frame.sampleMetadata.getColumnNames();
        for(String c : columns)
        {
            item = new JCheckBox(c);
            itemlist.add(item);
            selectionPanel.add(item);
        }
/*        selectionPane.add(selectionPanel);*/
        mainPanel.add(selectionPanel, BorderLayout.CENTER);
        optionspanel.setLayout(new GridLayout(3,3));
        optionspanel.add(selectlabel);
        allclrButton.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                   allclrbuttonpressed();
               }
        });
        invertButton.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                  //get the category to color by
                  invbuttonpressed();
              }
        });
        optionspanel.add(allclrButton);
        optionspanel.add(invertButton);
        optionspanel.add(new JLabel(""));
        optionspanel.add(delimlabel);
        optionspanel.add(delim);
        optionspanel.add(new JLabel(""));
        optionspanel.add(cancelButton);
        optionspanel.add(okButton);
/*        mainPanel.add(delimpanel, BorderLayout.CENTER);*/
/*        mainPanel.add(scroller, BorderLayout.CENTER);*/
        mainPanel.add(optionspanel, BorderLayout.SOUTH);

        this.add(mainPanel, BorderLayout.CENTER);
        this.show();
    }
    
    public void allclrbuttonpressed() {
        if(allclrButton.getText() == "ALL")
        {
            for(JCheckBox b :itemlist)
                b.setSelected(true);
            allclrButton.setText("CLR");
        }
        else
        {
            for(JCheckBox b :itemlist)
                b.setSelected(false);
            allclrButton.setText("ALL");
        }
    }  
    
    public void invbuttonpressed() {
        for(JCheckBox b :itemlist)
            b.setSelected(!(b.isSelected()));
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
