package com.example.violetdroidapplication;

import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by vishaalprasad on 10/27/16.
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

    public ClassDiagItem(String content, float x, float y) {
        this.content = content;
        this.x = x;
        this.y = y;
        outline = new Rect();
        setRect();
    }

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

    private void setRect() {
        //takes into account the padding
        getDefaultTextPaint().getTextBounds(content, 0, content.length(), outline);
        this.outline.set((int) x - DEFAULT_PADDING, (int) y - outline.height() - DEFAULT_PADDING,
                (int) x + outline.width() + DEFAULT_PADDING, (int) y + DEFAULT_PADDING);
    }

    public Rect getRect() {
        return outline;
    }

    public boolean contains(int x, int y) {
        return this.outline.contains(x, y);
    }

    public static Paint getDefaultTextPaint() {
        if (defaultTextPaint == null) {
            defaultTextPaint = new Paint();
            defaultTextPaint.setStyle(Paint.Style.FILL);
            defaultTextPaint.setTextSize(35);
        }

        return defaultTextPaint;
    }

    public static Paint getDefaultOutlinePaint() {
        if (defaultOutlinePaint == null) {
            defaultOutlinePaint = new Paint();
            defaultOutlinePaint.setStrokeWidth(2);
            defaultOutlinePaint.setStyle(Paint.Style.STROKE);
        }

        return defaultOutlinePaint;
    }
}
