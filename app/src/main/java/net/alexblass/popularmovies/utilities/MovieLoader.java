package net.alexblass.popularmovies.utilities;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import net.alexblass.popularmovies.models.Movie;
import net.alexblass.popularmovies.data.FavoritesContract.FavoritesEntry;

/**
 * Loads a list of Movies using the AsyncTask to perform the
 * network request to the given URL
 */

public class MovieLoader extends AsyncTaskLoader<Movie[]> {
    // Query URL
    private String mUrl = null;

    // Constructs a new MovieLoader
    public MovieLoader(Context context, String url){
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public Movie[] loadInBackground() {
        Movie[] movies = null;

        // If there is not a valid URL, we're pulling the movies from our database
        if (mUrl == null){

            String[] projection = {
                    FavoritesEntry._ID,
                    FavoritesEntry.COLUMN_MOVIE_ID,
                    FavoritesEntry.COLUMN_MOVIE_POSTER,
                    FavoritesEntry.COLUMN_MOVIE_TITLE,
                    FavoritesEntry.COLUMN_MOVIE_OVERVIEW,
                    FavoritesEntry.COLUMN_MOVIE_RATING,
                    FavoritesEntry.COLUMN_MOVIE_RELEASE_DATE,
                    FavoritesEntry.COLUMN_MOVIE_DURATION};

            // Check if the movie exists in the database and if so, it is a favorite
            Cursor cursor = getContext().getContentResolver().query(
                    FavoritesEntry.CONTENT_URI,
                    projection,
                    null,
                    null,
                    null,
                    null
            );

            if (cursor.moveToFirst()){
                movies = new Movie[cursor.getCount()];

                String id, poster, title, overview, releaseDate;
                double rating;
                int duration;

                id = cursor.getString(cursor.getColumnIndex(FavoritesEntry.COLUMN_MOVIE_ID));
                poster = cursor.getString(cursor.getColumnIndex(FavoritesEntry.COLUMN_MOVIE_POSTER));
                title = cursor.getString(cursor.getColumnIndex(FavoritesEntry.COLUMN_MOVIE_TITLE));
                overview = cursor.getString(cursor.getColumnIndex(FavoritesEntry.COLUMN_MOVIE_OVERVIEW));
                releaseDate = cursor.getString(cursor.getColumnIndex(FavoritesEntry.COLUMN_MOVIE_RELEASE_DATE));

                rating = cursor.getDouble(cursor.getColumnIndex(FavoritesEntry.COLUMN_MOVIE_RATING));
                duration = cursor.getInt(cursor.getColumnIndex(FavoritesEntry.COLUMN_MOVIE_DURATION));

                Movie favoriteMovie = new Movie(
                        id, title, poster, overview, rating, releaseDate, duration,
                        QueryUtils.getTrailers(id), QueryUtils.getReviews(id));
                movies[0] = favoriteMovie;

                for (int i = 1; cursor.moveToNext(); i++) {

                    id = cursor.getString(cursor.getColumnIndex(FavoritesEntry.COLUMN_MOVIE_ID));
                    poster = cursor.getString(cursor.getColumnIndex(FavoritesEntry.COLUMN_MOVIE_POSTER));
                    title = cursor.getString(cursor.getColumnIndex(FavoritesEntry.COLUMN_MOVIE_TITLE));
                    overview = cursor.getString(cursor.getColumnIndex(FavoritesEntry.COLUMN_MOVIE_OVERVIEW));
                    releaseDate = cursor.getString(cursor.getColumnIndex(FavoritesEntry.COLUMN_MOVIE_RELEASE_DATE));

                    rating = cursor.getDouble(cursor.getColumnIndex(FavoritesEntry.COLUMN_MOVIE_RATING));
                    duration = cursor.getInt(cursor.getColumnIndex(FavoritesEntry.COLUMN_MOVIE_DURATION));

                    favoriteMovie = new Movie(
                            id, title, poster, overview, rating, releaseDate, duration,
                            QueryUtils.getTrailers(id), QueryUtils.getReviews(id));
                    movies[i] = favoriteMovie;
                }
            }

            cursor.close();
            return movies;
        }

        movies = QueryUtils.fetchMovieData(mUrl);
        return movies;
    }
}