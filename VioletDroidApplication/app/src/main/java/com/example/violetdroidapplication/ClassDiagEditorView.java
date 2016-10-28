package com.example.violetdroidapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

/**
 * Created by vishaalprasad on 10/27/16.
 */
public class ClassDiagEditorView extends View {
    private static final String TAG = "ClassDiagEditorView";

    private ArrayList<ClassDiagItem> ClassItems;
    private ClassDiagItem selected = null; //null means none are selected
    private Context ctx;

    public ClassDiagEditorView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        this.ctx = ctx;
        ClassItems = new ArrayList<>();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        for (ClassDiagItem item : ClassItems) {
            //draw the text
            canvas.drawText(item.getContent(), item.getX(), item.getY(), ClassDiagItem.getDefaultTextPaint());

            //then draw the box around it
            canvas.drawRect(item.getRect(), ClassDiagItem.getDefaultOutlinePaint());
        }


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        int x = Math.round(event.getX());
        int y = Math.round(event.getY());

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "onTouchEvent: ACTION_DOWN [" + x + "," + y + "]");
                selected = findItem(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                if (selected != null)
                    selected.set(x, y);
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

    public void addItem() {
        Log.i(TAG, "addItem");
        final EditText inputView = new EditText(ctx);
        inputView.setHint(R.string.class_diag_enter_title_hint);
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(R.string.class_diag_enter_title);
        builder.setView(inputView);
        builder.setPositiveButton(R.string.ok_str, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String input = inputView.getText().toString();
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
        builder.show();

        postInvalidate();
    }

}
