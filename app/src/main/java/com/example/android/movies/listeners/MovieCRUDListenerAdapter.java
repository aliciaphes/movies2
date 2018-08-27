package com.example.android.movies.listeners;

import com.example.android.movies.models.Movie;

// we use this class as a listener so we don't have to implement all of the interface's methods
// only the ones we need
public class MovieCRUDListenerAdapter implements MovieCRUDListener {

    public void onPostExecuteConcluded() {
    }

    public void onPostExecuteConcluded(Movie movie) {

    }
}
