package asabre.com.buyout.view.ui;

import android.app.ProgressDialog;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import asabre.com.buyout.R;
import asabre.com.buyout.service.model.OutLaw;
import asabre.com.buyout.viewmodel.ViewModelHomeFragment;

public class ProductRatingDialog extends DialogFragment {
    private static final String TAG = ProductRatingDialog.class.getSimpleName();

    private RatingBar mProductRatingBar;
    private TextView mRatingScale;
    private TextView mRatingFeedback;
    private Button mSubmitRating;
    private ExtendedFloatingActionButton mProductRatingFAB;
    private ViewModelHomeFragment mViewModelHomeFragment;
    private ProgressDialog mProgressDialog;

    public static ProductRatingDialog getInstance(){
        return new ProductRatingDialog();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rating, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        setViewModel();
        setRatingBarCallback();
    }

    private void init(View view){
        mProductRatingBar = view.findViewById(R.id.productRatingBar);
        mRatingScale = view.findViewById(R.id.ratingScale);
        mRatingFeedback = view.findViewById(R.id.ratingFeedback);
        mSubmitRating = view.findViewById(R.id.submitRating);
        mProductRatingFAB = view.findViewById(R.id.productRatingFAB);

    }

    private void setRatingBarCallback(){

        mProductRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                mRatingScale.setText(String.valueOf(rating));
                switch ((int) ratingBar.getRating()){
                    case 1:
                        mRatingScale.setText(getString(R.string.review_very_bad));
                        break;
                    case 2:
                        mRatingScale.setText(R.string.review_need_some_improvement);
                        break;
                    case 3:
                        mRatingScale.setText(R.string.review_good);
                        break;
                    case 4:
                        mRatingScale.setText(R.string.review_Great);
                        break;
                    case 5:
                        mRatingScale.setText(R.string.review_awesome);
                        break;
                    default:
                        mRatingScale.setText(R.string.review_default);
                        break;
                }
            }
        });

    }

    private void setViewModel(){
        mViewModelHomeFragment = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(ViewModelHomeFragment.class);
        mViewModelHomeFragment.init();
    }

    private void setRatingViewModel(){
        if(!emptyFields()){
            showProgressDialog();
            mViewModelHomeFragment.createProductReview(reviewObj()).observe(this, new Observer<OutLaw>() {
                @Override
                public void onChanged(OutLaw outLaw) {
                    dismissProgressDialog();
                    dismiss();
                }
            });
        } else {
            Toast.makeText(getContext(), "Fields can't be empty", Toast.LENGTH_LONG).show();
        }
    }

    private HashMap<String, String> reviewObj(){
        HashMap<String, String> obj = new HashMap<>();
        obj.put("productId", ViewModelHomeFragment.productReviewId);
        obj.put("customerId", ViewModelHomeFragment.userEntity.getCustomerId());
        obj.put("msg", mRatingFeedback.getText().toString());
        obj.put("stars", String.valueOf(mProductRatingBar.getRating()));
        return obj;
    }

    private boolean emptyFields(){
        return mRatingFeedback.getText().toString().isEmpty();
    }


    private void showProgressDialog(){
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setTitle("Sending review");
        mProgressDialog.setMessage("Please wait.");
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

    @Override
    public void onResume() {

       // Full screen dialog
        // Get existing layout params for the window
        WindowManager.LayoutParams params = Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).getAttributes();
        // Assign window properties to fill the parent
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;

        Objects.requireNonNull(getDialog().getWindow()).setAttributes(params);


        super.onResume();
    }

    private View.OnClickListener rating(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRatingViewModel();
            }
        };
    }

    private View.OnClickListener dismissDialog(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mProductRatingFAB.setOnClickListener(dismissDialog());
        mSubmitRating.setOnClickListener(rating());
    }

    @Override
    public void onStop() {
        super.onStop();
        mProductRatingFAB.setOnClickListener(null);
        mSubmitRating.setOnClickListener(null);
    }
}
