package com.example.violetdroidapplication;

import android.graphics.Canvas;

import org.json.JSONObject;

/**
 * Any item that canbe selected in a UML editor
 */
public interface UmlDrawable {

    /**
     * Draw the item in a given Canvas
     * @param c Canvas on which to draw the UmlDrawable
     * @param selected whether this item is selected
     */
    void draw(Canvas c, boolean selected);

    /**
     * Check if the given points lie inside the UmlDrawable
     * @param x coordinate of point
     * @param y coordinate of point
     * @return true if this UmlDrawable contains the point, false otherwise
     */
    boolean contains(int x, int y);

    /**
     * @return a JSONObject representation of this UmlDrawable
     */
    JSONObject toJson();
}
