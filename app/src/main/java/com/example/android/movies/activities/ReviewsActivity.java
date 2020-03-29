package com.example.android.movies.activities;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.android.movies.R;
import com.example.android.movies.adapters.ReviewsAdapter;
import com.example.android.movies.databinding.ActivityReviewsBinding;
import com.example.android.movies.models.Review;
import com.example.android.movies.utils.Utilities;

import java.util.ArrayList;



public class ReviewsActivity extends AppCompatActivity {

    private ActivityReviewsBinding binding;

    private Parcelable mListState;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<Review> currentMovieReviews;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityReviewsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        if(savedInstanceState != null){
            currentMovieReviews = savedInstanceState.getParcelableArrayList(Utilities.EXTRA_MOVIE_REVIEWS);
        }
         else {
            currentMovieReviews = getIntent().getParcelableArrayListExtra(Utilities.EXTRA_MOVIE_REVIEWS);
         }

        String currentMovieTitle = getIntent().getStringExtra(Utilities.EXTRA_MOVIE_TITLE);

        if(currentMovieReviews != null && currentMovieReviews.size() > 0){

            setTitle(getString(R.string.reviews_for, currentMovieTitle));

            ReviewsAdapter mReviewsAdapter = new ReviewsAdapter(currentMovieReviews, this);
            binding.rvReviewslist.setAdapter(mReviewsAdapter);
            binding.rvReviewslist.setLayoutManager(new LinearLayoutManager(this));
            binding.rvReviewslist.setHasFixedSize(true);

            mLayoutManager = binding.rvReviewslist.getLayoutManager();
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList(Utilities.EXTRA_MOVIE_REVIEWS, currentMovieReviews);

        mListState = mLayoutManager.onSaveInstanceState();
        savedInstanceState.putParcelable(Utilities.RECYCLER_VIEW_STATE_KEY, mListState);

        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if(savedInstanceState != null) {
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
}
