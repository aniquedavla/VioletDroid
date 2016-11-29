package com.example.violetdroidapplication;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by vishaalprasad on 11/28/16.
 */
public class ClassDiagArrow implements ClassDiagramDrawable {

    private static final String TAG = "ClassDiagArrow";

    private enum Directions {HVH, VHV}

    private ClassDiagShape fromShape;
    private ClassDiagShape toShape;

    //represents the
    private Point[] lineRoute; //this does not need to be saved [json]
    private Directions direction;

    private Rect bounds; //used for selection

    //todo::add arrow type (enum?)

    /**
     * Create a new ClassDiagArrow with the given attributes
     *
     * @param fromShape where the ClassDiagArrow should point from
     * @param toShape   where the ClassDiagArrow should point to
     */
    public ClassDiagArrow(ClassDiagShape fromShape, ClassDiagShape toShape) {
        this.fromShape = fromShape;
        this.toShape = toShape;
        this.lineRoute = new Point[4];
    }

    /**
     * Draw this ClassDiagArrow to a given Canvas
     *
     * @param c        Canvas on which to draw the ClassDiagArrow
     * @param selected if the ClassDiagArrow is selected, changes the appearance
     */
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

    /**
     * Check this ClassDiagArrow "contains" the given point
     *
     * @param x coordinate of the point
     * @param y coordinate of the point
     * @return true if the given point is contained, false otherwise
     */
    @Override
    public boolean contains(int x, int y) {
        return false;
    }

    /**
     * Used to find which connection points to use from the fromShape and to the toShape
     */
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

        //todo::theres definitely a way to make this code shorter
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

    /**
     * make the intermediate points between the start and the end
     */
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

    /**
     * Json representation of this ClassDiagArrow
     *
     * @return a JSONObject containing all the information needed to save and load
     */
    @Override
    public JSONObject toJson() {
        try {
            JSONObject obj = new JSONObject();

            obj.put(FileHelper.ITEM_TYPE_KEY, getClass().getName());
            obj.put("cd_arrow_start", this.fromShape.toString());
            obj.put("cd_arrow_end", this.toShape.toString());

            return obj;

        } catch (Exception e) {
            Log.e(TAG, "toJson: ", e);
            return null;
        }
    }

    /**
     * Get a ClassDiagArrow from a saved JSONObject representing a ClassDiagArrow
     *
     * @param jsonObject     representing a ClassDiagArrow
     * @param alldrDrawables all possible shapes this ClassDiagArrow can point to/from
     * @return a new ClassDiagArrow
     */
    public static ClassDiagArrow fromJson(JSONObject jsonObject,
                                          List<ClassDiagramDrawable> alldrDrawables) {
        try {
            String startShapeStr = jsonObject.getString("cd_arrow_start");
            String endShapeStr = jsonObject.getString("cd_arrow_end");

            ClassDiagShape startShape = null;
            ClassDiagShape endShape = null;

            for (ClassDiagramDrawable shape : alldrDrawables) {
                if (shape instanceof ClassDiagShape) {
                    if (shape.toString().equals(startShapeStr)) startShape = (ClassDiagShape) shape;
                    if (shape.toString().equals(endShapeStr)) endShape = (ClassDiagShape) shape;
                }
            }

            //return a new arrow if we found both the items
            return (startShape == null || endShape == null) ? null
                    : new ClassDiagArrow(startShape, endShape);
        } catch (Exception e) {
            Log.e(TAG, "fromJson: ", e);
            return null;
        }
    }
}
