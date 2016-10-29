package com.example.violetdroidapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ClassDiagramEditorActivity extends AppCompatActivity implements View.OnClickListener {

    ClassDiagEditorView editorView;
    Button plusBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_diagram_editor);

        setViews();
    }

    private void setViews(){
        editorView = (ClassDiagEditorView) findViewById(R.id.class_diag_editor_view);
        plusBtn = (Button) findViewById(R.id.class_diag_editor_plus);
        plusBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.class_diag_editor_plus:
                editorView.addItem();
                break;
        }
    }
}
