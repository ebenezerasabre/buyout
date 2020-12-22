package asabre.com.buyout.view.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.HashMap;
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
import asabre.com.buyout.service.model.Customer;
import asabre.com.buyout.service.model.UserEntity;
import asabre.com.buyout.service.repository.DatabaseClient;
import asabre.com.buyout.view.callback.BaseFragment;
import asabre.com.buyout.viewmodel.ViewModelHomeFragment;

public class SignInFragment extends Fragment implements BaseFragment {
    private static final String TAG = SignInFragment.class.getSimpleName();

    private TextView signInTopWord;
    private EditText signInPhoneNumber;
    private EditText signInPassword;
    private TextView signUpHere;
    private Button signInFAB;

    // get viewModel
    private ViewModelHomeFragment mViewModelHomeFragment;
    private ProgressDialog mProgressDialog;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accoung_sign_in, container, false);
        init(view);
        return view;
    }


    private void init(View view){
        signInTopWord = view.findViewById(R.id.signInTopWord);
        signInPhoneNumber = view.findViewById(R.id.signInPhoneNumber);
        signInPassword = view.findViewById(R.id.signInPassword);
        signUpHere = view.findViewById(R.id.signUpHere);
        signInFAB = view.findViewById(R.id.signInFAB);
        signInPhoneNumber.requestFocus();
    }

    private boolean checkCustomerFields(){
        boolean empty = false;
        ArrayList<String> customerFields = new ArrayList<>();
        customerFields.add(signInPhoneNumber.getText().toString());
        customerFields.add(signInPassword.getText().toString());

        for(String str : customerFields){
            if (str.isEmpty()) {
                empty = true;
                break;
            }
        }
        return empty;
    }

    private HashMap<String, String> customerFields(){
        HashMap<String, String> obj = new HashMap<>();
        obj.put("password", signInPassword.getText().toString());
        obj.put("phoneNumber", signInPhoneNumber.getText().toString());
        return obj;
    }


    private View.OnClickListener signInListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkCustomerFields()){
                    signInFirstPart();
                }  else {
                    Toast.makeText(getContext(),"Field(s) can't be empty",Toast.LENGTH_SHORT).show();
                }

            }
        };
    }

    private void signInFirstPart(){
        showProgressDialog();
//      findProgressBar().setVisibility(View.VISIBLE);
        setViewModel(); // make request
    }

    private void setViewModel(){
        if(getActivity() != null){
            mViewModelHomeFragment = ViewModelProviders.of(getActivity()).get(ViewModelHomeFragment.class);
            mViewModelHomeFragment.initSignInCustomer(customerFields()).observe(this, new Observer<Customer>() {
                @Override
                public void onChanged(Customer customer) {

                    if(!customer.getFirstName().isEmpty()){
                        MainActivity.customer = customer;
                        signInSecondPart(setEntity(customer));
                    } else {
                        dismissProgressDialog();
                        infoDialog("Either phone number or password is incorrect");
                    }
                }
            });
        }
    }

    private void signInSecondPart(UserEntity entity){
        dismissProgressDialog();
//      findProgressBar().setVisibility(View.GONE);
        saveCustomerData(entity);
    }

    private void saveCustomerData(final UserEntity entity){
        Log.d(TAG, "saveCustomerData: called here");
        class SaveCustomer extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
//              clear database
                DatabaseClient.getInstance(getContext()).getAppDatabase()
                        .mUserDao()
                        .deleteAll();

//              save user info
                DatabaseClient
                        .getInstance(getContext())
                        .getAppDatabase()
                        .mUserDao()
                        .insert(entity);

                // reset
                resetViewModelArguments(entity);
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(getContext(),"Saved customer data", Toast.LENGTH_SHORT).show();
                signedInDialog();
                findHomeFrameLayout().setVisibility(View.VISIBLE);
                findProductFrameLayout().setVisibility(View.GONE);

                loadHomeFragment();
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
        mViewModelHomeFragment.resetCustomerHistory();
    }

    private UserEntity setEntity(Customer customer){
        return new UserEntity(
                customer.get_id(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getPhoneNumber(),
                customer.getEmail(),
                customer.getUserImage()
        );
    }


    private void loadHomeFragment(){
        Log.d(TAG, "loadHome calling wiiiiiish " + ViewModelHomeFragment.userEntity.getCustomerId());
        HomeFragment homeFragment = new HomeFragment();
        if(getActivity() != null){
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containerHomeFragment, homeFragment)
                    .commit();
            goBack();
        }
    }

    private void signedInDialog(){
        if(getContext() != null){
            final String[] options = {"Men Products","Women Products","children","Smart Phones","Electronics"};
            MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(getContext())
                    .setTitle( String.format(Locale.US, "Hello %s, here are our categories", MainActivity.customer.getFirstName()))
                    .setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FilterOptionsSelect(options[which]);
                        }
                    })
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            materialAlertDialogBuilder.show();
        }
    }

    private void FilterOptionsSelect(String options){
        switch (options){
            case "Men Products":
                onOptionsClick("Man");
                break;
            case "Women Products":
                onOptionsClick("Woman");
                break;
            case "children":
                onOptionsClick("Kid");
                break;
            case "Smart Phones":
                onOptionsClick("Smart Phones");
                break;
            case "Electronics":
                onOptionsClick("Electronics");
                break;
            default:
                // do nothing
                break;
        }
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

    private void onOptionsClick(String category){
        MainActivity.mHomeTrack = MainActivity.HomeTrack.PRODUCTS;
        if(!MainActivity.searchOther.equals(category.toLowerCase())){
            mViewModelHomeFragment.setCategoryProductsNull();
            ViewModelHomeFragment.categoryProductIncrease = 1;
        }
        MainActivity.searchOther = category.toLowerCase();
        MainActivity.searchRef = "category";
        loadProducts();
    }

    private void loadProducts(){
        // call products in parts
        MainActivity.seeAll = "category";

        MainActivity.mHomeTrack = MainActivity.HomeTrack.PRODUCTS;
        ProductsFragment productsFragment = new ProductsFragment();
        loadProductsFragment(productsFragment);
        Log.d(TAG, "loadProducts: MainActivity.seeAll value is " + MainActivity.seeAll);
    }

    private void loadProductsFragment(Fragment fragment){
        removePreviousProductsFameLayoutData();

        if(fragment != null && getActivity() != null){
            findHomeFrameLayout().setVisibility(View.GONE);
            findProductFrameLayout().setVisibility(View.VISIBLE);

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
            transaction.replace(R.id.containerProducts, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    private void removePreviousProductsFameLayoutData(){
        if(getActivity() != null){
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentById(R.id.containerProductDetails);
            if(fragment != null){
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .remove(fragment)
                        .commit();
            }
        }
    }

    private void goBack(){
        MainActivity.mHomeTrack = MainActivity.HomeTrack.HOME;
        setVisibility();
        removeThisFragment();
    }

    private View.OnClickListener goBackListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
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

    private View.OnClickListener loadSignUpListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUpFragment signUpFragment = new SignUpFragment();
                if(getActivity() != null){
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
                    transaction.replace(R.id.containerProducts, signUpFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
        };
    }

    private void showProgressDialog(){
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setTitle("Signing in");
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
    public void onStart() {
        super.onStart();
        signInTopWord.setOnClickListener(goBackListener());
        signInFAB.setOnClickListener(signInListener());
        signUpHere.setOnClickListener(loadSignUpListener());
    }


    @Override
    public void onStop() {
        super.onStop();
        signInTopWord.setOnClickListener(null);
        signUpHere.setOnClickListener(null);
        signInFAB.setOnClickListener(null);
    }

    @Override
    public void onBackPressed() {
        if(MainActivity.mHomeTrack == MainActivity.HomeTrack.PRODUCTS){
            goBack();
        }
    }

    private View findHomeFrameLayout(){ return Objects.requireNonNull(getActivity()).findViewById(R.id.containerHomeFragment);}
    private View findProductFrameLayout(){ return Objects.requireNonNull(getActivity()).findViewById(R.id.containerProducts);}
    private View findProgressBar(){ return Objects.requireNonNull(getActivity()).findViewById(R.id.indeterminateSignInFragment); }
    private View findProductsFloatingButton(){ return Objects.requireNonNull(getActivity()).findViewById(R.id.signInFAB);}

}
