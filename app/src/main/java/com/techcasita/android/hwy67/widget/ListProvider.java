package com.techcasita.android.hwy67.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.techcasita.android.hwy67.R;
import com.techcasita.android.hwy67.remote.Story;

import java.util.ArrayList;
import java.util.List;


public class ListProvider implements RemoteViewsService.RemoteViewsFactory {
    private List<Story> listItemList = new ArrayList<>();

    private Context context = null;


    @SuppressWarnings("UnusedParameters")
    public ListProvider(Context context, Intent intent, List<Story> listItemList) {
        this.listItemList = listItemList;
        this.context = context;
    }


    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public int getCount() {
        return listItemList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public RemoteViews getViewAt(final int position) {
        int layout = R.layout.widget_list_item;
        final RemoteViews remoteView = new RemoteViews(context.getPackageName(), layout);
        final Story story = listItemList.get(position);
        remoteView.setTextViewText(R.id.article_title, story.getTitle());
        remoteView.setTextViewText(R.id.article_subtitle, String.format("%s by %s", story.getDate(), story.getAuthor()));
        remoteView.setImageViewResource(R.id.thumbnail, R.drawable.hwy67);
        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }
}
