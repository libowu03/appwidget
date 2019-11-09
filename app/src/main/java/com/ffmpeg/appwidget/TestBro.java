package com.ffmpeg.appwidget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class TestBro extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("日志","testBro收到广播");
    }
}
