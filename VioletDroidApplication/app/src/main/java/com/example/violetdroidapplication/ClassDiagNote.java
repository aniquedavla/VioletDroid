package com.example.violetdroidapplication;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

import org.json.JSONObject;

/**
 * A ClassDiagNote represents a note in the class diagram.
 * The user may select its background color from a pre-defined list.
 */
public class ClassDiagNote extends ClassDiagShape {
    private static final String TAG = "ClassDiagNote";

    private static final int PADDING = 20;

    private String text;

    private float x;
    private float y;

    /**
     * Create a new ClassDiagNote
     *
     * @param text the text in the note
     * @param x    coordinate to place this item
     * @param y    coordinate to place this item
     */
    public ClassDiagNote(String text, float x, float y) {
        this.text = text;
        this.x = x;
        this.y = y;

        Log.i(TAG, "ClassDiagNote: text: " + text);
    }

    /**
     * Set the position (bottom left)
     *
     * @param x coordinate of new position
     * @param y coordinate of new position
     */
    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Sets the text for the note
     *
     * @param text The text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Retrieves the note's text.
     *
     * @return text
     */
    public String getText() {
        return text;
    }

    /**
     * Calculate how wide this item will end up being
     *
     * @return the int width
     */
    private int calcMaxWidth() {
        int maxWd = 0;
        for (String line : text.split("\n")) {
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
        float ht = (PADDING * 2)
                + (Paints.getDefaultTextPaint().descent()
                - Paints.getDefaultTextPaint().ascent()) * text.split("\n").length;

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
        outline = bounds;

        Point[] clippedOutline = getFoldPoints(bounds);

        Path bgPath = new Path();
        bgPath.moveTo(clippedOutline[0].x, clippedOutline[0].y);

        //draw the clipped outline
        for (int i = 1; i < clippedOutline.length; i++) {
            c.drawLine(clippedOutline[i - 1].x, clippedOutline[i - 1].y, clippedOutline[i].x,
                    clippedOutline[i].y, Paints.getDefaultOutlinePaint());

            bgPath.lineTo(clippedOutline[i].x, clippedOutline[i].y);
        }
        //manually close the last to the first
        c.drawLine(clippedOutline[clippedOutline.length - 1].x,
                clippedOutline[clippedOutline.length - 1].y, clippedOutline[0].x,
                clippedOutline[0].y, Paints.getDefaultOutlinePaint());
        bgPath.lineTo(clippedOutline[0].x, clippedOutline[0].y);

        bgPath.close();
        c.drawPath(bgPath, Paints.getDefaultBgNotePaint(selected));

        //draw the text
        drawMultiLineText(text, c, Paints.getDefaultTextPaint(), x, y, PADDING, bounds);
    }

    /**
     * Convert a Rect to a set of points that form the shape of a folded note (little triangle
     * is clipped off the top right hand corner)
     *
     * @param bounds to convert
     * @return a set of Points with length of 5, [0] refers to top left, goes counter clockwise
     */
    public Point[] getFoldPoints(Rect bounds) {
        Point[] result = new Point[5];
        int clipped = (int) (bounds.height() * 0.2);

        result[0] = new Point(bounds.left, bounds.top);
        result[1] = new Point(bounds.left, bounds.bottom);
        result[2] = new Point(bounds.right, bounds.bottom);
        result[3] = new Point(bounds.right, bounds.top + clipped);
        result[4] = new Point(bounds.right - clipped, bounds.top);

        return result;
    }

    /**
     * Draws multiline text and sets the bounds of the text in the given bounds
     *
     * @param toDraw  String to draw
     * @param c       Canvas on which to draw
     * @param p       Paint to use to draw text
     * @param xC      coordinate to draw the text
     * @param yC      coordinate to draw the text
     * @param padding between x&y and start of text
     * @param bounds  Rect to place the bounds of the drawn text
     */
    private void drawMultiLineText(String toDraw, Canvas c, Paint p, float xC, float yC, int padding, Rect bounds) {
        float x = xC;
        float y = yC;
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
     * @return a JSON representation of this ClassDiagItem
     */
    public JSONObject toJson() {
        try {
            JSONObject obj = new JSONObject();

            obj.put(FileHelper.ITEM_TYPE_KEY, getClass().getName());
            obj.put(FileHelper.LOC_X_KEY, this.x);
            obj.put(FileHelper.LOC_Y_KEY, this.y);
            obj.put("cdn_text", text);

            return obj;

        } catch (Exception e) {
            Log.e(TAG, "toJson: ", e);
            return null;
        }
    }

    /**
     * get a ClassDiagNote from a JSONObject
     *
     * @param obj JSONObject representation of a ClassDiagNote
     * @return a ClassDiagNote of the given JSONObject
     */
    public static ClassDiagNote fromJson(JSONObject obj) {
        try {
            return new ClassDiagNote(obj.getString("cdn_text"), (float) obj.getDouble(FileHelper.LOC_X_KEY),
                    (float) obj.getDouble(FileHelper.LOC_Y_KEY));
        } catch (Exception e) {
            Log.e(TAG, "fromJson: ", e);
            return null;
        }
    }

    /**
     * @return this note's text
     */
    @Override
    public String toString() {
        return text;
    }
}
