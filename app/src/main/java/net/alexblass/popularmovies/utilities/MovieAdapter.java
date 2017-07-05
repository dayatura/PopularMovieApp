package net.alexblass.popularmovies.utilities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import net.alexblass.popularmovies.R;
import net.alexblass.popularmovies.models.Movie;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Displays an array list of Movie posters in a RecyclerView with a GridLayout.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {
    // An array to hold our movies pulled from the network
    private Movie[] mMovies;

    private LayoutInflater mInflater;
    // A ClickListener so that when we click on a Movie, we can launch a new activity
    private ItemClickListener mClickListener;
    // The context of the Adapter so we can set the movie poster to the ImageView
    private Context mContext;

    // Construct a new MovieAdapter
    public MovieAdapter(Context context, Movie[] movies){
        this.mInflater = LayoutInflater.from(context);
        mMovies = movies;
        mContext = context;
    }

    // Notify adapter about dataset change to update views
    public void setMovies(Movie[] movies) {
        mMovies = movies;
        notifyDataSetChanged();
    }

    // Creates new views when we need to to fill the screen
    @Override
    public MovieAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.movie_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // Binds the movie poster image to the ImageView
    @Override
    public void onBindViewHolder(MovieAdapter.ViewHolder holder, int position) {
        // Get the currently selected movie
        Movie currentMovie = mMovies[position];

        // Set the movie poster to the view
        if (currentMovie != null && currentMovie.getImagePath() != null) {
            Picasso.with(mContext)
                    .load(currentMovie.getImagePath())
                    .placeholder(R.drawable.ic_photo_white_48dp)
                    .error(R.drawable.ic_photo_white_48dp)
                    .into(holder.moviePoster);
        }
    }

    // Get the number of movies in the array
    @Override
    public int getItemCount() {
        return mMovies.length;
    }

    // Get the current Movie
    public Movie getItem(int index){
        return mMovies[index];
    }

    // Stores and recycles views to improve app performance and smoother scrolling
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public @BindView(R.id.movie_poster) ImageView moviePoster;

        public ViewHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null) {
                mClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

    // Catches clicks on the movie posters
    public void setClickListener(ItemClickListener itemClickListener){
        mClickListener = itemClickListener;
    }

    // MainActivity.java will respond to click events by implementing this method
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
