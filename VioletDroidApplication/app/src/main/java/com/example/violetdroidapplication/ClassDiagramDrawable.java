package com.example.violetdroidapplication;

import android.graphics.Canvas;

import org.json.JSONObject;

/**
 * Created by vishaalprasad on 11/29/16.
 */
public interface ClassDiagramDrawable {

    void draw(Canvas c, boolean selected);

    boolean contains(int x, int y);

    JSONObject toJson();
}
