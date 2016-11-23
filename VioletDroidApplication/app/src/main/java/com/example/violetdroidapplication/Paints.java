package com.example.violetdroidapplication;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.TypedValue;

/**
 * Used to manage Paint objects
 */
public final class Paints {

    private static Paint defaultTextPaint;
    private static Paint defaultTextTitlePaint;
    private static Paint defaultOutlinePaint;
    private static Paint defaultBgPaint;

    private static final int SELECTED_BG_PAINT = Color.parseColor("#DBE9F9");  // color of selected item

    /**
     * Private constructor to make checkstyle happy
     */
    private Paints() { }

    /**
     * Call this in the MainActivity, sets all the Paint objects to be used later on
     * @param ctx Context of the application
     */
    public static void initializePaints(Context ctx) {
        defaultTextTitlePaint = new Paint();
        defaultTextTitlePaint.setStyle(Paint.Style.FILL);
        defaultTextTitlePaint.setTextSize(dpFrompx(22, ctx));

        defaultTextPaint = new Paint();
        defaultTextPaint.setStyle(Paint.Style.FILL);
        defaultTextPaint.setTextSize(dpFrompx(18, ctx));

        defaultOutlinePaint = new Paint();
        defaultOutlinePaint.setStrokeWidth(dpFrompx(1, ctx));
        defaultOutlinePaint.setStyle(Paint.Style.STROKE);
        defaultOutlinePaint.setColor(Color.BLACK);

        defaultBgPaint = new Paint();
        defaultBgPaint.setStrokeWidth(0);
        defaultBgPaint.setStyle(Paint.Style.FILL);
    }

    /**
     * @return the default Paint to be used with non-title text
     */
    public static Paint getDefaultTextPaint() {
        return defaultTextPaint;
    }

    /**
     * @return the default Paint to be used with Title text
     */
    public static Paint getDefaultTextTitlePaint() {
        return defaultTextTitlePaint;
    }

    /**
     * @return the default Paint to be used with the Outline of a Class Item
     */
    public static Paint getDefaultOutlinePaint() {
        return defaultOutlinePaint;
    }

    /**
     * @param selected changes background color to blue if true, white otherwise
     * @return the default Paint to be used with the background
     */
    public static Paint getDefaultBgPaint(boolean selected) {
        defaultBgPaint.setColor(selected ? SELECTED_BG_PAINT : Color.WHITE);

        return defaultBgPaint;
    }

    /**
     * convert Px to Dp
     *
     * @param px  a number in pixels unit
     * @param ctx Context used to calculate the conversion
     * @return px represented as dp for this device
     */
    private static int dpFrompx(int px, Context ctx) {

        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                px, ctx.getResources().getDisplayMetrics());

    }
}
