package com.example.violetdroidapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.PrintWriter;

/**
 * Created by vishaalprasad on 11/12/16.
 */
public class FileHelper {
    private static final String TAG = "FileHelper";

    public static final File VIOLET_DROID_FOLDER = new File(Environment.getExternalStorageDirectory() +"/violetdroid/");
    public static final String EXTENSION = ".vdroid";

    //the following are to be used with JSONObjects
    public static final String FILE_TYPE_KEY = "file_type";
    public static final String ITEM_TYPE_KEY = "item_type";
    public static final String LOC_X_KEY = "location_x";
    public static final String LOC_Y_KEY = "location_y";

    public static boolean writeFile(JSONObject obj, File f) {
        try {
            f.createNewFile();
            PrintWriter out = new PrintWriter(f);
            out.print(obj.toString());
            out.close();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "writeFile: ", e);
            return false;
        }
    }

    public static JSONObject getJsonFromFile(File f, Context ctx) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(f));
            return new JSONObject(reader.readLine());
        } catch (Exception e) {
            Toast.makeText(ctx, R.string.load_error, Toast.LENGTH_LONG).show();
            Log.e(TAG, "loadItem: ", e);
            return null;
        }
    }

}
