package com.example.fvaldiviadev.tmdb_project.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.fvaldiviadev.tmdb_project.R;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Adapters.PopularMovieListAdapter;
import Interfaces.OnLoadMoreMoviesListener;
import Interfaces.TheMovieDB_MovieService;
import Pojo.PopularMovie;
import Pojo.PopularMoviesFeed;
import Utils.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {


    private TextView tvEmptyView;
    private RecyclerView rvPopularMovieList;
    private PopularMovieListAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    private List<PopularMovie> popularMovieList;
    private int page;
    private int totalPages;

    protected Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();

        loadNextMovies(page);

        setAdapter();


    }

    private void initComponents() {
        tvEmptyView = (TextView) findViewById(R.id.tv_nomovies);
        rvPopularMovieList = (RecyclerView) findViewById(R.id.rv_popularmovielist);
        popularMovieList = new ArrayList<PopularMovie>();
        handler = new Handler();

        rvPopularMovieList.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        rvPopularMovieList.setLayoutManager(linearLayoutManager);

        page = 1;
    }

    private void setAdapter() {
        adapter = new PopularMovieListAdapter(popularMovieList, rvPopularMovieList);

        rvPopularMovieList.setAdapter(adapter);

        adapter.setLoading(true);


        if (popularMovieList.isEmpty()) {
            rvPopularMovieList.setVisibility(View.GONE);
            tvEmptyView.setVisibility(View.VISIBLE);

        } else {
            rvPopularMovieList.setVisibility(View.VISIBLE);
            tvEmptyView.setVisibility(View.GONE);
        }

        adapter.setOnLoadMoreMoviesListener(new OnLoadMoreMoviesListener() {
            @Override
            public void onLoadMoreMovies() {
                int nextPage = page + 1;
                if (nextPage < totalPages) {
                    loadNextMovies(++page);
                }

            }
        });
    }

    private void loadNextMovies(int page) {
        popularMovieList.add(null);
        if (adapter != null) {
            adapter.notifyItemInserted(popularMovieList.size() - 1);
        }

        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        TheMovieDB_MovieService theMovieDBMovieService = retrofit.create(TheMovieDB_MovieService.class);
        Map<String, String> data = new HashMap<>();
        data.put("api_key", Constants.API_KEY);
        data.put("language", Constants.LANGUAGE_GET_REQUEST);
        data.put("page", String.valueOf(page));
        Call<PopularMoviesFeed> call = theMovieDBMovieService.getData(data);

        call.enqueue(new Callback<PopularMoviesFeed>() {
            @Override
            public void onResponse(Call<PopularMoviesFeed> call, Response<PopularMoviesFeed> response) {
                switch (response.code()) {
                    case 200:

                        //   remove progress item
                        popularMovieList.remove(popularMovieList.size() - 1);
                        adapter.notifyItemRemoved(popularMovieList.size());

                        PopularMoviesFeed data = response.body();

                        totalPages = data.getTotalPages();

                        List<PopularMovie> newPopularMovieList = data.getPopularMovies();
                        for (int i = 0; i < newPopularMovieList.size(); i++) {
                            adapter.addItem(newPopularMovieList.get(i));
                        }

                        adapter.notifyDataSetChanged();

                        adapter.setLoading(false);

                        break;
                    case 401:
                        break;
                    default:
                        tvEmptyView.append(" - Error: " + response.code() + " - " + response.message() + " : " + call.request().url().url());
                        break;
                }
            }

            @Override
            public void onFailure(Call<PopularMoviesFeed> call, Throwable t) {
                Log.e("error", t.toString());
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:

                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);

                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

}
