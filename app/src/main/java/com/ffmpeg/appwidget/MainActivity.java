package com.ffmpeg.appwidget;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent send = new Intent();
        send.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        sendBroadcast(send);


    }


    @Override
    protected void onDestroy() {
        Intent send = new Intent();
        send.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        send.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        sendBroadcast(send);
        super.onDestroy();
    }
}
