package com.example.violetdroidapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
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

/**
 * Contains a ClassDiagEditorView and buttons to allow the user to create class diagrams
 */
public class SequenceDiagramEditorActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ClassDiagEditorAct";

    private File currentFile = null;

    private ClassDiagEditorView editorView;
    private Button plusBtn;
    private Button fileBtn;
    private Button arrowBtn;
    private Button btn4;
    private Button btn5;
    private Button btn6;
    private Button noteBtn;
    private Button deleteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ClassDiagEditorView pixelGrid = new ClassDiagEditorView(this);
        pixelGrid.setNumColumns(10);
        pixelGrid.setNumRows(20);
        setContentView(pixelGrid);

        //addContentView(R.layout.activity_class_diagram_editor,);
        LayoutInflater inflater = getLayoutInflater();
        getWindow().addContentView(inflater.inflate(R.layout.activity_sequence_diagram_editor, null),
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.FILL_PARENT,
                        ViewGroup.LayoutParams.FILL_PARENT));
        setViews();
    }

    /**
     * initialize the views in this Activity
     */
    private void setViews() {
        editorView = (ClassDiagEditorView) findViewById(R.id.seq_diag_editor_view);

        plusBtn = (Button) findViewById(R.id.seq_diag_editor_plus);
        plusBtn.setOnClickListener(this);
        fileBtn = (Button) findViewById(R.id.seq_diag_editor_file);
        fileBtn.setOnClickListener(this);
        arrowBtn = (Button) findViewById(R.id.seq_diag_editor_arrow);
        arrowBtn.setOnClickListener(this);
        btn4 = (Button) findViewById(R.id.seq_diag_editor_btn4);
        btn4.setOnClickListener(this);
        btn5 = (Button) findViewById(R.id.seq_diag_editor_btn5);
        btn5.setOnClickListener(this);
        btn6 = (Button) findViewById(R.id.seq_diag_editor_bnt6);
        btn6.setOnClickListener(this);
        noteBtn = (Button) findViewById(R.id.seq_diag_editor_note);
        noteBtn.setOnClickListener(this);
        deleteBtn = (Button) findViewById(R.id.seq_diag_editor_delete);
        deleteBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.seq_diag_editor_plus:
                editorView.addOrEditItem();
                break;
            case R.id.seq_diag_editor_file:
                fileDialog();
                break;
            case R.id.seq_diag_editor_arrow:
                addArrow();
                break;
            case R.id.seq_diag_editor_btn4:
                Toast.makeText(this, "Button not implemented", Toast.LENGTH_SHORT).show();
                break;
            case R.id.seq_diag_editor_btn5:
                Toast.makeText(this, "Button not implemented", Toast.LENGTH_SHORT).show();
                break;
            case R.id.seq_diag_editor_bnt6:
                Toast.makeText(this, "Button not implemented", Toast.LENGTH_SHORT).show();
                break;
            case R.id.seq_diag_editor_note:
                editorView.addOrEditNote();
                break;
            case R.id.seq_diag_editor_delete:
                editorView.deleteItem();
                break;
            default:
                break;
        }
    }

    /**
     * If the user tries to exit by pressing the back button make sure there are no unsaved changes
     * If there are, alert the user
     */
    @Override
    public void onBackPressed() {
        if (editorView.getSavePending()) {
            AlertDialog.Builder unsavedChangesDialog = new AlertDialog.Builder(this);
            unsavedChangesDialog.setTitle(R.string.changes_pending_dialog_title);
            unsavedChangesDialog.setMessage(R.string.changes_pending_dialog_body);
            unsavedChangesDialog.setPositiveButton(R.string.yes_str, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SequenceDiagramEditorActivity.super.onBackPressed();
                    dialog.dismiss();
                }
            });
            unsavedChangesDialog.setNegativeButton(R.string.no_str, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            unsavedChangesDialog.show();
        } else
            super.onBackPressed();
    }

    private void fileDialog() {
        AlertDialog.Builder fileDialogBuilder = new AlertDialog.Builder(this);
        fileDialogBuilder.setTitle(R.string.file);
        fileDialogBuilder.setItems(R.array.file_options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        newWorkingArea();
                        break;
                    case 1:
                        fileLoad();
                        break;
                    case 2:
                        fileSave();
                        break;
                    case 3:
                        fileSaveAs();
                        break;
                    case 4:
                        exportPrompt();
                        break;
                    default:
                        break;
                }
            }
        });
        fileDialogBuilder.setNegativeButton(R.string.cancel_str, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        fileDialogBuilder.show();
    }

    /**
     * loads a saved state from the given File
     * Logs and shows a Toast when an exception is encountered
     *
     * @param f File with saved state to fileLoad
     */
    private void loadFromFile(File f) {
        JSONObject obj = FileHelper.getJsonFromFile(f, this); //create a JSONObject from the File
        editorView.resetSpace(); //clear the view
        currentFile = f; //this is referenced later on in saveAs
        try {
            //get a JSONArray with all the items
            JSONArray arr = obj.getJSONArray(ClassDiagEditorView.ITEMS_KEY);

            //for each item, add it to the view
            for (int i = 0; i < arr.length(); i++) {
                if (arr.getJSONObject(i).getString(FileHelper.ITEM_TYPE_KEY).equals(ClassDiagItem.class.getName()))
                    editorView.addDrawable(ClassDiagItem.fromJson(arr.getJSONObject(i)));
                else if (arr.getJSONObject(i).getString(FileHelper.ITEM_TYPE_KEY).equals(ClassDiagNote.class.getName()))
                    editorView.addDrawable(ClassDiagNote.fromJson(arr.getJSONObject(i)));
                else if (arr.getJSONObject(i).getString(FileHelper.ITEM_TYPE_KEY)
                        .equals(ClassDiagArrow.class.getName()))
                    editorView.addDrawable(ClassDiagArrow.fromJson(arr.getJSONObject(i),
                            editorView.getAllClassDrawables()));
            }

            editorView.setSavePending(false); //when we fileLoad, there are no more saves pending

        } catch (Exception e) {
            Log.e(TAG, "loadFromJSON: ", e);
            Toast.makeText(this, R.string.load_error, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Save the current state
     */
    public void fileSave() {
        if (editorView.isEmpty()) //if it's empty, don't fileSave
            warnSaveEmpty();
        else if (currentFile == null) //if we are not currently "working on a file" fileSave as a new file
            fileSaveAs();
        else if (editorView.getSavePending()) { //we have unsaved changes
            FileHelper.writeFile(editorView.toJson(), currentFile, this);
            editorView.setSavePending(false);
        }

        //otherwise, we have no unsaved changes, no need to fileSave
    }

    /**
     * Lists available items by calling listItems if there are no changes pending
     */
    public void fileLoad() {
        if (editorView.getSavePending()) { // if we have changes pending then warn the user
            AlertDialog.Builder changesPendingBuilder = new AlertDialog.Builder(this);
            changesPendingBuilder.setTitle(R.string.changes_pending_dialog_title);
            changesPendingBuilder.setMessage(R.string.changes_pending_dialog_body);
            changesPendingBuilder.setPositiveButton(R.string.yes_str, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    listItems(); //list the items, the pending changes will be lost
                }
            });
            changesPendingBuilder.setNegativeButton(R.string.no_str, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss(); // just go back to editing
                }
            });
            changesPendingBuilder.show();
        } else
            listItems(); // we don't have changes pending, just list the items
    }

    /**
     * Allow the user to pick a name for the file they're saving
     */
    public void fileSaveAs() {
        if (editorView.isEmpty())
            warnSaveEmpty(); //if the current working area is empty, warn the user and do not fileSave anything
        else {
            final JSONObject obj = editorView.toJson();
            AlertDialog.Builder saveAsBuilder = new AlertDialog.Builder(this);
            final EditText fileNameEditText = new EditText(this);
            fileNameEditText.setHint(R.string.file_name_hint);
            fileNameEditText.setSingleLine();
            saveAsBuilder.setTitle(R.string.save_as);
            saveAsBuilder.setView(fileNameEditText);
            saveAsBuilder.setPositiveButton(R.string.done_str, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //check if the file exists and warn the user if it does
                    checkAndSaveJson(obj, fileNameEditText.getText().toString());
                    editorView.setSavePending(false); //once we've saved, we don't have changes pending
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
    }

    private void addArrow() {
        Thread arrowThread = new Thread(new Runnable() {
            @Override
            public void run() {
                ClassDiagShape start;
                ClassDiagShape end;

                showToast(R.string.arrow_start);
                editorView.setWaitingForArrowInput(true);
                while (editorView.isWaitingForArrowInput()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        /* do nothing */
                    }
                }

                start = editorView.getNewArrowHelper();

                showToast(R.string.arrow_end);
                editorView.setWaitingForArrowInput(true);
                while (editorView.isWaitingForArrowInput()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        /* do nothing */
                    }
                }

                end = editorView.getNewArrowHelper();

//                editorView.addDrawable(new ClassDiagArrow(start, end));

            }
        });
        arrowThread.start();
    }

    private void showToast(final int resId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SequenceDiagramEditorActivity.this, resId, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * List the available items and allows the user to pick one to load
     */
    public void listItems() {
        try {
            //get all the .vdroid files from the directory
            final File violetFiles[] = FileHelper.VIOLET_DROID_FOLDER.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(FileHelper.EXTENSION.toLowerCase());
                }
            });

            //if there are files to list, show them and ask the user to pick one
            if (violetFiles != null && violetFiles.length > 0) {
                final String fileNames[] = new String[violetFiles.length];
                for (int i = 0; i < violetFiles.length; i++)
                    fileNames[i] = violetFiles[i].getName().substring(0, violetFiles[i].getName().length()
                            - FileHelper.EXTENSION.length());
                AlertDialog.Builder listBuilder = new AlertDialog.Builder(this);

                listBuilder.setItems(fileNames, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadFromFile(violetFiles[which]); //when one is selected, fileLoad it
                    }
                });
                listBuilder.setNegativeButton(R.string.cancel_str, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); //the user hit "cancel"
                    }
                });
                listBuilder.setTitle(R.string.pick_file_title);
                listBuilder.show(); //show this dialog
            } else {
                //there are no violet files on the device, let the user know
                AlertDialog.Builder noFilesAlert = new AlertDialog.Builder(this);
                noFilesAlert.setTitle(R.string.no_violet_items_dialog_title);
                noFilesAlert.setMessage(R.string.no_violet_items_dialog_body);
                noFilesAlert.setPositiveButton(R.string.ok_str, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                noFilesAlert.show();
            }
        } catch (Exception e) {
            Toast.makeText(this, R.string.load_list_error, Toast.LENGTH_LONG).show();
            Log.e(TAG, "listLoadableItems: ", e);
        }
    }

    /**
     * check if the desired file exists before saving
     * if the file does exist, then warn the user about overwriting the file before saving
     *
     * @param obj      contents to fileSave
     * @param destFile location to fileSave the JSONObject
     */
    public void checkAndSaveJson(final JSONObject obj, final File destFile) {
        try {
            currentFile = destFile;

            if (!destFile.exists())
                FileHelper.writeFile(obj, destFile, this);
            else //the file already exists, warn the user that it will be overwritten
                warnOverwrite(obj, destFile, true);
        } catch (Exception e) {
            Toast.makeText(this, R.string.save_error, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "saveJsonToDisk: ", e);
        }
    }

    /**
     * helper method for checkAndSaveJson
     * Can pass a String instead of a File
     *
     * @param obj      contents to fileSave
     * @param fileName name for the File
     */
    public void checkAndSaveJson(final JSONObject obj, String fileName) {
        checkAndSaveJson(obj, new File(FileHelper.VIOLET_DROID_FOLDER.getAbsolutePath() + "/"
                + fileName + FileHelper.EXTENSION));
    }

    /**
     * Called when the user is trying to fileSave an empty working area
     * Just lets the user know that nothing will be done with an empty working area
     */
    public void warnSaveEmpty() {
        AlertDialog.Builder emptyAlert = new AlertDialog.Builder(this);
        emptyAlert.setTitle(R.string.empty_diagram_dialog_title);
        emptyAlert.setMessage(R.string.empty_diagram_dialog_body);
        emptyAlert.setPositiveButton(R.string.ok_str, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        emptyAlert.show();
    }

    /**
     * Prompt the user for a filename to use when saving the working area as an image
     */
    public void exportPrompt() {
        if (!editorView.isEmpty()) { //continue if it's not empty
            AlertDialog.Builder exportImgDialogBuilder = new AlertDialog.Builder(this);
            exportImgDialogBuilder.setTitle(R.string.export_dialog_title);
            final EditText fileNameEditText = new EditText(this);
            fileNameEditText.setHint(R.string.file_name_hint);
            fileNameEditText.setSingleLine();
            //if we are currently working on a file, set the text to that file's name
            if (this.currentFile != null)
                fileNameEditText.setText(currentFile.getName().substring(0,
                        currentFile.getName().length() - FileHelper.EXTENSION.length()));
            fileNameEditText.selectAll();
            exportImgDialogBuilder.setView(fileNameEditText);
            exportImgDialogBuilder.setPositiveButton(R.string.ok_str, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    exportImg(fileNameEditText.getText().toString());
                }
            });
            exportImgDialogBuilder.setNegativeButton(R.string.cancel_str, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            exportImgDialogBuilder.show();
        } else //the working area is empty, let the user know & do nothing else
            warnSaveEmpty();
    }

    /**
     * Warns the user that the file exists and prompts the user to overwrite the file
     *
     * @param toWrite           contents the user wants to fileSave
     * @param destFile          where the user wants to fileSave the contents
     * @param updateSavePending update the editorView's savePending boolean,
     *                          should be true for vdroid files, false for images
     */
    private void warnOverwrite(final Object toWrite, final File destFile, final boolean updateSavePending) {
        AlertDialog.Builder overwriteWarning = new AlertDialog.Builder(this);
        overwriteWarning.setTitle(R.string.overwrite_dialog_title);
        overwriteWarning.setMessage(R.string.overwrite_dialog_body);
        overwriteWarning.setPositiveButton(R.string.yes_str, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (FileHelper.writeFile(toWrite, destFile, SequenceDiagramEditorActivity.this))
                    if (updateSavePending)
                        editorView.setSavePending(false); //only call setSavePending if we're told to change it
            }
        });
        overwriteWarning.setNegativeButton(R.string.no_str, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); //do nothing
            }
        });
        overwriteWarning.show();
    }

    /**
     * exports the current editorView as an image
     * If the file already exists, warns the user about overwriting
     *
     * @param fileName to be used for the image
     */
    private void exportImg(String fileName) {
        Bitmap img = editorView.getBitmap();

        File destFile = new File(FileHelper.PICTURES_FOLDER, fileName + FileHelper.IMG_EXTENSION);
        if (destFile.exists()) //because we're saving this file as an image, we don't want to update the editorView's
            warnOverwrite(img, destFile, false);
        else
            FileHelper.writeFile(img, destFile, this);
    }

    /**
     * Prompt the user if they want to reset the working area
     */
    private void newWorkingArea() {
        //only show the dialog if it makes sense to do so
        if (currentFile != null || !editorView.isEmpty()) {

            AlertDialog.Builder resetAreaDialog = new AlertDialog.Builder(this);
            resetAreaDialog.setTitle(R.string.new_class_diagram_dialog_title);
            //the message is dependent on if unsaved changes are present
            resetAreaDialog.setMessage(editorView.getSavePending() ? R.string.changes_pending_dialog_body
                    : R.string.new_class_diagram_dialod_body);
            resetAreaDialog.setNegativeButton(R.string.no_str, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss(); //do nothing, the user hit "no"
                }
            });
            resetAreaDialog.setPositiveButton(R.string.yes_str, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //reset the working area
                    editorView.setSavePending(false);
                    editorView.resetSpace();
                    currentFile = null;
                }
            });
            resetAreaDialog.show();
        }
    }
}
