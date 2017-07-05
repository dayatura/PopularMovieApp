package net.alexblass.popularmovies.utilities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.alexblass.popularmovies.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An adapter to display the movie trailers in a linear layout in the recycler view.
 */

public class TrailerListAdapter extends RecyclerView.Adapter<TrailerListAdapter.ViewHolder> {
    // An array list to hold the keys to the youtube trailers
    private ArrayList<String> mTrailers;

    // Layout inflater to get the trailer list item layout
    private LayoutInflater mInflater;
    // A ClickListener so that when a trailer is clicked, it will play in youtube or on the web
    private ItemClickListener mClickListener;

    // Context to get the String from the resources
    private Context mContext;

    public TrailerListAdapter(Context context, ArrayList<String> trailers){
        this.mInflater = LayoutInflater.from(context);
        mTrailers = trailers;
        mContext = context;
    }

    // Notify adapter about dataset change to update views
    public void setTrailers(ArrayList<String> trailers) {
        mTrailers = trailers;
        notifyDataSetChanged();
    }

    // Creates new views when we need to to fill the screen
    @Override
    public TrailerListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.trailer_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // Binds the trailer number to the text view
    @Override
    public void onBindViewHolder(TrailerListAdapter.ViewHolder holder, int position) {
        holder.trailerIdTv.setText(
                mContext.getString(R.string.trailer_tag, Integer.toString(position + 1)));
    }

    // Get the number of trailers in the array
    @Override
    public int getItemCount() {
        return mTrailers.size();
    }

    // Get the current trailer
    public String getItem(int index){
        return mTrailers.get(index);
    }

    // Stores and recycles views to improve app performance and smoother scrolling
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public @BindView(R.id.trailer_id) TextView trailerIdTv;

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

    // Catches clicks on the list items
    public void setClickListener(ItemClickListener itemClickListener){
        mClickListener = itemClickListener;
    }

    // DetailActivity.java will respond to click events by implementing this method
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
