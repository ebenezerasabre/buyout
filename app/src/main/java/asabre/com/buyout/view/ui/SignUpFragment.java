package asabre.com.buyout.view.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Locale;
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
//import io.reactivex.rxjava3.core.Observable;

public class SignUpFragment extends Fragment implements BaseFragment {
    private static final String TAG = SignUpFragment.class.getSimpleName();

    private TextView signUpTopWord;
//    private TextView signUpWarning;
    private TextInputEditText enterFirstName;
    private TextInputEditText enterLastName;
//    private EditText phoneNumber;
//    private EditText email;
//    private EditText password;
//    private Button signUpFAB;
    private MaterialButton signUpFAB;
    private TextView signInHere;
    private ViewModelHomeFragment mViewModelHomeFragment;
    String customerId = "";
    private ProgressDialog mProgressDialog;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_create, container, false);
        init(view);
        return view;
    }

    private void init(View view){
        signUpTopWord = view.findViewById(R.id.signUpTopWord);
//        signUpWarning = view.findViewById(R.id.signUpWarning);
        enterFirstName = view.findViewById(R.id.enterFirstName);
        enterLastName = view.findViewById(R.id.enterLastName);
//        phoneNumber = view.findViewById(R.id.signUpPhoneNumber);
//        password = view.findViewById(R.id.signUpPassword);
//        email = view.findViewById(R.id.signUpEmail);
        signUpFAB = view.findViewById(R.id.signUpFAB);
        signInHere = view.findViewById(R.id.signInHere);
        mViewModelHomeFragment = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(ViewModelHomeFragment.class);
        mViewModelHomeFragment.init();

        enterFirstName.requestFocus();
    }

    private boolean checkCustomerFields(){
        boolean empty = false;
        ArrayList<String> customerFields = new ArrayList<>();
        customerFields.add(enterFirstName.getText().toString());
        customerFields.add(enterLastName.getText().toString());

//        customerFields.add(phoneNumber.getText().toString());
//        customerFields.add(password.getText().toString());

        for(String str : customerFields){
            if (str.isEmpty()) { empty = true; break; }
        }
        return !empty;
    }

