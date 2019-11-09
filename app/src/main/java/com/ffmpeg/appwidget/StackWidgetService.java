/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ffmpeg.appwidget;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class StackWidgetService extends RemoteViewsService {
    private StackRemoteViewsFactory stackRemoteViewsFactory;
    private NotificationManager notificationManager;
    private Notification.Builder builder;
    private Notification notification;
    private NotificationChannel channel;
    private String notificationId = "widget";
    private String notificationName = "widget";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        stackRemoteViewsFactory = new StackRemoteViewsFactory(this.getApplicationContext(), intent,getApplication());
        return stackRemoteViewsFactory;
    }

    @Override
    public void onDestroy() {
       /* Intent startBro = new Intent("com.ffmpeg.appwidget.destory");
        sendBroadcast(startBro);*/
        super.onDestroy();
        Log.e("日志","服务二执行销毁");
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate() {
        super.onCreate();

        showNotification();
        stopForeground(true);
       Log.e("日志","执行服务二");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log.e("日志","执行onStartCommand");
        return START_REDELIVER_INTENT;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void showNotification() {
        if (notificationManager == null){
            notificationManager = (NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
                channel = new NotificationChannel(notificationId,notificationName,NotificationManager.IMPORTANCE_MIN);
                notificationManager.createNotificationChannel(channel);
            }
        }

        startForeground(1,getNotification());
    }



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private Notification getNotification() {
        if (builder == null){
            builder = new Notification.Builder(this);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
                builder.setChannelId(notificationId);
            }
            notification = builder.build();
        }
        return notification;
    }
}

class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private static final int mCount = 1;
    private List<WidgetItem> mWidgetItems = new ArrayList<WidgetItem>();
    private static Context mContext;
    private int mAppWidgetId;

    public StackRemoteViewsFactory(Context context, Intent intent, Application application) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);


        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        mContext.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Log.e("日志","时间发生改变");
                //onDataSetChanged();
                Calendar calendar = Calendar.getInstance();
                String date = calendar.get(Calendar.YEAR)+"/"+calendar.get(Calendar.MONTH)+"/"+calendar.get(Calendar.DAY_OF_MONTH)+" "+calendar.get(Calendar.HOUR)+":"+calendar.get(Calendar.MINUTE);
                RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);
                rv.setTextViewText(R.id.widget_item, date);
                AppWidgetManager.getInstance(mContext).updateAppWidget(mAppWidgetId,rv);
                AppWidgetManager.getInstance(mContext).notifyAppWidgetViewDataChanged(mAppWidgetId,R.id.widget_item);
            }
        },intentFilter);
    }



    public void onCreate() {
       // Log.e("日志","执行onCreate"+mAppWidgetId);
        // In onCreate() you setup any connections / cursors to your data source. Heavy lifting,
        // for example downloading or creating content etc, should be deferred to onDataSetChanged()
        // or getViewAt(). Taking more than 20 seconds in this call will result in an ANR.
        for (int i = 0; i < mCount; i++) {
            mWidgetItems.add(new WidgetItem(i + "!"));
        }



        // We sleep for 3 seconds here to show how the empty view appears in the interim.
        // The empty view is set in the StackWidgetProvider and should be a sibling of the
        // collection view.
       /* try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

    }

    public void onDestroy() {
        // In onDestroy() you should tear down anything that was setup for your data source,
        // eg. cursors, connections, etc.
      //  Log.e("日志","被销毁");
        mWidgetItems.clear();
    }

    public int getCount() {
        return mCount;
    }

    public RemoteViews getViewAt(int position) {
       // Log.e("日志","获取的index为："+position);
        // position will always range from 0 to getCount() - 1.

        // We construct a remote views item based on our widget item xml file, and set the
        // text based on the position.
        Calendar calendar = Calendar.getInstance();
        String date = calendar.get(Calendar.YEAR)+"/"+calendar.get(Calendar.MONTH)+"/"+calendar.get(Calendar.DAY_OF_MONTH)+" "+calendar.get(Calendar.HOUR)+":"+calendar.get(Calendar.MINUTE);
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);
        rv.setTextViewText(R.id.widget_item, date);

        // Next, we set a fill-intent which will be used to fill-in the pending intent template
        // which is set on the collection view in StackWidgetProvider.
        Bundle extras = new Bundle();
        extras.putInt(StackWidgetProvider.EXTRA_ITEM, position);
        Intent fillInIntent = new Intent();
        fillInIntent.setClassName("com.ffmpeg.appwidget","StackWidgetService");
        fillInIntent.putExtras(extras);
        rv.setOnClickFillInIntent(R.id.widget_item, fillInIntent);

        // You can do heaving lifting in here, synchronously. For example, if you need to
        // process an image, fetch something from the network, etc., it is ok to do it here,
        // synchronously. A loading view will show up in lieu of the actual contents in the
        // interim.
       /* try {
            System.out.println("Loading view " + position);
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        // Return the remote views object.
        return rv;
    }

    public RemoteViews getLoadingView() {
        //在这里可以创建一个加载视图，即当view未加载完成时，可以先加载一个默认视图，如果放回null，则加载默认视图。
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.item_loading);
        return rv;
    }

    public int getViewTypeCount() {
        return 1;
    }

    public long getItemId(int position) {
        return position;
    }

    public boolean hasStableIds() {
        return true;
    }

    public void onDataSetChanged() {
        // This is triggered when you call AppWidgetManager notifyAppWidgetViewDataChanged
        // on the collection view corresponding to this factory. You can do heaving lifting in
        // here, synchronously. For example, if you need to process an image, fetch something
        // from the network, etc., it is ok to do it here, synchronously. The widget will remain
        // in its current state while work is being done here, so you don't need to worry about
        // locking up the widget.
/*        Calendar calendar = Calendar.getInstance();
        String date = calendar.get(Calendar.YEAR)+"/"+calendar.get(Calendar.MONTH)+"/"+calendar.get(Calendar.DAY_OF_MONTH)+" "+calendar.get(Calendar.HOUR)+":"+calendar.get(Calendar.MINUTE);
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);
        rv.setTextViewText(R.id.widget_item, date);*/
      //  Log.e("日志","数据繁盛变暖");
    }
}