package com.example.violetdroidapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Use to manage Files, i.e. saving and loading
 */
public final class FileHelper {
    /** Location for all saved data*/
    public static final File VIOLET_DROID_FOLDER
            = new File(Environment.getExternalStorageDirectory() + "/violetdroid/");
    /** Location for all saved photos */
    public static final File PICTURES_FOLDER = new File(Environment.getExternalStorageDirectory() + "/Pictures/");
    /** Extension for saved files */
    public static final String EXTENSION = ".vdroid";
    /** Extension used for images */
    public static final String IMG_EXTENSION = ".png";

    /** key used to get a file type (which editor) */
    public static final String FILE_TYPE_KEY = "file_type";
    /** key used to get an item type */
    public static final String ITEM_TYPE_KEY = "item_type";
    /** key used to get an item's location  */
    public static final String LOC_X_KEY = "location_x";
    /** key used to get an item's location  */
    public static final String LOC_Y_KEY = "location_y";

    private static final String TAG = "FileHelper";

    /**
     * Private constructor to make checkstyle happy
     */
    private FileHelper() { }

    /**
     * Saves the given contents to the given location
     * Will overwrite if the file already exists
     *
     * @param obj contents to sv
     * @param f   location to save the object
     * @param ctx used to throw toast
     * @return true if save was successful, false otherwise
     */
    public static boolean writeFile(Object obj, File f, Context ctx) {
        if (obj instanceof JSONObject)
            return writeJsonFile((JSONObject) obj, f, ctx);
        else if (obj instanceof Bitmap)
            return writeImageFile((Bitmap) obj, f, ctx);
        else {
            Log.w(TAG, "writeFile: not designed to handle [" + obj.getClass().getSimpleName() + "]");
            return false;
        }
    }

    /**
     * Writes an image file at the given location
     * Will overwrite if it already exists
     *
     * @param image           to be written
     * @param destinationFile where to write the given image
     * @param ctx Context of the application
     * @return true if the file was written successfully, false otherwise
     */
    public static boolean writeImageFile(Bitmap image, File destinationFile, Context ctx) {
        FileOutputStream out = null;
        try {
            if (destinationFile.exists()) destinationFile.delete();
            out = new FileOutputStream(destinationFile);
            image.compress(Bitmap.CompressFormat.PNG, 100, out); // PNG is lossless --> 100 is ignored
            Toast.makeText(ctx, R.string.save_successful, Toast.LENGTH_SHORT).show();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "writeImageFile: Error:", e);
            Toast.makeText(ctx, R.string.save_error, Toast.LENGTH_SHORT).show();
            return false;
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (IOException e) {
                //do nothing
            }
        }
    }

    /**
     * Writes an JSONObject at the given location
     * Will overwrite if it already exists
     *
     * @param jObj            to be written
     * @param destinationFile where to write the given JSONObject
     * @param ctx Context of the application
     * @return true if the file was written successfully, false otherwise
     */
    public static boolean writeJsonFile(JSONObject jObj, File destinationFile, Context ctx) {
        try {
            destinationFile.createNewFile();
            PrintWriter out = new PrintWriter(destinationFile);
            out.print(jObj.toString());
            out.close();
            Toast.makeText(ctx, R.string.save_successful, Toast.LENGTH_SHORT).show();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "writeFile: ", e);
            Toast.makeText(ctx, R.string.save_error, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * Return the JSONObject contents of a given File
     *
     * @param f   File where the JSONObject is located
     * @param ctx used to throw a toast
     * @return JSONObject contents of a given file
     */
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

    /**
     * Create the directory in which the violetDroid items will be saved
     */
    public static void initializeFiles() {
        if (!FileHelper.VIOLET_DROID_FOLDER.exists())
            if (!FileHelper.VIOLET_DROID_FOLDER.mkdir())
                Log.e(TAG, "initialize: could not create violetDriod directory ["
                        + FileHelper.VIOLET_DROID_FOLDER.getAbsolutePath() + "]");

        if (!FileHelper.PICTURES_FOLDER.exists()) {
            Log.w(TAG, "initialize: Pictures directory did not already exist. Attempting to create it now");
            if (!FileHelper.PICTURES_FOLDER.mkdir())
                Log.e(TAG, "initialize: could not create pictures directory");
        }
    }
}
