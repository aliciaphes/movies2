package com.example.android.movies.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.android.movies.R;
import com.example.android.movies.adapters.TrailersAdapter;
import com.example.android.movies.database.MovieViewModel;
import com.example.android.movies.database.MovieViewModel.GetMovieByIDAsyncTask;
import com.example.android.movies.database.MovieViewModel.MovieAsyncTask;
import com.example.android.movies.databinding.ActivityDetailBinding;
import com.example.android.movies.listeners.MovieCRUDListenerAdapter;
import com.example.android.movies.models.Movie;
import com.example.android.movies.models.Review;
import com.example.android.movies.models.Trailer;
import com.example.android.movies.network.MovieAPI;
import com.example.android.movies.network.ServiceGenerator;
import com.example.android.movies.utils.MovieAPIdeserializer;
import com.example.android.movies.utils.Utilities;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.android.movies.utils.Utilities.EXTRA_MOVIE_REVIEWS;
import static com.example.android.movies.utils.Utilities.EXTRA_MOVIE_TITLE;

public class DetailActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private Movie mCurrentMovie;

    private MovieViewModel mMovieViewModel;

    private ActivityDetailBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view /*R.layout.activity_detail*/);

        mMovieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);

        boolean errorRetrievingMovie = false;
        if (savedInstanceState != null) {
            mCurrentMovie = savedInstanceState.getParcelable(Utilities.EXTRA_MOVIE);

            if (mCurrentMovie != null) {
                displayMovieMainDetails();
                setCarouselOfTrailers();
                setSeeAllReviewsButtonLogic();
            } else {
                errorRetrievingMovie = true;
            }
        } else {
            Intent existingIntent = getIntent();
            mCurrentMovie = existingIntent.getParcelableExtra(Utilities.EXTRA_MOVIE);

            if (mCurrentMovie != null) {

                getTrailersFromNetwork();

                getReviewsFromNetwork();

                displayMovieMainDetails();
            } else {
                errorRetrievingMovie = true;
            }
        }

        if (errorRetrievingMovie) {
            Toast.makeText(this, R.string.movie_null, Toast.LENGTH_SHORT).show();
        }
    }


    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        MovieAsyncTask movieAsyncTask;
        if (isChecked) {
            movieAsyncTask = mMovieViewModel.insert();
            movieAsyncTask.setListener(new MovieCRUDListenerAdapter() {
                @Override
                public void onPostExecuteConcluded() {
                    Toast.makeText(DetailActivity.this,
                            getString(R.string.added, mCurrentMovie.getTitle()),
                            Toast.LENGTH_SHORT).show();
                }
            });
            movieAsyncTask.execute(mCurrentMovie);

        } else {
            movieAsyncTask = mMovieViewModel.delete();
            movieAsyncTask.setListener(new MovieCRUDListenerAdapter() {
                @Override
                public void onPostExecuteConcluded() {
                    Toast.makeText(DetailActivity.this,
                            getString(R.string.removed, mCurrentMovie.getTitle()),
                            Toast.LENGTH_SHORT).show();
                }
            });
            movieAsyncTask.execute(mCurrentMovie);
        }
    }


    private void setUpLogicForFavoriteMovie() {

        int criterion = Utilities.getCriterionFromSharedPreferences(this);

        binding.buttonFavorite.setChecked(false); // initially mark it as not favorite

        // if we're in 'display favorites' mode, just mark it as such

        if (criterion == R.id.favorites) {
            binding.buttonFavorite.setChecked(true);
            binding.buttonFavorite.setOnCheckedChangeListener(this);
        } else {
            // search for the movie in the database:
            GetMovieByIDAsyncTask findMovieTask = mMovieViewModel.getMovieByID();
            findMovieTask.setListener(new MovieCRUDListenerAdapter() {
                @Override
                public void onPostExecuteConcluded(Movie favoriteMovie) {
                    if (favoriteMovie != null) {
                        binding.buttonFavorite.setChecked(true);
                    }
                    // and now is when we set the listener: listen from now on, not before
                    binding.buttonFavorite.setOnCheckedChangeListener(DetailActivity.this);
                }
            });
            findMovieTask.execute(mCurrentMovie.getId());
        }
    }


    private void getTrailersFromNetwork() {
        String language = Utilities.getLanguage();
        String apiKey = Utilities.getMoviedbAPIkey(this);

        Map<String, String> params = new HashMap<>();
        params.put("api_key", apiKey);
        params.put("language", language);

        // check connectivity:
        if (Utilities.isNetworkAvailable(this) && Utilities.isOnline()) {

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(new TypeToken<List<Trailer>>() {
                            }.getType(),
                            new MovieAPIdeserializer<>(Trailer[].class)
                    )
                    .create();

            MovieAPI apiInterface = ServiceGenerator.createService(MovieAPI.class, gson);

            Call<List<Trailer>> call = apiInterface.getTrailers(mCurrentMovie.getId(), params);
            call.enqueue(new Callback<List<Trailer>>() {
                @Override
                public void onResponse(Call<List<Trailer>> call, Response<List<Trailer>> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            for (Trailer trailer : response.body()) {
                                if (trailer.getSite().equals("YouTube")) {
                                    mCurrentMovie.addTrailer(trailer);
                                }
                            }
                            setCarouselOfTrailers();
                        }
                    } else {
                        Toast.makeText(DetailActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Trailer>> call, Throwable t) {
                    Toast.makeText(DetailActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setCarouselOfTrailers() {
        int n = mCurrentMovie.getNumTrailers();
        if (n > 0) {
            binding.trailersLabel.setText(getString(R.string.trailers, n));

            TrailersAdapter mTrailersAdapter = new TrailersAdapter(mCurrentMovie.getTrailers(), DetailActivity.this);
            binding.rvTrailerlist.setAdapter(mTrailersAdapter);
            binding.rvTrailerlist.setLayoutManager(new LinearLayoutManager(DetailActivity.this, LinearLayoutManager.HORIZONTAL, false));
            binding.rvTrailerlist.setHasFixedSize(true);
        } else {
            binding.trailersLabel.setVisibility(View.INVISIBLE);
        }
    }


    private void getReviewsFromNetwork() {
        String language = Utilities.getLanguage();
        String apiKey = Utilities.getMoviedbAPIkey(this);

        Map<String, String> params = new HashMap<>();
        params.put("api_key", apiKey);
        params.put("language", language);

        // check connectivity:
        if (Utilities.isNetworkAvailable(this) && Utilities.isOnline()) {

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(new TypeToken<List<Review>>() {
                            }.getType(),
                            new MovieAPIdeserializer<>(Review[].class)
                    )
                    .create();

            MovieAPI apiInterface = ServiceGenerator.createService(MovieAPI.class, gson);

            Call<List<Review>> call = apiInterface.getReviews(mCurrentMovie.getId(), params);
            call.enqueue(new Callback<List<Review>>() {
                @Override
                public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {

                            mCurrentMovie.clearReviewsArray();

                            for (Review review : response.body()) {
                                mCurrentMovie.addReview(review);
                            }
                            setSeeAllReviewsButtonLogic();
                        }
                    } else {
                        Toast.makeText(DetailActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<Review>> call, @NonNull Throwable t) {
                    Toast.makeText(DetailActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setSeeAllReviewsButtonLogic() {
        int n = mCurrentMovie.getNumReviews();
        if (n > 0) {
            binding.buttonSeeAll.setText(getString(R.string.see_reviews, n));
        } else {
            binding.buttonSeeAll.setVisibility(View.INVISIBLE);
        }
    }


    private void displayMovieMainDetails() {
        setTitle(mCurrentMovie.getTitle());

        binding.movieOriginalTitle.setText(mCurrentMovie.getOriginalTitle());

        formatRating(mCurrentMovie.getRating());

        binding.movieReleasedate.setText(
                Utilities.formatDate(mCurrentMovie.getReleaseDate())
        );

        binding.buttonFavorite.setHeight(binding.movieRating.getHeight());

        Picasso.with(this)
                .load(mCurrentMovie.getPoster())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(binding.moviePoster);

        binding.movieSynopsis.setText(mCurrentMovie.getSynopsis());

        // set up logic for the 'heart' button to add/remove movie as favorite:
        setUpLogicForFavoriteMovie();
    }

    private void formatRating(Double rating) {
        double ratingOutOfFive = (5 * rating) / 10.0;
        // todo: this looks like it could be potentially improved:
        String stringValue = String.format("%.2f", ratingOutOfFive);
        binding.movieRating.setRating(Float.valueOf(stringValue));
    }


    public void displayAllReviews(View view) {
        Intent intent = new Intent(this, ReviewsActivity.class);
        intent.putParcelableArrayListExtra(EXTRA_MOVIE_REVIEWS, mCurrentMovie.getReviews());
        intent.putExtra(EXTRA_MOVIE_TITLE, mCurrentMovie.getTitle());
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(Utilities.EXTRA_MOVIE, mCurrentMovie);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCurrentMovie = savedInstanceState.getParcelable(Utilities.EXTRA_MOVIE);
    }
}
