package com.example.android.movies.network;

import com.example.android.movies.models.Movie;
import com.example.android.movies.models.Review;
import com.example.android.movies.models.Trailer;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface MovieAPI {

    //list of movies based on a specific criterion:
    //https://api.themoviedb.org/3/movie/{criterion}?api_key=xxxxx
    @GET("{criterion}")
    Call<List<Movie>> getMovies(@Path("criterion") String criterion,
                                @QueryMap Map<String, String> options);

    //trailers:
    //https://api.themoviedb.org/3/movie/{movieID}/videos?api_key=xxxxx
    @GET("{movieID}/videos")
    Call<List<Trailer>> getTrailers(@Path("movieID") long movieID,
                                    @QueryMap Map<String, String> options);

    //reviews:
    //https://api.themoviedb.org/3/movie/{movieID}/reviews?api_key=xxxxx
    @GET("{movieID}/reviews")
    Call<List<Review>> getReviews(@Path("movieID") long movieID,
                                  @QueryMap Map<String, String> options);
}
