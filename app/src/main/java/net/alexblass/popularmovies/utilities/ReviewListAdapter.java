package net.alexblass.popularmovies.utilities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.alexblass.popularmovies.R;
import net.alexblass.popularmovies.models.Review;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An adapter to display the movie reviews in the recycler view.
 */

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ViewHolder> {
    // An ArrayList with review-reviewer pairs
    private ArrayList<Review> mReviews;

    // Layout inflater to get the trailer list item layout
    private LayoutInflater mInflater;

    // Context to get the String from the resources
    private Context mContext;

    public ReviewListAdapter(Context context, ArrayList<Review> reviews){
        this.mInflater = LayoutInflater.from(context);
        mReviews = reviews;
        mContext = context;
    }

    // Notify adapter about dataset change to update views
    public void setReviews(ArrayList<Review> reviews) {
        mReviews = reviews;
        notifyDataSetChanged();
    }

    // Creates new views when we need to to fill the screen
    @Override
    public ReviewListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.review_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // Binds the reviewer and the review to the corresponding views
    @Override
    public void onBindViewHolder(ReviewListAdapter.ViewHolder holder, int position) {
        Review reviewPair = mReviews.get(position);
        holder.reviewerIdTv.setText(
                mContext.getString(R.string.reviewer_id_label, reviewPair.getReviewer()));
        holder.reviewContentTv.setText(reviewPair.getReviewContent());
    }

    // Get the number of reviews in the arraylist
    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    // Stores and recycles views to improve app performance and smoother scrolling
    public class ViewHolder extends RecyclerView.ViewHolder {

        public @BindView(R.id.reviewerId) TextView reviewerIdTv;
        public @BindView(R.id.reviewContent) TextView reviewContentTv;

        public ViewHolder(View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
