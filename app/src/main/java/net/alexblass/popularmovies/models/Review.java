package net.alexblass.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Holds the values of the reviewer's id and the review content.
 */

public class Review implements Parcelable {

    private String mReviewer;
    private String mReviewContent;

    public Review(String reviewer, String content){
        mReviewer = reviewer;
        mReviewContent = content;
    }

    private Review(Parcel source){
        mReviewer = source.readString();
        mReviewContent = source.readString();
    }

    public String getReviewer() {
        return mReviewer;
    }

    public String getReviewContent() {
        return mReviewContent;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mReviewer);
        dest.writeString(mReviewContent);
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }

        @Override
        public Review createFromParcel(Parcel source) {
            return new Review(source);
        }
    };
}
