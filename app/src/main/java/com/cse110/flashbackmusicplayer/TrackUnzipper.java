package com.cse110.flashbackmusicplayer;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


class TrackUnzipper extends AsyncTask<String, Void, Boolean> {

    // The object that is notified when this task finishes.
    private TrackUnzipperObserver observer = null;

    // The list of files we created.
    private List<String> filenames = null;

    // Observer class from this subject.
    public interface TrackUnzipperObserver {
        void update(List<String> filenames);
    }

    public TrackUnzipper(TrackUnzipperObserver observer) {
        this.observer = observer;
    }

    /*
     * Looked at this code to understand how to unzip something:
     * https://stackoverflow.com/questions/3382996/how-to-unzip-files-programmatically-in-android
     */
    @Override
    protected Boolean doInBackground(String... params) {
        filenames = new ArrayList<>();

        try
        {
            // Get the path as it appears in the phone
            String filepath = Uri.parse(params[0]).getPath();
            Log.d("TrackUnzipper", "Unzipping the album at " + filepath);
            String dir = filepath.substring(0, filepath.lastIndexOf(File.separator) + 1);

            InputStream inputStream = new FileInputStream(filepath);
            ZipInputStream zipStream = new ZipInputStream(new BufferedInputStream(inputStream));

            // Get the files in the zip folder.
            ZipEntry entry;
            while ((entry = zipStream.getNextEntry()) != null)
            {
                // Get the name of the first file.
                String filename = entry.getName();
                Log.d("TrackUnzipper", "Extracting " + filename);
                filenames.add(dir + filename);

                // Read from the zipped file and copy the file to the directory.
                FileOutputStream fout = new FileOutputStream(dir + filename);

                byte[] buffer = new byte[1024];
                int count;
                while ((count = zipStream.read(buffer)) != -1)
                {
                    fout.write(buffer, 0, count);
                }

                fout.close();
                zipStream.closeEntry();
            }

            zipStream.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        Log.d("TrackUnzipper", "Finished extraction");
        observer.update(filenames);
    }
}
