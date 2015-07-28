package com.herenow.fase1;

import android.os.AsyncTask;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

/**
 * Created by Milenko on 21/07/2015.
 */
public class Position2 {
    private void loadFile(String filename) {
        new GetFileTask().execute(filename);
    }

    private class GetFileTask extends AsyncTask {
        protected void doInBackground(String filename) {
            Query query = new Query.Builder()
                    .addFilter(Filters.eq(SearchableField.TITLE, filename))
                    .build();
            // Invoke the query synchronously
            GoogleApiClient mGoogleApiClient = null;
            DriveApi.MetadataBufferResult result =
                    Drive.DriveApi.query(mGoogleApiClient, query).await();

            // Continue doing other stuff synchronously
//            ...
        }

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p/>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Object doInBackground(Object[] params) {
            return null;
        }
    }
}
