package topiarytool;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.event.*;


public class TreeToolbar extends JToolBar {

    JButton zoomOutButton  = new JButton("-");
    JButton zoomInButton = new JButton("+");
    JSlider zoomSlider = new JSlider(0, 8, 0);
    JTextField search = new JTextField();
    JPanel spacer1 = new JPanel();

    MainFrame frame = null;

    public TreeToolbar(MainFrame _frame) {

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
						frame.tree.getHilitedNodes().add(n);
					} else {
						frame.tree.getHilitedNodes().remove(n);
					}
				}
				frame.tree.redraw();
			}
		});


        add(zoomSlider);
        add(zoomInButton);
        setFloatable(false);
        spacer1.setPreferredSize(new Dimension(100,10));
        add(spacer1);
        add(new JLabel("Search:"));
        search.setPreferredSize(new Dimension(100,28));
        search.setMaximumSize(new Dimension(100,28));
        add(search);
    }

    public void updateZoomBounds() {
        if (frame.tree.getTree()==null) {return;}

		double maxYScale = frame.tree.getMaxYScale();
		double minYScale = (frame.tree.getHeight() - 2*frame.tree.getMargin())/
			frame.tree.getTree().getNumberOfLeaves();

		//each step from min to max should be a scaling by a factor of sqrt(2).  So, the number of
		//steps from min to max will be log(min/max)/log(1/sqrt(2)).
		double exactNumSteps =  Math.log(minYScale/maxYScale) /
			 Math.log(1.0/Math.pow(2.0, 0.5));
		//zooming occurs in discreet steps, so take the floor
		int numSteps = (int) Math.floor(exactNumSteps);

		//set the maximum number of steps
		zoomSlider.setMaximum(numSteps);
		zoomSlider.setMinimum(0);
    }

    public void syncTreeWithZoomSlider() {
        if (frame.tree.getTree() == null) return;
        double minYScale = (frame.tree.getHeight() - 2*frame.tree.getMargin())/frame.tree.getTree().getNumberOfLeaves();
        double newScale = minYScale * Math.pow(Math.pow(2.0, 0.5), zoomSlider.getValue());
        frame.tree.setScaleFactor(newScale, frame.tree.width/2.0, frame.tree.height/2.0);
    }

    public void syncZoomSliderWithTree() {
        if (frame.tree.getTree() == null) return;
        double minYScale = (frame.tree.getHeight() - 2*frame.tree.getMargin())/frame.tree.getTree().getNumberOfLeaves();
        int currStep = (int) Math.floor(Math.log(frame.tree.getYScale()/minYScale) / Math.log(Math.pow(2.0,0.5))+0.00001);
        zoomSlider.setValue(currStep);
    }

}
