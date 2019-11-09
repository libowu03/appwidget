package com.ffmpeg.appwidget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class RebootBro extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("日志","开机广播已收到");
        Intent send = new Intent();
        send.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        send.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        context.sendBroadcast(send);

     /*   IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Intent startServer = new Intent();

            }
        },intentFilter);*/
    }
}
