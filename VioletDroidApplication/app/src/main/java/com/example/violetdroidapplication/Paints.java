package com.example.violetdroidapplication;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.TypedValue;

/**
 * Created by vishaalprasad on 11/17/16.
 */
public class Paints {

    public static Paint defaultTextPaint;
    public static Paint defaultTextTitlePaint;
    public static Paint defaultOutlinePaint;
    public static Paint defaultBgPaint;

    private static int SELECTED_BG_PAINT = Color.parseColor("#DBE9F9");  // color of selected item

    private static Context ctx;

    /**
     * Call this in the MainActivity
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

    public static Paint getDefaultTextPaint() {
        return defaultTextPaint;
    }

    public static Paint getDefaultTextTitlePaint() {
        return defaultTextTitlePaint;
    }

    public static Paint getDefaultOutlinePaint() {
        return defaultOutlinePaint;
    }

    public static Paint getDefaultBgPaint(boolean selected) {
        defaultBgPaint.setColor(selected ? SELECTED_BG_PAINT : Color.WHITE);

        return defaultBgPaint;
    }

    private static int dpFrompx(int px, Context ctx) {

        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                px, ctx.getResources().getDisplayMetrics());

    }
}
