package topiaryexplorer;

import java.awt.*;
import java.awt.event.*;
import java.awt.font.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.io.*;
import java.lang.*;
import javax.imageio.ImageIO;

/** 
* Panel that has a collapse/uncollapse ability for hiding and revealing content.
* Code adapted from http://www.coderanch.com/t/341737/GUI/java/Expand-Collapse-Panels 
* @see #javax.swing.JPanel
**/
 
class CollapsablePanel extends JPanel {
	private boolean selected;
	private boolean nested;
	private JPanel contentPanel_;
	private HeaderPanel headerPanel_;
	private Color selectedColor = new Color(20,200,250);
	private Color hoverColor = new Color(20,200,250);
	private Color unselectedColor = new Color(170,210,220);
	private Color currentColor = new Color(170,210,220);
    /**
    * A private class used within a <code>CollapsablePanel</code>.
    **/
	private class HeaderPanel extends JPanel implements MouseListener {
		String text_;
		Font font;
		// Images for open and closed state of panel
		BufferedImage open, closed;
		// Offsets for the text in the header and padding for the image
		int OFFSET = 30, PAD = 5;

        /**
        * Creates a header panel with a title.
        **/
		public HeaderPanel(String text) {
		    // Mouse listener so that the header will change colors when hovered
			addMouseListener(this);
			text_ = text;
			font = new Font("sans-serif", Font.PLAIN, 12);
			// If this panel is nested, the width needs to be smaller
			if(nested)
			{
                setPreferredSize(new Dimension(190, 20));
                PAD += 10;
                OFFSET += 10;
            }
            else
                setPreferredSize(new Dimension(200, 20));
                
			int w = getWidth();
			int h = getHeight();
			// Draw the header with the color corresponding to collapsed or uncollapsed status
            if(selected)
                currentColor = selectedColor;
            else
                currentColor = unselectedColor;
                
            setBackground(currentColor);
            
            try {
                open = ImageIO.read(new File("./src/images/down_mini.gif"));
                closed = ImageIO.read(new File("./src/images/right_mini.gif"));
            } catch (IOException e) {
                e.printStackTrace();
            }

		}
        /**
        * Draws the header using the proper colors and open or closed image
        * @see JComponent#paintComponent(Graphics)
        **/
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			int h = getHeight();
			
	        if (selected)
	               g2.drawImage(open, PAD, 0, h, h, this);
	           else
	               g2.drawImage(closed, PAD, 0, h, h, this);

			g2.setFont(font);
			FontRenderContext frc = g2.getFontRenderContext();
			LineMetrics lm = font.getLineMetrics(text_, frc);
			float height = lm.getAscent() + lm.getDescent();
			float x = OFFSET;
			float y = (h + height) / 2 - lm.getDescent();
			g2.drawString(text_, x, y);
		}

		public void mouseClicked(MouseEvent e) {
			toggleSelection();
		}

		public void mouseEntered(MouseEvent e) {
		    headerPanel_.setBackground(hoverColor);
		}

		public void mouseExited(MouseEvent e) {
		    headerPanel_.setBackground(currentColor);
		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}

	}
    
    /**
    * Creates a collapsable panel with header and internal panel
    **/
	CollapsablePanel(String text, JPanel panel) {
		super(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(1, 3, 0, 3);
		gbc.weightx = 1.0;
		gbc.fill = gbc.HORIZONTAL;
		gbc.gridwidth = gbc.REMAINDER;

		selected = false;
		headerPanel_ = new HeaderPanel(text);

		setBackground(new Color(0, 0, 0));
		contentPanel_ = panel;

		add(headerPanel_, gbc);
		add(contentPanel_, gbc);
		contentPanel_.setVisible(false);

		JLabel padding = new JLabel();
		gbc.weighty = 1.0;
		add(padding, gbc);

	}
	
	/**
    * Creates a collapsable panel with header, internal panel, selected state and nested state.
    **/
	CollapsablePanel(String text, JPanel panel, boolean _selected, boolean _nested) {
		super(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(1, 3, 0, 3);
		gbc.weightx = 1.0;
		gbc.fill = gbc.HORIZONTAL;
		gbc.gridwidth = gbc.REMAINDER;

		selected = _selected;
		nested = _nested;
		headerPanel_ = new HeaderPanel(text);

		setBackground(new Color(0, 0, 0));
		contentPanel_ = panel;

		add(headerPanel_, gbc);
		add(contentPanel_, gbc);
		contentPanel_.setVisible(selected);

		JLabel padding = new JLabel();
		gbc.weighty = 1.0;
		add(padding, gbc);
	}
    
    /**
    * Inverts selected state of the panel.
    **/
	void toggleSelection() {
		selected = !selected;
        
        if(selected)
            currentColor = selectedColor;
        else
            currentColor = unselectedColor;
        
		if(contentPanel_.isShowing())
			contentPanel_.setVisible(false);
		else
			contentPanel_.setVisible(true);

		validate();

		headerPanel_.repaint();
	}
	
	/**
	* Adds a component to the header bar.
	**/
	void addToHeader(Component c) {
	    headerPanel_.add(c);
	}

}