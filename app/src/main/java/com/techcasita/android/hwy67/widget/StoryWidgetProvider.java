package com.techcasita.android.hwy67.widget;


import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.techcasita.android.hwy67.R;

public class StoryWidgetProvider extends AppWidgetProvider {
    private static String LOG_TAG = StoryWidgetProvider.class.getName();

    public StoryWidgetProvider() {
        super();
    }


    /**
     * Called every 15" as specified on widgetinfo.xml as well as on reboot
     *
     * @param context          {@link Context}
     * @param appWidgetManager {@link AppWidgetManager}
     * @param appWidgetIds     {@link int array}
     */
    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        Log.d(LOG_TAG, "Widget onUpdate() called");
        for (final int appWidgetId : appWidgetIds) {
            RemoteViews remoteViews = updateWidgetListView(context, appWidgetId);
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private RemoteViews updateWidgetListView(final Context context, final int appWidgetId) {
        final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_update);

        final Intent intent = new Intent(context, WidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        //noinspection deprecation
        remoteViews.setRemoteAdapter(appWidgetId, R.id.listViewWidget, intent);
        remoteViews.setEmptyView(R.id.listViewWidget, R.id.empty_view);
        return remoteViews;
    }

    @Override
    public void onEnabled(final Context context) {
        super.onEnabled(context);
    }
}

