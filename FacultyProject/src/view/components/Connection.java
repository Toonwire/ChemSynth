package view.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.JComponent;
import javax.swing.JPanel;


public class Connection extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final int CONNECT_LINE_TYPE_SIMPLE = 0;
    public static final int CONNECT_LINE_TYPE_RECTANGULAR = 1;
    
    private JComponent source;
    private JComponent dest;
    
    private Line line;
    private Color lineColor = Color.BLACK;
    private int lineArrow = Line.LINE_ARROW_DEST;
    private int lineType = CONNECT_LINE_TYPE_RECTANGULAR;

   
    public Connection(JComponent source, JComponent dest) {
        this.source = source;
        this.dest = dest;
    }
    
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        computeLine();
        
        // draw the connection
        if (line != null) {
            Shape clip = g2d.getClip();
            g2d.setClip(getLineBounds());
            g2d.setColor(lineColor);
            line.paint(g2d);
            g2d.setClip(clip);
        }
    }

    private void computeLine() {
    	Rectangle start = source.getBounds();
    	Rectangle end = dest.getBounds();
    	
    	// don't draw a line if there is no space between the components 
        if (start.intersects(end)) {
            line = null;
            System.out.println("intesect");
            return;
        }
        
        /*
         * Rectantle.x/y is used as opposed to .getX() and .getY()
         * to avoid conversion from doubles.. 
         * Although accessing the public fields feels wonky as well
         */

        boolean xIntersect = (start.x <= end.x && start.x + start.width >= end.x)
            || (end.x <= start.x && end.x + end.width >= start.x);
        boolean yIntersect = start.y <= end.y && start.y + start.height >= end.y
            || (end.y <= start.y && end.y + end.height >= start.y);

        if (xIntersect) {
            int y1, y2;
            int x1 = start.x + start.width / 2;
            int x2 = end.x + end.width / 2;
            
            // determine which component is closest to the top
            if (start.y + start.height <= end.y) {
                y1 = start.y + start.height;
                y2 = end.y;
            }
            else {
                y1 = start.y;
                y2 = end.y + end.height;
            }
            
            line = new Line(new Point(x1, y1), new Point(x2, y2), Line.LINE_TYPE_RECT_2BREAK, Line.LINE_START_VERTICAL, lineArrow);
            if (lineType == CONNECT_LINE_TYPE_SIMPLE) {
                line.setLineType(Line.LINE_TYPE_SIMPLE);
            }
        }
        else if (yIntersect) {
        	int x1, x2;
            int y1 = start.y + start.height / 2;
            int y2 = end.y + end.height / 2;
            
            if (start.x + start.width <= end.x) {
                x1 = start.x + start.width;
                x2 = end.x;
            }
            else {
                x1 = start.x;
                x2 = end.x + end.width;
            }
            line = new Line(new Point(x1, y1), new Point(x2, y2), Line.LINE_TYPE_RECT_2BREAK, Line.LINE_START_HORIZONTAL, lineArrow);
            if (lineType == CONNECT_LINE_TYPE_SIMPLE) {
                line.setLineType(Line.LINE_TYPE_SIMPLE);
            }
        }
        else {
            int x1,x2,y1,y2;
            
            if (start.y + start.height <= end.y) {
                //source higher
                y1 = start.y + start.height / 2;
                y2 = end.y;
                if (start.x + start.width <= end.x) {
                    x1 = start.x + start.width;
                }
                else {
                    x1 = start.x;
                }
                x2 = end.x + end.width / 2;
            }
            else {
                y1 = start.y + start.height / 2;
                y2 = end.y + end.height;
                if (start.x + start.width <= end.x) {
                    x1 = start.x + start.width;
                }
                else {
                    x1 = start.x;
                }
                x2 = end.x + end.width / 2;
            }
            line = new Line(new Point(x1, y1), new Point(x2, y2), Line.LINE_TYPE_RECT_1BREAK, Line.LINE_START_HORIZONTAL, lineArrow);
            if (lineType == CONNECT_LINE_TYPE_SIMPLE) {
                line.setLineType(Line.LINE_TYPE_SIMPLE);
            }
        }
    }

    private Rectangle getLineBounds() {
        int add = 10;
        int maxX = Math.max(line.getP1().x, line.getP2().x);
        int minX = Math.min(line.getP1().x, line.getP2().x);
        int maxY = Math.max(line.getP1().y, line.getP2().y);
        int minY = Math.min(line.getP1().y, line.getP2().y);

        Rectangle res = new Rectangle(minX - add, minY - add, maxX - minX + 2 * add, maxY - minY + 2 * add);
        return res;
    }

    public Color getLineColor() {
        return lineColor;
    }

    public void setLineColor(Color c) {
        lineColor = c;
    }

    public int getLineType() {
        return lineType;
    }

    public void setLineType(int type) {
        lineType = type;
    }

    public int getLineArrow() {
        return lineArrow;
    }

    public void setLineArrow(int arrow) {
        lineArrow = arrow;
    }
}

