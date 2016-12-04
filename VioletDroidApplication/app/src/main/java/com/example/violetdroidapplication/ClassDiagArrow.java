package com.example.violetdroidapplication;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by vishaalprasad on 11/28/16.
 */
public class ClassDiagArrow implements ClassDiagramDrawable {

    /**
     * possible arrow head types
     */
    public enum ArrHeadType { EMPTY, V, TRIANGLE, FILLED_TRIANGLE, DIAMOND, FILLED_DIAMOND }

    private static final String TAG = "ClassDiagArrow";

    private enum ArrDirections { HVH, VHV, SELF } //calculated automatically, user can not set this

    //how far the user can click away from an arrow to select it
    private final double ARROW_ANGLE = Math.PI / 6d;

    //this information needs to be saved
    private ClassDiagShape fromShape;
    private ClassDiagShape toShape;
    private boolean solid;
    private ArrHeadType startHead;
    private ArrHeadType endHead;
    //todo::add option for unbent arrow (different class, logic is very different)

    //represents the points that define the line s
    private Point[] linePoints; //this does not need to be saved [json]
    private ArrDirections direction;

    private Rect[] selectionBounds; //used for selection

    /**
     * Create a new ClassDiagArrow with the given attributes
     *
     * @param fromShape where the ClassDiagArrow should point from
     * @param toShape   where the ClassDiagArrow should point to
     * @param solid     true for solid line arrow, false for dashed
     * @param startHead the type of arrowhead to be used at the start of the arrow
     * @param endHead   the type of arrowhead to be used at the end of an arrow
     */
    public ClassDiagArrow(ClassDiagShape fromShape, ClassDiagShape toShape, boolean solid,
                          ArrHeadType startHead, ArrHeadType endHead) {
        this.fromShape = fromShape;
        this.toShape = toShape;
        this.solid = solid;
        this.startHead = startHead;
        this.endHead = endHead;

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
        if (fromShape == null || toShape == null) return;

        if (fromShape == toShape) this.direction = ArrDirections.SELF; //arrow points to itself

        if (direction == ArrDirections.SELF) {
            calcSelfArrowPoints();
        } else {
            findConnectionPoints();
            calcAllPoints();
        }

        setSelectionBounds();

        //draw lines
        Path pathLines = new Path();
        pathLines.moveTo(linePoints[0].x, linePoints[0].y);
        for (int i = 1; i < linePoints.length; i++)
            pathLines.lineTo(linePoints[i].x, linePoints[i].y);
        c.drawPath(pathLines, Paints.getDefaultArrowPaint(selected, this.solid));

        //then draw the arrow heads
        drawArrowHead(c, selected, this.startHead, linePoints[0], linePoints[1]);
        drawArrowHead(c, selected, this.endHead,
                linePoints[linePoints.length - 1], linePoints[linePoints.length - 2]);
    }

    /**
     * Draw an arrow head with the given parameters
     *
     * @param c              Canvas used for drawing
     * @param selected       if this arrow is selected
     * @param headType       type of arrow head to be drawn
     * @param location       where the arrow points exactly
     * @param directionPoint another point used to determine direction
     */
    private void drawArrowHead(Canvas c, boolean selected, ArrHeadType headType,
                               Point location, Point directionPoint) {
        if (headType == ArrHeadType.EMPTY) return;

        float dx = location.x - directionPoint.x;
        float dy = location.y - directionPoint.y;

        double angle = Math.atan2(dy, dx);

        int arrLength = Paints.dpFrompx(20);

        /* logic inspired by Cay Horstmann's Violet */
        float x1 = (float) (location.x - arrLength * Math.cos(angle + ARROW_ANGLE));
        float y1 = (float) (location.y - arrLength * Math.sin(angle + ARROW_ANGLE));
        float x2 = (float) (location.x - arrLength * Math.cos(angle - ARROW_ANGLE));
        float y2 = (float) (location.y - arrLength * Math.sin(angle - ARROW_ANGLE));

        Path outlinePath = new Path();
        outlinePath.moveTo(location.x, location.y);
        outlinePath.lineTo(x1, y1);
        boolean fill = (headType == ArrHeadType.FILLED_DIAMOND
                || headType == ArrHeadType.FILLED_TRIANGLE);

        if (headType == ArrHeadType.V) {
            outlinePath.moveTo(x2, y2);
            outlinePath.lineTo(location.x, location.y);
        } else if (headType == ArrHeadType.TRIANGLE || headType == ArrHeadType.FILLED_TRIANGLE) {
            outlinePath.lineTo(x2, y2);
            outlinePath.close();
        } else if (headType == ArrHeadType.DIAMOND || headType == ArrHeadType.FILLED_DIAMOND) {
            float x3 = (float) (x2 - arrLength * Math.cos(angle + ARROW_ANGLE));
            float y3 = (float) (y2 - arrLength * Math.sin(angle + ARROW_ANGLE));
            outlinePath.lineTo(x3, y3);
            outlinePath.lineTo(x2, y2);
            outlinePath.close();
        }

        c.drawPath(outlinePath, Paints.getDefaultArrowHeadFillPaint(selected, fill));
        c.drawPath(outlinePath, Paints.getDefaultArrowPaint(selected, true));
    }

