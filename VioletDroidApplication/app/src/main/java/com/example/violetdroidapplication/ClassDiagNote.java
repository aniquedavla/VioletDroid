package com.example.violetdroidapplication;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import org.json.JSONObject;

/**
 * A ClassDiagNote represents a note in the class diagram.
 * The user may select its background color from a pre-defined list.
 */
public class ClassDiagNote {
    private static final String TAG = "ClassDiagNote";

    private static final int PADDING = 20;

    private String text;

    private float x;
    private float y;
    private Rect outline; // Outermost Rect that contains this item

    /**
     * Create a new ClassDiagNote
     *
     * @param text       the text in the note
     * @param x          coordinate to place this item
     * @param y          coordinate to place this item
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
        this.outline = bounds;

        //draw the rectangle outline
        c.drawRect(bounds, Paints.getDefaultOutlinePaint());
        //then draw the rectangle background
        c.drawRect(bounds, Paints.getDefaultBgPaint(selected));

        //draw the text
        drawMultiLineText(text, c, Paints.getDefaultTextPaint(), x, y, PADDING, bounds);
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
     * Check to see if the given location is contained in this ClassDiagNote
     *
     * @param x coordinate of location
     * @param y coordinate of location
     * @return true if the given location is contained in this ClassDiagNote, false otherwise
     */
    public boolean contains(int x, int y) {
        return this.outline.contains(x, y);
    }

}
