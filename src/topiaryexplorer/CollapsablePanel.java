package topiaryexplorer;

import java.awt.*;
import java.awt.event.*;
import java.awt.font.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.io.*;
import java.lang.*;
import javax.imageio.ImageIO;

/** Code adapted from http://www.coderanch.com/t/341737/GUI/java/Expand-Collapse-Panels 
**/
 
 
class CollapsablePanel extends JPanel {

	private boolean selected;
	private boolean nested;
	JPanel contentPanel_;
	HeaderPanel headerPanel_;
	private Color selectedColor = new Color(200,255,200);
	private Color hoverColor = new Color(100,255,100);
	private Color unselectedColor = new Color(200,220,200);
	private Color currentColor = new Color(200,220,200);

	private class HeaderPanel extends JPanel implements MouseListener {
		String text_;
		Font font;
		BufferedImage open, closed;
		final int OFFSET = 30, PAD = 5;

		public HeaderPanel(String text) {
			addMouseListener(this);
			text_ = text;
			font = new Font("sans-serif", Font.PLAIN, 12);
			// setRequestFocusEnabled(true);
			if(nested)
			{
                setPreferredSize(new Dimension(190, 20));
            }
            else
                setPreferredSize(new Dimension(200, 20));
                
			int w = getWidth();
			int h = getHeight();
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
	                         // Uncomment once you have your own images
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

	public CollapsablePanel(String text, JPanel panel) {
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
	
	public CollapsablePanel(String text, JPanel panel, boolean _selected, boolean _nested) {
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

	public void toggleSelection() {
		selected = !selected;
        
        if(selected)
            currentColor = selectedColor;
        else
            currentColor = unselectedColor;
        
		if (contentPanel_.isShowing())
			contentPanel_.setVisible(false);
		else
			contentPanel_.setVisible(true);

		validate();

		headerPanel_.repaint();
	}
	
	public void addToHeader(Component c) {
	    headerPanel_.add(c);
	}

}