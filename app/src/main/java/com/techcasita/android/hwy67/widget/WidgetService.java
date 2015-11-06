package com.techcasita.android.hwy67.widget;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

import com.techcasita.android.hwy67.content.StoriesDB;
import com.techcasita.android.hwy67.remote.Story;

import java.util.List;

public class WidgetService extends RemoteViewsService {
    private static final String LOG_TAG = WidgetService.class.getName();

    @Override
    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.d(LOG_TAG, "onGetViewFactory() ");
        List<Story> stories = StoriesDB.getAllStories(getApplicationContext());
        if (stories != null) {
            Log.d(LOG_TAG, "stories.size()=" + stories.size());
        } else {
            Log.d(LOG_TAG, "stories is null");
        }
        return (new ListProvider(this.getApplicationContext(), intent, stories));
    }
}
