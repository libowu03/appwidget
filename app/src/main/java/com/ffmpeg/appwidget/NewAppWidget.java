package com.ffmpeg.appwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Calendar;

/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider implements MyService.ChangeTimeListener {
    private MyService.MyBind myService;
    private int widgetid;
    private AppWidgetManager appWidgetManager;
    private ServiceConnection connent;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        Calendar calendar = Calendar.getInstance();
        String date = calendar.get(Calendar.YEAR)+"/"+calendar.get(Calendar.MONTH)+"/"+calendar.get(Calendar.DAY_OF_MONTH)+" "+calendar.get(Calendar.HOUR)+":"+calendar.get(Calendar.MINUTE);
        views.setTextViewText(R.id.appwidget_text,date);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals("android.appwidget.action.APPWIDGET_UPDATE")){
            int[] widgetids =  AppWidgetManager.getInstance(context).getAppWidgetIds(intent.getComponent());
            appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());
            onUpdate(context.getApplicationContext(),appWidgetManager,widgetids);
        }
    }

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.e("日志","执行onUpdate");
        // There may be multiple widgets active, so update all of them
        for (final int appWidgetId : appWidgetIds) {
            Log.e("日志","id为："+widgetid);
            updateAppWidget(context, appWidgetManager, appWidgetId);
            widgetid = appWidgetId;
            this.appWidgetManager = appWidgetManager;
          MyService.startServer(context.getApplicationContext(),appWidgetId,this);
          Intent startServer = new Intent(context.getApplicationContext(),StackWidgetService.class);
          context.getApplicationContext().startService(startServer);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void startChangeTime(Context context,int appWidgetId) {
        Calendar calendar = Calendar.getInstance();
        String date = calendar.get(Calendar.YEAR)+"/"+calendar.get(Calendar.MONTH)+"/"+calendar.get(Calendar.DAY_OF_MONTH)+" "+calendar.get(Calendar.HOUR)+":"+calendar.get(Calendar.MINUTE);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        // Instruct the widget manager to update the widget
        views.setTextViewText(R.id.appwidget_text,date);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}

