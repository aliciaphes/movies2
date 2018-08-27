package com.example.android.movies.listeners;

import com.example.android.movies.models.Movie;

public interface MovieCRUDListener {
    void onPostExecuteConcluded();
    void onPostExecuteConcluded(Movie movie);
}
