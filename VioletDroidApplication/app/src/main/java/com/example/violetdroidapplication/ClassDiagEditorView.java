package com.example.violetdroidapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
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
public class ClassDiagEditorView extends View {
    private static final String TAG = "ClassDiagEditorView";

    //Items: everything in this block needs to be saved/loaded
    private ArrayList<ClassDiagItem> ClassItems;
    //todo::add a list of arrows

    private ClassDiagItem selected = null; //null means none are selected
    private Context ctx;

    private int x;  // starting x-coordinate of touch
    private int y;  // starting y-coordinate of touch
    boolean draggable;  // whether selected item is ready to be dragged

    // handles long presses
    private Handler handler;
    private Runnable longPress;
    private boolean isLongPressed = false;

    //cell layout
    private int numColumns, numRows;
    private int cellWidth, cellHeight;
    private Paint blackPaint = new Paint();
    private boolean[][] cellChecked;

    //saving and loading
    private boolean savePending = false;
    public static final String ITEMS_KEY = "items";
    private static final String FILE_TYPE = "class_diagram";

    public ClassDiagEditorView(Context context) {
        this(context, null);
    }

    /**
     * Create a new editor view
     *
     * @param ctx
     * @param attrs
     */
    public ClassDiagEditorView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        blackPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.ctx = ctx;
        ClassItems = new ArrayList<>();

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

    public void setNumColumns(int numColumns) {
        this.numColumns = numColumns;
        calculateDimensions();
    }

    public int getNumColumns() {
        return numColumns;
    }

    public void setNumRows(int numRows) {
        this.numRows = numRows;
        calculateDimensions();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        calculateDimensions();
    }

    private void calculateDimensions() {
        if (numColumns < 1 || numRows < 1) {
            return;
        }

        cellWidth = getWidth() / numColumns;
        cellHeight = getHeight() / numRows;

        cellChecked = new boolean[numColumns][numRows];

        invalidate();
    }

    public int getNumRows() {
        return numRows;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Log.d(TAG, "onDraw, selected: " + selected);

        for (ClassDiagItem item : ClassItems) {
            if (selected != null) {
                if (selected.equals(item)) {
                    item.draw(canvas, true);
                } else {
                    item.draw(canvas, false);
                }
            } else {
                item.draw(canvas, false);
            }
        }

        if (numColumns == 0 || numRows == 0)
            return;

        int width = getWidth();
        int height = getHeight();

        for (int i = 0; i < numColumns; i++) {
            for (int j = 0; j < numRows; j++) {
                if (cellChecked[i][j]) {

                    canvas.drawRect(i * cellWidth, j * cellHeight,
                            (i + 1) * cellWidth, (j + 1) * cellHeight,
                            blackPaint);
                }
            }
        }

        for (int i = 1; i < numColumns; i++) {
            canvas.drawLine(i * cellWidth, 0, i * cellWidth, height, blackPaint);
        }

        for (int i = 1; i < numRows; i++) {
            canvas.drawLine(0, i * cellHeight, width, i * cellHeight, blackPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // longPress will be called in 800 ms if not cancelled
                handler.postDelayed(longPress, 800);
                Log.i(TAG, "onTouchEvent: ACTION_DOWN [" + x + "," + y + "]");
                x = Math.round(event.getX());
                y = Math.round(event.getY());
                if (findItem(x, y) != null) {
                    if (findItem(x, y).equals(selected)) {
                        draggable = true;
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                // call to longPress cancelled (if press&hold < 800 ms)
                handler.removeCallbacks(longPress);

                // code for handling short taps
                if (Math.abs(event.getX() - x) <= 2 && !isLongPressed) {
                    Log.i(TAG, "onTouchEvent: ACTION_UP - is a tap");
                    // unselect item
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
                if (draggable && selected != null) {
                    selected.set(moveX, moveY);  // move item by dragging
                    savePending = true;
                }

                break;
        }

        postInvalidate();
        return true;
    }

    public ClassDiagItem findItem(int x, int y) {
        Log.i(TAG, "findItem");
        for (ClassDiagItem item : ClassItems)
            if (item.contains(x, y))
                return item;

        Log.i(TAG, "findItem: FOUND NOTHING: returning null");
        return null;
    }

    /**
     * prompts the user to add an Item with some String
     * immediately adds the view
     */
    public void addItem() {
        Log.i(TAG, "addItem");
        final LinearLayout inputHolders = new LinearLayout(ctx);
        inputHolders.setOrientation(LinearLayout.VERTICAL);
        final EditText inputTitleView = new EditText(ctx); //this EditText will lie inside the AlertDialog
        inputTitleView.setHint(R.string.class_diag_enter_title_hint);
        final EditText inputAttrsView = new EditText(ctx); //this EditText will lie inside the AlertDialog
        inputAttrsView.setHint(R.string.class_diag_enter_attrs_hint);
        final EditText inputMethodsView = new EditText(ctx); //this EditText will lie inside the AlertDialog
        inputMethodsView.setHint(R.string.class_diag_enter_methods_hint);
        inputHolders.addView(inputTitleView);
        inputHolders.addView(inputAttrsView);
        inputHolders.addView(inputMethodsView);
        //create the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(R.string.class_diag_enter_title);
        builder.setView(inputHolders);
        builder.setPositiveButton(R.string.ok_str, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String inputTitle = inputTitleView.getText().toString();
                String inputAttrs = inputAttrsView.getText().toString();
                String inputMethods = inputMethodsView.getText().toString();

                // 100, 100 in the following line is an arbitrary point
                selected = new ClassDiagItem(inputTitle, inputAttrs, inputMethods, 100, 100); //add a new item AND select it
                ClassItems.add(selected);
                savePending = true;
                postInvalidate();
            }
        });
        builder.setNegativeButton(R.string.cancel_str, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show(); //show the AlertDialog

        postInvalidate(); //once we're out of the AlertDialog, force update the view 
    }

    public void addItem(ClassDiagItem cdi) {
        ClassItems.add(cdi);
        postInvalidate();
    }

    public boolean isEmpty(){
        return this.ClassItems.isEmpty();
    }

    public JSONObject toJson() {
        try {
            JSONArray arr = new JSONArray();
            for (ClassDiagItem currItem : ClassItems)
                arr.put(currItem.toJson());

            JSONObject obj = new JSONObject();
            obj.put(FileHelper.FILE_TYPE_KEY, FILE_TYPE);
            obj.put(ITEMS_KEY, arr);

            return obj;

        } catch (Exception e) {
            Toast.makeText(ctx, R.string.save_error, Toast.LENGTH_LONG).show();
            Log.e(TAG, "toArray: ", e);
            return null;
        }
    }

    public boolean getSavePending(){ return savePending; }

    public void setSavePending(boolean savePending){
        this.savePending = savePending;
    }

    /**
     * Removes all items and "clears the working space"
     * THIS SHOULD ONLY BE CALLED AFTER USER'S CONSENT
     */
    public void resetSpace() {
        ClassItems.clear();
        //todo::add arrowsList.clear()
        savePending = false;
    }

}
