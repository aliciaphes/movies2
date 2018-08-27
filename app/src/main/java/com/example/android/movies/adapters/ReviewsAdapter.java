package com.example.android.movies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.movies.R;
import com.example.android.movies.models.Review;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

        View view = inflater.inflate(R.layout.item_review, parent, false);

        return new ReviewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ReviewsViewHolder holder, int position) {
        Review review = mReviewsList.get(position);

        holder.tvReviewAuthor.setText(context.getString(R.string.review_by, review.getAuthor()));

        holder.ivArrow.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_arrow_right));

        holder.tvReviewContent.setText(review.getReviewContent());

        holder.toggleReviewVisibility(review.getContentVisibility());
    }


    @Override
    public int getItemCount() {
        if (mReviewsList != null)
            return mReviewsList.size();
        else return 0;
    }

    public class ReviewsViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.tv_author)
        TextView tvReviewAuthor;

        @BindView(R.id.tv_review)
        TextView tvReviewContent;

        @BindView(R.id.arrow)
        ImageView ivArrow;

        @OnClick(R.id.cl_arrow_and_author)
        public void showContentOfReview(){
            int position = getAdapterPosition();
            Review review = mReviewsList.get(position);
            boolean contentVisibility = review.getContentVisibility();
            review.setContentVisibility(!contentVisibility);//toggle
            mReviewsList.set(position, review);//overwrite with new value
            toggleReviewVisibility(review.getContentVisibility());
        }



        private void toggleReviewVisibility(boolean displaying){
            if (!displaying) {
                ivArrow.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_arrow_right));
                tvReviewContent.setVisibility(View.GONE);
            } else {
                ivArrow.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_arrow_down));
                tvReviewContent.setVisibility(View.VISIBLE);
            }
        }

        private ReviewsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
