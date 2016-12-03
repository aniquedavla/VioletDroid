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

    private enum ArrDirections {HVH, VHV, SELF}

    //how far the user can click away from an arrow to select it
    private static final int SELECT_PADDING = 10;

    private ClassDiagShape fromShape;
    private ClassDiagShape toShape;

    //represents the
    private Point[] linePoints; //this does not need to be saved [json]
    private ArrDirections direction;

    private Rect[] selectionBounds; //used for selection

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

        if (fromShape == toShape) direction = ArrDirections.SELF;
        //else we determine arrow direction at draw time

        //if the arrow points to itself, 5 points determine the arrow, otherwise, 4 points
        //this is because a self-pointing arrow has 4 segments, all other arrows have 3 segments
        linePoints = new Point[fromShape == toShape ? 5 : 4];
        selectionBounds = new Rect[linePoints.length - 1];
    }

    /**
     * Draw this ClassDiagArrow to a given Canvas
     *
     * @param c        Canvas on which to draw the ClassDiagArrow
     * @param selected if the ClassDiagArrow is selected, changes the appearance
     */
    public void draw(Canvas c, boolean selected) {
        if (direction == ArrDirections.SELF) {
            calcSelfArrowPoints();
        } else {
            findConnectionPoints();
            calcAllPoints();
        }

        setSelectionBounds();

        //draw lines
        for (int i = 1; i < linePoints.length; i++) {
            c.drawLine(linePoints[i - 1].x, linePoints[i - 1].y, linePoints[i].x, linePoints[i].y,
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
        for (Rect rect : selectionBounds) if (rect.contains(x, y)) return true;
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

        //todo::there's definitely a way to make this code shorter
        if (deltaX < 0) { //to is LEFT of from

            if (deltaY < 0) { // to is ABOVE from
                if (fromShape.outline.top < toShape.outline.bottom) {
                    linePoints[0] = fromConnectionPoints[0]; //use left
                    linePoints[3] = toConnectionPoints[2]; //use right
                    direction = ArrDirections.HVH;
                } else {
                    linePoints[0] = fromConnectionPoints[1]; //use top
                    linePoints[3] = toConnectionPoints[3]; //use bottom
                    direction = ArrDirections.VHV;
                }
            } else { //to is BELOW from
                if (fromShape.outline.bottom > toShape.outline.top) {
                    linePoints[0] = fromConnectionPoints[0]; //use left
                    linePoints[3] = toConnectionPoints[2]; //use right
                    direction = ArrDirections.HVH;
                } else {
                    linePoints[0] = fromConnectionPoints[3]; //use bottom
                    linePoints[3] = toConnectionPoints[1]; //use top
                    direction = ArrDirections.VHV;
                }
            }

        } else { //else to is RIGHT of from

            if (deltaY < 0) { // to is ABOVE from
                if (fromShape.outline.top < toShape.outline.bottom) {
                    linePoints[0] = fromConnectionPoints[2]; //use right
                    linePoints[3] = toConnectionPoints[0]; //use left
                    direction = ArrDirections.HVH;
                } else {
                    linePoints[0] = fromConnectionPoints[1]; //use top
                    linePoints[3] = toConnectionPoints[3]; //use bottom
                    direction = ArrDirections.VHV;
                }
            } else { //to is BELOW from
                if (fromShape.outline.bottom > toShape.outline.top) {
                    linePoints[0] = fromConnectionPoints[2]; //use right
                    linePoints[3] = toConnectionPoints[0]; //use left
                    direction = ArrDirections.HVH;
                } else {
                    linePoints[0] = fromConnectionPoints[3]; //use bottom
                    linePoints[3] = toConnectionPoints[1]; //use top
                    direction = ArrDirections.VHV;
                }
            }
        }
    }

    /**
     * make the intermediate points between the start and the end
     */
    private void calcAllPoints() {
        float centerX = (linePoints[0].x + linePoints[3].x) / 2.0f;
        float centerY = (linePoints[0].y + linePoints[3].y) / 2.0f;

        switch (direction) {
            case HVH:
                linePoints[1] = new Point((int) centerX, linePoints[0].y);
                linePoints[2] = new Point((int) centerX, linePoints[3].y);
                break;
            case VHV:
                linePoints[1] = new Point(linePoints[0].x, (int) centerY);
                linePoints[2] = new Point(linePoints[3].x, (int) centerY);
                break;
            default:
                break;
        }
    }

    /**
     * Use this method to find all points that a self-pointing arrow should go through
     */
    private void calcSelfArrowPoints() {
        int shapeWd = toShape.getOutline().width();
        int shapeHt = toShape.getOutline().height();
        Rect shapeBounds = toShape.getOutline();
        int size = (int) (0.3f * (shapeWd > shapeHt ? shapeHt : shapeWd));

        //the path of the self arrow
        linePoints[0] = new Point(shapeBounds.right - size, shapeBounds.top);
        linePoints[1] = new Point(shapeBounds.right - size, shapeBounds.top - size);
        linePoints[2] = new Point(shapeBounds.right + size, shapeBounds.top - size);
        linePoints[3] = new Point(shapeBounds.right + size, shapeBounds.top + size);
        linePoints[4] = new Point(shapeBounds.right, shapeBounds.top + size);
    }


    /**
     * Add all Rects to the the selectionBounds array
     */
    private void setSelectionBounds() {
        for (int i = 0; i < selectionBounds.length; i++)
            selectionBounds[i] = selectionRectFromPoints(linePoints[i], linePoints[i + 1]);
    }

    /**
     * This Rect will be used for selecting
     * These two Points MUST be directly horizontal or vertical to each other
     *
     * @param a Point to be used in the Rect
     * @param b another Paint to be used in the Rect
     * @return a Rect containing the two points with padding (SELECT_PADDING), or null if these
     * Points are not directly horizontal or vertical to each other
     */
    private Rect selectionRectFromPoints(Point a, Point b) {
        int left;
        int top;
        int right;
        int bottom;

        if (a.x == b.x) { //the points are vertical to each other

            top = (a.y < b.y) ? a.y : b.y;
            bottom = (a.y > b.y) ? a.y : b.y;
            left = a.x - SELECT_PADDING;
            right = a.x + SELECT_PADDING;
            return new Rect(left, top, right, bottom);

        } else if (a.y == b.y) { //the points are horizontal to each other

            top = a.y - SELECT_PADDING;
            bottom = a.y + SELECT_PADDING;
            left = (a.x < b.x) ? a.x : b.x;
            right = (a.x > b.x) ? a.x : b.x;

            return new Rect(left, top, right, bottom);

        } else return null;
    }

    /**
     * Check to see if this arrow points to or from a given object
     *
     * @param drawable to check
     * @return true if the Shape points to/from the object, false otherwise
     */
    public boolean reliesOn(ClassDiagramDrawable drawable) {
        return drawable == this.fromShape || drawable == this.toShape;
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
