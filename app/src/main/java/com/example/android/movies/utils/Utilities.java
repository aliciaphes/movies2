package com.example.android.movies.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.android.movies.R;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Utilities {

    // poster size
    // it can be "w92", "w154", "w185", "w342", "w500", "w780", or "original"
    public final static String POSTER_SIZE = "342";

    public final static String POSTER_BASE_URL = "https://image.tmdb.org/t/p";
    public final static String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";

    public final static String TOP_RATED = "top_rated";
    public final static String MOST_POPULAR = "popular";

    public final static String CRITERION_LABEL = "CRITERION_LABEL";

    public final static String EXTRA_MOVIE = "EXTRA_MOVIE";
    public final static String EXTRA_MOVIE_REVIEWS = "EXTRA_MOVIE_REVIEWS";
    public final static String EXTRA_MOVIE_TITLE = "EXTRA_MOVIE_TITLE";

    public final static String MOVIELIST_KEY = "MOVIELIST_KEY";
    public final static String MOVIELIST_FROMDB_KEY = "MOVIELIST_FROMDB_KEY";

    public final static String RECYCLER_VIEW_STATE_KEY = "RECYCLER_VIEW_STATE_KEY";

    private final static String SETTINGS = "SETTINGS";

    private final static String DEFAULT_LANGUAGE = "en-US";

    // the API returns dates in the form yyyy-mm-dd
    private final static String DATE_FORMAT = "yyyy-mm-dd";

    public static String formatDate(String stringDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        Date date;
        String dateString = null;
        try {
            date = dateFormat.parse(stringDate);
            dateFormat.applyPattern("EEEE, d MMM yyyy");
            dateString = dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateString;
    }

    public static String getLanguage() {
        return DEFAULT_LANGUAGE;
    }

    public static String getMoviedbAPIkey(Context context) {
        return context.getString(R.string.moviedb_api_key);
    }

    public static String getYouTubeAPIkey(Context context) {
        return context.getString(R.string.youtube_api_key);
    }

    public static int getDefaultCriterion() {
        return R.id.most_popular;
    }

    public static int getDefaultCriterionLabel() {
        return R.string.most_popular;
    }

    public static int getCriterionFromSharedPreferences(Context context){
        SharedPreferences mPreferences = context.getSharedPreferences(Utilities.SETTINGS, Context.MODE_PRIVATE);
        return mPreferences.getInt("criterion", 0);
    }


    public static void setCriterionToSharedPreferences(Context context, int criterion){
        SharedPreferences.Editor editor = context.getSharedPreferences(Utilities.SETTINGS, Context.MODE_PRIVATE).edit();
        editor.putInt("criterion", criterion);
        editor.apply();
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public static boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}
