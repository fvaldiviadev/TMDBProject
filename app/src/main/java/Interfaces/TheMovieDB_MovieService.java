package Interfaces;

import java.util.Map;

import Pojo.PopularMoviesFeed;
import Pojo.SearchResults;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface TheMovieDB_MovieService {
    @GET("/3/movie/popular")
    Call<PopularMoviesFeed> getData(
            @QueryMap Map<String, String> parameters
    );

    @GET("/3/search/movie")
    Call<SearchResults> getSearchResults(
            @QueryMap Map<String, String> parameters
    );

}