//    private HashMap<String, String> cusObj(){
//        HashMap<String, String> obj = new HashMap<>();
//        obj.put("firstName", firstName.getText().toString());
//        obj.put("lastName", lastName.getText().toString());
//        obj.put("phoneNumber", phoneNumber.getText().toString());
//        obj.put("password", password.getText().toString());
//        obj.put("email", email.getText().toString());
//        return obj;
//    }

    private void setFields(){
        ViewModelHomeFragment.createObject.put("firstName", enterFirstName.getText().toString());
        ViewModelHomeFragment.createObject.put("lastName", enterLastName.getText().toString());
    }
    private void signUpCustomer(){
        mViewModelHomeFragment.signUpCustomer(ViewModelHomeFragment.createObject).observe(Objects.requireNonNull(getActivity()), new Observer<OutLaw>() {
            @Override
            public void onChanged(OutLaw outLaw) {
                dismissProgressDialog();
                signUpState(outLaw);
            }
        });
    }

    private View.OnClickListener signUpViewModel(){
      return new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              if(checkCustomerFields()){
                  setFields();
                  showProgressDialog();
                 signUpCustomer();
              }   else {
                  Toast.makeText(getContext(),"Field(s) can't be empty", Toast.LENGTH_SHORT).show();
              }
          }
      };
    }

    private void signUpState(OutLaw outLaw){
        switch (outLaw.getMsg()){
            case "User account created successfully":
                    Log.d(TAG, "onChanged:  user created " + outLaw);
//                    signUpWarning.setVisibility(View.VISIBLE);
                    customerId = outLaw.get_id();
                    saveCustomerData();
                break;
            case "Phone number already exists!":
//                signUpWarning.setText(outLaw.getMsg());
//                signUpWarning.setVisibility(View.VISIBLE);
                infoDialog("Phone number already exists!");
                break;
            default:
                // do nothing
                break;
        }
    }

    private UserEntity customerInfo(){
        UserEntity userEntity = new UserEntity();
        userEntity.setCustomerId(customerId);

//        userEntity.setFirstName(Objects.requireNonNull(firstName.getText()).toString());
//        userEntity.setLastName(Objects.requireNonNull(lastName.getText()).toString());
//        userEntity.setPhoneNumber(Objects.requireNonNull(phoneNumber.getText()).toString());

        userEntity.setFirstName(ViewModelHomeFragment.createObject.get("firstName"));
        userEntity.setLastName(ViewModelHomeFragment.createObject.get("lastName"));
        userEntity.setPhoneNumber(ViewModelHomeFragment.createObject.get("phoneNumber"));

        userEntity.setUserImage("");
        return userEntity;
    }



    private void saveCustomerData(){
        class SaveCustomer extends AsyncTask<Void, Void, Void>{
            @Override
            protected Void doInBackground(Void... voids) {
//                clear database
                DatabaseClient.getInstance(getContext()).getAppDatabase()
                        .mUserDao()
                        .deleteAll();
//                save user info
                DatabaseClient
                        .getInstance(getContext())
                        .getAppDatabase()
                        .mUserDao()
                        .insert(customerInfo());

                // reset
                resetViewModelArguments(customerInfo());
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(getContext(),"Saved customer data", Toast.LENGTH_SHORT).show();
                loadHomeFragment();

//                String intro =

                infoDialog(String.format(Locale.US,
                        "Hello %s, welcome on board.Browse our categories and find the perfect product for you. " +
                                "Click on the home button on the left of the search bar and select help and tutorials for more info on how to navigate your way here.",
                        ViewModelHomeFragment.userEntity.getFirstName()));

            }
        }
        SaveCustomer saveCustomer = new SaveCustomer();
        saveCustomer.execute();
    }

    private void resetViewModelArguments(UserEntity entity){
        ViewModelHomeFragment.userEntity = entity;
        ViewModelHomeFragment.wishListIncrease = 1;
        mViewModelHomeFragment.setWishListNull();
        mViewModelHomeFragment.setCartNull();
        mViewModelHomeFragment.setOrdersNull();
        mViewModelHomeFragment.resetCustomerAddress();
    }

    private void infoDialog(String msg){
        if(getContext() != null){
            MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(getContext())
                    .setMessage(msg)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
            materialAlertDialogBuilder.show();
        }
    }

    private void getCustomerData(){
//       class GetCustomerData extends AsyncTask<Void, Void, List<UserEntity>>{
//            @Override
//            protected List<UserEntity> doInBackground(Void... voids) {
//                List<UserEntity> customerList = DatabaseClient
//                        .getInstance(getContext())
//                        .getAppDatabase()
//                        .mUserDao()
//                        .getAll();
//                return customerList;
//            }
//            @Override
//            protected void onPostExecute(List<UserEntity> userEntities) {
//                super.onPostExecute(userEntities);
//                Log.d(TAG, "onPostExecute: after signUp " + userEntities.get(0).getFirstName());
//            }
//        }
//        GetCustomerData gc = new GetCustomerData();
//        gc.execute();
    }

    private void loadHomeFragment(){
        Log.d(TAG, "loadHome calling wish " + ViewModelHomeFragment.userEntity.getCustomerId());
        HomeFragment homeFragment = new HomeFragment();
        if(getActivity() != null){
            getActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containerHomeFragment, homeFragment)
                    .commit();
            goBack();
        }
    }

    private void showProgressDialog(){
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setTitle("Signing up");
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

    private View.OnClickListener loadSignInListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignInFragment signInFragment = new SignInFragment();
                if(getActivity() != null){
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
                    transaction.replace(R.id.containerProducts, signInFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        signUpTopWord.setOnClickListener(PreviousFragmentListener());
        signUpFAB.setOnClickListener(signUpViewModel());
        signInHere.setOnClickListener(loadSignInListener());
    }

    @Override
    public void onStop() {
        super.onStop();
        signUpTopWord.setOnClickListener(null);
        signUpFAB.setOnClickListener(null);
        signInHere.setOnClickListener(null);
    }

    @Override
    public void onBackPressed() {
        if(MainActivity.mHomeTrack == MainActivity.HomeTrack.PRODUCTS){
//            goBack();
            loadEnterEmailFragment();
        }
    }

    private void loadEnterEmailFragment() {

        EnterEmailFragment enterEmailFragment = new EnterEmailFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        transaction.replace(R.id.containerProducts, enterEmailFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void goBack(){
        MainActivity.mHomeTrack = MainActivity.HomeTrack.HOME;
        setVisibility();
        removeThisFragment();
    }

    private View.OnClickListener PreviousFragmentListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                goBack();
                loadEnterEmailFragment();
            }
        };
    }

    private void setVisibility(){
        if(getActivity() != null){
            getActivity().findViewById(R.id.containerProducts).setVisibility(View.GONE);
            getActivity().findViewById(R.id.containerHomeFragment).setVisibility(View.VISIBLE);
        }
    }

    private void removeThisFragment(){
        if(getActivity() != null){
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentById(R.id.containerProducts);
            if(fragment != null){
                getActivity().getSupportFragmentManager().beginTransaction()
                        .remove(fragment)
                        .commit();
            }
        }
    }

    private View findHomeFrameLayout(){ return Objects.requireNonNull(getActivity()).findViewById(R.id.containerHomeFragment);}
    private View findProductFrameLayout(){ return Objects.requireNonNull(getActivity()).findViewById(R.id.containerProducts);}


}
