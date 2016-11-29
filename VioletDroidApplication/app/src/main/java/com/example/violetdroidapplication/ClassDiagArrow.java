package com.example.violetdroidapplication;

import android.graphics.Canvas;
import android.graphics.Point;
import android.util.Log;

import org.json.JSONObject;

/**
 * Created by vishaalprasad on 11/28/16.
 */
public class ClassDiagArrow implements ClassDiagramDrawable {

    private static final String TAG = "ClassDiagArrow";

    private enum Directions {HVH, VHV}

    private ClassDiagShape fromShape;
    private ClassDiagShape toShape;

//    private Point fromPoint;
//    private Point toPoint;

    private Point[] lineRoute; //this does not need to be saved [json]
    private Directions direction;

    //todo::add arrow type (enum?)

    public ClassDiagArrow(ClassDiagShape fromShape, ClassDiagShape toShape) {
        this.fromShape = fromShape;
        this.toShape = toShape;
        this.lineRoute = new Point[4];
    }

    public void draw(Canvas c, boolean selected) {
        findConnectionPoints();
        getIntermediatePoints();

        //draw lines
        for (int i = 1; i < lineRoute.length; i++) {
            c.drawLine(lineRoute[i - 1].x, lineRoute[i - 1].y,
                    lineRoute[i].x, lineRoute[i].y,
                    Paints.getDefaultArrowPaint(selected));
        }
    }

    @Override
    public boolean contains(int x, int y) {
        return false;
    }

    private void findConnectionPoints() {
        int deltaX = toShape.outline.centerX() - fromShape.outline.centerX();
        int deltaY = toShape.outline.centerY() - fromShape.outline.centerY();

        Point toConnectionPoints[] = new Point[4];
        toConnectionPoints[0] = new Point(toShape.outline.left, toShape.outline.centerY());
        toConnectionPoints[1] = new Point(toShape.outline.centerX(), toShape.outline.top);
        toConnectionPoints[2] = new Point(toShape.outline.right, toShape.outline.centerY());
        toConnectionPoints[3] = new Point(toShape.outline.centerX(), toShape.outline.bottom);

        Point fromConnectionPoints[] = new Point[4];
        fromConnectionPoints[0] = new Point(fromShape.outline.left, fromShape.outline.centerY());
        fromConnectionPoints[1] = new Point(fromShape.outline.centerX(), fromShape.outline.top);
        fromConnectionPoints[2] = new Point(fromShape.outline.right, fromShape.outline.centerY());
        fromConnectionPoints[3] = new Point(fromShape.outline.centerX(), fromShape.outline.bottom);

        if (deltaX < 0) { //to is LEFT of from

            if (deltaY < 0) { // to is ABOVE from
                if (fromShape.outline.top < toShape.outline.bottom) {
                    lineRoute[0] = fromConnectionPoints[0]; //use left
                    lineRoute[3] = toConnectionPoints[2]; //use right
                    direction = Directions.HVH;
                } else {
                    lineRoute[0] = fromConnectionPoints[1]; //use top
                    lineRoute[3] = toConnectionPoints[3]; //use bottom
                    direction = Directions.VHV;
                }
            } else { //to is BELOW from
                if (fromShape.outline.bottom > toShape.outline.top) {
                    lineRoute[0] = fromConnectionPoints[0]; //use left
                    lineRoute[3] = toConnectionPoints[2]; //use right
                    direction = Directions.HVH;
                } else {
                    lineRoute[0] = fromConnectionPoints[3]; //use bottom
                    lineRoute[3] = toConnectionPoints[1]; //use top
                    direction = Directions.VHV;
                }
            }

        } else { //else to is RIGHT of from

            if (deltaY < 0) { // to is ABOVE from
                if (fromShape.outline.top < toShape.outline.bottom) {
                    lineRoute[0] = fromConnectionPoints[2]; //use right
                    lineRoute[3] = toConnectionPoints[0]; //use left
                    direction = Directions.HVH;
                } else {
                    lineRoute[0] = fromConnectionPoints[1]; //use top
                    lineRoute[3] = toConnectionPoints[3]; //use bottom
                    direction = Directions.VHV;
                }
            } else { //to is BELOW from
                if (fromShape.outline.bottom > toShape.outline.top) {
                    lineRoute[0] = fromConnectionPoints[2]; //use right
                    lineRoute[3] = toConnectionPoints[0]; //use left
                    direction = Directions.HVH;
                } else {
                    lineRoute[0] = fromConnectionPoints[3]; //use bottom
                    lineRoute[3] = toConnectionPoints[1]; //use top
                    direction = Directions.VHV;
                }
            }
        }
    }

    private void getIntermediatePoints() {
        float centerX = (lineRoute[0].x + lineRoute[3].x) / 2.0f;
        float centerY = (lineRoute[0].y + lineRoute[3].y) / 2.0f;

        if (direction == Directions.HVH) {
            lineRoute[1] = new Point((int) centerX, lineRoute[0].y);
            lineRoute[2] = new Point((int) centerX, lineRoute[3].y);
        } else {
            lineRoute[1] = new Point(lineRoute[0].x, (int) centerY);
            lineRoute[2] = new Point(lineRoute[3].x, (int) centerY);
        }
    }

    @Override
    public JSONObject toJson() {
        try {
            JSONObject obj = new JSONObject();

            obj.put(FileHelper.ITEM_TYPE_KEY, getClass().getName());
            obj.put("cd_arrow_start", this.fromShape.toString());
            obj.put("cd_arrow_end", this.fromShape.toString());

            return obj;

        } catch (Exception e) {
            Log.e(TAG, "toJson: ", e);
            return null;
        }
    }
}
