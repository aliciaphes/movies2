package com.example.android.movies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.movies.R;
import com.example.android.movies.databinding.ItemReviewBinding;
import com.example.android.movies.models.Review;

import java.util.ArrayList;



public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsViewHolder> {

    private ArrayList<Review> mReviewsList;
    private Context context;

    public ReviewsAdapter(ArrayList<Review> reviewsList, Context ctx) {
        context = ctx;
        mReviewsList = reviewsList;
    }

    @NonNull
    @Override
    public ReviewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        ItemReviewBinding binding = ItemReviewBinding.inflate(inflater); // R.layout.item_review
        return new ReviewsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ReviewsViewHolder holder, int position) {
        Review review = mReviewsList.get(position);

        holder.binding.tvAuthor.setText(context.getString(R.string.review_by, review.getAuthor()));

        holder.binding.arrow.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_arrow_right));

        holder.binding.tvReview.setText(review.getReviewContent());

        holder.toggleReviewVisibility(review.getContentVisibility());
    }




    @Override
    public int getItemCount() {
        if (mReviewsList != null)
            return mReviewsList.size();
        else return 0;
    }

    public class ReviewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ItemReviewBinding binding;


        @Override
        public void onClick(View v) { //R.id.cl_arrow_and_author
            int position = getAdapterPosition();
            Review review = mReviewsList.get(position);
            boolean contentVisibility = review.getContentVisibility();
            review.setContentVisibility(!contentVisibility);//toggle
            mReviewsList.set(position, review);//overwrite with new value
            toggleReviewVisibility(review.getContentVisibility());
        }


        private void toggleReviewVisibility(boolean displaying){
            if (!displaying) {
                binding.arrow.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_arrow_right));
                binding.tvReview.setVisibility(View.GONE);
            } else {
                binding.arrow.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_arrow_down));
                binding.tvReview.setVisibility(View.VISIBLE);
            }
        }

        private ReviewsViewHolder(ItemReviewBinding b) {
            super(b.getRoot());
            this.binding = b;

            itemView.setOnClickListener(this);
        }
    }
}
