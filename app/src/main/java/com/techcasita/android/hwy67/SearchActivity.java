package com.techcasita.android.hwy67;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.techcasita.android.hwy67.content.Contract;
import com.techcasita.android.hwy67.content.StoryLoader;

public class SearchActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = SearchActivity.class.getName();
    private RecyclerView mRecyclerView;
    private String mSearchTerm;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //noinspection deprecation
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        findViewById(R.id.swipe_refresh_layout).setEnabled(false);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mTextView = (TextView) findViewById(R.id.no_results);
        handleIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(final Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            mSearchTerm = "q=" + intent.getStringExtra(SearchManager.QUERY);
            getLoaderManager().restartLoader(0, null, this);
        }
    }

    //
    // Implement LoaderManager.LoaderCallbacks<Cursor>,
    //

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return StoryLoader.newAllStoriesInstance(this, mSearchTerm);
    }

    @Override
    public void onLoadFinished(final Loader<Cursor> loader, final Cursor data) {
        if (0 == data.getCount()) {
            mTextView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mTextView.setVisibility(View.GONE);
            final Adapter adapter = new Adapter(data);
            adapter.setHasStableIds(true);
            mRecyclerView.setAdapter(adapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }


    //
    // RecyclerView.ViewHolder
    //
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnailView;
        public TextView titleView;
        public TextView subtitleView;
        public TextView contentView;

        public ViewHolder(final View view) {
            super(view);
            thumbnailView = (ImageView) view.findViewById(R.id.thumbnail);
            titleView = (TextView) view.findViewById(R.id.article_title);
            subtitleView = (TextView) view.findViewById(R.id.article_subtitle);
            contentView = (TextView) view.findViewById(R.id.article_content);
        }
    }

    //
    //  RecyclerView.Adapter
    //
    private class Adapter extends RecyclerView.Adapter<ViewHolder> {
        private Cursor mCursor;

        public Adapter(final Cursor cursor) {
            mCursor = cursor;
        }

        @Override
        public long getItemId(final int position) {
            mCursor.moveToPosition(position);
            return mCursor.getLong(StoryLoader.Query._ID);
        }

        @Override
        public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            final View view = getLayoutInflater().inflate(R.layout.search_item, parent, false);
            final ViewHolder vh = new ViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                /**
                 * Create new view intent, for the clicked item, also add the currently selected cat.
                 * @param view {@link View}
                 */
                @Override
                public void onClick(final View view) {
                    final Intent intent = new Intent(Intent.ACTION_VIEW, Contract.Items.buildItemUri(getItemId(vh.getAdapterPosition())));
                    startActivity(intent.putExtra(Contract.Items.CATEGORIES, mSearchTerm));
                }
            });
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            mCursor.moveToPosition(position);
            final String date = mCursor.getString(StoryLoader.Query.DATE);
            final String author = mCursor.getString(StoryLoader.Query.AUTHOR);

            holder.titleView.setText(mCursor.getString(StoryLoader.Query.TITLE));
            holder.subtitleView.setText(String.format("%s by %s", date, author));
            holder.contentView.setText(Html.fromHtml(mCursor.getString(StoryLoader.Query.CONTENT)));
            try {
                Glide.with(SearchActivity.this)
                        .load(mCursor.getString(StoryLoader.Query.IMAGE_URL))
                        .fallback(R.drawable.web512)
                        .centerCrop()
                        .listener(
                                new RequestListener<String, GlideDrawable>() {
                                    @Override
                                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                        holder.thumbnailView.setImageResource(R.drawable.hwy67);
                                        return true;
                                    }

                                    @Override
                                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                        return false;
                                    }
                                })
                        .into(holder.thumbnailView);
            } catch (Exception e) {
                Log.w(LOG_TAG, e.toString());
            }
        }

        @Override
        public int getItemCount() {
            return mCursor.getCount();
        }
    }
}

