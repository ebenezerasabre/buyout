package asabre.com.buyout.view.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
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

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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
import asabre.com.buyout.service.model.Address;
import asabre.com.buyout.service.model.Cart;
import asabre.com.buyout.service.model.OutLaw;
import asabre.com.buyout.view.adapter.UseLocationAdapter;
import asabre.com.buyout.view.callback.LocationDialogCallback;
import asabre.com.buyout.view.callback.PaymentCallback;
import asabre.com.buyout.viewmodel.ViewModelHomeFragment;

public class LocationDialog extends DialogFragment
        implements
        LocationDialogCallback,
        PaymentCallback {
    private static final String TAG = LocationDialog.class.getSimpleName();

    private RecyclerView mRecyclerViewAddress;
    private UseLocationAdapter mUseLocationAdapter;

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

    public static LocationDialog getInstance(){
        return new LocationDialog();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_location, container);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }



    private void init(View view){

        // for recyclerView
        mUseLocationAdapter = new UseLocationAdapter(getContext(), new ArrayList<Address>(), mLocationDialogCallback);
        mRecyclerViewAddress = view.findViewById(R.id.holderAddress);
        mRecyclerViewAddress.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerViewAddress.setAdapter(mUseLocationAdapter);

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
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        setViewModel();
    }


    // from LocationDialogCallback starts
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


    // from PaymentCallback starts
    @Override
    public void payOnDelivery() {
        // make order request
        createOrdersViewModel();
    }

    @Override
    public void orderPlaceSuccessfully(String successMsg, String extraMsg) {

        // show material alert dialog
        orderSuccessDialog(successMsg);

        // refresh orderFragment
         refreshOrderFragment();

        // refresh cartFragment
        refreshCartFragment();

        // remove LocationDialog
//        dismiss();
    }
    // from PaymentCallback ends

    private void orderSuccessDialog(String successMsg){
        if(getContext() != null){
            MaterialAlertDialogBuilder ma = new MaterialAlertDialogBuilder(getContext())
                    .setTitle(successMsg)
                    .setCancelable(false)
                    .setPositiveButton("Go to order", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Objects.requireNonNull(getActivity()).findViewById(R.id.containerOrder).setVisibility(View.VISIBLE);
                            getActivity().findViewById(R.id.containerCart).setVisibility(View.GONE);
//                            dialog.dismiss();
                            dismiss();
                        }
                    })
                    .setNegativeButton("Home", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Objects.requireNonNull(getActivity()).findViewById(R.id.containerHomeFragment).setVisibility(View.VISIBLE);
                            getActivity().findViewById(R.id.containerCart).setVisibility(View.GONE);
//                            dialog.dismiss();
                            dismiss();
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
        Log.d(TAG, "setViewModel: locat");
        mViewModelHomeFragment = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(ViewModelHomeFragment.class);
        mViewModelHomeFragment.init();

        // address has already been downloaded by cartFragment, observe it
        mViewModelHomeFragment.getCustomerAddress().observe(this, new Observer<List<Address>>() {
            @Override
            public void onChanged(List<Address> list) {
                Log.d(TAG, "onChanged: the list from location dialog " + list);
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
//        mViewModelHomeFragment.mCreateOrder = new MutableLiveData<>();
        mViewModelHomeFragment.createOrder(orderObj()).observe(this, new Observer<String[]>() {
            @Override
            public void onChanged(String[] strings) {
                dismissProgressDialog();
                String successMsg = strings[0];
                String extraMsg = strings[1];
                mPaymentCallback.orderPlaceSuccessfully(successMsg, extraMsg);
//                mViewModelHomeFragment.mCreateOrder = null;
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

    private void lockScreenInPortraitMode(){
        Objects.requireNonNull(getActivity()).
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    private void setScreenInNormalMode(){
        Objects.requireNonNull(getActivity()).
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

    private View.OnClickListener dismissListener(){
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
        lockScreenInPortraitMode();
        saveLocationFAB.setOnClickListener(savingLocationListener());
        addressBtn.setOnClickListener(hideShowButtonListener());
        payOnDeliveryBtn.setOnClickListener(payOnDeliveryListener());
        closeLocationFAB.setOnClickListener(dismissListener());

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

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: location dialog stopped");
        // set app rotation to normal
       setScreenInNormalMode();
        saveLocationFAB.setOnClickListener(null);
        addressBtn.setOnClickListener(null);
        payOnDeliveryBtn.setOnClickListener(null);
        closeLocationFAB.setOnClickListener(null);
    }


}






















