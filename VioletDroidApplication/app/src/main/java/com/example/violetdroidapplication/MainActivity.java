package com.example.violetdroidapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Starting Activity of this application
 * Handles initialization of the application
 */
public class MainActivity extends AppCompatActivity {

//    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.initialize();

    }

    private void initialize() {
        Paints.initializePaints(this);

        FileHelper.initializeFiles();

        ((Button) findViewById(R.id.main_class_editor_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editorIntent = new Intent(MainActivity.this, ClassDiagramEditorActivity.class);
                startActivity(editorIntent);
            }
        });

    }
}
