package net.alexblass.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.alexblass.popularmovies.data.FavoritesContract.FavoritesEntry;

/**
 * Manages the local database for our favorited movies.
 */

public class FavoritesDbHelper extends SQLiteOpenHelper {

    // Database name
    public static final String DATABASE_NAME = "favorites.db";

    // The version of the database schema
    private static final int DATABASE_VERSION = 5;

    public FavoritesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Creation statement
        final String SQL_CREATE_FAVORITES_TABLE =

                "CREATE TABLE " + FavoritesEntry.TABLE_NAME + " (" +

                        FavoritesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        FavoritesEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL," +

                        FavoritesEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                        FavoritesEntry.COLUMN_MOVIE_POSTER + " TEXT NOT NULL, " +
                        FavoritesEntry.COLUMN_MOVIE_OVERVIEW + " TEXT NOT NULL, " +
                        FavoritesEntry.COLUMN_MOVIE_RATING + " REAL NOT NULL, " +
                        FavoritesEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL, " +
                        FavoritesEntry.COLUMN_MOVIE_DURATION + " INTEGER NOT NULL);";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoritesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
