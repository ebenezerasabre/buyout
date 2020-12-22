package asabre.com.buyout.view.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import asabre.com.buyout.R;
import asabre.com.buyout.service.model.OutLaw;
import asabre.com.buyout.service.model.UserEntity;
import asabre.com.buyout.service.repository.DatabaseClient;
import asabre.com.buyout.view.callback.BaseFragment;
import asabre.com.buyout.viewmodel.ViewModelHomeFragment;

public class ProfileEditFragment extends Fragment implements BaseFragment {
    private static final String TAG = ProfileEditFragment.class.getSimpleName();

    TextView editProfilePasswordWarning;
    TextView editProfileWarning;
    private ProgressDialog mProgressDialog;
    private TextView editProfileTopWord;
    private LinearLayout editProfileView;
    private EditText editFirstName;
    private EditText editLastName;
    private EditText editEmail;
    private EditText editPhoneNumber;

    private Button saveEditedProfile;
    private ViewModelHomeFragment mViewModelHomeFragment;

    private EditText confirmPassword;
    private Button confirmPasswordButton;
    private LinearLayout passView;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profild_edit, container, false);
        init(view);
        setViewModel();
        setCustomerFields();
        return view;
    }

    private void setViewModel(){
        if(getActivity() != null){
            mViewModelHomeFragment = ViewModelProviders.of(getActivity()).get(ViewModelHomeFragment.class);
        }
    }

    private void setCustomerFields(){
        editFirstName.setText(ViewModelHomeFragment.userEntity.getFirstName());
        editLastName.setText(ViewModelHomeFragment.userEntity.getLastName());
        editPhoneNumber.setText(ViewModelHomeFragment.userEntity.getPhoneNumber());
        editEmail.setText(ViewModelHomeFragment.userEntity.getEmail());
    }

    private boolean checkCustomerFields(){
        boolean empty = false;
        ArrayList<String> customerFields = new ArrayList<>();
        customerFields.add(editFirstName.getText().toString());
        customerFields.add(editLastName.getText().toString());
        customerFields.add(editPhoneNumber.getText().toString());

        for(String str : customerFields){
            if(str.isEmpty()){
                empty = true;
                break;
            }
        }
        return empty;
    }

    private boolean emptyPasswordField(){
        return confirmPassword.getText().toString().isEmpty();
    }

    private HashMap<String, String> cusObj(){
        HashMap<String, String> obj = new HashMap<>();
        obj.put("firstName", editFirstName.getText().toString());
        obj.put("lastName", editLastName.getText().toString());
        obj.put("phoneNumber", editPhoneNumber.getText().toString());
        obj.put("email", editEmail.getText().toString());
        obj.put("password", confirmPassword.getText().toString());
        return obj;
    }

    private void saveProfileViewModel(HashMap<String, String> body){
        showProgressDialog();
        mViewModelHomeFragment.updateCustomer(body).observe(this, new Observer<OutLaw>() {
            @Override
            public void onChanged(OutLaw outLaw) {
                hideVirtualKeyboard();
                dismissProgressDialog();
                sortEditProfileResponse(outLaw);

                // hide virtual keyboard


            }
        });
    }

    private void hideVirtualKeyboard(){
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }

    private void sortEditProfileResponse(OutLaw outLaw){
        switch (outLaw.getMsg()){
            case "Incorrect password":
                editProfilePasswordWarning.setVisibility(View.VISIBLE);
                editProfilePasswordWarning.setText(outLaw.getMsg());
                break;
            case "New number already exists":
                passView.setVisibility(View.GONE);
                editProfileView.setVisibility(View.VISIBLE);
                editProfileWarning.setVisibility(View.VISIBLE);
                editProfileWarning.setText(outLaw.getMsg());
                editPhoneNumber.requestFocus();
                break;
            case "Profile updated":
                saveUpdatedCustomer();
                profileUpdateSuccess();
                setVisibility();
                break;
            default:
                // do nothing
                break;
        }
    }

    private void profileUpdateSuccess(){
        if(getContext() != null){
            MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(getContext())
                    .setTitle("Profile updated successfully")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            materialAlertDialogBuilder.show();
        }
    }

    private View.OnClickListener confirmPass(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!emptyPasswordField()){
                    saveProfileViewModel(cusObj());
                }
            }
        };
    }

    private View.OnClickListener saveEditedListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkCustomerFields()){
                    editProfileView.setVisibility(View.GONE);
                    passView.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getContext(), "Fields(s) can't be empty",Toast.LENGTH_SHORT).show();
                }
            }
        };
    }


    @Override
    public void onStart() {
        super.onStart();
        lockScreenInPortraitMode();
        saveEditedProfile.setOnClickListener(saveEditedListener());
        editProfileTopWord.setOnClickListener(goBackListener());
        confirmPasswordButton.setOnClickListener(confirmPass());
    }


    @Override
    public void onStop() {
        super.onStop();
        setScreenInNormalMode();
        saveEditedProfile.setOnClickListener(null);
        editProfileTopWord.setOnClickListener(null);
        confirmPasswordButton.setOnClickListener(null);
    }

    private void showProgressDialog(){
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setTitle("Saving profile");
        mProgressDialog.setMessage("Please wait.");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    private void dismissProgressDialog(){
        if(mProgressDialog != null){
            if(mProgressDialog.isShowing()){
                mProgressDialog.dismiss(); }
        }
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


    @Override
    public void onBackPressed() {
        if(MainActivity.mTrackMain == MainActivity.TrackMain.PROFILE){
            if(MainActivity.mProfileTrack == MainActivity.ProfileTrack.PROFILE_DETAIL){
                goBack();
            }
        }
    }

    private void resetUserEntity(HashMap<String, String> body){
        ViewModelHomeFragment.userEntity.setFirstName(body.get("firstName"));
        ViewModelHomeFragment.userEntity.setLastName(body.get("lastName"));
        ViewModelHomeFragment.userEntity.setPhoneNumber(body.get("phoneNumber"));
        ViewModelHomeFragment.userEntity.setEmail(body.get("email"));
    }

    private void saveUpdatedCustomer(){
        resetUserEntity(cusObj());

        Log.d(TAG, "saveCustomerData: called");
        class SaveUpdatedCustomer extends AsyncTask<Void, Void, Void>{
            @Override
            protected Void doInBackground(Void... voids) {
                // clear database
                DatabaseClient.getInstance(getContext()).getAppDatabase()
                        .mUserDao()
                        .deleteAll();
                // save updated info
                DatabaseClient
                        .getInstance(getContext())
                        .getAppDatabase()
                        .mUserDao()
                        .insert(ViewModelHomeFragment.userEntity);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(getContext(), "Saved customer data", Toast.LENGTH_SHORT).show();
            }
        }
        SaveUpdatedCustomer saveUpdatedCustomer = new SaveUpdatedCustomer();
        saveUpdatedCustomer.execute();
    }

    private void init(View view){
        editProfilePasswordWarning = view.findViewById(R.id.editProfilePasswordWarning);
        editProfileWarning = view.findViewById(R.id.editProfileWarning);
        editProfileTopWord = view.findViewById(R.id.editProfileTopWord);
        editProfileView = view.findViewById(R.id.editProfileView);
        editFirstName = view.findViewById(R.id.editFirstName);
        editLastName = view.findViewById(R.id.editLastName);
        editEmail = view.findViewById(R.id.editEmail);
        editPhoneNumber = view.findViewById(R.id.editPhoneNumber);
        saveEditedProfile = view.findViewById(R.id.saveEditedProfile);

        passView = view.findViewById(R.id.passView);
        confirmPassword = view.findViewById(R.id.confirmPassword);
        confirmPasswordButton = view.findViewById(R.id.confirmPasswordButton);

    }

    private void lockScreenInPortraitMode(){
        Objects.requireNonNull(getActivity()).
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    private void setScreenInNormalMode(){
        Objects.requireNonNull(getActivity()).
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }




}
