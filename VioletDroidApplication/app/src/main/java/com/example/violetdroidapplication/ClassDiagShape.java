package com.example.violetdroidapplication;

/**
 * A shape representing an object in the class diagram
 */
// TODO: remove duplicate fields/methods from ClassDiagItem
public abstract class ClassDiagShape {
    private float x;
    private float y;

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
}
