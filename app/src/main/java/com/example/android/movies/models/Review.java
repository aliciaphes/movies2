package com.example.android.movies.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Review implements Parcelable {

    private String author;
    private String content;
    private boolean visibility;


    public Review(String author, String content) {
        this.author = author;
        this.content = content;
        this.visibility = false;
    }

    public String getAuthor() {
        return author;
    }

    public String getReviewContent() {
        return content;
    }

    public boolean getContentVisibility() {
        return visibility;
    }

    public void setContentVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    private Review(Parcel in) {
        author = in.readString();
        content = in.readString();
        visibility = in.readInt() != 0;
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(author);
        dest.writeString(content);
        dest.writeInt(visibility ? 1 : 0);
    }
}
