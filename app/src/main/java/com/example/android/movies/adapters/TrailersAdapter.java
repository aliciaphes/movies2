
package com.example.android.movies.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android.movies.R;
import com.example.android.movies.databinding.ItemTrailerBinding;
import com.example.android.movies.models.Trailer;
import com.example.android.movies.utils.Utilities;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import java.util.ArrayList;
import java.util.List;

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailerViewHolder>{

    private static final int RECOVERY_REQUEST = 1;

    private Context context;
    private ArrayList<Trailer> mTrailerList;
    private String youTubeAPIkey;
    private YouTubeThumbnailLoader youTubeThumbnailLoader;




    public TrailersAdapter(ArrayList<Trailer> trailerList, Context ctx) {
        context = ctx;
        mTrailerList = trailerList;
        youTubeAPIkey = Utilities.getYouTubeAPIkey(context);
    }

    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        ItemTrailerBinding binding = ItemTrailerBinding.inflate(inflater); // R.layout.item_trailer
        return new TrailerViewHolder(binding);
    }



    @Override
    public void onBindViewHolder(@NonNull final TrailerViewHolder holder, final int position) {

        Trailer trailer = mTrailerList.get(position);
        final String videoKey = trailer.getKey();

        holder.binding.thumbnail.setTag(videoKey);

        holder.binding.thumbnail.initialize(youTubeAPIkey, new YouTubeThumbnailView.OnInitializedListener(){

            @Override
            public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, final YouTubeThumbnailLoader thumbnailLoader) {
                youTubeThumbnailLoader = thumbnailLoader;
                youTubeThumbnailLoader.setVideo(youTubeThumbnailView.getTag().toString());
                //.setVideo(videoKey);
                youTubeThumbnailLoader.setOnThumbnailLoadedListener(new YouTubeThumbnailLoader.OnThumbnailLoadedListener(){
                    @Override
                    public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
                        youTubeThumbnailLoader.release();
                    }

                    @Override
                    public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {
                        Toast.makeText(context, R.string.error_loading_thumbnail, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
                Toast.makeText(context, R.string.error_initializing_thumbnailview, Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public int getItemCount() {
        if (mTrailerList != null)
            return mTrailerList.size();
        else return 0;
    }





    class TrailerViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, YouTubePlayer.OnInitializedListener {

        ItemTrailerBinding binding;

        private TrailerViewHolder(ItemTrailerBinding b) {
            super(b.getRoot());
            binding = b;

            binding.thumbnail.getLayoutParams().height = Integer.valueOf(Utilities.POSTER_SIZE);

            itemView.setOnClickListener(this);
        }



        @Override
        public void onClick(View v) {
            int position = getLayoutPosition();
            Trailer trailer = mTrailerList.get(position);
            if(trailer != null){
                String trailerKey = trailer.getKey();
                if(trailerKey != null){

                    // open a little dialog to allow the user to choose among
                    // the YouTube app and other apps to play the video

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Utilities.YOUTUBE_BASE_URL + trailerKey));

                    List<ResolveInfo> activities = context.getPackageManager().queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
                    if (activities.size() > 0) {
                        Intent chooser = Intent.createChooser(intent, context.getResources().getString(R.string.choose_app));
                        if (intent.resolveActivity(context.getPackageManager()) != null) {
                            context.startActivity(chooser);
                        }
                    } else {
                        intent = YouTubeStandalonePlayer.createVideoIntent((Activity) context,
                                youTubeAPIkey,
                                trailer.getKey(),
                                100,  //after this time, video will start automatically
                                true, //autoplay or not
                                true  //lightbox mode or not; show the video in a small box; this also allows playing in vertical
                        );
                        context.startActivity(intent);
                    }
                }
            }
        }


        @Override
        public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
            //Here we can set some flags on the player

            //This flag tells the player to switch to landscape when in fullscreen, it will also return to portrait
            //when leaving fullscreen
            youTubePlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION);

            //This flag tells the player to automatically enter fullscreen when in landscape. Since we don't have
            //landscape layout for this activity, this is a good way to allow the user rotate the video player.
            youTubePlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE);

            //This flag controls the system UI such as the status and navigation bar, hiding and showing them
            //alongside the player UI
            youTubePlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI);
        }

        @Override
        public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
            if (youTubeInitializationResult.isUserRecoverableError()) {
                youTubeInitializationResult.getErrorDialog((Activity) context, RECOVERY_REQUEST).show();
            } else {
                //Handle the failure
                Toast.makeText(context, youTubeInitializationResult.toString(), Toast.LENGTH_LONG).show();
            }
        }
    } // end class TrailerViewHolder

} // end class TrailersAdapter

