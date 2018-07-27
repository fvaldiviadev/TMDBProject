package Interfaces;

import java.util.Map;

import Pojo.PopularMoviesFeed;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface TheMovieDB_MovieService {
    @GET("/popular")
    Call<PopularMoviesFeed> getData(
            @QueryMap Map<String, String> parameters
    );
}
