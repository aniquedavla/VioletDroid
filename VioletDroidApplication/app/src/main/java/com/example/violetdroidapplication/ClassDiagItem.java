package com.example.violetdroidapplication;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

/**
 * Created by vishaalprasad on 10/27/16.
 */

/**
 * A ClassDiagItem represents one Item in a Class Diagram Editor
 * It contains the text and the Rectangle around the text
 */
public class ClassDiagItem {
    private static final String TAG = "ClassDiagItem";

    private String title, attributes, methods;

    private float x;
    private float y;
    private Rect outline; //Outermost Rect that contains this item

    private static Paint defaultTextPaint;
    private static Paint defaultTextTitlePaint;
    private static Paint defaultOutlinePaint;
    private static Paint defaultBgPaint;

    private static final int PADDING = 20; // TODO: 11/7/16 rename to PADDING
    private static final int TITLE_PADDING = 30; // TODO: 11/7/16 rename to PADDING

    /**
     * Create a new ClassDiagItem
     *
     * @param title
     * @param x
     * @param y
     */
    public ClassDiagItem(String title, String attributes, String methods, float x, float y) {
        this.title = title;
        this.attributes = attributes;
        this.methods = methods;
        this.x = x;
        this.y = y;

        Log.i(TAG, "ClassDiagItem: methods: " + methods);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public void setMethods(String methods) {
        this.methods = methods;
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
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public String getTitle() {
        return title;
    }

    /**
     * @return the outline of this rectangle
     */
    public Rect getOutline() {
        return outline;
    }

//    public Rect getTitleOutline(){
//
//    }
//
//    public Rect getAttrsOutline(){
//
//    }
//
//    public Rect getMethodsOutline(){
//
//    }

    private int calcMaxWidth() {
        int maxWd = 0;
        for (String line : title.split("\n")) {
            Rect temp = new Rect();
            getDefaultTextTitlePaint().getTextBounds(line, 0, line.length(), temp);
            if (maxWd < temp.width() + TITLE_PADDING * 2)
                maxWd = temp.width() + TITLE_PADDING * 2;
        }
        for (String line : attributes.split("\n")) {
            Rect temp = new Rect();
            getDefaultTextPaint().getTextBounds(line, 0, line.length(), temp);
            if (maxWd < temp.width() + PADDING * 2)
                maxWd = temp.width() + PADDING * 2;
        }
        for (String line : methods.split("\n")) {
            Rect temp = new Rect();
            getDefaultTextPaint().getTextBounds(line, 0, line.length(), temp);
            if (maxWd < temp.width() + PADDING * 2)
                maxWd = temp.width() + PADDING * 2;
        }
        return maxWd;
    }

    private int calcMaxHeight() {
        float ht = (TITLE_PADDING * 2) +
                (getDefaultTextTitlePaint().descent() - getDefaultTextTitlePaint().ascent()) * title.split("\n").length;

        if (attributes.length() + methods.length() > 0) {
            ht += PADDING * 4; //twice for attributes, twice for methods (each top/bottom)
            int lineCt = attributes.split("\n").length + methods.split("\n").length;
            ht += (getDefaultTextPaint().descent() - getDefaultTextPaint().ascent()) * lineCt;
        }

        return (int) ht;

    }

    public void draw(Canvas c) {
        Rect bounds = new Rect();
        bounds.left = (int) x;
        bounds.top = (int) y;
        bounds.right = (int) x + this.calcMaxWidth();
        bounds.bottom = bounds.top + calcMaxHeight();
        this.outline = bounds;

        c.drawRect(bounds, getDefaultOutlinePaint());
        c.drawRect(bounds, getDefaultBgPaint());

        Rect titleBounds = new Rect();
        drawMultiLineText(title, c, getDefaultTextTitlePaint(), x, y, TITLE_PADDING, titleBounds);

        c.drawLine(x, y + titleBounds.height(), x + bounds.width(), y + titleBounds.height(), getDefaultOutlinePaint());

        Rect attrBounds = new Rect();
        drawMultiLineText(attributes, c, getDefaultTextPaint(), x, y + titleBounds.height(),
                PADDING, attrBounds);

        c.drawLine(x, y + titleBounds.height() + attrBounds.height(),
                x + bounds.width(), y + titleBounds.height() + attrBounds.height(),
                getDefaultOutlinePaint());

        Rect methodsBounds = new Rect();
        drawMultiLineText(methods, c, getDefaultTextPaint(), x,
                y + titleBounds.height() + attrBounds.height(), PADDING, methodsBounds);
    }

    private void drawMultiLineText(String toDraw, Canvas c, Paint p, float x, float y, int padding, Rect bounds) {
        bounds.left = (int) x;
        bounds.top = (int) y;
        bounds.right = (int) x;
        bounds.bottom = (int) (y + p.descent() - p.ascent()); //only used if the String is blank

        y += padding - p.ascent();

        for (String line : toDraw.split("\n")) {
            c.drawText(line, x + padding, y, p);

            Rect currBounds = new Rect();
            p.getTextBounds(line, 0, line.length(), currBounds);

            if (bounds.width() < currBounds.width())
                bounds.right = bounds.left + currBounds.width() + padding * 2;

            bounds.bottom = (int) (y + p.descent() + padding);

            y += p.descent() - p.ascent();
        }
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
     * All ClassDiagItem will be painted with this Paint (for the Title)
     *
     * @return
     */
    public static Paint getDefaultTextTitlePaint() {
        if (defaultTextTitlePaint == null) {
            defaultTextTitlePaint = new Paint();
            defaultTextTitlePaint.setStyle(Paint.Style.FILL);
            defaultTextTitlePaint.setTextSize(42);
        }

        return defaultTextTitlePaint;
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
            defaultTextPaint.setTextSize(34);
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
            defaultOutlinePaint.setStrokeWidth(3);
            defaultOutlinePaint.setStyle(Paint.Style.STROKE);
            defaultOutlinePaint.setColor(Color.BLACK);
        }

        return defaultOutlinePaint;
    }

    /**
     * All ClassDiagItem will be painted with this Paint (for the rectangle background)
     *
     * @return
     */
    public static Paint getDefaultBgPaint() {
        if (defaultBgPaint == null) {
            defaultBgPaint = new Paint();
            defaultBgPaint.setStrokeWidth(0);
            defaultBgPaint.setStyle(Paint.Style.FILL);
            defaultBgPaint.setColor(Color.WHITE);
        }

        return defaultBgPaint;
    }
}
