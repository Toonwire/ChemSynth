package view.components;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;

public class Line {
    public static final int LINE_TYPE_SIMPLE = 0;
    public static final int LINE_TYPE_RECT_1BREAK = 1;
    public static final int LINE_TYPE_RECT_2BREAK = 2;

    public static final int LINE_START_HORIZONTAL = 0;
    public static final int LINE_START_VERTICAL = 1;

    public static final int LINE_ARROW_NONE = 0;
    public static final int LINE_ARROW_SOURCE = 1;
    public static final int LINE_ARROW_DEST = 2;
    public static final int LINE_ARROW_BOTH = 3;

    public static int LINE_ARROW_WIDTH = 10;

    
    private Point p1;
    private Point p2;

    private int lineType = LINE_TYPE_SIMPLE;
    private int lineStart = LINE_START_HORIZONTAL;
    private int lineArrow = LINE_ARROW_NONE;
    
    public Line(Point p1, Point p2) {
        this(p1, p2, LINE_TYPE_SIMPLE, LINE_START_HORIZONTAL, LINE_ARROW_NONE);
    }

    
    public Line(Point p1, Point p2, int lineType, int lineStart, int lineArrow) {
        this.p1 = p1;
        this.p2 = p2;
        this.lineType = lineType;
        this.lineStart = lineStart;
        this.lineArrow = lineArrow;
    }

    public void paint(Graphics2D g2d) {
        switch (lineType) {
            case LINE_TYPE_SIMPLE:
                paintSimple(g2d);
                break;
            case LINE_TYPE_RECT_1BREAK:
                paint1Break(g2d);
                break;
            case LINE_TYPE_RECT_2BREAK:
                paint2Breaks(g2d);
                break;
        }
    }

    private void paintSimple(Graphics2D g2d) {
        g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
        switch (lineArrow) {
            case LINE_ARROW_DEST:
                paintArrow(g2d, p1, p2);
                break;
            case LINE_ARROW_SOURCE:
                paintArrow(g2d, p2, p1);
                break;
            case LINE_ARROW_BOTH:
                paintArrow(g2d, p1, p2);
                paintArrow(g2d, p2, p1);
                break;
        }
    }

    private void paintArrow(Graphics2D g2d, Point p1, Point p2) {
        paintArrow(g2d, p1, p2, getRestrictedArrowWidth(p1, p2));
    }

    private void paintArrow(Graphics2D g2d, Point p1, Point p2, int width) {
        Point2D.Float pp1 = new Point2D.Float(p1.x, p1.y);
        Point2D.Float pp2 = new Point2D.Float(p2.x, p2.y);
        Point2D.Float left = getLeftArrowPoint(pp1, pp2, width);
        Point2D.Float right = getRightArrowPoint(pp1, pp2, width);

        g2d.drawLine(p2.x, p2.y, Math.round(left.x), Math.round(left.y));
        g2d.drawLine(p2.x, p2.y, Math.round(right.x), Math.round(right.y));
    }

    private void paint1Break(Graphics2D g2d) {
        if (lineStart == LINE_START_HORIZONTAL) {
            g2d.drawLine(p1.x, p1.y, p2.x, p1.y);
            g2d.drawLine(p2.x, p1.y, p2.x, p2.y);
            switch (lineArrow) {
                case LINE_ARROW_DEST:
                    paintArrow(g2d, new Point(p2.x, p1.y), p2);
                    break;
                case LINE_ARROW_SOURCE:
                    paintArrow(g2d, new Point(p2.x, p1.y), p1);
                    break;
                case LINE_ARROW_BOTH:
                    paintArrow(g2d, new Point(p2.x, p1.y), p2);
                    paintArrow(g2d, new Point(p2.x, p1.y), p1);
                    break;
            }
        }
        else if (lineStart == LINE_START_VERTICAL) {
            g2d.drawLine(p1.x, p1.y, p1.x, p2.y);
            g2d.drawLine(p1.x, p2.y, p2.x, p2.y);
            switch (lineArrow) {
                case LINE_ARROW_DEST:
                    paintArrow(g2d, new Point(p1.x, p2.y), p2);
                    break;
                case LINE_ARROW_SOURCE:
                    paintArrow(g2d, new Point(p1.x, p2.y), p1);
                    break;
                case LINE_ARROW_BOTH:
                    paintArrow(g2d, new Point(p1.x, p2.y), p2);
                    paintArrow(g2d, new Point(p1.x, p2.y), p1);
                    break;
            }
        }
    }

