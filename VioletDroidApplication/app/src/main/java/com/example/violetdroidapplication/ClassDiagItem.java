package com.example.violetdroidapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import org.json.JSONObject;

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
//    private Context ctx;
    private Rect outline; //Outermost Rect that contains this item

    private static final int PADDING = 20;
    private static final int TITLE_PADDING = 30;

    /**
     * Create a new ClassDiagItem
     *
     * @param title
     * @param x
     * @param y
     */
    public ClassDiagItem(String title, String attributes, String methods, float x, float y) {
//        this.ctx = ctx;
        this.title = title;
        this.attributes = attributes;
        this.methods = methods;
        this.x = x;
        this.y = y;

        Log.i(TAG, "ClassDiagItem: methods: " + methods);
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

    /**
     * Calculate how wide this item will end up being
     *
     * @return the int width
     */
    private int calcMaxWidth() {
        int maxWd = 0;
        for (String line : title.split("\n")) {
            Rect temp = new Rect();
            Paints.getDefaultTextTitlePaint().getTextBounds(line, 0, line.length(), temp);
            if (maxWd < temp.width() + TITLE_PADDING * 2)
                maxWd = temp.width() + TITLE_PADDING * 2;
        }
        for (String line : attributes.split("\n")) {
            Rect temp = new Rect();
            Paints.getDefaultTextPaint().getTextBounds(line, 0, line.length(), temp);
            if (maxWd < temp.width() + PADDING * 2)
                maxWd = temp.width() + PADDING * 2;
        }
        for (String line : methods.split("\n")) {
            Rect temp = new Rect();
            Paints.getDefaultTextPaint().getTextBounds(line, 0, line.length(), temp);
            if (maxWd < temp.width() + PADDING * 2)
                maxWd = temp.width() + PADDING * 2;
        }
        return maxWd;
    }

    /**
     * Calculates how tall this item will end up being
     * Includes the padding
     *
     * @return an int height
     */
    private int calcMaxHeight() {
        float ht = (TITLE_PADDING * 2) +
                (Paints.getDefaultTextTitlePaint().descent() - Paints.getDefaultTextTitlePaint().ascent()) * title.split("\n").length;

        if (attributes.length() + methods.length() > 0) {
            ht += PADDING * 4; //twice for attributes, twice for methods (each top/bottom)
            int lineCt = attributes.split("\n").length + methods.split("\n").length;
            ht += (Paints.getDefaultTextPaint().descent() - Paints.getDefaultTextPaint().ascent()) * lineCt;
        }

        return (int) ht;
    }

    /**
     * Draw this item in the given Canvas
     *
     * @param c        The canvas on which to draw
     * @param selected Whether the item is selected
     */
    public void draw(Canvas c, boolean selected) {
        //find out how big the outermost rectangle has to be
        Rect bounds = new Rect();
        bounds.left = (int) x;
        bounds.top = (int) y;
        bounds.right = (int) x + this.calcMaxWidth();
        bounds.bottom = bounds.top + calcMaxHeight();
        this.outline = bounds;

        //draw the rectangle outline
        c.drawRect(bounds, Paints.getDefaultOutlinePaint());
        //then draw the rectangle background
        c.drawRect(bounds, Paints.getDefaultBgPaint(selected));

        //draw the title
        Rect titleBounds = new Rect();
        drawMultiLineText(title, c, Paints.getDefaultTextTitlePaint(), x, y, TITLE_PADDING, titleBounds);

        if (this.methods.length() + this.attributes.length() > 0) {

            //horizontal line below the title
            c.drawLine(x, y + titleBounds.height(), x + bounds.width(), y + titleBounds.height(), Paints.getDefaultOutlinePaint());

            //draw the attributes
            Rect attrBounds = new Rect();
            drawMultiLineText(attributes, c, Paints.getDefaultTextPaint(), x, y + titleBounds.height(),
                    PADDING, attrBounds);

            //horizontal line below the attributes
            c.drawLine(x, y + titleBounds.height() + attrBounds.height(),
                    x + bounds.width(), y + titleBounds.height() + attrBounds.height(),
                    Paints.getDefaultOutlinePaint());

            //draw the methods
            Rect methodsBounds = new Rect();
            drawMultiLineText(methods, c, Paints.getDefaultTextPaint(), x,
                    y + titleBounds.height() + attrBounds.height(), PADDING, methodsBounds);
        }
    }

    /**
     * Draws multiline text and sets the bounds of the text in the given bounds
     *
     * @param toDraw  String to draw
     * @param c       Canvas on which to draw
     * @param p       Paint to use to draw text
     * @param x       coordinate to draw the text
     * @param y       coordinate to draw the text
     * @param padding between x&y and start of text
     * @param bounds  Rect to place the bounds of the drawn text
     */
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
     * @return a JSON representation of this ClassDiagItem
     */
    public JSONObject toJson() {
        try {
            JSONObject obj = new JSONObject();

            obj.put(FileHelper.ITEM_TYPE_KEY, getClass().getName());
            obj.put(FileHelper.LOC_X_KEY, this.x);
            obj.put(FileHelper.LOC_Y_KEY, this.y);
            obj.put("cdi_title", title);
            obj.put("cdi_attrs", attributes);
            obj.put("cdi_methods", methods);

            return obj;

        } catch (Exception e) {
            Log.e(TAG, "toJson: ", e);
            return null;
        }
    }

    /**
     * get a ClassDiagItem from a JSONObject
     * @param obj JSONObject representation of a ClassDiagItem
     * @return a ClassDiagItem of the given JSONObject
     */
    public static ClassDiagItem fromJson(JSONObject obj) {
        try {
            return new ClassDiagItem(obj.getString("cdi_title"), obj.getString("cdi_attrs"),
                    obj.getString("cdi_methods"), (float) obj.getDouble(FileHelper.LOC_X_KEY),
                    (float) obj.getDouble(FileHelper.LOC_Y_KEY));
        } catch (Exception e) {
            Log.e(TAG, "fromJson: ", e);
            return null;
        }
    }

}
