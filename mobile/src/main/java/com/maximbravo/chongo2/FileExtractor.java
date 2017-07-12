package com.maximbravo.chongo2;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Maxim Bravo on 7/10/2017.
 */

public class FileExtractor extends Activity{
    private static final int READ_REQUEST_CODE = 1337;

    private String fileString;
    public static final String TAG = "StorageClientFragment";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        performFileSearch();
    }


    /**
     * Fires an intent to spin up the "file chooser" UI and select an image.
     */
    public void performFileSearch() {

        // BEGIN_INCLUDE (use_open_document_intent)
        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a file (as opposed to a list
        // of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers, it would be
        // "*/*".
        intent.setType("text/*");

        startActivityForResult(intent, READ_REQUEST_CODE);
        // END_INCLUDE (use_open_document_intent)
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        Log.i(TAG, "Received an \"Activity Result\"");
        // BEGIN_INCLUDE (parse_open_document_response)
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code READ_REQUEST_CODE.
        // If the request code seen here doesn't match, it's the response to some other intent,
        // and the below code shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.  Pull that uri using "resultData.getData()"
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                Log.i(TAG, "Uri: " + uri.toString());
                showImage(uri);
            }
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("name", fileName);
            startActivity(intent);
            // END_INCLUDE (parse_open_document_response)
        }
    }
    private String fileName;
    /**
     * Given the URI of an image, shows it on the screen using a DialogFragment.
     *
     * @param uri the Uri of the image to display.
     */
    public void showImage(Uri uri) {
        // BEGIN_INCLUDE (create_show_image_dialog)
        if (uri != null) {
            // Since the URI is to an image, create and show a DialogFragment to display the
            // image to the user.


            AsyncTask<Uri, Void, String> imageLoadAsyncTask = new AsyncTask<Uri, Void, String>() {
                @Override
                protected String doInBackground(Uri... uris) {
                    getName(uris[0]);
                    return getFileFromUri(uris[0]);
                }

                @Override
                protected void onPostExecute(String file) {
                    System.out.println(file);
                    fileString = file;
                    Utils.personalFileString = fileString;
                    Utils.personal = true;
                }


            };
            imageLoadAsyncTask.execute(uri);
        }
        // END_INCLUDE (create_show_image_dialog)
    }

    private String getFileFromUri(Uri uri) {
        ParcelFileDescriptor parcelFileDescriptor = null;
        try {
            parcelFileDescriptor = this.getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();

            FileInputStream inputStream = new FileInputStream(fileDescriptor);
            BufferedReader bufferReader  = new BufferedReader(
                    new InputStreamReader(inputStream,"UTF-8"));

            String result = "";
            int i = 0;
            while ((i = bufferReader.read()) != -1) {
                result += (char) i;
            }
//                Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
//                parcelFileDescriptor.close();
            return result;
        } catch (Exception e) {
            Log.e(TAG, "Failed to load image.", e);
            return null;
        } finally {
            try {
                if (parcelFileDescriptor != null) {
                    parcelFileDescriptor.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Error closing ParcelFile Descriptor");
            }
        }
    }

    public void getName(Uri uri) {
        // BEGIN_INCLUDE (dump_metadata)

        // The query, since it only applies to a single document, will only return one row.
        // no need to filter, sort, or select fields, since we want all fields for one
        // document.
        Cursor cursor = this.getContentResolver()
                .query(uri, null, null, null, null, null);

        try {
            // moveToFirst() returns false if the cursor has 0 rows.  Very handy for
            // "if there's anything to look at, look at it" conditionals.
            if (cursor != null && cursor.moveToFirst()) {

                // Note it's called "Display Name".  This is provider-specific, and
                // might not necessarily be the file name.
                String displayName = cursor.getString(
                        cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                fileName = displayName;
                Log.i(TAG, "Display Name: " + displayName);

                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                // If the size is unknown, the value stored is null.  But since an int can't be
                // null in java, the behavior is implementation-specific, which is just a fancy
                // term for "unpredictable".  So as a rule, check if it's null before assigning
                // to an int.  This will happen often:  The storage API allows for remote
                // files, whose size might not be locally known.
                String size = null;
                if (!cursor.isNull(sizeIndex)) {
                    // Technically the column stores an int, but cursor.getString will do the
                    // conversion automatically.
                    size = cursor.getString(sizeIndex);
                } else {
                    size = "Unknown";
                }
                Log.i(TAG, "Size: " + size);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        // END_INCLUDE (dump_metadata)
    }

}
