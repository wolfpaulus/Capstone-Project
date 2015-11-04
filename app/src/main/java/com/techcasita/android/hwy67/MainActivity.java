package com.techcasita.android.hwy67;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.techcasita.android.hwy67.content.Contract;
import com.techcasita.android.hwy67.content.StoryLoader;
import com.techcasita.android.hwy67.content.UpdateService;
import com.techcasita.android.hwy67.gcm.RegistrationIntentService;

/**
 * Main Activity, showing a list of all available items, or a list, filtered by category,
 */
public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        SwipeRefreshLayout.OnRefreshListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String STATE_TITLE = MainActivity.class.getName() + "_STATE_TITLE";
    private static final String LOG_TAG = MainActivity.class.getName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static boolean hasPlayServices = false;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private String mCategory = null;
    private boolean mInitData = true;
    private BroadcastReceiver mRefreshingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (UpdateService.BROADCAST_ACTION_STATE_CHANGE.equals(intent.getAction())) {
                boolean isRefreshing = intent.getBooleanExtra(UpdateService.EXTRA_REFRESHING, false);
                mSwipeRefreshLayout.setRefreshing(isRefreshing);
                Log.d(LOG_TAG, "SwipeRefreshLayout isRefreshing is" + isRefreshing);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState != null) {
            this.setTitle(savedInstanceState.getCharSequence(STATE_TITLE));
        } else {
            setTitle(getString(R.string.default_title));
        }

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mSwipeRefreshLayout.setRefreshing(false);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int k = mRecyclerView.getChildAdapterPosition(mRecyclerView.getChildAt(0));
                mSwipeRefreshLayout.setEnabled(k == 0);
            }
        });


        getLoaderManager().initLoader(0, null, this);


        MainActivity.hasPlayServices = checkPlayServices();
        if (MainActivity.hasPlayServices) {
            // Start IntentService to register this application with GCM.
            startService(new Intent(this, RegistrationIntentService.class));
            Log.d(LOG_TAG, "GCM RegistrationIntentService called.");
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(LOG_TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putCharSequence(STATE_TITLE, this.getTitle());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        //
        // cool stuff: http://javapapers.com/android/android-searchview-action-bar-tutorial/
        //
        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            startActivity(new Intent(this, SearchActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSwipeRefreshLayout.setRefreshing(false);
        LocalBroadcastManager.getInstance(this).registerReceiver(mRefreshingReceiver, new IntentFilter(UpdateService.BROADCAST_ACTION_STATE_CHANGE));
        if (mInitData) {
            onRefresh();
        }
    }

    @Override
    protected void onRestart() {
        mInitData = false;
        super.onRestart();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRefreshingReceiver);
        super.onPause();
    }

    //
    //  Implement NavigationView.OnNavigationItemSelectedListener
    //
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_julian:
                setTitle("Hwy67 - Julian");
                mCategory = "Julian";
                getLoaderManager().restartLoader(0, null, this);
                break;
            case R.id.nav_ysabel:
                setTitle("Hwy67 - Santa Ysabel");
                mCategory = "Ysabel";
                getLoaderManager().restartLoader(0, null, this);
                break;
            case R.id.nav_ramona:
                setTitle("Hwy67 - Ramona");
                mCategory = "Ramona";
                getLoaderManager().restartLoader(0, null, this);
                break;
            case R.id.nav_lakeside:
                setTitle("Hwy67 - Lakeside");
                mCategory = "Lakeside";
                getLoaderManager().restartLoader(0, null, this);
                break;
            case R.id.nav_santee:
                setTitle("Hwy67 - Santee");
                mCategory = "Santee";
                getLoaderManager().restartLoader(0, null, this);
                break;
            case R.id.nav_alpine:
                setTitle("Hwy67 - Alpine");
                mCategory = "Alpine";
                getLoaderManager().restartLoader(0, null, this);
                break;
            case R.id.nav_descanso:
                setTitle("Hwy67 - Descanso");
                mCategory = "Descanso";
                getLoaderManager().restartLoader(0, null, this);
                break;
            case R.id.nav_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
            default:
                mCategory = null;
                setTitle("Hwy67 - Front Page");
                getLoaderManager().restartLoader(0, null, this);
                break;
        }
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //
    //  Implement SwipeRefreshLayout.OnRefreshListener
    //
    @Override
    public void onRefresh() {
        Log.d(LOG_TAG, "onRefresh called");
        startService(new Intent(this, UpdateService.class));
    }


    //
    // Implement LoaderManager.LoaderCallbacks<Cursor>,
    //

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return mCategory == null ? StoryLoader.newAllStoriesInstance(this) : StoryLoader.newAllStoriesInstance(this, mCategory);
    }

    @Override
    public void onLoadFinished(final Loader<Cursor> loader, final Cursor data) {
        final Adapter adapter = new Adapter(data);
        adapter.setHasStableIds(true);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
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

        public ViewHolder(final View view) {
            super(view);
            thumbnailView = (ImageView) view.findViewById(R.id.thumbnail);
            titleView = (TextView) view.findViewById(R.id.article_title);
            subtitleView = (TextView) view.findViewById(R.id.article_subtitle);
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
            final View view = getLayoutInflater().inflate(R.layout.list_item, parent, false);
            final ViewHolder vh = new ViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                /**
                 * Create new view intent, for the clicked item, also add the currently selected cat.
                 * @param view {@link View}
                 */
                @Override
                public void onClick(final View view) {
                    final Intent intent = new Intent(Intent.ACTION_VIEW, Contract.Items.buildItemUri(getItemId(vh.getAdapterPosition())));
                    startActivity(intent.putExtra(Contract.Items.CATEGORIES, mCategory));
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
            try {
                Glide.with(MainActivity.this)
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
