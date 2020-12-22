package asabre.com.buyout.view.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import asabre.com.buyout.R;
import asabre.com.buyout.service.model.Address;
import asabre.com.buyout.service.model.OutLaw;
import asabre.com.buyout.viewmodel.ViewModelHomeFragment;

public class EditLocationDialog extends DialogFragment {
    private static final String TAG = EditLocationDialog.class.getSimpleName();

    private EditText editLocation;
    private EditText editLocationBuildingNo;
    private EditText editLocationLandmark;
    private EditText editLocationName;
    private EditText editLocationTag;
    private ExtendedFloatingActionButton editLocationCloseFAB;
    private ExtendedFloatingActionButton editLocationSaveFAB;
    private ExtendedFloatingActionButton editLocationDeleteFAB;

    private ViewModelHomeFragment mViewModelHomeFragment;
    private Address mOldAddress;
    private Address mUpdatedAddress;
    private String mUserCase;

    private ProgressDialog mProgressDialog;
    // for location service
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private FusedLocationProviderClient mFusedLocationProviderClient;


    public static EditLocationDialog getInstance(String userCase, Address address){
        Bundle arguments = new Bundle();
        arguments.putString("userCase", userCase);
        arguments.putSerializable("address", address);

        EditLocationDialog editLocationDialog = new EditLocationDialog();
        editLocationDialog.setArguments(arguments);
        return editLocationDialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_edit_location, container, false);
        init(view);
        setViewModel();
        setEditViews();
        return view;
    }


    private void init(View view){
        editLocation = view.findViewById(R.id.editLocation);
        editLocationBuildingNo = view.findViewById(R.id.editLocationBuildingNo);
        editLocationLandmark = view.findViewById(R.id.editLocationLandmark);
        editLocationName = view.findViewById(R.id.editLocationName);
        editLocationTag = view.findViewById(R.id.editLocationTag);
        editLocationCloseFAB = view.findViewById(R.id.editLocationCloseFAB);
        editLocationSaveFAB = view.findViewById(R.id.editLocationSaveFAB);
        editLocationDeleteFAB = view.findViewById(R.id.editLocationDeleteFAB);


    }

    private void setViewModel(){
        if(getActivity() != null){
            mViewModelHomeFragment = ViewModelProviders.of(getActivity()).get(ViewModelHomeFragment.class);
        }
    }

    private void setEditViews(){
        Bundle args = getArguments();
        if(args != null){
            mOldAddress = (Address) args.getSerializable("address");
            mUserCase = args.getString("userCase");
            if(mOldAddress != null){
                mUpdatedAddress = mOldAddress; // set address
                editLocation.setText(mOldAddress.getLocation());
                editLocationBuildingNo.setText(mOldAddress.getBuildingNo());
                editLocationLandmark.setText(mOldAddress.getLandmark());
                editLocationName.setText(mOldAddress.getNickName());
                editLocationTag.setText(mOldAddress.getTag());
            }

            if(mUserCase != null){
                setUserCase(mUserCase);
            }
        }
    }

    private void setUserCase(String userCase){
        switch (mUserCase){
            case "view": editLocationSaveFAB.setVisibility(View.GONE); break;
            case "add":
            case "edit":
                editLocationDeleteFAB.setVisibility(View.GONE);
                editLocationSaveFAB.setVisibility(View.VISIBLE);
                break;

            default: break;
        }
    }



    private boolean fieldIsNotEmpty(){
        return !editLocation.getText().toString().isEmpty() &&
                !editLocationBuildingNo.getText().toString().isEmpty() &&
                !editLocationLandmark.getText().toString().isEmpty() &&
                !editLocationName.getText().toString().isEmpty() &&
                !editLocationTag.getText().toString().isEmpty();
    }

    private HashMap<String, String> getFields(){
        HashMap<String, String> fields = new HashMap<>();
        if(mUserCase.equals("edit")){ fields.put("_id", mOldAddress.get_id()); }

        fields.put("customerId", ViewModelHomeFragment.userEntity.getCustomerId());
        fields.put("location", editLocation.getText().toString());
        fields.put("buildingNo", editLocationBuildingNo.getText().toString());
        fields.put("landmark", editLocationLandmark.getText().toString());
        fields.put("nickName", editLocationName.getText().toString());
        fields.put("tag", editLocationTag.getText().toString());
        return fields;
    }

    private void setUpdatedAddress(HashMap<String, String> body){
        mUpdatedAddress.setLocation(body.get("location"));
        mUpdatedAddress.setBuildingNo(body.get("buildingNo"));
        mUpdatedAddress.setLandmark(body.get("landmark"));
        mUpdatedAddress.setNickName(body.get("nickName"));
        mUpdatedAddress.setTag(body.get("tag"));
    }



    private void updateLocationViewModel(){
        setUpdatedAddress(getFields());
        if(getActivity() != null){
            showProgressDialog("Updating location");
            mViewModelHomeFragment.updateUserAddress(getFields()).observe(this, new Observer<OutLaw>() {
                @Override
                public void onChanged(OutLaw outLaw) {
                    dismiss();
                    dismissProgressDialog();
                    mViewModelHomeFragment.removeFromAddress(mOldAddress);
                    mViewModelHomeFragment.addMoreUserAddress(mUpdatedAddress);
                    hideVirtualKeyboard();
                    infoDialog(outLaw.getMsg());
                }
            });
        }
    }

    private void addNewAddressViewModel(){
        if(getActivity() != null){
            showProgressDialog("Saving location");
            mViewModelHomeFragment.createUserAddress(getFields()).observe(this, new Observer<Address>() {
                @Override
                public void onChanged(Address address) {
                    mViewModelHomeFragment.addMoreUserAddress(address);
                    dismiss();
                    dismissProgressDialog();
                    hideVirtualKeyboard();
                    infoDialog("New address added");

                }
            });
        }
    }

    private void deleteLocationViewModel(){
        HashMap<String, String> obj = new HashMap<>();
        obj.put("customerId", ViewModelHomeFragment.userEntity.getCustomerId());
        obj.put("addressId", mOldAddress.get_id());

        if(getActivity() != null){
            showProgressDialog("Deleting address");
            mViewModelHomeFragment.deleteCustomerAddress(obj).observe(this, new Observer<OutLaw>() {
                @Override
                public void onChanged(OutLaw outLaw) {
                    if(outLaw.getMsg().equals("address deleted")){
                        mViewModelHomeFragment.removeFromAddress(mOldAddress);

                        dismissProgressDialog();
                        dismiss();
                    }
                }
            });
        }
    }


    private View.OnClickListener deleteLocationListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteLocationViewModel();
            }
        };
    }


    private View.OnClickListener saveLocationListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fieldIsNotEmpty()){
                    if(mUserCase.equals("edit")){ updateLocationViewModel(); }
                    else{ addNewAddressViewModel(); }
                } else { Toast.makeText(getContext(), "Fields can't be empty",Toast.LENGTH_SHORT).show(); }
            }
        };
    }

    private View.OnClickListener closeDialog(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        };
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



    private void showProgressDialog(String msg){
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setTitle(msg);
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

    private void hideVirtualKeyboard(){
        if(getActivity() != null && getActivity().getCurrentFocus() != null){
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }




    @Override
    public void onResume() {

        //      Full screen dialog
//      Get existing layout params for the window
        WindowManager.LayoutParams params = Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).getAttributes();
        // Assign window properties to fill the parent
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        Objects.requireNonNull(getDialog().getWindow()).setAttributes(params);






////       Store access variables for window and blank point
//        Window window = Objects.requireNonNull(getDialog()).getWindow();
//        Point size = new Point();
//        // Store dimensions of the screen in size
//        assert window != null;
//        Display display = window.getWindowManager().getDefaultDisplay();
//        display.getSize(size);
//        // Set  the width of the dialog proportional to 75% of the screen width
//        window.setLayout((int) (size.x), WindowManager.LayoutParams.WRAP_CONTENT);
//        window.setGravity(Gravity.CENTER);
//        // call super onResume after sizing
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        editLocationCloseFAB.setOnClickListener(closeDialog());
        editLocationSaveFAB.setOnClickListener(saveLocationListener());
        editLocationDeleteFAB.setOnClickListener(deleteLocationListener());

        getLocationPermission();
    }

    @Override
    public void onStop() {
        super.onStop();
        editLocationCloseFAB.setOnClickListener(null);
        editLocationSaveFAB.setOnClickListener(null);
        editLocationDeleteFAB.setOnClickListener(null);
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
                                        if(editLocation != null){
                                            editLocation.setText(result);
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
