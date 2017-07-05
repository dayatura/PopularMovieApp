package net.alexblass.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.alexblass.popularmovies.data.FavoritesContract;
import net.alexblass.popularmovies.data.FavoritesContract.FavoritesEntry;
import net.alexblass.popularmovies.models.Movie;
import net.alexblass.popularmovies.utilities.ReviewListAdapter;
import net.alexblass.popularmovies.utilities.TrailerListAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

// This activity shows a detail view of a selected Movie

public class DetailActivity extends AppCompatActivity implements TrailerListAdapter.ItemClickListener {
    @BindView(R.id.detail_imageview) ImageView mPoster;
    @BindView(R.id.detail_title_tv) TextView mTitle;
    @BindView(R.id.detail_synopsis_tv) TextView mSynopsis;
    @BindView(R.id.detail_rating_tv) TextView mRating;
    @BindView(R.id.detail_release_date_tv) TextView mReleaseDate;
    @BindView(R.id.detail_duration_tv) TextView mDuration;

    @BindView(R.id.movie_trailers_list) RecyclerView mTrailersList;
    @BindView(R.id.movie_reviews_list) RecyclerView mReviewsList;

    @BindView(R.id.favorite_button) FloatingActionButton mFaveFab;

    private Movie currentMovie;
    private boolean isFavorite = false;

    private Uri mCurrentMovieUri;

    // The URL to Youtube to get the trailers
    private static final String TRAILER_BASE_URL = "https://www.youtube.com/watch?v=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            // If the intent has a Movie object Extra, read the information from it
            // and display it in the correct views
            if (intentThatStartedThisActivity.hasExtra("Movie")) {
                currentMovie = intentThatStartedThisActivity.getParcelableExtra("Movie");


                mCurrentMovieUri = intentThatStartedThisActivity.getData();

                if (currentMovie.getImagePath() != null) {
                    Picasso.with(this)
                            .load(currentMovie.getImagePath())
                            .placeholder(R.drawable.ic_photo_white_48dp)
                            .error(R.drawable.ic_photo_white_48dp)
                            .into(mPoster);
                }
                mTitle.setText(currentMovie.getTitle());

                if (!currentMovie.getOverview().isEmpty()) {
                    mSynopsis.setText(currentMovie.getOverview());
                } else {
                    mSynopsis.setText(R.string.empty_value);
                }

                mRating.setText(Double.toString(currentMovie.getRating()));

                if (!currentMovie.getReleaseDate().isEmpty()) {
                    mReleaseDate.setText(currentMovie.getReleaseDate());
                } else {
                    mReleaseDate.setText(R.string.empty_value);
                }

                mDuration.setText(getString(
                        R.string.duration_units, Integer.toString(currentMovie.getDuration())));
            }

            // Inflate the recycler view with the trailers for the movie
            TrailerListAdapter mTrailerAdapter = new TrailerListAdapter(this, currentMovie.getTrailerKeys());
            mTrailerAdapter.setClickListener(this);
            mTrailersList.setAdapter(mTrailerAdapter);
            mTrailersList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

            // Inflate the recycler view with the reviews for the movie
            ReviewListAdapter mReviewAdapter = new ReviewListAdapter(this, currentMovie.getReviews());
            mReviewsList.setAdapter(mReviewAdapter);
            mReviewsList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

            // Define a projection to get the movie ID column
            String[] projection = {
                    FavoritesEntry.COLUMN_MOVIE_ID};

            // Define a selection to choose the movie by it's movie ID
            String selection = FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID + " = ? ";
            String[] selectionArgs = { currentMovie.getId()};

            // Check if the movie exists in the database and if so, it is a favorite
            Cursor cursor = getContentResolver().query(
                    FavoritesEntry.CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null
            );

            if (cursor.moveToFirst()){
                isFavorite = true;
            }


            // Initialize the FAB with a filled heart if the movie is favorited, and an empty one if it is not
            if (isFavorite){
                mFaveFab.setImageResource(R.drawable.ic_favorite_white_48dp);
            } else {
                mFaveFab.setImageResource(R.drawable.ic_favorite_border_white_48dp);
            }
            cursor.close();
        }
    }

    // When the user clicks a trailer, play the trailer in Youtube or on a web browser
    @Override
    public void onItemClick(View view, int position) {
        // Get the selected trailer's trailer key
        String youtubeUrlString = TRAILER_BASE_URL + currentMovie.getTrailerKeys().get(position);

        // Launch intent to open video trailer in youtube or on the internet app
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrlString));
        startActivity(intent);
    }

    public boolean toggleFavorite(View view){
        // If already favorited, remove favorite
        if (isFavorite){
            mFaveFab.setImageResource(R.drawable.ic_favorite_border_white_48dp);
            isFavorite = false;
            removeFavorite();
        } else { // Else add favorite
            mFaveFab.setImageResource(R.drawable.ic_favorite_white_48dp);
            isFavorite = true;
            addFavorite();
        }
        return isFavorite;
    }

    // Add a movie to the favorites database
    private void addFavorite(){
        ContentValues values = new ContentValues();
        values.put(FavoritesEntry.COLUMN_MOVIE_ID, currentMovie.getId());
        values.put(FavoritesEntry.COLUMN_MOVIE_POSTER, currentMovie.getImagePath());
        values.put(FavoritesEntry.COLUMN_MOVIE_TITLE, currentMovie.getTitle());
        values.put(FavoritesEntry.COLUMN_MOVIE_OVERVIEW, currentMovie.getOverview());
        values.put(FavoritesEntry.COLUMN_MOVIE_RATING, currentMovie.getRating());
        values.put(FavoritesEntry.COLUMN_MOVIE_RELEASE_DATE, currentMovie.getReleaseDate());
        values.put(FavoritesEntry.COLUMN_MOVIE_DURATION, currentMovie.getDuration());

        Uri newUri = getContentResolver().insert(FavoritesEntry.CONTENT_URI, values);
    }

    // Remove a movie to the favorites database
    private void removeFavorite(){
        int rowDeleted = -1;

        if (mCurrentMovieUri != null) {
            rowDeleted = getContentResolver().delete(mCurrentMovieUri, null, null);
        }
    }
}
