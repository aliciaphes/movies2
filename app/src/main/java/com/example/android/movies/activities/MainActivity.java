package com.example.android.movies.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.android.movies.R;
import com.example.android.movies.adapters.MoviesAdapter;
import com.example.android.movies.database.MovieViewModel;
import com.example.android.movies.listeners.MovieListListener;
import com.example.android.movies.models.Movie;
import com.example.android.movies.network.MovieAPI;
import com.example.android.movies.network.ServiceGenerator;
import com.example.android.movies.utils.MovieAPIdeserializer;
import com.example.android.movies.utils.Utilities;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.rv_movielist)
    RecyclerView mMovieRecyclerView;

    @BindView(R.id.view_flipper)
    ViewFlipper mViewFlipper;

    private Parcelable mListState;
    private RecyclerView.LayoutManager mLayoutManager;

    private MoviesAdapter mMovieAdapter;

    private ArrayList<Movie> mMovieList = new ArrayList<>();
    private ArrayList<Movie> mMovieListFromDB = new ArrayList<>();

    private String mCriterionLabel;

    private MovieViewModel mMovieViewModel;



    private MovieListListener movieListListener = new MovieListListener() {
        @Override
        public void changeLayout() {
            if(mMovieList.isEmpty()){
                mViewFlipper.setDisplayedChild(0);
            } else{
                mViewFlipper.setDisplayedChild(1);
            }
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        // Re-created activities receive the same mMovieViewModel instance created by the first activity:
        mMovieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
        setUpObserverForMovieDB();

        // set default criterion:
        setDefaultCriterion();

        // prepare grid of movies:
        mMovieAdapter = new MoviesAdapter( this, mMovieList);
        mMovieRecyclerView.setAdapter(mMovieAdapter);
        mMovieRecyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns()));

        mLayoutManager = mMovieRecyclerView.getLayoutManager();

        if(savedInstanceState != null){
            mCriterionLabel = savedInstanceState.getString(Utilities.CRITERION_LABEL, getString(Utilities.getDefaultCriterionLabel()));
            setTitle(getString(R.string.app_name) + ": " + mCriterionLabel);
            ArrayList<Movie> movieListFromInstanceState = savedInstanceState.getParcelableArrayList(Utilities.MOVIELIST_KEY);
            mMovieList.addAll(movieListFromInstanceState);
            mMovieAdapter = new MoviesAdapter( this, mMovieList);
            mMovieRecyclerView.swapAdapter(mMovieAdapter, true);
            movieListListener.changeLayout();
        } else {
            fetchMovies();
        }
    }


    private void setUpObserverForMovieDB(){
        mMovieViewModel.getAllMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable final List<Movie> movies) {
                // Update the cached copy of the movies in the adapter.

                if (movies != null) {
                    mMovieListFromDB.clear();
                    if(movies.size() != 0){
                        mMovieListFromDB.addAll(movies);
                    }
                }

                int criterion = Utilities.getCriterionFromSharedPreferences(MainActivity.this);
                if(criterion == R.id.favorites){
                    showFavoriteMoviesFromDB();
                }
            }
        });
    }


    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        //this divider can be changed to adjust the size of the poster
        int widthDivider = Integer.valueOf(Utilities.POSTER_SIZE);
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if(nColumns < 2) return 2;
        return nColumns;
    }


    private void setDefaultCriterion(){
        int criterion = Utilities.getCriterionFromSharedPreferences(this);
        if(criterion == 0){
            Utilities.setCriterionToSharedPreferences(this, Utilities.getDefaultCriterion());
        }
    }



    private void fetchMovies() {
        int criterion = Utilities.getCriterionFromSharedPreferences(this);
        mCriterionLabel = "";
        switch (criterion){
            case R.id.top_rated:
                mMovieList.clear();
                getMoviesFromNetwork(Utilities.TOP_RATED);
                mCriterionLabel = getString(R.string.top_rated);
                break;
            case R.id.most_popular:
                mMovieList.clear();
                getMoviesFromNetwork(Utilities.MOST_POPULAR);
                mCriterionLabel = getString(R.string.most_popular);
                break;
            case R.id.favorites:
                showFavoriteMoviesFromDB();
                mCriterionLabel = getString(R.string.favorites);
                break;
        }
        setTitle(getString(R.string.app_name) + ": " + mCriterionLabel);
    }


    private void showFavoriteMoviesFromDB() {
        mMovieList.clear();
        if(mMovieListFromDB.size() > 0) {
            mMovieList.addAll(mMovieListFromDB);
        }
        mMovieAdapter = new MoviesAdapter( MainActivity.this, mMovieList);
        mMovieRecyclerView.swapAdapter(mMovieAdapter, true);
        movieListListener.changeLayout();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.preferences_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Utilities.setCriterionToSharedPreferences(this, item.getItemId());

        fetchMovies();
        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList(Utilities.MOVIELIST_KEY, mMovieList);
        savedInstanceState.putParcelableArrayList(Utilities.MOVIELIST_FROMDB_KEY, mMovieListFromDB);
        savedInstanceState.putString(Utilities.CRITERION_LABEL, mCriterionLabel);

        mListState = mLayoutManager.onSaveInstanceState();
        savedInstanceState.putParcelable(Utilities.RECYCLER_VIEW_STATE_KEY, mListState);

        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if(savedInstanceState != null) {
            mMovieList = savedInstanceState.getParcelableArrayList(Utilities.MOVIELIST_KEY);
            mMovieListFromDB = savedInstanceState.getParcelableArrayList(Utilities.MOVIELIST_FROMDB_KEY);
            mCriterionLabel = savedInstanceState.getString(Utilities.CRITERION_LABEL);
            mListState = savedInstanceState.getParcelable(Utilities.RECYCLER_VIEW_STATE_KEY);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mListState != null) {
            mLayoutManager.onRestoreInstanceState(mListState);
        }
    }


    private void getMoviesFromNetwork(String criterionParameter) {

        String language = Utilities.getLanguage();
        String apiKey = Utilities.getMoviedbAPIkey(this);

        Map<String, String> params = new HashMap<>();
        params.put("api_key", apiKey);
        params.put("language", language);

        // check connectivity:
        if (Utilities.isNetworkAvailable(this) && Utilities.isOnline()) {

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(new TypeToken<List<Movie>>() {
                            }.getType(),
                            new MovieAPIdeserializer<>(Movie[].class)
                    )
                    .create();

            MovieAPI apiInterface = ServiceGenerator.createService(MovieAPI.class, gson);
            Call<List<Movie>> call = apiInterface.getMovies(criterionParameter, params);
            call.enqueue(new Callback<List<Movie>>() {
                @Override
                public void onResponse(@NonNull Call<List<Movie>> call, @NonNull Response<List<Movie>> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            ArrayList<Movie> moviesFromNetwork = new ArrayList<>();
                            for (Movie movie : response.body()) {
                                movie.setPoster(movie.getPoster());
                                moviesFromNetwork.add(movie);
                            }
                            if (moviesFromNetwork.size() > 0) {
                                mMovieList.addAll(moviesFromNetwork);
                                mMovieAdapter = new MoviesAdapter( MainActivity.this, mMovieList);
                                mMovieRecyclerView.swapAdapter(mMovieAdapter, true);
                            }
                            movieListListener.changeLayout();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<Movie>> call, Throwable t) {
                    Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // launch offline activity:
            Intent intent = new Intent(this, OfflineActivity.class);
            startActivity(intent);
        }
    }
} // end of class
