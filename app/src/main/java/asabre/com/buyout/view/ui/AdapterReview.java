package asabre.com.buyout.view.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import asabre.com.buyout.R;
import asabre.com.buyout.service.model.Review;
import asabre.com.buyout.view.callback.ReviewCallback;

public class AdapterReview extends RecyclerView.Adapter<AdapterReview.ViewHolder> {

  private Context mContext;
  private List<Review> mReviewList;
  private ReviewCallback mReviewCallback;

    public AdapterReview(Context context, List<Review> reviewList, ReviewCallback reviewCallback) {
        mContext = context;
        mReviewList = reviewList;
        mReviewCallback = reviewCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.browse_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(mReviewList == null || mReviewList.size() == 0){
            // when data is not available
        } else {
            Review review = mReviewList.get(position);

            holder.customer_name.setText(review.getCustomerName());
            holder.time.setText(review.getTime());
            holder.msg.setText(review.getMsg());
            holder.review_ratingBar.setRating(Float.parseFloat(review.getStars()));
            holder.thumbs_up_count.setText(review.getThumbs()[0]);
            holder.thumbs_down_count.setText(review.getThumbs()[1]);

            holder.thumbs_up.setOnClickListener(increase(review.get_id(), holder));
            holder.thumbs_down.setOnClickListener(decrease(review.get_id(), holder));
        }
    }

    @Override
    public int getItemCount() {
        return mReviewList != null && mReviewList.size() != 0 ? mReviewList.size() : 0;
    }

    public double loadNewData(List<Review> list){
        mReviewList = list;
        notifyDataSetChanged();

        // return total number of reviews
        return list.size();
    }

   public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView customer_name;
        TextView time;
        TextView msg;
        RatingBar review_ratingBar;

        ImageView thumbs_up;
        TextView thumbs_up_count;
        ImageView thumbs_down;
        TextView thumbs_down_count;

        private ViewHolder(View itemView){
            super(itemView);

            customer_name = itemView.findViewById(R.id.review_customer_name);
            time = itemView.findViewById(R.id.review_time);
            msg = itemView.findViewById(R.id.review_msg);
            review_ratingBar = itemView.findViewById(R.id.review_ratingBar);

            thumbs_up = itemView.findViewById(R.id.thumbs_up);
            thumbs_up_count = itemView.findViewById(R.id.thumbs_up_count);
            thumbs_down = itemView.findViewById(R.id.thumbs_down);
            thumbs_down_count = itemView.findViewById(R.id.thumbs_down_count);
        }

    }

    private View.OnClickListener increase(final String reviewId, final ViewHolder holder){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReviewCallback.thumbsUp(reviewId, holder);
            }
        };
    }

    private View.OnClickListener decrease(final String reviewId, final ViewHolder holder){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReviewCallback.thumbsDown(reviewId, holder);
            }
        };
    }


}
