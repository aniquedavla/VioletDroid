package com.example.violetdroidapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FilenameFilter;

public class ClassDiagramEditorActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ClassDiagEditorAct";

    private File currentFile = null;

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
                save();
                break;
            case R.id.class_diag_editor_save_as:
                saveAs();
                break;
            case R.id.class_diag_editor_load:
                load();
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

    private void loadFromFile(File f) {
        JSONObject obj = FileHelper.getJsonFromFile(f, this);
        editorView.resetSpace();
        currentFile = f;
        try {
            JSONArray arr = obj.getJSONArray(ClassDiagEditorView.ITEMS_KEY);

            for (int i = 0; i < arr.length(); i++) {
                if (arr.getJSONObject(i).getString(FileHelper.ITEM_TYPE_KEY).equals(ClassDiagItem.class.getName()))
                    editorView.addItem(ClassDiagItem.fromJson(arr.getJSONObject(i)));
                // todo::if it's an arrow then ArrowsList.add the item
            }


        } catch (Exception e) {
            Log.e(TAG, "loadFromJSON: ", e);
            Toast.makeText(this, R.string.load_error, Toast.LENGTH_LONG).show();
        }
    }

    public void save() {
        if (currentFile == null) saveAs();
        else FileHelper.checkAndSave(editorView.toJson(), currentFile, this);
    }

    public void load() {
        if (editorView.getSavePending()) {
            AlertDialog.Builder changesPendingBuilder = new AlertDialog.Builder(this);
            changesPendingBuilder.setTitle(R.string.changes_pending_dialog_title);
            changesPendingBuilder.setMessage(R.string.changes_pending_dialog_body);
            changesPendingBuilder.setPositiveButton(R.string.yes_str, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    listItems();
                }
            });
        } else
            listItems();

    }

    public void saveAs() {
        final JSONObject obj = editorView.toJson();
        AlertDialog.Builder saveAsBuilder = new AlertDialog.Builder(this);
        final EditText fileNameEditText = new EditText(this);
        fileNameEditText.setHint(R.string.file_name_hint);
        saveAsBuilder.setView(fileNameEditText);
        saveAsBuilder.setPositiveButton(R.string.done_str, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FileHelper.checkAndSave(obj, fileNameEditText.getText().toString(), ClassDiagramEditorActivity.this);
                editorView.setSavePending(false);
            }
        });
        saveAsBuilder.setNegativeButton(R.string.cancel_str, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        saveAsBuilder.show();
    }

    public void listItems() {
        try {
            final File violetFiles[] = Environment.getExternalStorageDirectory().listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(FileHelper.EXTENSION);
                }
            });
            final String fileNames[] = new String[violetFiles.length];
            for (int i = 0; i < violetFiles.length; i++)
                fileNames[i] = violetFiles[i].getName().substring(0, (int) violetFiles[i].length() - FileHelper.EXTENSION.length());

            AlertDialog.Builder listBuilder = new AlertDialog.Builder(this);
            listBuilder.setItems(fileNames, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    loadFromFile(violetFiles[which]);
                }
            });
            listBuilder.setNegativeButton(R.string.cancel_str, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, R.string.load_list_error, Toast.LENGTH_LONG).show();
            Log.e(TAG, "listLoadableItems: ", e);
        }
    }
}
