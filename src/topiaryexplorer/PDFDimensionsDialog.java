package topiaryexplorer;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.text.*;
import java.text.*;

class PDFDimensionsDialog extends JDialog implements ActionListener, DocumentListener {

    private JOptionPane optionPane;
    
    private DecimalFormat df = new DecimalFormat("#.####");
    private JTextField xdim;
    private JTextField ydim;
    
    public double dims[] = {0, 0};

    public PDFDimensionsDialog(final Frame frame) {
        super(frame, true);
        this.setLocation(frame.getWidth()/2, frame.getHeight()/2);
        setTitle("Export Tree Image");
        
        TreeVis tree = ((TreeWindow)frame).tree;
        String treeLayout = tree.getTreeLayout();
        
       if (treeLayout.equals("Rectangular") || treeLayout.equals("Triangular")) {
          dims[0] = (tree.getXScale() * tree.getTree().depth()) + tree.getMargin() + tree.getTreeMargin();
          dims[1] = (tree.getYScale() * tree.getTree().getNumberOfLeaves()) + 2*tree.getMargin();
      } else if (treeLayout.equals("Radial") || treeLayout.equals("Polar")) {
          dims[0] = ((tree.getXScale() * tree.getTree().depth()) + tree.getMargin())*2;
          dims[1] = dims[0];
      }
      
     
        xdim = new JTextField();
        xdim.setText(df.format(dims[0]));
        xdim.setColumns(10);
        xdim.getDocument().addDocumentListener(this);
        xdim.getDocument().putProperty("name", "xdim");
        ydim = new JTextField();
        ydim.setText(df.format(dims[1]));
        ydim.setColumns(10);
        ydim.getDocument().addDocumentListener(this);
        ydim.getDocument().putProperty("name", "ydim");

       Container cp = getContentPane();
       cp.setLayout(new BoxLayout(cp, BoxLayout.PAGE_AXIS));
       JPanel j = new JPanel();
       j.setLayout(new BoxLayout(j, BoxLayout.PAGE_AXIS));
       
       JPanel j1 = new JPanel();
       j1.setLayout(new FlowLayout());
       j1.add(new JLabel("Horizontal:")); 
       j1.add(xdim);
       j.add(j1);
       
       j.add(j1);
       JPanel j2 = new JPanel();
       j2.setLayout(new FlowLayout());
       j2.add(new JLabel("Vertical:"));     
       j2.add(ydim);
       j.add(j2);
       
       cp.add(j);
       JButton cancelbutton = new JButton("Cancel");
       cancelbutton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
              dims[0] = 0;
              dims[1] = 0;
              dispose();
          }
       });
       JButton okbutton = new JButton("Export");
       okbutton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               dispose();
           }
       });
       JPanel j3 = new JPanel();
       j3.setLayout(new FlowLayout());
       j3.add(cancelbutton);
       j3.add(okbutton);
       cp.add(j3);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    public void change(DocumentEvent e) {
        String name = (String)e.getDocument().getProperty("name");
        if (name.equals("xdim")) {
            double newval = 0;
            try {
                newval = Double.parseDouble(xdim.getText());
                dims[0] = newval;
            } catch (NumberFormatException ex) {
            }
        } else if (name.equals("ydim")) {
            double newval = 0;
            try {
                newval = Double.parseDouble(ydim.getText());
                dims[1] = newval;
            } catch (NumberFormatException ex) {
            }
        }
    }
    
    public void insertUpdate(DocumentEvent e) {
        change(e);
    }
    public void removeUpdate(DocumentEvent e) {
    }
    public void changedUpdate(DocumentEvent e) {
    }
    
    public void actionPerformed(ActionEvent e) {
    }

}