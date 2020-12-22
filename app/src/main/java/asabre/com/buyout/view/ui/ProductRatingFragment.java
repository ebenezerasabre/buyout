package asabre.com.buyout.view.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import asabre.com.buyout.R;
import asabre.com.buyout.service.model.OutLaw;
import asabre.com.buyout.view.callback.BaseFragment;
import asabre.com.buyout.viewmodel.ViewModelHomeFragment;

public class ProductRatingFragment extends Fragment implements BaseFragment {
    private static final String TAG = ProductRatingFragment.class.getSimpleName();


    private TextView productRatingTopWord;
    private RatingBar mProductRatingBar;
    private TextView mRatingScale;
    private EditText mRatingFeedback;
    private Button mSubmitRating;
    private ExtendedFloatingActionButton mProductRatingFAB;
    private ViewModelHomeFragment mViewModelHomeFragment;
    private ProgressDialog mProgressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rating, container, false);
        init(view);
        setViewModel();
        setRatingBarCallback();
        return view;
    }

    private void init(View view){
        productRatingTopWord = view.findViewById(R.id.productRatingTopWord);
        mProductRatingBar = view.findViewById(R.id.productRatingBar);
        mRatingScale = view.findViewById(R.id.ratingScale);
        mRatingFeedback = view.findViewById(R.id.ratingFeedback);
        mSubmitRating = view.findViewById(R.id.submitRating);
        mProductRatingFAB = view.findViewById(R.id.productRatingFAB);

        mRatingFeedback.requestFocus();

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
                    hideVirtualKeyboard();
                    dismissProgressDialog();
                    setVisibility();
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


    private View.OnClickListener rating(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRatingViewModel();
            }
        };
    }


    private void setVisibility(){
        if(MainActivity.mOrderTrack == MainActivity.OrderTrack.ORDER_DETAIL){
            MainActivity.mOrderTrack = MainActivity.OrderTrack.ORDER;
            if (getActivity() != null) {
                getActivity().findViewById(R.id.containerOrderDetails).setVisibility(View.GONE);
                getActivity().findViewById(R.id.containerOrder).setVisibility(View.VISIBLE);
                removeThisFragment();
            }
        }
    }

    private View.OnClickListener goBackListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibility();
            }
        };
    }

    private void removeThisFragment(){
        if(getActivity() != null){
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentById(R.id.containerOrderDetails);
            if(fragment != null){
                getActivity().getSupportFragmentManager().beginTransaction()
                        .remove(fragment)
                        .commit();
            }
        }
    }

    private void hideVirtualKeyboard(){
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public void onStart() {
        super.onStart();
        productRatingTopWord.setOnClickListener(goBackListener());
        mProductRatingFAB.setOnClickListener(goBackListener());
        mSubmitRating.setOnClickListener(rating());
    }


    @Override
    public void onStop() {
        super.onStop();
        productRatingTopWord.setOnClickListener(null);
        mProductRatingFAB.setOnClickListener(null);
        mSubmitRating.setOnClickListener(null);
    }

    @Override
    public void onBackPressed() {
        if(MainActivity.mTrackMain == MainActivity.TrackMain.ORDER){
            setVisibility();
        }
    }
}
