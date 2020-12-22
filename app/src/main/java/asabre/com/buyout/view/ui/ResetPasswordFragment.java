package asabre.com.buyout.view.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;

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

public class ResetPasswordFragment extends Fragment implements BaseFragment {
    private static final String TAG = ResetPasswordFragment.class.getSimpleName();


    private TextView resetTopWord;
    private EditText resetOldPassword;
    private EditText resetNewPassword;
    private Button resetPasswordButton;
    private ExtendedFloatingActionButton resetPasswordCloseFAB;
    private ProgressDialog mProgressDialog;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reset_password, container, false);
        init(view);
        return view;
    }

    private void init(View view){
        resetTopWord = view.findViewById(R.id.resetTopWord);
        resetOldPassword = view.findViewById(R.id.resetOldPassword);
        resetNewPassword = view.findViewById(R.id.resetNewPassword);
        resetPasswordButton = view.findViewById(R.id.resetPasswordButton);
        resetPasswordCloseFAB = view.findViewById(R.id.resetPasswordCloseFAB);
    }

    private void resetPasswordViewModel(){
        if(getActivity() != null){
            ViewModelHomeFragment viewModelHomeFragment = ViewModelProviders.of(getActivity()).get(ViewModelHomeFragment.class);
            if(fieldsNotEmpty()){
                showProgressDialog();
                HashMap<String, String> obj = new HashMap<>();
                obj.put("oldPassword", resetOldPassword.getText().toString());
                obj.put("newPassword", resetNewPassword.getText().toString());
                viewModelHomeFragment.resetCustomerPassword(obj).observe(this, new Observer<OutLaw>() {
                    @Override
                    public void onChanged(OutLaw outLaw) {
                        hideVirtualKeyboard();
                        dismissProgressDialog();
                        setVisibility();
                        resetSuccessDialog(outLaw.getMsg());
                    }
                });

            } else {
                Toast.makeText(getContext(), "Field(s) can't be empty", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void hideVirtualKeyboard(){
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }

    private boolean fieldsNotEmpty(){
        return !resetOldPassword.getText().toString().isEmpty() &&
                !resetNewPassword.getText().toString().isEmpty();
    }

    private void resetSuccessDialog(String msg){
        if(getContext() != null){
            MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(getContext())
                    .setTitle(msg)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
            materialAlertDialogBuilder.show();
        }
    }



    @Override
    public void onStart() {
        super.onStart();
        resetPasswordButton.setOnClickListener(resetListener());
        resetTopWord.setOnClickListener(goBackListener());
        resetPasswordCloseFAB.setOnClickListener(goBackListener());
    }

    @Override
    public void onStop() {
        super.onStop();
        resetPasswordButton.setOnClickListener(null);
        resetTopWord.setOnClickListener(null);
        resetPasswordCloseFAB.setOnClickListener(null);
    }

    @Override
    public void onBackPressed() {
        if(MainActivity.mTrackMain == MainActivity.TrackMain.PROFILE){
            if(MainActivity.mProfileTrack == MainActivity.ProfileTrack.PROFILE_DETAIL){
                goBack();
            }
        }
    }


    private View.OnClickListener resetListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPasswordViewModel();
            }
        };
    }

    private void setVisibility(){
        if(getActivity() != null){
            getActivity().findViewById(R.id.containerProfile).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.containerProfileDetails).setVisibility(View.GONE);
            MainActivity.mProfileTrack = MainActivity.ProfileTrack.PROFILE;
        }
        removeThisFragment();
    }

    private void removeThisFragment(){
        if(getActivity() != null){
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentById(R.id.containerProfileDetails);
            if(fragment != null){
                getActivity().getSupportFragmentManager().beginTransaction()
                        .remove(fragment)
                        .commit();
                Log.d(TAG, "removeThisFragment: called");
            }
        }
    }


    private View.OnClickListener goBackListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        };
    }
    private void goBack(){
        setVisibility();
    }


    private void showProgressDialog(){
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setTitle("Resetting password");
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



}
