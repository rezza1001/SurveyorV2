package com.wadaro.surveyor.util;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class MyTimer {

    private Context mContext;
    private Timer updateTimer;

    public MyTimer(Context context) {
        mContext = context;
    }

    public void runTime() {

        updateTimer = new Timer();
        updateTimer.schedule(new TimerTask() {
            public void run() {
                try {
                    Calendar calendar = Calendar.getInstance();
                    long mills = calendar.getTimeInMillis();
                    int hours = (int) ((mills / (1000 * 60 * 60)));
                    int minutes = (int) ((mills / (1000 * 60)) % 60);
                    int seconds = (int) (mills / 1000) % 60;

                    Message message = new Message();
                    message.what = 1;
                    Bundle bundle = new Bundle();
                    bundle.putInt("seconds", seconds);
                    bundle.putInt("minutes", minutes);
                    bundle.putInt("hours", hours);
                    bundle.putString("status", "RUN");
                    if (hours <= 0 && minutes <= 0 && seconds <= 0) {
                        this.cancel();
                        bundle.putString("status",  "OFF");
                        bundle.putInt("seconds", 0);
                        bundle.putInt("minutes", 0);
                        bundle.putInt("hours", 0);
                    }
                    message.setData(bundle);
                    handler.sendMessage(message);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1000); // here 1000 means 1000 mills i.e. 1 second

    }

    public void cancel() {
        updateTimer.cancel();
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            Bundle b = message.getData();
            if (message.what == 1) {
                if (mListener != null) {
                    if (b.getString("status").equals("OFF")) {
                        mListener.onFinish(b.getInt("hours"), b.getInt("minutes"), b.getInt("seconds"));
                    }else{
                        mListener.onProgress(b.getInt("hours"), b.getInt("minutes"), b.getInt("seconds"));
                    }
                }
            }
            return false;
        }
    });

    private OnRunListener mListener;

    public void setOnRunListener(OnRunListener onRunListener) {
        mListener = onRunListener;
    }

    public interface OnRunListener {
        void onProgress(int hour, int minute, int second);

        void onFinish(int hour, int minute, int second);
    }
}
