package net.alexblass.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the offline movie favorites database.
 */
public class FavoritesContract {

    // The content authority
    public static final String CONTENT_AUTHORITY = "net.alexblass.popularmovies";

    // The base URI for our table
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Path to the table directory
    public static final String PATH_FAVORITES = "favorites";

    // Defines the contents of the table
    public static final class FavoritesEntry implements BaseColumns {

        // The base CONTENT_URI used to query the Favorites table from the content provider
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITES)
                .build();

        // The table name
        public static final String TABLE_NAME = "favorites";

        // Movie ID pulled from the API
        public static final String COLUMN_MOVIE_ID = "movie_id";

        // Movie poster path
        public static final String COLUMN_MOVIE_POSTER = "poster";

        // Movie title
        public static final String COLUMN_MOVIE_TITLE = "title";

        // Movie overview
        public static final String COLUMN_MOVIE_OVERVIEW = "overview";

        // The movie rating
        public static final String COLUMN_MOVIE_RATING = "rating";

        public static final String COLUMN_MOVIE_RELEASE_DATE = "release_date";

        public static final String COLUMN_MOVIE_DURATION = "duration";
    }
}
