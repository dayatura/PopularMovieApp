package net.alexblass.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import net.alexblass.popularmovies.models.Movie;

/**
 * A Content provider to access our data.
 */

public class FavoritesProvider extends ContentProvider {

    // Uri codes
    public static final int CODE_FAVORITES_TABLE = 100;
    public static final int CODE_FAVORITE_MOVIE_ID = 101;

    private static final int MOVIE_ID_INDEX = 1;

    // Uri matcher to help match our codes with a URI
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    // Helps us access the data
    private FavoritesDbHelper mOpenHelper;

    // Tag for any error messages
    private static final String LOG_TAG = FavoritesProvider.class.getSimpleName();

    // Set our URI codes so we can determine what data is being requested
    public static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FavoritesContract.CONTENT_AUTHORITY;

        // Uri for the database of all favorites
        matcher.addURI(authority, FavoritesContract.PATH_FAVORITES, CODE_FAVORITES_TABLE);

        // Uri to access a single favorited movie
        matcher.addURI(authority, FavoritesContract.PATH_FAVORITES + "/#", CODE_FAVORITE_MOVIE_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new FavoritesDbHelper(getContext());
        return true;
    }

    // Insert a single favorite movie to the database
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        switch (match) {
            case CODE_FAVORITES_TABLE:
                // Insert the movie to favorites
                long id = db.insert(FavoritesContract.FavoritesEntry.TABLE_NAME, null, contentValues);
                if (id == -1) {
                    Log.e(LOG_TAG, "Failed to insert row for " + uri);
                    return null;
                }

                // Notify all listeners that the data has changed
                getContext().getContentResolver().notifyChange(uri, null);

                return ContentUris.withAppendedId(uri, id);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    // Query the database for all the information or movies by their id
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        final int match = sUriMatcher.match(uri);
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        Cursor cursor;

        // Determine which URI to use
        switch (match) {

            // Get the movie by its ID code
            case CODE_FAVORITE_MOVIE_ID: {
                String movieIdString = uri.getLastPathSegment();

                String[] selectionArguments = new String[]{movieIdString};

                cursor = db.query(
                        FavoritesContract.FavoritesEntry.TABLE_NAME,
                        projection,
                       FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);

                break;
            }

            // Return all the data in the table
            case CODE_FAVORITES_TABLE: {
                cursor = db.query(
                        FavoritesContract.FavoritesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    // Deleted a movie from the user's favorites and return the number of rows deleted
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        int rowDeleted;

        switch (match) {

            // Get the ID for the single movie and delete it
            case CODE_FAVORITE_MOVIE_ID:
                String movieId = uri.getPathSegments().get(1);
                rowDeleted = db.delete(
                        FavoritesContract.FavoritesEntry.TABLE_NAME,
                        "movie_id=?",
                        new String[] {movieId});

                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowDeleted;
    }

    // Method required by interface
    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("Method not implemented by Popular Movies.");
    }

    // Data will be deleted or inserted, but never updated
    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new RuntimeException("Movie data cannot be altered.");
    }
}