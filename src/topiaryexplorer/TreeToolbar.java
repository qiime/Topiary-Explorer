package topiaryexplorer;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.event.*;


public class TreeToolbar extends JToolBar {

    JButton zoomOutButton  = new JButton("-");
    JButton zoomInButton = new JButton("+");
    JSlider zoomSlider = new JSlider(1, 100, 1);
    JTextField search = new JTextField();
    JPanel spacer1 = new JPanel();    
    TreeWindow frame = null;
    JLabel status = new JLabel("");
    double minXScale = 0;

    public TreeToolbar(TreeWindow _frame) {

        frame = _frame;

        zoomOutButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                zoomSlider.setValue(zoomSlider.getValue() - 1);
                syncTreeWithZoomSlider();
            }

        });
        zoomInButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
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
            public void stateChanged(ChangeEvent e) {
                if (zoomSlider.getValueIsAdjusting()){
                    syncTreeWithZoomSlider();
                }
            }
        });
        
        add(zoomSlider);
        add(zoomInButton);

        setFloatable(false);
        spacer1.setPreferredSize(new Dimension(10,10));
        add(spacer1);
        status.setFont(new Font("Courier",Font.PLAIN,12));
        add(status);
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
					if (str.length() > 0 && n.getName().toUpperCase().indexOf(str) != -1) {
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
    
    public void setStatus(String p, String s) {
        if(s.length() > 56)
        {
            s = p +"..." + s.substring(s.length()-Math.min(56-p.length(),s.length()),s.length()-1);
        }
        else
            s = p+s;
        status.setText(s);
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
        double newScale = minXScale *zoomSlider.getValue();
        frame.tree.setScaleFactor(newScale, frame.tree.getYScale(), frame.tree.getXStart(), frame.tree.getYStart());
    }

    public void syncZoomSliderWithTree() {
        if (frame.tree.getTree() == null) return;        
        zoomSlider.setValue((int)(frame.tree.getXScale()/minXScale));
    }

}
