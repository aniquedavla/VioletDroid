package com.example.violetdroidapplication;

import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by vishaalprasad on 10/27/16.
 */

/**
 * A ClassDiagItem represents one Item in a Class Diagram Editor
 * It contains the text and the Rectangle around the text
 */
public class ClassDiagItem {
    private static final String TAG = "ClassDiagItem";

    private String content;
    private float x;
    private float y;
    private Rect outline;

    private static Paint defaultTextPaint;
    private static Paint defaultOutlinePaint;

    private static final int DEFAULT_PADDING = 20;

    /**
     * Create a new ClassDiagItem
     *
     * @param content
     * @param x
     * @param y
     */
    public ClassDiagItem(String content, float x, float y) {
        this.content = content;
        this.x = x;
        this.y = y;
        outline = new Rect();
        setRect();
    }

    /**
     * Set the position (bottom left)
     *
     * @param x
     * @param y
     */
    public void set(float x, float y) {
        this.x = x;
        this.y = y;
        setRect();
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public String getContent() {
        return content;
    }

    /**
     * to be used internally, sets the outline of this item
     */
    private void setRect() {
        //takes into account the padding
        getDefaultTextPaint().getTextBounds(content, 0, content.length(), outline);
        this.outline.set((int) x - DEFAULT_PADDING, (int) y - outline.height() - DEFAULT_PADDING,
                (int) x + outline.width() + DEFAULT_PADDING, (int) y + DEFAULT_PADDING);
    }

    /**
     * @return the outline of this rectangle
     */
    public Rect getRect() {
        return outline;
    }

    /**
     * Check to see if the given coordinates are contained in this ClassDiagItem
     *
     * @param x
     * @param y
     * @return
     */
    public boolean contains(int x, int y) {
        return this.outline.contains(x, y);
    }

    /**
     * All ClassDiagItem will be painted with this Paint (for text)
     *
     * @return
     */
    public static Paint getDefaultTextPaint() {
        if (defaultTextPaint == null) {
            defaultTextPaint = new Paint();
            defaultTextPaint.setStyle(Paint.Style.FILL);
            defaultTextPaint.setTextSize(35);
        }

        return defaultTextPaint;
    }

    /**
     * All ClassDiagItem will be painted with this Paint (for the rectangle outline)
     *
     * @return
     */
    public static Paint getDefaultOutlinePaint() {
        if (defaultOutlinePaint == null) {
            defaultOutlinePaint = new Paint();
            defaultOutlinePaint.setStrokeWidth(2);
            defaultOutlinePaint.setStyle(Paint.Style.STROKE);
        }

        return defaultOutlinePaint;
    }
}
