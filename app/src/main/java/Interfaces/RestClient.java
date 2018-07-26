package Interfaces;

import Pojo.PopularMoviesFeed;
import retrofit2.Call;
import retrofit2.http.GET;

public interface RestClient {
    @GET("popularMovies")
    Call<PopularMoviesFeed> getData();
}
