package com.techcasita.android.hwy67;

import com.techcasita.android.hwy67.remote.Story;

import org.junit.Test;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.http.GET;
import retrofit.http.Query;

import static org.junit.Assert.assertTrue;


public class ReadRemoteContentTest {

    public static final String BASE_URL = "http://localhost:8080";
    public static final long Article_ID = 16887;

    @Test
    public void accessTest() {

        // setup Retrofit
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // setup api service
        final Api apiService = retrofit.create(Api.class);

        // access the remote data
        final Call<Story> call1 = apiService.getArticle(Article_ID);

        call1.enqueue(new Callback<Story>() {
            @Override
            public void onResponse(Response<Story> response, Retrofit retrofit) {
                int statusCode = response.code();
                assertTrue(statusCode == 200);
                Story story = response.body();
                assertTrue(story.getID() == Article_ID);
            }

            @Override
            public void onFailure(Throwable t) {
                assert false;
            }
        });

        final Call<List<Story>> call2 = apiService.getArticles("");

        call2.enqueue(new Callback<List<Story>>() {
            @Override
            public void onResponse(Response<List<Story>> response, Retrofit retrofit) {
                int statusCode = response.code();
                assertTrue(statusCode == 200);
                List<Story> list = response.body();
                for (final Story a : list) {
                    System.out.println(a.getTitle());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                assert false;
            }
        });

    }

    interface Api {

        @GET("/rest/hwy67/article")
        Call<Story> getArticle(@Query("id") long id);

        @GET("/rest/hwy67/articles")
        Call<List<Story>> getArticles(@Query("sort") String sort);
    }
}
