package com.example.violetdroidapplication;

import android.graphics.Canvas;

import org.json.JSONObject;

/**
 * Created by vishaalprasad on 11/29/16.
 */
public interface ClassDiagramDrawable {

    /**
     * Draw the item in a given Canvas
     * @param c Canvas on which to draw the ClassDiagramDrawable
     * @param selected whether this item is selected
     */
    void draw(Canvas c, boolean selected);

    /**
     * Check if the given points lie inside the ClassDiagramDrawable
     * @param x coordinate of point
     * @param y coordinate of point
     * @return true if this ClassDiagramDrawable contains the point, false otherwise
     */
    boolean contains(int x, int y);

    /**
     * @return a JSONObject representation of this ClassDiagramDrawable
     */
    JSONObject toJson();
}
