package com.example.violetdroidapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by vishaalprasad on 10/27/16.
 */

/**
 * A View to be used inside an Activity
 * This View will be used as the main editor for Class Diagrams
 */
public class ClassDiagEditorView extends DiagEditorView {
    /**
     * used for saving files
     */
    public static final String ITEMS_KEY = "items";
    private static final String FILE_TYPE = "class_diagram";
    private static final String TAG = "ClassDiagEditorView";

    //Items: everything here needs to be saved/loaded
    private ArrayList<ClassDiagramDrawable> allClassDrawables;

    private ClassDiagramDrawable selected = null; //null means none are selected
    private Context ctx;

    private int x;  // starting x-coordinate of touch
    private int y;  // starting y-coordinate of touch
    private boolean draggable;  // whether selected item is ready to be dragged

    // handles long presses
    private Handler handler;
    private Runnable longPress;
    private boolean isLongPressed = false;

    //only used temporarily
    private ClassDiagShape newArrowHelper;
    private boolean waitingForArrowInput = false;

    //cell layout
    private int numColumns;
    private int numRows;
    private int cellWidth;
    private int cellHeight;
    private Paint blackPaint = new Paint();
    private boolean[][] cellChecked;

    private boolean savePending = false;

    /**
     * @param context of this application
     */
    public ClassDiagEditorView(Context context) {
        this(context, null);
    }

    /**
     * Create a new editor view
     *
     * @param ctx   of this application
     * @param attrs attributes used to create this View
     */
    public ClassDiagEditorView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        blackPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.ctx = ctx;
        allClassDrawables = new ArrayList<>();

        handler = new Handler();
        longPress = new Runnable() {
            public void run() {
                isLongPressed = true;
                if (findItem(x, y) != null) {
                    selected = findItem(x, y);
                    postInvalidate();
                }
                Log.i(TAG, "Long press");
            }
        };
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // longPress will be called in 800 ms if not cancelled
                if (waitingForArrowInput) {
                    ClassDiagramDrawable tapped = findItem((int) event.getX(), (int) event.getY());
                    if (tapped != null && tapped instanceof ClassDiagShape) {
                        newArrowHelper = (ClassDiagShape) tapped;
                        waitingForArrowInput = false;
                    }
                } else {
                    handler.postDelayed(longPress, 800);
                    Log.i(TAG, "onTouchEvent: ACTION_DOWN [" + x + "," + y + "]");
                    x = Math.round(event.getX());
                    y = Math.round(event.getY());
                    if (findItem(x, y) != null) {
                        if (findItem(x, y).equals(selected)) {
                            draggable = true;
                        }
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                // call to longPress cancelled (if press&hold < 800 ms)
                handler.removeCallbacks(longPress);

                // code for handling short taps
                if (Math.abs(event.getX() - x) <= 2 && !isLongPressed) {
                    Log.i(TAG, "onTouchEvent: ACTION_UP - is a tap");
                    // deselect item
                    if (selected != null) {
                        if (selected.equals(findItem(x, y))) {
                            selected = null;
                        }
                    }
                    postInvalidate();
                    return false;
                }

                Log.i(TAG, "onTouchEvent: ACTION_UP - not a tap");
                isLongPressed = false;
                draggable = false;
                break;

            case MotionEvent.ACTION_MOVE:
                // call to longPress cancelled (if press&hold < 800 ms)
                handler.removeCallbacks(longPress);
                int moveX = Math.round(event.getX());
                int moveY = Math.round(event.getY());
                if (draggable && selected != null && selected instanceof ClassDiagShape) {
                    ClassDiagShape selectedShape = (ClassDiagShape) selected;
                    selectedShape.set(moveX, moveY);  // move item by dragging
                    savePending = true;
                }

                break;
            default:
                break;
        }

        postInvalidate();
        return true;
    }

    /**
     * Add a ClassDiagramDrawable to this View
     * @param drawable to add to this View
     */
    public void addDrawable(ClassDiagramDrawable drawable) {
        allClassDrawables.add(drawable);
        postInvalidate();
    }


    /**
     * @return true if this working area is empty, false otherwise
     */
    public boolean isEmpty() {
//        return (this.mClassItems.isEmpty() && this.mClassNotes.isEmpty());
        return this.allClassDrawables.isEmpty();
    }

    /**
     * @return an ArrayList contianing all the drawables
     */
    public ArrayList<ClassDiagramDrawable> getAllClassDrawables() {
        return allClassDrawables;
    }

    /**
     * Get whether this View is waiting for an arrow input
     * @return waitingForArrowInput
     */
    public boolean isWaitingForArrowInput() {
        return waitingForArrowInput;
    }

    /**
     * set this View's waitingForArrowInput
     * @param waitingForArrowInput new value to set
     */
    public void setWaitingForArrowInput(boolean waitingForArrowInput) {
        this.waitingForArrowInput = waitingForArrowInput;
    }

    /**
     * Get the object that the user tapped if waiting for an object
     * @return ClassDiagShape that the user tapped
     */
    public ClassDiagShape getNewArrowHelper() {
        return newArrowHelper;
    }

    /**
     * @return a JSONObject that contains all the information of this editor
     */
    public JSONObject toJson() {
        try {
            JSONArray arr = new JSONArray();
            for (ClassDiagramDrawable drawable : allClassDrawables)
                arr.put(drawable.toJson());

            JSONObject obj = new JSONObject();
            obj.put(FileHelper.FILE_TYPE_KEY, FILE_TYPE);
            obj.put(ITEMS_KEY, arr);

            return obj;

        } catch (Exception e) {
            Log.e(TAG, "toArray: ", e);
            Toast.makeText(ctx, R.string.save_error, Toast.LENGTH_LONG).show();
            return null;
        }
    }

    /**
     * @return true if there is a change pending to be saved, false otherwise
     */
    public boolean getSavePending() {
        return savePending;
    }

    /**
     * @param savePending new boolean whether change is pending
     */
    public void setSavePending(boolean savePending) {
        this.savePending = savePending;
    }

    /**
     * Removes all items and "clears the working space"
     * THIS SHOULD ONLY BE CALLED AFTER USER'S CONSENT
     */
    public void resetSpace() {
        allClassDrawables.clear();
        selected = null;

        savePending = false;
        postInvalidate();
    }

    /**
     * Deletes the currently selected item
     */
    public void deleteItem() {
        if (selected != null) {
            allClassDrawables.remove(selected);
            savePending = true; //if an item is deleted, a save is pending
            selected = null; //now nothing is selected
            draggable = false;
        }

        postInvalidate();
    }

    /**
     * @return a Bitmap object containing this View's items
     */
    public Bitmap getBitmap() {
        Bitmap result = Bitmap.createBitmap(this.getWidth(), this.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Drawable bgDrawable = this.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        this.draw(canvas);
        return result;
    }

}