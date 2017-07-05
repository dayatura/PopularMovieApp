package net.alexblass.popularmovies.utilities;

import android.text.TextUtils;
import android.util.Log;

import net.alexblass.popularmovies.BuildConfig;
import net.alexblass.popularmovies.models.Movie;
import net.alexblass.popularmovies.models.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Helper methods to request and receive Movie data
 */

public class QueryUtils {
    // Log tag for error messages
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    // The base URL for the Move DB API
    private static final String BASE_URL = "https://api.themoviedb.org/3/movie/";

    // Empty private constructor because no QueryUtils
    // object should be initialized
    private QueryUtils(){}

    // Query the dataset and return a list of Movies
    public static Movie[] fetchMovieData(String requestUrl){
        URL url = createUrl(requestUrl);

        // perform the HTTP request to the URL to receive a JSON response
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request", e);
        }

        // Extract the relevant fields and create a list of Movies
        Movie[] movies = extractFeatureFromJson(jsonResponse);

        return movies;
    }

    // Returns the new URL object from the given String url
    private static URL createUrl(String stringUrl){
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL", e);
        }
        return url;
    }

    // Make an HTTP request to the given URL and return a String as the response
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null){
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(1000);
            urlConnection.setConnectTimeout(1500);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful, then read the input stream
            // and parse the response
            if (urlConnection.getResponseCode() == 200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the movie JSON results.", e);
        } finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            if (inputStream != null){
                inputStream.close();
            }
        }

        return jsonResponse;
    }

    // Convert the InputStream into a String which contains the whole
    // JSON response from the server
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();

        if (inputStream != null) {
            InputStreamReader inputStreamReader =
                    new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }

        return output.toString();
    }

    // Return a list of Movie objects built from parsing the JSON response
    private static Movie[] extractFeatureFromJson(String jsonResponse){
        if (TextUtils.isEmpty(jsonResponse)){
            return null;
        }

        Movie[] movies = new Movie[0];

        // Try to parse the JSON response  If there's a formatting problem,
        // an exception will be thrown
        try {
            JSONObject baseJsonResponse = new JSONObject(jsonResponse);

            JSONArray results = baseJsonResponse.getJSONArray("results");
            movies = new Movie[results.length()];

            for (int i = 0; i < results.length(); i++){
                JSONObject currentMovie = results.getJSONObject(i);

                String movieId = currentMovie.getString("id");
                String movieTitle = currentMovie.getString("original_title");

                String movieImagePath = "";
                if (!currentMovie.getString("poster_path").equalsIgnoreCase("null")){
                    movieImagePath = currentMovie.getString("poster_path");
                }

                String movieOverview = currentMovie.getString("overview");
                Double movieRating = currentMovie.getDouble("vote_average");
                String movieReleaseDate = currentMovie.getString("release_date");

                // Format the release date
                SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat myFormat = new SimpleDateFormat("yyyy");
                String formattedDate = "";

                try {

                    formattedDate = myFormat.format(oldFormat.parse(movieReleaseDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                ArrayList<String> movieTrailers = getTrailers(movieId);

                Movie newMovie = new Movie(movieId, movieTitle, movieImagePath,
                                movieOverview, movieRating, formattedDate,
                                getMovieDuration(movieId), movieTrailers, getReviews(movieId));
                movies[i] = newMovie;
            }
        } catch (JSONException e){
            Log.e(LOG_TAG, "Problem parsing the JSON response.");
        }
        return movies;
    }

    // Helper method to get the duration of the movie at the Movie's individual URL,
    // not from the list of movies JSON result
    private static int getMovieDuration(String id){
        String movieUrlString = BASE_URL + id
                + "?language=en-US&api_key=" + BuildConfig.THE_MOVIE_DB_API_TOKEN;

        URL movieUrl = createUrl(movieUrlString);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(movieUrl);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request", e);
        }

        int runtime = 0;
        try{
            JSONObject baseJsonResponse = new JSONObject(jsonResponse);
                runtime = baseJsonResponse.getInt("runtime");
        } catch (JSONException e){
            Log.e(LOG_TAG, "Problem parsing the JSON response.");
        }

        return runtime;
    }

    // Get the Youtube keys for the Movie's trailers
    public static ArrayList<String> getTrailers(String id){
        String trailerUrlString = BASE_URL + id + "/videos" +
                "?language=en-US&api_key=" + BuildConfig.THE_MOVIE_DB_API_TOKEN;

        ArrayList<String> result = new ArrayList();

        URL trailerUrl = createUrl(trailerUrlString);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(trailerUrl);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request", e);
        }

        try{
            JSONObject baseJsonResponse = new JSONObject(jsonResponse);

            JSONArray results = baseJsonResponse.getJSONArray("results");

            // Check if each video is a trailer, and if it is, add it to our array
            for (int i = 0; i < results.length(); i++){
                JSONObject video = results.getJSONObject(i);
                if (video.getString("type").equalsIgnoreCase("Trailer")){
                    result.add(video.getString("key"));
                }
            }

        } catch (JSONException e){
            Log.e(LOG_TAG, "Problem parsing the JSON response.");
        }

        return result;
    }

    // Get the movie's reviews
    public static ArrayList<Review> getReviews(String id){
        String reviewUrlString = BASE_URL + id + "/reviews" +
                "?language=en-US&api_key=" + BuildConfig.THE_MOVIE_DB_API_TOKEN;

        URL reviewUrl = createUrl(reviewUrlString);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(reviewUrl);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request", e);
        }

        ArrayList<Review> reviews = null;

        try{
            JSONObject baseJsonResponse = new JSONObject(jsonResponse);

            JSONArray results = baseJsonResponse.getJSONArray("results");

            reviews = new ArrayList<>();
            for (int i = 0; i < results.length(); i++){
                JSONObject review = results.getJSONObject(i);
                Review reviewerReviewPair = new Review(review.getString("author"), review.getString("content"));
                reviews.add(reviewerReviewPair);
            }

        } catch (JSONException e){
            Log.e(LOG_TAG, "Problem parsing the JSON response.");
        }
        return reviews;
    }
}
