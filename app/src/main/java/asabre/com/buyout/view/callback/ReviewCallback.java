package asabre.com.buyout.view.callback;

import asabre.com.buyout.view.ui.AdapterReview;

public interface ReviewCallback {
    void thumbsUp(String reviewId, AdapterReview.ViewHolder holder);
    void thumbsDown(String reviewId, AdapterReview.ViewHolder holder);
}
