package com.techcasita.android.hwy67.remote;

import android.util.Log;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.http.GET;
import retrofit.http.Query;


public class ContentReader {
    private static final String LOG_TAG = ContentReader.class.getName();

    public static List<Story> fetchArticles(final String BASE_URL) {
        // setup Retrofit
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // setup api service
        final Api apiService = retrofit.create(Api.class);
        final Call<List<Story>> call = apiService.getArticles("");

        try {
            final Response<List<Story>> response = call.execute();
            return response.body();
        } catch (Exception e) {
            Log.e(LOG_TAG, e.toString());
        }
        return null;
    }

    public static void pushDemo(final String BASE_URL, final String uid) {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .build();
        // setup api service
        final Api apiService = retrofit.create(Api.class);
        final Call<Void> call = apiService.pushDemo(uid);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Response<Void> response, Retrofit retrofit) {
                Log.d(LOG_TAG, "Push Demo requested");
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(LOG_TAG, "Push Demo request failed: " + t.toString());
            }
        });
    }

    //
    //  Describing server-side REST interface
    //
    public interface Api {

        @SuppressWarnings("unused")
        @GET("rest/hwy67/article")
        Call<Story> getArticle(@Query("sid") long sid);

        @GET("rest/hwy67/articles")
        Call<List<Story>> getArticles(@Query("sort") String sort);

        @GET("rest/hwy67/push")
        Call<Void> pushDemo(@Query("uid") String uid);
    }
}
