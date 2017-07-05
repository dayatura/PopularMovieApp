package net.alexblass.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Movie object to store JSON values pulled from the network.
 * Implements Parcelable so that Movie objects can be passed
 * via Intent to open a new activity.
 */

public class Movie implements Parcelable {
    // Information about the movie relevant to our app
    // Pulled from the JSON objects into Movie objects
    private String mId;
    private String mTitle;
    private String mImagePath;
    private String mOverview;
    private double mRating;
    private String mReleaseDate;
    private int mDuration;
    private ArrayList<String> mTrailerKeys;
    private ArrayList<Review> mReviews;

    // Base URL for image paths
    private static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";

    // The size image we need for our app
    private static final String IMAGE_SIZE = "w185";

    // Constructor to set the values of a particular Movie
    public Movie(String id, String title, String img, String overview, double rating,
                 String releaseDate, int duration, ArrayList<String> trailerKeys,
                 ArrayList<Review> reviews){
        mId = id;
        mTitle = title;
        mImagePath = img;
        mOverview = overview;
        mRating = rating;
        mReleaseDate = releaseDate;
        mDuration = duration;
        mTrailerKeys = trailerKeys;
        mReviews = reviews;
    }

    private Movie(Parcel source){
        mId = source.readString();
        mTitle = source.readString();
        mImagePath = source.readString();
        mOverview = source.readString();
        mRating = source.readDouble();
        mReleaseDate = source.readString();
        mDuration = source.readInt();
        mTrailerKeys = source.readArrayList(null);
        mReviews = source.readArrayList(Review.class.getClassLoader());
    }

    public String getId() { return mId; }

    public String getTitle() {
        return mTitle;
    }

    public String getImagePath() {
        if (mImagePath.isEmpty()){
            return null;
        } else if (mImagePath.startsWith(BASE_IMAGE_URL)){
            return mImagePath;
        }else {
            return BASE_IMAGE_URL + IMAGE_SIZE + mImagePath;
        }
    }

    public String getOverview() {
        return mOverview;
    }

    public double getRating() {
        return mRating;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public int getDuration() {
        return mDuration;
    }

    public ArrayList<String> getTrailerKeys(){
        return mTrailerKeys;
    }

    public ArrayList<Review> getReviews() {
        return mReviews;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mTitle);
        dest.writeString(mImagePath);
        dest.writeString(mOverview);
        dest.writeDouble(mRating);
        dest.writeString(mReleaseDate);
        dest.writeInt(mDuration);
        dest.writeList(mTrailerKeys);
        dest.writeList(mReviews);
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }

        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }
    };
}
