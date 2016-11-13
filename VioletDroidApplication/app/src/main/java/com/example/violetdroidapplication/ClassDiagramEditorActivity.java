package com.example.violetdroidapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class ClassDiagramEditorActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ClassDiagEditorAct";

    private ClassDiagEditorView editorView;
    private Button plusBtn;
    private Button saveBtn;
    private Button saveAsBtn;
    private Button loadBtn;
    private Button button4;
    private Button button5;
    private Button button6;
    private Button button7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ClassDiagEditorView pixelGrid = new ClassDiagEditorView(this);
        pixelGrid.setNumColumns(10);
        pixelGrid.setNumRows(20);
        setContentView(pixelGrid);

        //addContentView(R.layout.activity_class_diagram_editor,);
        LayoutInflater inflater = getLayoutInflater();
        getWindow().addContentView(inflater.inflate(R.layout.activity_class_diagram_editor, null),
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.FILL_PARENT,
                        ViewGroup.LayoutParams.FILL_PARENT));
        setViews();
    }

    private void setViews() {
        editorView = (ClassDiagEditorView) findViewById(R.id.class_diag_editor_view);

        plusBtn = (Button) findViewById(R.id.class_diag_editor_plus);
        plusBtn.setOnClickListener(this);
        saveBtn = (Button) findViewById(R.id.class_diag_editor_save);
        saveBtn.setOnClickListener(this);
        saveAsBtn = (Button) findViewById(R.id.class_diag_editor_save_as);
        saveAsBtn.setOnClickListener(this);
        loadBtn = (Button) findViewById(R.id.class_diag_editor_load);
        loadBtn.setOnClickListener(this);
        button4 = (Button) findViewById(R.id.button4);
        button4.setOnClickListener(this);
        button5 = (Button) findViewById(R.id.button5);
        button5.setOnClickListener(this);
        button6 = (Button) findViewById(R.id.button6);
        button6.setOnClickListener(this);
        button7 = (Button) findViewById(R.id.button7);
        button7.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.class_diag_editor_plus:
                editorView.addItem();
                break;
            case R.id.class_diag_editor_save:
                editorView.save();
                break;
            case R.id.class_diag_editor_save_as:
                editorView.saveAs();
                break;
            case R.id.class_diag_editor_load:
                editorView.load();
                break;
            case R.id.button4:
                Toast.makeText(this, "Not implemented yet!", Toast.LENGTH_LONG).show();
                break;
            case R.id.button5:
                Toast.makeText(this, "Not implemented yet!", Toast.LENGTH_LONG).show();
                break;
            case R.id.button6:
                Toast.makeText(this, "Not implemented yet!", Toast.LENGTH_LONG).show();
                break;
            case R.id.button7:
                Toast.makeText(this, "Not implemented yet!", Toast.LENGTH_LONG).show();
                break;
        }
    }
}
