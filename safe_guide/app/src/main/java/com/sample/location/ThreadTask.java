package com.sample.location;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

public abstract class ThreadTask<T1, T2> implements Runnable {
    T1 mArgument;
    T2 mResult;

    public final int WORK_DONE = 0;
    Handler mResultHandler = new Handler() {
      @Override
      public void handleMessage(@NonNull Message msg) {
          super.handleMessage(msg);
      }
    };

    final public T2 execute(final T1 arg) {
        mArgument = arg;

        Thread thread = new Thread(this);
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            Log.e("ThreadTask", "thread join failed.");
            e.printStackTrace();
        }

        return mResult;
    }

    @Override
    public void run() {
        mResult = doInBackground(mArgument);
        mResultHandler.sendEmptyMessage((WORK_DONE));
    }

    protected abstract T2 doInBackground(T1 arg);
}
