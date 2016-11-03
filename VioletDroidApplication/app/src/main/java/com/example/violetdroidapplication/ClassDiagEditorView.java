package com.example.violetdroidapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

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

    private ArrayList<ClassDiagItem> ClassItems;
    private ClassDiagItem selected = null; //null means none are selected
    private Context ctx;

    private int x;  // starting x-coordinate of touch
    private int y;  // starting y-coordinate of touch
    boolean draggable;  // whether selected item is ready to be dragged

    // handles long presses
    private Handler handler;
    Runnable longPress;
    boolean isLongPressed = false;

    //cell layout
    private int numColumns, numRows;
    private int cellWidth, cellHeight;
    private Paint blackPaint = new Paint();
    private boolean[][] cellChecked;

    public ClassDiagEditorView(Context context) {
        this(context, null);
    }
    /**
     * Create a new editor view
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
                }
                Log.i("", "Long press");
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
        //canvas.drawColor(Color.WHITE);
        super.onDraw(canvas);

        for (ClassDiagItem item : ClassItems) {
            //draw the text
            canvas.drawText(item.getContent(), item.getX(), item.getY(), ClassDiagItem.getDefaultTextPaint());

            //then draw the box around it
            canvas.drawRect(item.getRect(), ClassDiagItem.getDefaultOutlinePaint());
        }

        if (numColumns == 0 || numRows == 0) {
            return;
        }

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
                    Log.i(TAG, "onTouchEvent: ACTION_UP - is a tap" );
                    if (selected != null) {
                        if (selected.equals(findItem(x, y))) {
                            selected = null;
                        }
                    }
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
                    // move item by dragging
                    selected.set(moveX, moveY);
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
     * immediately adds teh view
     */
    public void addItem() {
        Log.i(TAG, "addItem");
        final EditText inputView = new EditText(ctx); //this EditText will lie inside the AlertDialog
        inputView.setHint(R.string.class_diag_enter_title_hint);
        //create the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(R.string.class_diag_enter_title);
        builder.setView(inputView);
        builder.setPositiveButton(R.string.ok_str, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String input = inputView.getText().toString();
                // 100, 100 in the following line is an arbitrary point
                selected = new ClassDiagItem(input, 100, 100); //add a new item AND select it
                ClassItems.add(selected);
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

}