    /**
     * Check if this ClassDiagArrow "contains" the given point
     *
     * @param x coordinate of the point
     * @param y coordinate of the point
     * @return true if the given point is contained, false otherwise
     */
    @Override
    public boolean contains(int x, int y) {
        if (selectionBounds == null) return false;

        for (Rect currRect : selectionBounds) {
            if (currRect != null && currRect.contains(x, y)) return true;
        }

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

        int selectPadding = Paints.dpFrompx(20);

        if (a.x == b.x) { //the points are vertical to each other

            top = (a.y < b.y) ? a.y : b.y;
            bottom = (a.y > b.y) ? a.y : b.y;
            left = a.x - selectPadding;
            right = a.x + selectPadding;
            return new Rect(left, top, right, bottom);

        } else if (a.y == b.y) { //the points are horizontal to each other

            top = a.y - selectPadding;
            bottom = a.y + selectPadding;
            left = (a.x < b.x) ? a.x : b.x;
            right = (a.x > b.x) ? a.x : b.x;

            return new Rect(left, top, right, bottom);

        } else return null;
    }

    /**
     * Check to see if this arrow points to or from a given object
     * To be used when deleting items
     *
     * @param drawable to check
     * @return true if the Shape points to/from the object, false otherwise
     */
    public boolean reliesOn(ClassDiagramDrawable drawable) {
        return drawable == this.fromShape || drawable == this.toShape;
    }

    /**
     * Set which shapes this arrow points to and from
     *
     * @param fromShape where this arrow should point from
     * @param toShape   where this arrow should point to
     */
    public void setFromAndToShape(ClassDiagShape fromShape, ClassDiagShape toShape) {
        this.fromShape = fromShape;
        this.toShape = toShape;

        linePoints = new Point[fromShape == toShape ? 5 : 4];
        selectionBounds = new Rect[linePoints.length - 1];
    }

    /**
     * @return the type of Arrow head that lies at the start of this arrow
     */
    public ArrHeadType getStartHead() {
        return startHead;
    }

    /**
     * @return the type of Arrow head that lies at the end of this arrow
     */
    public ArrHeadType getEndHead() {
        return endHead;
    }

    /**
     * Set a new type of arrowhead that this arrow should start with
     *
     * @param startHead new start head
     */
    public void setStartHead(ArrHeadType startHead) {
        this.startHead = startHead;
    }

    /**
     * Set a new type of arrowhead that this arrow should start with
     *
     * @param endHead new end head
     */
    public void setEndHead(ArrHeadType endHead) {
        this.endHead = endHead;
    }

    /**
     * @return true if this arrow has a solid line, false for dashed
     */
    public boolean isSolid() {
        return solid;
    }

    /**
     * Set dashed vs solid line
     *
     * @param solid true to set this arrow's line as solid, false for dashed
     */
    public void setSolid(boolean solid) {
        this.solid = solid;
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
            obj.put("cd_arrow_solid", this.solid);
            obj.put("cd_arrow_start_head", this.startHead.ordinal());
            obj.put("cd_arrow_end_head", this.endHead.ordinal());

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
            boolean solid = jsonObject.getBoolean("cd_arrow_solid");
            ArrHeadType startHead = ArrHeadType.values()[jsonObject.getInt("cd_arrow_start_head")];
            ArrHeadType endHead = ArrHeadType.values()[jsonObject.getInt("cd_arrow_end_head")];

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
                    : new ClassDiagArrow(startShape, endShape, solid, startHead, endHead);
        } catch (Exception e) {
            Log.e(TAG, "fromJson: ", e);
            return null;
        }
    }
}
