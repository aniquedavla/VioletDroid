package com.example.violetdroidapplication;

import android.graphics.Rect;

/**
 * A shape representing an object in the class diagram
 */
// TODO: remove duplicate fields/methods from ClassDiagItem and ClassDiagNote
public abstract class ClassDiagShape implements ClassDiagramDrawable {
    protected float x;
    protected float y;

    protected Rect outline;

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
     * Check to see if the given location is contained in this ClassDiagItem
     *
     * @param x coordinate of location
     * @param y coordinate of location
     * @return true if the given location is contained in this ClassDiagItem, false otherwise
     */
    public boolean contains(int x, int y) {
        return this.outline.contains(x, y);
    }

    /**
     * @return the Rect that defines this Shape
     */
    public Rect getOutline() {
        return this.outline;
    }
}
