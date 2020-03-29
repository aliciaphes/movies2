package com.example.android.movies.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.movies.R;
import com.example.android.movies.activities.DetailActivity;
import com.example.android.movies.databinding.ItemMovieBinding;
import com.example.android.movies.models.Movie;
import com.example.android.movies.utils.Utilities;
import com.squareup.picasso.Picasso;

import java.util.List;



public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {
    private Context context;
    private List<Movie> mMovieList;


    public MoviesAdapter(Context context, List<Movie> movieList) {
        this.context = context;
        this.mMovieList = movieList;
    }


    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        ItemMovieBinding binding = ItemMovieBinding.inflate(inflater); // R.layout.item_movie
        return new MovieViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = mMovieList.get(position);

        Picasso.with(context)
                .load(movie.getPoster())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(holder.binding.moviePoster);
    }

    @Override
    public int getItemCount() {
        if (mMovieList != null)
            return mMovieList.size();
        else return 0;
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ItemMovieBinding binding; // R.id.movie_poster

        private MovieViewHolder(ItemMovieBinding b) {
            super(b.getRoot());
            this.binding = b;

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Movie m = mMovieList.get(position);
            if(m != null) {
                launchDetailActivity(m);
            }
        }

        private void launchDetailActivity(Movie movie){
            Intent detailIntent = new Intent(context, DetailActivity.class);

            // put extra for the movie that was just clicked on
            detailIntent.putExtra(Utilities.EXTRA_MOVIE, movie);

            context.startActivity(detailIntent);
        }
    }
}
