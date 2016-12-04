package com.example.violetdroidapplication;

import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
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
    private static Paint defaultBgNotePaint;
    private static Paint defaultArrowPaint;
    private static Paint defaultArrowHeadFillPaint;

    private static Context ctx;

    private static final int SELECTED_BG_PAINT = Color.parseColor("#DBE9F9");  // color of selected item
    private static final int DEFAULT_NOTE_COLOR = Color.parseColor("#FFFFBB");  // color of selected item

    /**
     * Private constructor to make checkstyle happy
     */
    private Paints() {
    }

    /**
     * Call this in the MainActivity, sets all the Paint objects to be used later on
     *
     * @param ctx Context of the application
     */
    public static void initializePaints(Context ctx) {
        Paints.ctx = ctx;
        defaultTextTitlePaint = new Paint();
        defaultTextTitlePaint.setStyle(Paint.Style.FILL);
        defaultTextTitlePaint.setTextSize(dpFrompx(22, ctx));

        defaultTextPaint = new Paint();
        defaultTextPaint.setStyle(Paint.Style.FILL);
        defaultTextPaint.setTextSize(dpFrompx(18, ctx));

        defaultOutlinePaint = new Paint();
        defaultOutlinePaint.setStrokeWidth(dpFrompx(2, ctx));
        defaultOutlinePaint.setStyle(Paint.Style.STROKE);
        defaultOutlinePaint.setColor(Color.BLACK);

        defaultBgPaint = new Paint();
        defaultBgPaint.setStrokeWidth(0);
        defaultBgPaint.setStyle(Paint.Style.FILL);

        defaultBgNotePaint = new Paint();
        defaultBgNotePaint.setStrokeWidth(0);
        defaultBgNotePaint.setStyle(Paint.Style.FILL);

        defaultArrowPaint = new Paint();
        defaultArrowPaint.setStrokeWidth(dpFrompx(2, ctx));
        defaultArrowPaint.setStyle(Paint.Style.STROKE);

        defaultArrowHeadFillPaint = new Paint();
        defaultArrowHeadFillPaint.setStyle(Paint.Style.FILL);
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
     * @param selected changes background color to blue if true, light yellow otherwise
     * @return the default Paint to be used with the background of a note
     */
    public static Paint getDefaultBgNotePaint(boolean selected) {
        defaultBgNotePaint.setColor(selected ? SELECTED_BG_PAINT : DEFAULT_NOTE_COLOR);
        return defaultBgNotePaint;
    }

    /**
     * To be used with arrow lines AND arrow head outlines
     *
     * @param selected whether the Arrow is selected
     * @param solid    whether the line we want is dashed
     * @return the default Paint to be used with arrows
     */
    public static Paint getDefaultArrowPaint(boolean selected, boolean solid) {
        defaultArrowPaint.setColor(selected ? Color.BLUE : Color.BLACK);
        defaultArrowPaint.setStrokeWidth(selected ? dpFrompx(4, ctx) : 2);
        if (!solid && !selected) //dashed effect
            defaultArrowPaint.setPathEffect(new DashPathEffect(new float[]{24, 16}, 0));
        else if (!solid)
            defaultArrowPaint.setPathEffect(new DashPathEffect(new float[]{36, 24}, 0));
        else
            defaultArrowPaint.setPathEffect(null);
        return defaultArrowPaint;
    }

    /**
     * Paint to be used with ArrowHeads that have a fill
     *
     * @param selected    makes the Paint blue in color, false for black
     * @param filledBlack true for a black fill, false for white
     * @return the default paint to be used with filled arrow heads
     */
    public static Paint getDefaultArrowHeadFillPaint(boolean selected, boolean filledBlack) {

        if (selected) defaultArrowHeadFillPaint.setColor(Color.BLUE);
        else if (filledBlack) defaultArrowHeadFillPaint.setColor(Color.BLACK);
        else defaultArrowHeadFillPaint.setColor(Color.WHITE);
        return defaultArrowHeadFillPaint;
    }

    /**
     * convert pixels to density independent pixels
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
