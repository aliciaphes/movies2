package com.example.android.movies.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.example.android.movies.utils.Utilities;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

@Entity
public class Movie implements Parcelable {

    @SerializedName("id")
    @PrimaryKey
    @NonNull
    private long id;

    @SerializedName("title")
    private String title;

    @SerializedName("original_title")
    private String originalTitle;

    @SerializedName("overview")
    private String synopsis;

    @SerializedName("poster_path")
    private String posterURL;

    @SerializedName("vote_average")
    private Double rating;

    @SerializedName("release_date")
    private String releaseDate;

    @Ignore
    @SerializedName("trailers")
    private ArrayList<Trailer> trailers;

    @Ignore
    @SerializedName("reviews")
    private ArrayList<Review> reviews;




    public Movie() {
        id = 0L;
        title = "";
        originalTitle = "";
        synopsis = "";
        posterURL = "";
        rating = 0.0;
        releaseDate = "";
        trailers = new ArrayList<>();
        reviews = new ArrayList<>();
    }




    private Movie(Parcel in) {
        id = in.readLong();
        title = in.readString();
        originalTitle = in.readString();
        synopsis = in.readString();
        posterURL = in.readString();
        rating = in.readDouble();
        releaseDate = in.readString();

        trailers = (ArrayList<Trailer>)in.readArrayList(Trailer.class.getClassLoader());
        reviews  = (ArrayList<Review>)in.readArrayList(Review.class.getClassLoader());
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getPosterURL() {
        return posterURL;
    }

    public void setPosterURL(String posterURL) {
        this.posterURL = posterURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getPoster() {
        return posterURL;
    }

    public void setPoster(String posterId) {
        this.posterURL = Utilities.POSTER_BASE_URL + "/" + "w" + Utilities.POSTER_SIZE + posterId;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }


    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public ArrayList<Trailer> getTrailers() {
        return trailers;
    }

    public int getNumTrailers(){
        return trailers.size();
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public int getNumReviews(){
        return reviews.size();
    }

    public void clearReviewsArray() {
        reviews.clear();
    }

    public void addTrailer(Trailer trailer) {
        trailers.add(trailer);
    }

    public void addReview(Review review) {
        reviews.add(review);
    }




    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(originalTitle);
        dest.writeString(synopsis);
        dest.writeString(posterURL);
        dest.writeDouble(rating);
        dest.writeString(releaseDate);
        dest.writeList(trailers);
        dest.writeList(reviews);
    }


    //Interface that must be implemented and provided as a public CREATOR field that generates instances of the Movie class from a Parcel.
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        //Create a new instance of the Parcelable class, instantiating it from the given Parcel whose data had previously been written by Parcelable.writeToParcel()
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        //Create a new array of the Parcelable class.
        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
