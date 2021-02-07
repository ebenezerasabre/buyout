package asabre.com.buyout.view.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import asabre.com.buyout.R;
import asabre.com.buyout.view.callback.BaseFragment;
import asabre.com.buyout.viewmodel.ViewModelHomeFragment;

public class EnterEmailFragment extends Fragment implements BaseFragment {
    private static final String TAG = EnterEmailFragment.class.getSimpleName();

    private TextView enterEmailBack;
    private TextInputEditText enterEmail;
    private TextInputEditText enterPwd;
    private MaterialButton enterEmailContinue;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enter_email, container, false);
        init(view);
        emailContinueListener();
        emailBackListener();
        return view;
    }

    private void init(View view) {
        enterEmailBack = view.findViewById(R.id.enterEmailBack);
        enterEmail = view.findViewById(R.id.enterEmail);
        enterPwd = view.findViewById(R.id.enterPassword);
        enterEmailContinue = view.findViewById(R.id.enterEmailContinue);
    }

    private boolean fieldIsNotEmpty(){
        return !enterEmail.getText().toString().isEmpty() &&
                !enterPwd.getText().toString().isEmpty();
    }

    private void emailContinueListener(){
        enterEmailContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fieldIsNotEmpty()){
                    ViewModelHomeFragment.createObject.put("email", enterEmail.getText().toString());
                    ViewModelHomeFragment.createObject.put("password", enterPwd.getText().toString());

                    loadSignUpFragment();
                } else {
                    Toast.makeText(getContext(), "Field can't be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void emailBackListener(){
        enterEmailBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadEnterNumberFragment();
            }
        });
    }



    private void loadEnterNumberFragment(){
        EnterNumberFragment enterNumberFragment = new EnterNumberFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        transaction.replace(R.id.containerProducts, enterNumberFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void loadSignUpFragment(){
        SignUpFragment signUpFragment = new SignUpFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        transaction.replace(R.id.containerProducts, signUpFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }



    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: email called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: email called");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: email called");
    }

    @Override
    public void onBackPressed() {
        if(MainActivity.mHomeTrack == MainActivity.HomeTrack.PRODUCTS){
            loadEnterNumberFragment();
        }
    }
}

