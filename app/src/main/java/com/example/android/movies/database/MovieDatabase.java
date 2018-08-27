package com.example.android.movies.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.android.movies.models.Movie;

@Database(entities = {Movie.class}, version = 1)
public abstract class MovieDatabase extends RoomDatabase {

    private static String DATABASE_NAME = "movie_database";
    private static MovieDatabase MovieDatabaseInstance;

    public abstract MovieDao getMovieDao();


    public static MovieDatabase getDatabase(final Context context) {
        if (MovieDatabaseInstance == null) {
            synchronized (MovieDatabase.class) {
                if (MovieDatabaseInstance == null) {
                    MovieDatabaseInstance = Room.databaseBuilder(context.getApplicationContext(),
                            MovieDatabase.class, DATABASE_NAME).build();
                }
            }
        }
        return MovieDatabaseInstance;
    }
}