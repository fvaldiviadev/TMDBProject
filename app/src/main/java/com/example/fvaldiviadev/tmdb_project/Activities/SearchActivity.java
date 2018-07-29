package com.example.fvaldiviadev.tmdb_project.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.fvaldiviadev.tmdb_project.R;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Adapters.SearchMovieListAdapter;
import Interfaces.OnLoadMoreMoviesListener;
import Interfaces.TheMovieDB_MovieService;
import Pojo.FoundMovie;
import Pojo.SearchResults;
import Utils.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchActivity extends AppCompatActivity {

    private EditText searchEditText;
    private RecyclerView recyclerView;
    private LinearLayoutManager mLayoutManager;
    private SearchMovieListAdapter adapter;
    private TextView nomoviesfoundTextView;
    private ProgressBar pb_searchlist;
    private int totalPages;

    int page;

    Call<SearchResults> call;
    String currentSearch = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);

        initComponents();

        setAdapter();

    }

    private void initComponents() {
        searchEditText = findViewById(R.id.et_search);
        recyclerView = findViewById(R.id.rv_searchmovielist);
        nomoviesfoundTextView = findViewById(R.id.tv_searchnomovies);
        pb_searchlist = findViewById(R.id.pb_searchlist);


        searchEditText.setOnKeyListener(listenerSearchEditText());

        recyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(mLayoutManager);

    }

    private void setAdapter() {
        adapter = new SearchMovieListAdapter(recyclerView);

        recyclerView.setAdapter(adapter);

        adapter.setLoading(true);


        adapter.setOnLoadMoreMoviesListener(new OnLoadMoreMoviesListener() {
            @Override
            public void onLoadMoreMovies() {
                int nextPage = page + 1;
                if (nextPage < totalPages) {
                    search(nextPage, false);
                }

            }
        });
    }

    private View.OnKeyListener listenerSearchEditText() {
        View.OnKeyListener listener = new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                String searchText = searchEditText.getText().toString();

                if (keyEvent.getKeyCode() != KeyEvent.KEYCODE_BACK) {
                    if (!currentSearch.equals(searchText) && searchText.length() > 1) {
                        search(1, true);
                        currentSearch = searchText;
                    }
                }

                return false;
            }
        };
        return listener;
    }

    private void search(int searchPage, final boolean firstSearch) {

        pb_searchlist.setVisibility(View.VISIBLE);

        if (call != null && call.isExecuted()) {
            call.cancel();
        }
        page = searchPage;
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
        data.put("query", searchEditText.getText().toString());
        data.put("page", String.valueOf(page));
        call = theMovieDBMovieService.getSearchResults(data);


        call.enqueue(new Callback<SearchResults>() {
            @Override
            public void onResponse(Call<SearchResults> call, Response<SearchResults> response) {
                switch (response.code()) {
                    case 200:
                        pb_searchlist.setVisibility(View.GONE);

                        SearchResults data = response.body();

                        if (firstSearch) {
                            adapter.clearList();
                        }

                        totalPages = data.getTotalPages();

                        List<FoundMovie> newFoundMovieList = data.getResults();
                        for (int i = 0; i < newFoundMovieList.size(); i++) {
                            adapter.addItem(newFoundMovieList.get(i));
                            adapter.setLoading(false);
                        }

                        break;
                    case 401:
                        pb_searchlist.setVisibility(View.GONE);
                        break;
                    default:
                        nomoviesfoundTextView.append(" - Error: " + response.code() + " - " + response.message() + " : " + call.request().url().url());
                        break;
                }
            }

            @Override
            public void onFailure(Call<SearchResults> call, Throwable t) {
                Log.e("error", t.toString());
            }
        });
    }
}
