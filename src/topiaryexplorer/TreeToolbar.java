package topiaryexplorer;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.event.*;


public class TreeToolbar extends JToolBar {

    JButton zoomOutButton  = new JButton("-");
    JButton zoomInButton = new JButton("+");
    JSlider zoomSlider = new JSlider(1, 500, 1);
    JTextField search = new JTextField();
    JPanel spacer1 = new JPanel();    
    TreeWindow frame = null;
    JPanel statusPanel = new JPanel();
    JLabel status = new JLabel("");
    JLabel statusLine2 = new JLabel("");
    double minXScale = 0;

    public TreeToolbar(TreeWindow _frame) {

        frame = _frame;

        zoomOutButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                if(frame.zoomLocked)
                {
                    frame.zoomOut();
                    return;
                }
                zoomSlider.setValue(zoomSlider.getValue() - 1);
                syncTreeWithZoomSlider();
            }

        });
        zoomInButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if(frame.zoomLocked)
                {
                    frame.zoomIn();
                    return;
                }
                zoomSlider.setValue(zoomSlider.getValue() + 1);
                syncTreeWithZoomSlider();
            }

        });
        add(spacer1);
        add(zoomOutButton);
        zoomSlider.setSnapToTicks(true);
        zoomSlider.setPreferredSize(new Dimension(200,28));
        zoomSlider.setMaximumSize(new Dimension(200,28));
        zoomSlider.addChangeListener(new ChangeListener() {
            public synchronized void stateChanged(ChangeEvent e) {
                if (zoomSlider.getValueIsAdjusting()){
                    syncTreeWithZoomSlider();
                    if(frame.zoomLocked)
                    {
                        frame.verticalTreeToolbar.syncTreeWithZoomSlider(zoomSlider);
                    }
                }
                frame.tree.redraw();
            }
        });
        
        add(zoomSlider);
        add(zoomInButton);

        setFloatable(false);
        spacer1.setPreferredSize(new Dimension(10,10));
        add(spacer1);
        statusPanel.setLayout(new GridLayout(2,1));
        status.setFont(new Font("Courier",Font.PLAIN,10));
        statusLine2.setFont(new Font("Courier",Font.PLAIN,12));
        statusPanel.add(status);
        statusPanel.add(statusLine2);
        add(statusPanel);
        spacer1.setPreferredSize(new Dimension(100,10));
        add(spacer1);
        add(new JLabel("Search:"));
        search.setPreferredSize(new Dimension(100,28));
        search.setMaximumSize(new Dimension(100,28));
        
       search.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
			public void changedUpdate(javax.swing.event.DocumentEvent evt) {
				doSearch();
			}

			public void insertUpdate(javax.swing.event.DocumentEvent evt) {
				doSearch();
			}

			public void removeUpdate(javax.swing.event.DocumentEvent evt) {
				doSearch();
			}

			private void doSearch() {
				String str = search.getText().trim().toUpperCase();
				ArrayList<Node> nodes = frame.tree.getTree().getNodes();
				boolean first = true;
				for (Node n : nodes) {
					if (str.length() > 0 && n.getLabel().toUpperCase().indexOf(str) != -1) {
						if (first) {
							frame.tree.setSelectedNode(n);
							first = false;
						}
						frame.tree.getHilightedNodes().add(n);
					} else {
						frame.tree.getHilightedNodes().remove(n);
					}
				}
				frame.tree.redraw();
			}
		});
        add(search);
    }
    
    public void sliderEnabled(boolean b) {
        zoomSlider.setEnabled(b);
    }
    
    public void setValue(int i) {
        zoomSlider.setValue(i);
        syncTreeWithZoomSlider();
    }
    
    public void zoomIn() {
        if (frame.tree.getTree() == null) return;      
        frame.tree.setHorizontalScaleFactor(frame.tree.getXScale()+minXScale); 
        zoomSlider.setValue((int)(frame.tree.getXScale()/minXScale));
/*        double newScale = minXScale*zoomSlider.getValue();*/
/*        frame.tree.setHorizontalScaleFactor(newScale);*/
        frame.tree.redraw();
/*        System.out.println(zoomSlider.getValue());*/
/*        zoomSlider.setValue(zoomSlider.getValue()+1);*/
/*        System.out.println(zoomSlider.getValue());*/
/*        syncTreeWithZoomSlider();*/
    }
    
    public void zoomOut() {
        if (frame.tree.getTree() == null) return;      
        if(frame.tree.getXScale()-minXScale >= minXScale)
            frame.tree.setHorizontalScaleFactor(frame.tree.getXScale()-minXScale); 
        else
            frame.tree.setHorizontalScaleFactor(minXScale);

        zoomSlider.setValue((int)(frame.tree.getXScale()/minXScale));
/*        double newScale = minXScale*zoomSlider.getValue();*/
/*        frame.tree.setHorizontalScaleFactor(newScale);*/
        frame.tree.redraw();
/*        System.out.println(zoomSlider.getValue());*/
/*        zoomSlider.setValue(zoomSlider.getValue()+1);*/
/*        System.out.println(zoomSlider.getValue());*/
/*        syncTreeWithZoomSlider();*/
    }
    
    public void setStatus(String p, String s, String s2) {
        status.setText(p+s);
        statusLine2.setText(s2);
    }

    public void setScale() {
        if (frame.tree.getTreeLayout().equals("Rectangular") || frame.tree.getTreeLayout().equals("Triangular")) {
		    minXScale = (frame.tree.getWidth()-frame.tree.getMargin()-frame.tree.getTreeMargin())/frame.tree.getTree().depth();
		} else {
		    minXScale = (Math.min(frame.tree.getWidth(), frame.tree.getHeight())*0.5-frame.tree.getMargin())/frame.tree.getTree().depth();
		}
    }
    
    public void syncTreeWithZoomSlider() {
        if (frame.tree.getTree() == null) return;
        double newScale = minXScale*zoomSlider.getValue();
        frame.tree.setHorizontalScaleFactor(newScale);
        frame.tree.redraw();
    }

    public void syncZoomSliderWithTree() {
        if (frame.tree.getTree() == null) return;        
        zoomSlider.setValue((int)(frame.tree.getXScale()/minXScale));
    }

}
