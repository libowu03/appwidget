package com.ffmpeg.appwidget;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.RequiresApi;

public class MyService extends Service {
    private static ServiceConnection connent;
    private MyBind myBind;
    private AppWidgetManager appWidgetManager;
    private int widgetId;
    private ChangeTimeListener changeTimeListener;
    private String notificationId = "widget";
    private String notificationName = "widget";
    private NotificationManager notificationManager;
    private Notification.Builder builder;
    private Notification notification;
    private NotificationChannel channel;


    public static void startServer(final Context context, final int widgetid, final ChangeTimeListener changeTimeListener){
        Intent intent = new Intent(context,MyService.class);
        connent = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                MyBind myService = (MyBind) service;
                myService.setWidgetId(widgetid);
                ((MyBind) service).setTimeChangeListener(changeTimeListener);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Intent intent = new Intent(context.getApplicationContext(), MyService.class);
                context.getApplicationContext().bindService(intent, connent, Context.BIND_ABOVE_CLIENT);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.getApplicationContext().startForegroundService(intent);
                } else {
                    context.getApplicationContext().startService(intent);
                }
                context.getApplicationContext().startService(intent);

            }
        };
        context.getApplicationContext().bindService(intent,connent,Context.BIND_ABOVE_CLIENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            context.getApplicationContext().startForegroundService(intent);
        }else {
            context.getApplicationContext().startService(intent);
        }
        context.getApplicationContext().startService(intent);
    }


    public MyService() {

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate() {
        super.onCreate();
        showNotification();
        //startForeground(1,new Notification());
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

        Log.e("日志","第一个服务启动");
        final Intent send = new Intent();
        send.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        getApplication().sendBroadcast(send);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        getApplication().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
               if (changeTimeListener != null){
                   changeTimeListener.startChangeTime(context,widgetId);
               }
               if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
                   Intent sendBro = new Intent();
                   sendBro.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                   sendBroadcast(sendBro);
                   Log.e("日志","开启屏幕");
                   if (notificationManager != null){
                       notificationManager.cancel(1);
                       notificationManager.cancelAll();
                       //stopForeground(true);
                   }
               }else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
                  startServer(getApplicationContext(),widgetId,changeTimeListener);
                  //showNotification();
               }
            }
        },intentFilter);
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("日志","onStartCommand第一个服务启动");
        return START_REDELIVER_INTENT;
    }


    @Override
    public IBinder onBind(Intent intent) {
        myBind = new MyBind();
        return myBind;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        startServer(getApplicationContext(),widgetId,changeTimeListener);

    }

    class MyBind extends Binder{
        private int widgetId;

        public void setWidgetId(int widgetId){
            this.widgetId = widgetId;
            MyService.this.widgetId = widgetId;
        }

        public void setTimeChangeListener(ChangeTimeListener changeListener){
            MyService.this.changeTimeListener = changeListener;
        }
    }


    interface ChangeTimeListener{
        void startChangeTime(Context context,int appWidgetId);
    }
}
