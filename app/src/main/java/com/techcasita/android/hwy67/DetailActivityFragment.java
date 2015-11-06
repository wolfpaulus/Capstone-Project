package com.techcasita.android.hwy67;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ShareCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.techcasita.android.hwy67.content.StoryLoader;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {
    public static final String ARG_ITEM_ID = DetailActivityFragment.class.getName() + "_ARG__ITEM_ID";

    private static final String LOG_TAG = DetailActivityFragment.class.getName();
    private Cursor mCursor;
    private long mItemId;
    private View mRootView;
    private ImageView mPhotoView;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;

    /**
     * @param itemId {@link long} itemid
     * @return {@link DetailActivityFragment} instance
     */
    public static DetailActivityFragment newInstance(final long itemId) {
        final Bundle arguments = new Bundle();
        arguments.putLong(ARG_ITEM_ID, itemId);
        final DetailActivityFragment fragment = new DetailActivityFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // In support library r8, calling initLoader for a fragment in a FragmentPagerAdapter in
        // the fragment's onCreate may cause the same LoaderManager to be dealt to multiple
        // fragments because their mIndex is -1 (haven't been added to the activity yet). Thus,
        // we do this in onActivityCreated.
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItemId = getArguments().getLong(ARG_ITEM_ID);
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //noinspection deprecation
            window.setStatusBarColor(getActivity().getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mPhotoView = (ImageView) mRootView.findViewById(R.id.photo);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) mRootView.findViewById(R.id.collapsing_toolbar);

        mRootView.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText("Some sample text")
                        .getIntent(), getString(R.string.action_share)));
            }
        });

        final Toolbar toolbar = (Toolbar) mRootView.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                getActivity().finish();
            }
        });
        bindViews();
        return mRootView;
    }


    //
    //  implements LoaderManager.LoaderCallbacks<Cursor>
    //

    private void bindViews() {
        if (mRootView == null) {
            return;
        }

        final TextView title = (TextView) mRootView.findViewById(R.id.article_title);
        final TextView subTitle = (TextView) mRootView.findViewById(R.id.article_byline);
        final TextView content = (TextView) mRootView.findViewById(R.id.article_body);
        final Button button = (Button) mRootView.findViewById(R.id.btn_open);

        subTitle.setMovementMethod(new LinkMovementMethod());
        content.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Roboto-Regular.ttf"));

        if (mCursor != null) {
            mRootView.setAlpha(0);
            mRootView.setVisibility(View.VISIBLE);
            mRootView.animate().alpha(1);
            mCollapsingToolbarLayout.setTitle(mCursor.getString(StoryLoader.Query.TITLE));

            final String author = mCursor.getString(StoryLoader.Query.AUTHOR);
            final String date = mCursor.getString(StoryLoader.Query.DATE);
            final String url = mCursor.getString(StoryLoader.Query.FOREIGN_URL);
            if (url != null && 0 < url.length()) {
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)));
                    }
                });
            }

            title.setText(mCursor.getString(StoryLoader.Query.TITLE));
            subTitle.setText(String.format("%s by %s", date, author));
            content.setText(Html.fromHtml(mCursor.getString(StoryLoader.Query.CONTENT)));

            Glide.with(DetailActivityFragment.this)
                    .load(mCursor.getString(StoryLoader.Query.IMAGE_URL))
                    .centerCrop()
                    .listener(
                            new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    mPhotoView.setImageResource(R.drawable.web512);
                                    mRootView.findViewById(R.id.meta_bar).setBackgroundColor(0xFF333333);
                                    return true;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    final Bitmap bitmap = Util.drawableToBitmap(resource.getCurrent());
                                    final Palette p = new Palette.Builder(bitmap).generate();
                                    final int col = p.getVibrantColor(0xFF333333);
                                    mRootView.findViewById(R.id.meta_bar).setBackgroundColor(col);
                                    return false;
                                }
                            })
                    .into(mPhotoView);


        } else {
            mRootView.setVisibility(View.GONE);
            mCollapsingToolbarLayout.setTitle(getString(R.string.app_name));
            title.setText("N/A");
            subTitle.setText("N/A");
            content.setText("N/A");
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(final int id, final Bundle args) {
        return StoryLoader.newInstanceForItemId(getActivity(), mItemId);
    }

    @Override
    public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor) {
        if (!isAdded()) {
            if (cursor != null) {
                cursor.close();
            }
            return;
        }

        mCursor = cursor;
        if (mCursor != null && !mCursor.moveToFirst()) {
            Log.e(LOG_TAG, "Error reading item detail cursor");
            mCursor.close();
            mCursor = null;
        }
        bindViews();
    }

    @Override
    public void onLoaderReset(final Loader<Cursor> loader) {
        mCursor = null;
        bindViews();
    }
}
