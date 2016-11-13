package com.example.violetdroidapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            FileHelper.VIOLET_DROID_FOLDER.mkdir();
        } catch (Exception e){
            Toast.makeText(this, "Error making violetDriod directory", Toast.LENGTH_LONG);
            Log.e(TAG, "onCreate: ", e);
        }


        ((Button) findViewById(R.id.main_class_editor_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editorIntent = new Intent(MainActivity.this, ClassDiagramEditorActivity.class);
                startActivity(editorIntent);
            }
        });


    }
}
