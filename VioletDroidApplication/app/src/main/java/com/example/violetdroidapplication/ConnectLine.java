package com.example.violetdroidapplication;
import android.graphics.Point;

import android.*;
import android.graphics.*;
/**
 * Created by aniquedavla on 11/10/16.
 */

public class ConnectLine {
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
    public static Paint x = new Paint();

    /**
     * Source line point
     */
    PointF p1;
    /**
     * Destination line point
     */
    PointF p2;

    /**
     * Line type can be one of LINE_TYPE_SIMPLE, LINE_TYPE_RECT_1BREAK, LINE_TYPE_RECT_2BREAK
     */
    int lineType = LINE_TYPE_SIMPLE;
    /**
     * for the LINE_TYPE_RECT_2BREAK type the param defines how line should be rendered
     */
    int lineStart = LINE_START_HORIZONTAL;
    /**
     * arrow can be one of following
     * LINE_ARROW_NONE - no arrow
     * LINE_ARROW_SOURCE - arrow beside source point
     * LINE_ARROW_DEST - arrow beside dest point
     * LINE_ARROW_BOTH - both source and dest has arrows
     */
    int lineArrow = LINE_ARROW_NONE;
    /**
     * Constructs default line
     * @param p1 Point start
     * @param p2 Point end
     */
    public ConnectLine(PointF p1, PointF p2) {
        this(p1, p2, LINE_TYPE_SIMPLE, LINE_START_HORIZONTAL, LINE_ARROW_NONE);
    }

    /**
     * Constructs line with specified params
     * @param p1 Point start
     * @param p2 Point end
     * @param lineType int type of line (LINE_TYPE_SIMPLE, LINE_TYPE_RECT_1BREAK, LINE_TYPE_RECT_2BREAK)
     * @param lineStart int for the LINE_TYPE_RECT_2BREAK type the param defines how line should be rendered
     * @param lineArrow int defines line arrow type
     */
    public ConnectLine(PointF p1, PointF p2, int lineType, int lineStart, int lineArrow) {
        this.p1 = p1;
        this.p2 = p2;
        this.lineType = lineType;
        this.lineStart = lineStart;
        this.lineArrow = lineArrow;
    }

