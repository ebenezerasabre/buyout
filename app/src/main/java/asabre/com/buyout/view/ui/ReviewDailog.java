package asabre.com.buyout.view.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import asabre.com.buyout.R;
import asabre.com.buyout.service.model.Review;
import asabre.com.buyout.view.callback.ReviewCallback;
import asabre.com.buyout.viewmodel.ViewModelHomeFragment;

public class ReviewDailog extends DialogFragment implements ReviewCallback {
    private static final String TAG = ReviewDailog.class.getSimpleName();

    private TextView productReviewTopWord;
    private ExtendedFloatingActionButton reviewCloseFAB;
    private RecyclerView mRecyclerViewProductReview;
    private AdapterReview mReviewAdapter;
    private ReviewCallback mReviewCallback;
    private ViewModelHomeFragment mViewModelHomeFragment;
    private ProgressDialog mProgressDialog;
    private ProgressBar indeterminateReviewDialog;


    public static ReviewDailog getInstance(){
        return new ReviewDailog();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_review, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        setReviewViewModel();
    }

    private void init(View view){

        productReviewTopWord = view.findViewById(R.id.productReviewTopWord);
        reviewCloseFAB = view.findViewById(R.id.reviewCloseFAB);
        indeterminateReviewDialog = view.findViewById(R.id.indeterminateReviewDialog);

        // for recyclerView
        mReviewAdapter = new AdapterReview(getContext(), new ArrayList<Review>(), this);
        mRecyclerViewProductReview = view.findViewById(R.id.holderReview);
        mRecyclerViewProductReview.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerViewProductReview.setAdapter(mReviewAdapter);
    }


    private List<HashMap<String, String>> getRevObj(){
        HashMap<String, String> obj = new HashMap<>();
        obj.put("productId", ViewModelHomeFragment.productReviewId);

        List<HashMap<String, String>> list = new ArrayList<>();
        list.add(obj);
        return list;
    }

    private void setReviewViewModel(){
//        showProgressDialog("Loading reviews..","Please wait.");
        mViewModelHomeFragment = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(ViewModelHomeFragment.class);
        mViewModelHomeFragment.initProductReview(getRevObj());
        mViewModelHomeFragment.getProductReview().observe(this, new Observer<List<Review>>() {
            @Override
            public void onChanged(List<Review> reviews) {
                mReviewAdapter.loadNewData(reviews);
                indeterminateReviewDialog.setVisibility(View.GONE);
//                dismissProgressDialog();
            }
        });

    }

    private void showProgressDialog(String title,String msg){
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(msg);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    private void dismissProgressDialog(){
        if(mProgressDialog != null){
            if(mProgressDialog.isShowing()){
                mProgressDialog.dismiss();
            }
        }
    }

    private View.OnClickListener dismissDialog(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        };
    }

    private void reviewThumbs(String sub, String subId){
        HashMap<String, String> obj = new HashMap<>();
        obj.put("sub", sub);
        obj.put("subId", subId);
        mViewModelHomeFragment.reviewThumbs(obj);
    }

    @Override
    public void thumbsUp(String reviewId, AdapterReview.ViewHolder holder) {
        reviewThumbs("thumbsUp", reviewId);
        TextView reviewQuantity = holder.thumbs_up_count;
        int prevQuantity = reviewQuantity.getText().toString().isEmpty() ? 0 : Integer.parseInt(reviewQuantity.getText().toString());
        prevQuantity += 1;
        reviewQuantity.setText(String.valueOf(prevQuantity));

    }

    @Override
    public void thumbsDown(String reviewId, AdapterReview.ViewHolder holder) {
        reviewThumbs("thumbsDown", reviewId);
        TextView reviewQuantity = holder.thumbs_down_count;
        int prevQuantity = reviewQuantity.getText().toString().isEmpty() ? 0 : Integer.parseInt(reviewQuantity.getText().toString());
        prevQuantity += 1;
        reviewQuantity.setText(String.valueOf(prevQuantity));
    }

    @Override
    public void onResume() {
//      Full screen dialog
        // Get existing layout params for the window
        WindowManager.LayoutParams params = Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).getAttributes();
        // Assign window properties to fill the parent
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        Objects.requireNonNull(getDialog().getWindow()).setAttributes(params);

        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        productReviewTopWord.setOnClickListener(dismissDialog());
        reviewCloseFAB.setOnClickListener(dismissDialog());
    }

    @Override
    public void onStop() {
        super.onStop();
        reviewCloseFAB.setOnClickListener(null);
    }
}
