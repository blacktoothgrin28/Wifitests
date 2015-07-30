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
    private static String fileName;
    private static String currentDateandTime;

    public static void initialize() {
        int file_size;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        currentDateandTime = sdf.format(new Date());

        fileName = currentDateandTime + "_mhp.txt";
        File logFile = new File(Environment.getExternalStorageDirectory() + "/WCLOG/rt.txt");
        file_size = Integer.parseInt(String.valueOf(logFile.length() / 1024));
        if (file_size > parameters.LogFileSize) logFile.delete();
        appendLog("++++++++++++++++++++++++Session: " + currentDateandTime + "+++++++++++++++++++++++");

    }

    public static void appendLog(String text) {
        appendLog(text, "mhp");
    }

    public static void appendLog(String text, String TAG) {
        Log.d(TAG, text);

        File logFile = new File(Environment.getExternalStorageDirectory(), "/WCLOG/" + currentDateandTime + "_" + TAG + ".txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
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
            e.printStackTrace();
        }
    }
}