package util;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Milenko on 16/07/2015.
 */
public class AppendLog {

    public static void initialize() {
        //Delete files if too greater than 1M
        File logFile = new File(Environment.getExternalStorageDirectory()+"/WCLOG/logWeacon.txt");
        int file_size = Integer.parseInt(String.valueOf(logFile.length() / 1024));
        if (file_size > 80) logFile.delete();
        logFile = new File(Environment.getExternalStorageDirectory() + "/WCLOG/rt.log");
        file_size = Integer.parseInt(String.valueOf(logFile.length() / 1024));
        if (file_size > 10) logFile.delete();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(new Date());
        appendLog("filesize = " + file_size + "************ Session: " + currentDateandTime);
    }

    public static void appendLog(String text) {
        appendLog(text, "mhp");
    }

    public static void appendLog(String text, String TAG) {
        Log.d(TAG, text);

        File logFile = new File(Environment.getExternalStorageDirectory(), "logWeacon.txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss (dd)| ");
            String currentDateandTime = sdf.format(new Date());

            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(currentDateandTime + text);
            buf.newLine();
            buf.flush();
            buf.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}