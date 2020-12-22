package asabre.com.buyout.view.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import asabre.com.buyout.R;
import asabre.com.buyout.service.model.Address;
import asabre.com.buyout.service.model.Cart;
import asabre.com.buyout.service.model.OutLaw;
import asabre.com.buyout.view.adapter.UseLocationAdapter;
import asabre.com.buyout.view.callback.BaseFragment;
import asabre.com.buyout.view.callback.LocationDialogCallback;
import asabre.com.buyout.view.callback.PaymentCallback;
import asabre.com.buyout.viewmodel.ViewModelHomeFragment;

public class LocationFragment extends Fragment
        implements BaseFragment,
        LocationDialogCallback,
        PaymentCallback {
    private static final String TAG = LocationFragment.class.getCanonicalName();

    private RecyclerView mRecyclerViewAddress;
    private UseLocationAdapter mUseLocationAdapter;

    private TextView addressTopWord;
    private EditText location;
    private TextView locationHere;
    private EditText locationBuildingNo;
    private EditText locationLandmark;
    private EditText locationName;
    private EditText locationTag;
    private Button addressBtn;
    private ExtendedFloatingActionButton saveLocationFAB;
    private ExtendedFloatingActionButton closeLocationFAB;
    private LinearLayout addAddressContainer;
    private LinearLayout paymentContainer;
    private LinearLayout selectAddressContainer;
    private Button payOnDeliveryBtn;
    private static boolean mShowHide = false;
    private static boolean editingAddress = false;
    private Address mUpdatedAddress;
    private Address mOldAddress;

    private LocationDialogCallback mLocationDialogCallback = this;
    private PaymentCallback mPaymentCallback = this;

    private ViewModelHomeFragment mViewModelHomeFragment;
    private ProgressDialog mProgressDialog;

    // for location service
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private FusedLocationProviderClient mFusedLocationProviderClient;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location, container, false);
        init(view);
        return view;
    }

    private void init(View view){

        // for recyclerView
        mUseLocationAdapter = new UseLocationAdapter(getContext(), new ArrayList<Address>(), mLocationDialogCallback);
        mRecyclerViewAddress = view.findViewById(R.id.holderAddress);
        mRecyclerViewAddress.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerViewAddress.setAdapter(mUseLocationAdapter);

        addressTopWord = view.findViewById(R.id.addressTopWord);
        addressBtn = view.findViewById(R.id.addressBtn);
        location = view.findViewById(R.id.location);
        locationHere = view.findViewById(R.id.locationHere);
        locationBuildingNo = view.findViewById(R.id.locationBuildingNo);
        locationLandmark = view.findViewById(R.id.locationLandmark);
        locationName = view.findViewById(R.id.locationName);
        locationTag = view.findViewById(R.id.locationTag);
        saveLocationFAB = view.findViewById(R.id.saveLocationFAB);
        closeLocationFAB = view.findViewById(R.id.closeLocationFAB);
        selectAddressContainer = view.findViewById(R.id.selectAddressContainer);
        addAddressContainer = view.findViewById(R.id.addAddressContainer);
        paymentContainer = view.findViewById(R.id.paymentContainer);
        payOnDeliveryBtn = view.findViewById(R.id.payOnDeliveryBtn);

        location.requestFocus();

//        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        setViewModel();
    }

    @Override
    public void addLocation(String location, String houseNo, String landmark, String name, String tag) {
        HashMap<String, String> newAddress = new HashMap<>();
        newAddress.put("customerId", ViewModelHomeFragment.userEntity.getCustomerId());
        newAddress.put("location", location);
        newAddress.put("buildingNo", houseNo);
        newAddress.put("landmark", landmark);
        newAddress.put("nickName", name);
        newAddress.put("tag", tag);

        createUserAddressViewModel(newAddress);
    }

    @Override
    public void useLocation(String addressId) {
        Toast.makeText(getContext(), addressId, Toast.LENGTH_LONG).show();
        ViewModelHomeFragment.selectedAddressId = addressId;
        setVisibility(paymentContainer, selectAddressContainer, addAddressContainer, saveLocationFAB);
    }

    @Override
    public void updateLocation(Address address) {
        editingAddress = true;
        ViewModelHomeFragment.selectedAddressId = address.get_id();
        mOldAddress = address;
        mUpdatedAddress = address;
        addressBtn.setText("Hide");
        mShowHide = !mShowHide;

        addAddressContainer.setVisibility(View.VISIBLE);
        saveLocationFAB.setVisibility(View.VISIBLE);
        closeLocationFAB.setVisibility(View.GONE);
        fillFieldsWithAddressDetails(address);
    }
    // from LocationDialogCallback ends

    private void fillFieldsWithAddressDetails(Address address){
        location.setText(address.getLocation());
        locationBuildingNo.setText(address.getBuildingNo());
        locationLandmark.setText(address.getLandmark());
        locationName.setText(address.getNickName());
        locationTag.setText(address.getTag());
    }

    @Override
    public void payOnDelivery() {
        // make order request
        createOrdersViewModel();
    }

    @Override
    public void orderPlaceSuccessfully(String successMsg, String extraMsg) {
        // show material alert dialog
        orderSuccessDialog(successMsg, extraMsg);

        // refresh orderFragment
        refreshOrderFragment();

        // refresh cartFragment
        refreshCartFragment();
    }


    private void orderSuccessDialog(String successMsg, String extraMsg){
        if(getContext() != null){

            MaterialAlertDialogBuilder ma = new MaterialAlertDialogBuilder(getContext())
                    .setTitle(successMsg)
                    .setMessage(extraMsg)
                    .setCancelable(false)
                    .setPositiveButton("Go to order", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setVisibility();
                            Objects.requireNonNull(getActivity()).findViewById(R.id.containerOrder).setVisibility(View.VISIBLE);
                            getActivity().findViewById(R.id.containerCart).setVisibility(View.GONE);
                        }
                    })
                    .setNegativeButton("Home", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setVisibility();
                            Objects.requireNonNull(getActivity()).findViewById(R.id.containerHomeFragment).setVisibility(View.VISIBLE);
                            getActivity().findViewById(R.id.containerCart).setVisibility(View.GONE);

                        }
                    });
            ma.show();
        }
    }


    private void refreshCartFragment(){
        mViewModelHomeFragment.setCartNull();
        CartFragment cartFragment = new CartFragment();
        if(getActivity() != null){
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containerCart, cartFragment)
                    .commit();
        }
    }

    private void refreshOrderFragment(){
        mViewModelHomeFragment.setOrdersNull();
        OrderFragment orderFragment = new OrderFragment();
        if(getActivity() != null){
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containerOrder, orderFragment)
                    .commit();
        }
    }

    private void setViewModel(){
        mViewModelHomeFragment = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(ViewModelHomeFragment.class);
        mViewModelHomeFragment.init();

        // address has already been downloaded by cartFragment, observe it
        mViewModelHomeFragment.getCustomerAddress().observe(this, new Observer<List<Address>>() {
            @Override
            public void onChanged(List<Address> list) {
                Log.d(TAG, "onChanged: the list from fragment is " + list);
                mUseLocationAdapter.loadNewData(list);
            }
        });
    }

    private List<HashMap<String, String>> orderObj(){
        ArrayList<Cart> carts = new ArrayList<>(Objects.requireNonNull(ViewModelHomeFragment.mCarts.getValue()));
        List<HashMap<String, String>> list = new ArrayList<>();

        for(Cart cart : carts){
            HashMap<String, String> cartObj = new HashMap<>();
            cartObj.put("addressId", ViewModelHomeFragment.selectedAddressId);
            cartObj.put("cartId", cart.get_id());
            list.add(cartObj);
        }
        return list;
    }

    private void createOrdersViewModel(){
        showProgressDialog("Placing order");
        mViewModelHomeFragment.createOrder(orderObj()).observe(this, new Observer<String[]>() {
            @Override
            public void onChanged(String[] strings) {
                dismissProgressDialog();
                String successMsg = strings[0];
                String extra = strings[1];
                mPaymentCallback.orderPlaceSuccessfully(successMsg, extra);
            }
        });
    }

    private void showProgressDialog(String title){
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setTitle(title);
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

    private void setVisibility(View v1, View v2, View v3, View v4){
        v1.setVisibility(View.VISIBLE);
        v2.setVisibility(View.GONE);
        v3.setVisibility(View.GONE);
        v4.setVisibility(View.GONE);
    }

    private boolean checkEmptyFields(){
        boolean emptyField = false;
        ArrayList<String> list = new ArrayList<>();
        list.add(location.getText().toString());
        list.add(locationBuildingNo.getText().toString());
        list.add(locationLandmark.getText().toString());
        list.add(locationName.getText().toString());
        list.add(locationTag.getText().toString());

        for(String str : list){
            if(str.isEmpty()){ emptyField = true; }
        }
        return emptyField;
    }

    private HashMap<String, String> getFields(){
        HashMap<String, String> fields = new HashMap<>();
        fields.put("customerId", ViewModelHomeFragment.userEntity.getCustomerId());
        fields.put("location", location.getText().toString());
        fields.put("buildingNo", locationBuildingNo.getText().toString());
        fields.put("landmark", locationLandmark.getText().toString());
        fields.put("nickName", locationName.getText().toString());
        fields.put("tag", locationTag.getText().toString());
        return fields;
    }

    private void createUserAddressViewModel(HashMap<String, String> body){
        // show progressDialog
        showProgressDialog("Creating address");
        mViewModelHomeFragment.createUserAddress(body).observe(this, new Observer<Address>() {
            @Override
            public void onChanged(Address address) {
                Log.d(TAG, "onChanged: the address created is " + address);
                mViewModelHomeFragment.addMoreUserAddress(address);

                // dismiss progressDialog
                dismissProgressDialog();
                addAddressContainer.setVisibility(View.GONE);
                saveLocationFAB.setVisibility(View.GONE);
                closeLocationFAB.setVisibility(View.VISIBLE);
            }
        });

    }

    private void setUpdatedAddress(HashMap<String, String> body){
        mUpdatedAddress.setLocation(body.get("location"));
        mUpdatedAddress.setBuildingNo(body.get("buildingNo"));
        mUpdatedAddress.setLandmark(body.get("landmark"));
        mUpdatedAddress.setNickName(body.get("nickName"));
        mUpdatedAddress.setTag(body.get("tag"));
    }

    private void updateUserAddressViewModel(HashMap<String, String> body) {
        setUpdatedAddress(body);
        addressBtn.setText("Hide");
        showProgressDialog("Editing address");

        // saving edited address
        mViewModelHomeFragment.updateUserAddress(body).observe(this, new Observer<OutLaw>() {
            @Override
            public void onChanged(OutLaw outLaw) {
                dismissProgressDialog();
                addressBtn.setText("Add new address");
                addAddressContainer.setVisibility(View.GONE);
                saveLocationFAB.setVisibility(View.GONE);
                closeLocationFAB.setVisibility(View.VISIBLE);

                Log.d(TAG, "onChanged: updating address : "  + outLaw);
                mViewModelHomeFragment.removeFromAddress(mOldAddress);
                mViewModelHomeFragment.addMoreUserAddress(mUpdatedAddress);

            }
        });
    }

    private View.OnClickListener savingLocationListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> obj = getFields();
                if(!editingAddress){
                    // saving new address
                    if(!checkEmptyFields()){
                        mLocationDialogCallback.addLocation(
                                obj.get("location"),
                                obj.get("buildingNo"),
                                obj.get("landmark"),
                                obj.get("nickName"),
                                obj.get("tag")
                        );
                    } else {
                        Toast.makeText(getContext(),"Field(s) can't be empty", Toast.LENGTH_LONG).show();
                    }
                } else {
                    obj.put("_id", ViewModelHomeFragment.selectedAddressId);
                    updateUserAddressViewModel(obj);
                }
            }
        };
    }

    private View.OnClickListener hideShowButtonListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShowHide = !mShowHide;

                if(mShowHide){
                    addAddressContainer.setVisibility(View.VISIBLE);
                    saveLocationFAB.setVisibility(View.VISIBLE);
                    addressBtn.setText("Hide");
                    closeLocationFAB.setVisibility(View.GONE);
                } else {
                    addAddressContainer.setVisibility(View.GONE);
                    saveLocationFAB.setVisibility(View.GONE);
                    addressBtn.setText("Add new address");
                    closeLocationFAB.setVisibility(View.VISIBLE);
                }
            }
        };
    }

    private View.OnClickListener payOnDeliveryListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPaymentCallback.payOnDelivery();
            }
        };
    }
    private View.OnClickListener goBackListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibility();
            }
        };
    }

    private void setVisibility(){
        if(MainActivity.mCartTrack == MainActivity.CartTrack.CART_DETAILS){
            MainActivity.mCartTrack = MainActivity.CartTrack.CART;
            if (getActivity() != null) {
                getActivity().findViewById(R.id.containerCartDetails).setVisibility(View.GONE);
                getActivity().findViewById(R.id.containerCart).setVisibility(View.VISIBLE);
                removeThisFragment();
            }
        }
    }

    private void removeThisFragment(){
        if(getActivity() != null){
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentById(R.id.containerCartDetails);
            if(fragment != null){
                getActivity().getSupportFragmentManager().beginTransaction()
                        .remove(fragment)
                        .commit();
            }
        }
    }

    private void lockScreenInPortraitMode(){
        Objects.requireNonNull(getActivity()).
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    private void setScreenInNormalMode(){
        Objects.requireNonNull(getActivity()).
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

    @Override
    public void onStart() {
        super.onStart();
        lockScreenInPortraitMode();
        saveLocationFAB.setOnClickListener(savingLocationListener());
        addressBtn.setOnClickListener(hideShowButtonListener());
        payOnDeliveryBtn.setOnClickListener(payOnDeliveryListener());
        closeLocationFAB.setOnClickListener(goBackListener());
        addressTopWord.setOnClickListener(goBackListener());

        getLocationPermission();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: location dialog stopped");
        // set app rotation to normal
        setScreenInNormalMode();
        addressTopWord.setOnClickListener(null);
        saveLocationFAB.setOnClickListener(null);
        addressBtn.setOnClickListener(null);
        payOnDeliveryBtn.setOnClickListener(null);
        closeLocationFAB.setOnClickListener(null);
    }

    @Override
    public void onBackPressed() {
        if(MainActivity.mTrackMain == MainActivity.TrackMain.CART){
            setVisibility();
        }
    }



    // getting device location

    private void getLocationPermission(){
        if(getActivity() != null && getContext() != null){
            List<String> listPermissionNeeded = new ArrayList<>();
            int permissionCoarseLocation = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
            int permissionFineLocation = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);

            if(permissionCoarseLocation == PackageManager.PERMISSION_GRANTED){
                if(permissionFineLocation == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getContext(), "The location permission granted",Toast.LENGTH_SHORT).show();
                    getDeviceLocation(); // get device location
                } else {
                    listPermissionNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
                    ActivityCompat.requestPermissions(getActivity(), listPermissionNeeded.toArray(new String[0]), REQUEST_ID_MULTIPLE_PERMISSIONS);
                }
            } else {
                listPermissionNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
                ActivityCompat.requestPermissions(getActivity(), listPermissionNeeded.toArray(new String[0]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }

        }
    }

    private void getDeviceLocation(){
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
            try {

                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Location currentLocation = (Location) task.getResult();
                            Log.d(TAG, "onComplete: found location");
                            if(currentLocation != null){
                                // getting the street name out of location
                                Log.d(TAG, "onComplete: found location 2 " + currentLocation.getLatitude());
                                getAddressFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), getContext());
                            } else {
                                Log.d(TAG, "onComplete: current location is null ");
                            }
                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(getContext(), "unable to get location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (SecurityException e){
                Log.d(TAG, "getDeviceLocation: SecurityException" + e.getMessage());
            }
    }


    private void getAddressFromLocation(final double latitude, final double longitude, final Context context){
        Log.d(TAG, "getAddressFromLocation: the latitude was " + latitude);
        Thread thread = new Thread(){
            String result = "";
            @Override
            public void run() {
                super.run();
                Geocoder geocoder;
                if(context != null){
                    geocoder = new Geocoder(context, Locale.getDefault());
                    try {
                        List<android.location.Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                        if(addressList != null && addressList.size() > 0){
                            android.location.Address address = addressList.get(0);
                            StringBuilder sb = new StringBuilder();
                            for(int z=0; z<address.getMaxAddressLineIndex(); z++){
                                sb.append(address.getAddressLine(z)).append(",");
                            }
                            sb.append(address.getSubLocality()).append(","); // tafo
                            sb.append(address.getLocality()).append(","); // kumasi
                            sb.append(address.getAdminArea()).append(","); // ashanti reg
                            sb.append(address.getCountryName()); // ghana
                            final String result = sb.toString();

                            if(getActivity() != null){
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(location != null){
                                            location.setText(result);
                                        }
                                    }
                                });
                            }

                        }
                    } catch (IOException e){
                        Log.d(TAG, "getAddressFromLocation: Unable to connect to Geocoder" + e.getMessage());
                    }
                    Log.d(TAG, "getAddressFromLocation: the quest address " + result);

                }

            }
        };
        thread.start();
    }


}
