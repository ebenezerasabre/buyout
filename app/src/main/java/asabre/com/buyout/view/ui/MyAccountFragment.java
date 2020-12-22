package asabre.com.buyout.view.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import asabre.com.buyout.R;
import asabre.com.buyout.service.model.Address;
import asabre.com.buyout.view.adapter.MyAccountAddressAdapter;
import asabre.com.buyout.view.callback.AccountAddressCallback;
import asabre.com.buyout.view.callback.BaseFragment;
import asabre.com.buyout.viewmodel.ViewModelHomeFragment;

public class MyAccountFragment extends Fragment implements
        BaseFragment, AccountAddressCallback {
    private static final String TAG = MyAccountFragment.class.getSimpleName();

    private TextView myAccountTopWord;
    private TextView myAccountAddNewLocation;
    private TextView myAccountFirstName;
    private TextView myAccountLastName;
    private TextView myAccountPhoneNumber;
    private TextView myAccountEmail;
    private ExtendedFloatingActionButton myAccountCloseFAB;

    private MyAccountAddressAdapter mMyAccountAddressAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_account, container, false);
        init(view);
        setViewModel();
        setPersonalInfo();
        return view;
    }

    private void init(View view) {
        myAccountTopWord = view.findViewById(R.id.myAccountTopWord);
        myAccountAddNewLocation = view.findViewById(R.id.myAccountAddNewLocation);
        myAccountFirstName = view.findViewById(R.id.myAccountFirstName);
        myAccountLastName = view.findViewById(R.id.myAccountLastName);
        myAccountPhoneNumber = view.findViewById(R.id.myAccountPhoneNumber);
        myAccountEmail = view.findViewById(R.id.myAccountEmail);
        myAccountCloseFAB = view.findViewById(R.id.myAccountCloseFAB);

        // for recyclerView
        RecyclerView recyclerView = view.findViewById(R.id.holderMyAccountLocation);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mMyAccountAddressAdapter = new MyAccountAddressAdapter(this.getContext(), new ArrayList<Address>(), this);
        recyclerView.setAdapter(mMyAccountAddressAdapter);
    }

    private void setPersonalInfo(){
        myAccountFirstName.setText(ViewModelHomeFragment.userEntity.getFirstName());
        myAccountLastName.setText(ViewModelHomeFragment.userEntity.getLastName());
        myAccountPhoneNumber.setText(ViewModelHomeFragment.userEntity.getPhoneNumber());
        myAccountEmail.setText(ViewModelHomeFragment.userEntity.getEmail());
    }

    private void setViewModel(){
        if(getActivity() != null){
            ViewModelHomeFragment viewModelHomeFragment = ViewModelProviders.of(getActivity()).get(ViewModelHomeFragment.class);
            viewModelHomeFragment.init();
            viewModelHomeFragment.getCustomerAddress().observe(this, new Observer<List<Address>>() {
                @Override
                public void onChanged(List<Address> list) {
                    Log.d(TAG, "onChanged: the list is " + list);
                    mMyAccountAddressAdapter.loadNewData(list);
                }
            });
        }
    }




    // from AccountAddressCallback
    @Override
    public void editAddress(Address address) {
        Toast.makeText(getContext(), address.getNickName(),Toast.LENGTH_SHORT).show();
        loadEditLocationDialog("edit", address);
    }

    @Override
    public void viewAddress(Address address) {
        Toast.makeText(getContext(), address.getNickName(),Toast.LENGTH_SHORT).show();
        loadEditLocationDialog("view", address);
    }

    private View.OnClickListener addNewAddressListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Address address = new Address();
                loadEditLocationDialog("add", address);
            }
        };
    }


    private void loadEditLocationDialog(String userCase, Address address){
        FragmentManager fm = getFragmentManager();
        if(fm != null){
            EditLocationDialog eL = EditLocationDialog.getInstance(userCase, address);
            eL.setTargetFragment(MyAccountFragment.this, 300);
            eL.show(fm, "CartAddDialog");
        }
    }

    private void hideVirtualKeyboard(){
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }


    @Override
    public void onStart() {
        super.onStart();
        myAccountCloseFAB.setOnClickListener(goBackListener());
        myAccountTopWord.setOnClickListener(goBackListener());
        myAccountAddNewLocation.setOnClickListener(addNewAddressListener());
    }

    @Override
    public void onStop() {
        super.onStop();
        myAccountCloseFAB.setOnClickListener(null);
        myAccountTopWord.setOnClickListener(null);
        myAccountAddNewLocation.setOnClickListener(null);
//        myAccountEditPI.setOnClickListener(null);
    }

    @Override
    public void onBackPressed() {
        if(MainActivity.mProfileTrack == MainActivity.ProfileTrack.PROFILE_DETAIL){
            setVisibility();
        }
    }



    private void setVisibility(){
        if(getActivity() != null){
            MainActivity.mProfileTrack = MainActivity.ProfileTrack.PROFILE;
            getActivity().findViewById(R.id.containerProfileDetails).setVisibility(View.GONE);
            getActivity().findViewById(R.id.containerProfile).setVisibility(View.VISIBLE);
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
                setVisibility();
            }
        };
    }




}
