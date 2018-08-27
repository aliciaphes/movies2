package com.example.android.movies.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.android.movies.models.Movie;

import java.util.List;

@Dao
public interface MovieDao {

    // Add a movie to the database
    @Insert
    void insert(Movie movie);

    // Remove a movie from the database
    @Delete
    void delete(Movie person);

    // Get all movies in the database
    @Query("SELECT * FROM movie")
    LiveData<List<Movie>> getFavoriteMovies();

    // Find movie by ID
    @Query("SELECT * FROM movie WHERE id LIKE :movieID")
    Movie getMovieByID(long movieID);


}
