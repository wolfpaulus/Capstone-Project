package com.techcasita.android.hwy67;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.MenuItem;

import com.techcasita.android.hwy67.content.Contract;
import com.techcasita.android.hwy67.content.StoryLoader;

/**
 * DetailActivity shows a full, detail view of story.
 * Swiping shows other stories (optionally filtered based on a previously selected category (i.e. town name).
 */
public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private Cursor mCursor;
    private long mStartId;


    private ViewPager mPager;
    private MyAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        getLoaderManager().initLoader(0, null, this);
        mPagerAdapter = new MyAdapter(getFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);
        mPager.setPageMargin((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        mPager.setPageMarginDrawable(new ColorDrawable(0x333));

        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }

            @Override
            public void onPageSelected(int position) {
                if (mCursor != null) {
                    mCursor.moveToPosition(position);
                }

            }
        });

        if (savedInstanceState == null) {
            if (getIntent() != null && getIntent().getData() != null) {
                mStartId = Contract.Items.getItemId(getIntent().getData());
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //
    // implements LoaderManager.LoaderCallbacks<Cursor>
    //

    @Override
    public Loader<Cursor> onCreateLoader(final int id, final Bundle args) {
        final String cat = this.getIntent().getStringExtra(Contract.Items.CATEGORIES);
        return cat == null ? StoryLoader.newAllStoriesInstance(this) : StoryLoader.newAllStoriesInstance(this, cat);
    }

    @Override
    public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor) {
        mCursor = cursor;
        mPagerAdapter.notifyDataSetChanged();
        if (mStartId > 0) {
            mCursor.moveToFirst();
            while (!mCursor.isAfterLast()) {
                if (mCursor.getLong(StoryLoader.Query._ID) == mStartId) {
                    final int position = mCursor.getPosition();
                    mPager.setCurrentItem(position, false);
                    break;
                }
                mCursor.moveToNext();
            }
            mStartId = 0;
        }
    }

    @Override
    public void onLoaderReset(final Loader<Cursor> loader) {
        mCursor = null;
        mPagerAdapter.notifyDataSetChanged();
    }

    //
    //  FragmentStatePagerAdapter
    //  http://developer.android.com/reference/android/support/v4/app/FragmentStatePagerAdapter.html

    private class MyAdapter extends FragmentStatePagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return (mCursor != null) ? mCursor.getCount() : 0;
        }

        @Override
        public Fragment getItem(int position) {
            mCursor.moveToPosition(position);
            return DetailActivityFragment.newInstance(mCursor.getLong(StoryLoader.Query._ID));
        }
    }
}