    /**
     * Paints the line with specified params
     * @param g2d Graphics2D
     */
    public void paint(Canvas g2d) {
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

    protected void paintSimple(Canvas g2d) {
        Paint color = new Paint();
        g2d.drawLine(p1.x,p1.y,p2.x,p2.y,x);
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

    protected void paintArrow(Canvas g2d, PointF p1, PointF p2) {
        paintArrow(g2d, p1, p2, getRestrictedArrowWidth(p1, p2));
    }

    protected void paintArrow(Canvas g2d, PointF p1, PointF p2, int width) {
        PointF pp1 = new PointF(p1.x, p1.y);
        PointF pp2 = new PointF(p2.x, p2.y);
        PointF left = getLeftArrowPoint(pp1, pp2,width);
        PointF right = getRightArrowPoint(pp1, pp2, width);

        g2d.drawLine(p2.x, p2.y, Math.round(left.x), Math.round(left.y),x);
        g2d.drawLine(p2.x, p2.y, Math.round(right.x), Math.round(right.y),x);
    }


    protected void paint1Break(Canvas g2d) {
        if (lineStart == LINE_START_HORIZONTAL) {
            g2d.drawLine(p1.x, p1.y, p2.x, p1.y,x);
            g2d.drawLine(p2.x, p1.y, p2.x, p2.y,x);
            switch (lineArrow) {
                case LINE_ARROW_DEST:
                    paintArrow(g2d, new PointF(p2.x, p1.y), p2);
                    break;
                case LINE_ARROW_SOURCE:
                    paintArrow(g2d, new PointF(p2.x, p1.y), p1);
                    break;
                case LINE_ARROW_BOTH:
                    paintArrow(g2d, new PointF(p2.x, p1.y), p2);
                    paintArrow(g2d, new PointF(p2.x, p1.y), p1);
                    break;
            }
        }
        else if (lineStart == LINE_START_VERTICAL) {
            g2d.drawLine(p1.x, p1.y, p1.x, p2.y,x);
            g2d.drawLine(p1.x, p2.y, p2.x, p2.y,x);
            switch (lineArrow) {
                case LINE_ARROW_DEST:
                    paintArrow(g2d, new PointF(p1.x, p2.y), p2);
                    break;
                case LINE_ARROW_SOURCE:
                    paintArrow(g2d, new PointF(p1.x, p2.y), p1);
                    break;
                case LINE_ARROW_BOTH:
                    paintArrow(g2d, new PointF(p1.x, p2.y), p2);
                    paintArrow(g2d, new PointF(p1.x, p2.y), p1);
                    break;
            }
        }
    }

    protected void paint2Breaks(Canvas g2d) {
        if (lineStart == LINE_START_HORIZONTAL) {
            g2d.drawLine(p1.x, p1.y, p1.x + (p2.x - p1.x) / 2, p1.y,x);
            g2d.drawLine(p1.x + (p2.x - p1.x) / 2, p1.y, p1.x + (p2.x - p1.x) / 2, p2.y,x);
            g2d.drawLine(p1.x + (p2.x - p1.x) / 2, p2.y, p2.x, p2.y,x);
            switch (lineArrow) {
                case LINE_ARROW_DEST:
                    paintArrow(g2d, new PointF(p1.x + (p2.x - p1.x) / 2, p2.y), p2);
                    break;
                case LINE_ARROW_SOURCE:
                    paintArrow(g2d, new PointF(p1.x + (p2.x - p1.x) / 2, p1.y), p1);
                    break;
                case LINE_ARROW_BOTH:
                    paintArrow(g2d, new PointF(p1.x + (p2.x - p1.x) / 2, p2.y), p2);
                    paintArrow(g2d, new PointF(p1.x + (p2.x - p1.x) / 2, p1.y), p1);
                    break;
            }
        }
        else if (lineStart == LINE_START_VERTICAL) {
            g2d.drawLine(p1.x, p1.y, p1.x, p1.y + (p2.y - p1.y) / 2,x);
            g2d.drawLine(p1.x, p1.y + (p2.y - p1.y) / 2, p2.x, p1.y + (p2.y - p1.y) / 2,x);
            g2d.drawLine(p2.x, p1.y + (p2.y - p1.y) / 2, p2.x, p2.y,x);

            switch (lineArrow) {
                case LINE_ARROW_DEST:
                    paintArrow(g2d, new PointF(p2.x, p1.y + (p2.y - p1.y) / 2), p2);
                    break;
                case LINE_ARROW_SOURCE:
                    paintArrow(g2d, new PointF(p1.x, p1.y + (p2.y - p1.y) / 2), p1);
                    break;
                case LINE_ARROW_BOTH:
                    paintArrow(g2d, new PointF(p2.x, p1.y + (p2.y - p1.y) / 2), p2);
                    paintArrow(g2d, new PointF(p1.x, p1.y + (p2.y - p1.y) / 2), p1);
                    break;
            }
        }
    }

    public int getLineType() {
        return lineType;
    }

    public void setLineType(int type) {
        lineType = type;
    }

    public int getLineStart() {
        return lineStart;
    }

    public void setLineStart(int start) {
        lineStart = start;
    }

    public int getLineArrow() {
        return lineArrow;
    }

    public void setLineArrow(int arrow) {
        lineType = lineArrow;
    }

    public PointF getP1() {
        return p1;
    }

    public void setP1(PointF p) {
        p1 = p;
    }

    public PointF getP2() {
        return p2;
    }

    public void setP2(PointF p) {
        p2 = p;
    }

    protected static PointF getMidArrowPoint(PointF p1, PointF p2, float w) {
        PointF res = new PointF();
        float d = Math.round(Math.sqrt( (p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y)));

        if (p1.x < p2.x) {
            res.x = p2.x - w * Math.abs(p1.x - p2.x) / d;
        }
        else {
            res.x = p2.x + w * Math.abs(p1.x - p2.x) / d;
        }

        if (p1.y < p2.y) {
            res.y = p2.y - w * Math.abs(p1.y - p2.y) / d;
        }
        else {
            res.y = p2.y + w * Math.abs(p1.y - p2.y) / d;
        }

        return res;
    }

    protected static PointF getLeftArrowPoint(PointF p1, PointF p2) {
        return getLeftArrowPoint(p1, p2, LINE_ARROW_WIDTH);
    }

    protected static PointF getLeftArrowPoint(PointF p1, PointF p2, int w) {
        PointF res = new PointF();
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

    protected static PointF getRightArrowPoint(PointF p1, PointF p2) {
        return getRightArrowPoint(p1, p2, LINE_ARROW_WIDTH);
    }

    protected static PointF getRightArrowPoint(PointF p1, PointF p2, float w) {
        PointF res = new PointF();
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

    protected int getRestrictedArrowWidth(PointF p1, PointF p2) {
        return Math.min(LINE_ARROW_WIDTH, (int) Math.sqrt( (p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y)));
    }
}

