package topiarytool;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

class PCSelectDialog extends JDialog implements ActionListener {

    private JOptionPane optionPane;

    private JList axis1;
    private JList axis2;
    private JList axis3;


    public PCSelectDialog(final Frame frame) {
        super(frame, false);
        setTitle("Select Axes");
        
        String[] data = new String[((PcoaWindow)frame).pcoa.spData[0].coords.length];
        for (int i = 0; i < ((PcoaWindow)frame).pcoa.spData[0].coords.length; i++) {
            Integer in = new Integer(i+1);
            data[i] = new String("PC"+ in.toString());
        }
        axis1 = new JList(data);
        axis1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        axis1.setSelectedIndex(((PcoaWindow)frame).pcoa.getAxis(1));
        axis1.addListSelectionListener( new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false)
                    ((PcoaWindow)frame).pcoa.setAxis(1, axis1.getSelectedIndex());
            }
        });        
        axis2 = new JList(data);
        axis2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        axis2.setSelectedIndex(((PcoaWindow)frame).pcoa.getAxis(2));
        axis2.addListSelectionListener( new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false)
                    ((PcoaWindow)frame).pcoa.setAxis(2, axis2.getSelectedIndex());            }
        });        
        axis3 = new JList(data);
        axis3.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        axis3.setSelectedIndex(((PcoaWindow)frame).pcoa.getAxis(3));
        axis3.addListSelectionListener( new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false)
                    ((PcoaWindow)frame).pcoa.setAxis(3, axis3.getSelectedIndex());            }
        });


       Container cp = getContentPane();
       cp.setLayout(new BoxLayout(cp, BoxLayout.PAGE_AXIS));
       JPanel j = new JPanel();
       j.setLayout(new FlowLayout());
       
       JPanel j1 = new JPanel();
       j1.setLayout(new BoxLayout(j1, BoxLayout.PAGE_AXIS));
       j1.add(new JLabel("Axis 1:")); 
       JScrollPane s1 = new JScrollPane(axis1);
       s1.setPreferredSize(new Dimension(100, 100));
       j1.add(s1);
       
       j.add(j1);
       JPanel j2 = new JPanel();
       j2.setLayout(new BoxLayout(j2, BoxLayout.PAGE_AXIS));
       j2.add(new JLabel("Axis 2:"));     
        JScrollPane s2 = new JScrollPane(axis2);
       s2.setPreferredSize(new Dimension(100, 100));
       j2.add(s2);
       j.add(j2);
       JPanel j3 = new JPanel();
       j3.setLayout(new BoxLayout(j3, BoxLayout.PAGE_AXIS));
       j3.add(new JLabel("Axis 3:"));    
       JScrollPane s3 = new JScrollPane(axis3);
       s3.setPreferredSize(new Dimension(100, 100));
       j3.add(s3);
       j.add(j3);
       
       cp.add(j);
       JButton okbutton = new JButton("OK");
       okbutton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               dispose();
           }
       });
       cp.add(okbutton);
       
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); 

    }

    public void actionPerformed(ActionEvent e) {
    }

}