    private void paint2Breaks(Graphics2D g2d) {
        if (lineStart == LINE_START_HORIZONTAL) {
            g2d.drawLine(p1.x, p1.y, p1.x + (p2.x - p1.x) / 2, p1.y);
            g2d.drawLine(p1.x + (p2.x - p1.x) / 2, p1.y, p1.x + (p2.x - p1.x) / 2, p2.y);
            g2d.drawLine(p1.x + (p2.x - p1.x) / 2, p2.y, p2.x, p2.y);
            switch (lineArrow) {
                case LINE_ARROW_DEST:
                    paintArrow(g2d, new Point(p1.x + (p2.x - p1.x) / 2, p2.y), p2);
                    break;
                case LINE_ARROW_SOURCE:
                    paintArrow(g2d, new Point(p1.x + (p2.x - p1.x) / 2, p1.y), p1);
                    break;
                case LINE_ARROW_BOTH:
                    paintArrow(g2d, new Point(p1.x + (p2.x - p1.x) / 2, p2.y), p2);
                    paintArrow(g2d, new Point(p1.x + (p2.x - p1.x) / 2, p1.y), p1);
                    break;
            }
        }
        else if (lineStart == LINE_START_VERTICAL) {
            g2d.drawLine(p1.x, p1.y, p1.x, p1.y + (p2.y - p1.y) / 2);
            g2d.drawLine(p1.x, p1.y + (p2.y - p1.y) / 2, p2.x, p1.y + (p2.y - p1.y) / 2);
            g2d.drawLine(p2.x, p1.y + (p2.y - p1.y) / 2, p2.x, p2.y);

            switch (lineArrow) {
                case LINE_ARROW_DEST:
                    paintArrow(g2d, new Point(p2.x, p1.y + (p2.y - p1.y) / 2), p2);
                    break;
                case LINE_ARROW_SOURCE:
                    paintArrow(g2d, new Point(p1.x, p1.y + (p2.y - p1.y) / 2), p1);
                    break;
                case LINE_ARROW_BOTH:
                    paintArrow(g2d, new Point(p2.x, p1.y + (p2.y - p1.y) / 2), p2);
                    paintArrow(g2d, new Point(p1.x, p1.y + (p2.y - p1.y) / 2), p1);
                    break;
            }
        }
    }

    public void setLineType(int type) {
        lineType = type;
    }

    public void setLineStart(int start) {
        lineStart = start;
    }

    public void setLineArrow(int arrow) {
        lineType = lineArrow;
    }

    public Point getP1() {
        return p1;
    }

    public Point getP2() {
        return p2;
    }

    private Point2D.Float getLeftArrowPoint(Point2D.Float p1, Point2D.Float p2, float w) {
        Point2D.Float res = new Point2D.Float();
        double alpha = Math.PI / 2;
        if (p2.x != p1.x) {
            alpha = Math.atan( (p2.y - p1.y) / (p2.x - p1.x));
        }
        alpha += Math.PI / 10;
        float xShift = Math.abs(Math.round(Math.cos(alpha) * w));
        float yShift = Math.abs(Math.round(Math.sin(alpha) * w));
        if (p1.x <= p2.x) {
            res.x = p2.x - xShift;
        }
        else {
            res.x = p2.x + xShift;
        }
        if (p1.y < p2.y) {
            res.y = p2.y - yShift;
        }
        else {
            res.y = p2.y + yShift;
        }
        return res;
    }

    private Point2D.Float getRightArrowPoint(Point2D.Float p1, Point2D.Float p2, float w) {
        Point2D.Float res = new Point2D.Float();
        double alpha = Math.PI / 2;
        if (p2.x != p1.x) {
            alpha = Math.atan( (p2.y - p1.y) / (p2.x - p1.x));
        }
        alpha -= Math.PI / 10;
        float xShift = Math.abs(Math.round(Math.cos(alpha) * w));
        float yShift = Math.abs(Math.round(Math.sin(alpha) * w));
        if (p1.x < p2.x) {
            res.x = p2.x - xShift;
        }
        else {
            res.x = p2.x + xShift;
        }
        if (p1.y <= p2.y) {
            res.y = p2.y - yShift;
        }
        else {
            res.y = p2.y + yShift;
        }
        return res;
    }

    private int getRestrictedArrowWidth(Point p1, Point p2) {
        return Math.min(LINE_ARROW_WIDTH, (int) Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y)));
    }
}
