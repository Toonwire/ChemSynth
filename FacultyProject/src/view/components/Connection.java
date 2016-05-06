package view.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Line2D;

import javax.swing.JComponent;
import javax.swing.JPanel;


public class Connection extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JComponent source, dest; 
	private Point sourceMidBot, destMidTop;
	private int thickness = 2;

	private Line2D.Double line;
	private Color lineColor = Color.BLACK;

	public Connection(JComponent source, JComponent dest) {
		this.source = source;
		this.dest = dest;
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		
		Rectangle start = source.getBounds();
		Rectangle end = dest.getBounds();
		
		// don't draw a line if there is no space between the components
		// Doesn't really apply here as we manually space the vertices,
		// but we might as well just handle it just in case.
		if (!start.intersects(end)) {
			
			this.sourceMidBot = new Point(start.x + start.width / 2, start.y + start.height);
			this.destMidTop = new Point(end.x + end.width / 2, end.y);
			line = new Line2D.Double(sourceMidBot, destMidTop);
			
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
			g2d.setStroke(new BasicStroke(thickness));
			
			// draw the connection        
			Shape clip = g2d.getClip();
			g2d.setClip(getLineBounds());	/* to reduce rendering area local to the line */
			g2d.setColor(lineColor);
			g2d.draw(line);
			g2d.setClip(clip);
		}   
	}
	

	private Rectangle getLineBounds() {
		int add = 10;
		int maxX = Math.max(sourceMidBot.x, destMidTop.x);
		int minX = Math.min(sourceMidBot.x, destMidTop.x);
		int maxY = Math.max(sourceMidBot.y, destMidTop.y);
		int minY = Math.min(sourceMidBot.y, destMidTop.y);
		
		Rectangle res = new Rectangle(minX - add, minY - add, maxX - minX + 2 * add, maxY - minY + 2 * add);
		return res;
	}

	public Color getLineColor() {
		return lineColor;
	}

	public void setLineColor(Color c) {
		lineColor = c;
	}

}
