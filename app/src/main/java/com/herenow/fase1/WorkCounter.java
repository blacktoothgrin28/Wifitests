package com.herenow.fase1;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by Milenko on 21/01/2016.
 */
public class WorkCounter {
    private final Context ctx;
    private int runningTasks;

    public WorkCounter(int numberOfTasks, Context ctx) {
        this.runningTasks = numberOfTasks;
        this.ctx = ctx;
    }

    // Only call this in onPostExecute! (or add synchronized to method declaration)
    public void taskFinished() {
        if (--runningTasks == 0) {
            LocalBroadcastManager mgr = LocalBroadcastManager.getInstance(this.ctx);
            mgr.sendBroadcast(new Intent("all_tasks_have_finished"));
        }
    }
}
