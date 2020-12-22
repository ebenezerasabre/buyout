package asabre.com.buyout.view.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import asabre.com.buyout.R;
import asabre.com.buyout.service.model.OutLaw;
import asabre.com.buyout.service.model.Product;
import asabre.com.buyout.view.adapter.WishListAdapter;
import asabre.com.buyout.view.callback.WishListCallback;
import asabre.com.buyout.viewmodel.ViewModelHomeFragment;

public class WishListFragment extends Fragment implements WishListCallback {
    private static final String TAG = WishListFragment.class.getSimpleName();

    private WishListAdapter mWishListAdapter;
    private RecyclerView mRecyclerViewWishList;
    private ViewModelHomeFragment mViewModelHomeFragment;
    private ExtendedFloatingActionButton wishListFAB;
    private TextView wishListTopWord;


    private HashMap<String, String> wishSum = new HashMap<>();
    private Product removeProduct = new Product();
    private ProgressDialog mProgressDialog;

    String mCustomerId = ViewModelHomeFragment.userEntity == null ?
            "" : ViewModelHomeFragment.userEntity.getCustomerId();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: called here");
        View view = inflater.inflate(R.layout.fragment_wish_list, container, false);
        init(view);
        setWishListViewModel();
        return view;
    }

    private void init(View view){
        wishListTopWord = view.findViewById(R.id.wishListTopWord);
        wishListFAB = view.findViewById(R.id.wishListFAB);

        // for wishList
        mWishListAdapter = new WishListAdapter(getContext(), new ArrayList<Product>(), this);
        mRecyclerViewWishList = view.findViewById(R.id.holderWishList);
        mRecyclerViewWishList.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerViewWishList.setAdapter(mWishListAdapter);

    }

    private List<HashMap<String, String>> cusWishObj(){

        HashMap<String, String> wishObj = new HashMap<>();
        wishObj.put("customerId", mCustomerId);
        wishObj.put("part", String.valueOf(ViewModelHomeFragment.wishListIncrease));
        List<HashMap<String, String>> list = new ArrayList<>();
        list.add(wishObj);
        ViewModelHomeFragment.wishListIncrease += 1;
        Log.d(TAG, "cusWishObj: the customerId is " +  mCustomerId);
        return list;
    }

    private void setWishListViewModel(){
        if(getActivity() != null){
            mViewModelHomeFragment = ViewModelProviders.of(getActivity()).get(ViewModelHomeFragment.class);
            mViewModelHomeFragment.init();
            mViewModelHomeFragment.initWishLists(cusWishObj());

            mViewModelHomeFragment.getCustomerWishList().observe(this, new Observer<List<Product>>() {
                @Override
                public void onChanged(List<Product> products) {
                    wishSum = mWishListAdapter.loadNewData(products);
                    navigationBadge();
                }
            });

        }
    }

    private void addMoreWishList(List<HashMap<String, String>> body){
        mViewModelHomeFragment.addMoreWishList(body).observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                mViewModelHomeFragment.addToWishList(products);
            }
        });
        mViewModelHomeFragment.isUpdating.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    showProgressBar();
                } else {
                    hideProgressBar();
                    mRecyclerViewWishList.smoothScrollToPosition(mViewModelHomeFragment.getCustomerWishList().getValue().size() - 1);
                }
            }
        });
    }

    private View.OnClickListener addMoreWishListListener(final List<HashMap<String, String>> body){
       return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userIsSignedIn()){
                    addMoreWishList(body);
                } else {
                    showSignIn();
                }

            }
        };
    }


    private boolean userIsSignedIn(){
        return ViewModelHomeFragment.userEntity != null && !ViewModelHomeFragment.userEntity.getCustomerId().isEmpty();
    }

    private void showSignIn(){
        // load signInFragment
        SignInFragment signInFragment = new SignInFragment();
        MainActivity.mHomeTrack = MainActivity.HomeTrack.PRODUCTS;
        loadProductsFragment(signInFragment);
    }

    private void loadProductsFragment(Fragment fragment){
        removePreviousProductsFameLayoutData();

        if(fragment != null && getActivity() != null){
            findWishListFrameLayout().setVisibility(View.GONE);
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



    private void navigationBadge(){
        if (getActivity() != null){

            BottomNavigationView navigationView = getActivity().findViewById(R.id.navigation);
            BottomNavigationMenuView bottomNavigationMenuView = (BottomNavigationMenuView) navigationView.getChildAt(0);
            View v = bottomNavigationMenuView.getChildAt(1);
            BottomNavigationItemView itemView = (BottomNavigationItemView) v;

            View badge = LayoutInflater.from(getContext())
                    .inflate(R.layout.nav_count_num, bottomNavigationMenuView, false);
            TextView tv = badge.findViewById(R.id.notification_badge);

            View badge1 = LayoutInflater.from(getContext())
                    .inflate(R.layout.nav_count_num1, bottomNavigationMenuView, false);
            TextView tv1 = badge1.findViewById(R.id.notification_badge1);

            String countWish = wishSum.get("count");
            if(countWish != null){
                int count = Integer.parseInt(countWish);

                if(count > 0){
                    tv.setText(countWish);
                    itemView.addView(badge);
                }
                if(count <= 0){
                    tv1.setText("");
                    itemView.addView(badge1);
                }

            }

        }

    }



    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: wishFragment started id is " + mCustomerId);
        wishListFAB.setOnClickListener(addMoreWishListListener(cusWishObj()));
//        ViewModelHomeFragment.wishListIncrease = 1;
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: called");
        wishListFAB.setOnClickListener(null);
//        ViewModelHomeFragment.wishListIncrease = 1;
    }


    @Override
    public void productDetail(Product product) {
        MainActivity.mWishListTrack = MainActivity.WishListTrack.WISH_LIST_DETAILS;
        findWishListFrameLayout().setVisibility(View.GONE);
        findWishListDetailsFrameLayout().setVisibility(View.VISIBLE);
        // set bundle
        Bundle arguments = new Bundle();
        arguments.putSerializable(Product.class.getSimpleName(), product);
        arguments.putString("instance", "WISHLIST");

        ProductDetailsFragment productDetailsFragment = new ProductDetailsFragment();
        productDetailsFragment.setArguments(arguments);
        loadWishListDetailsFragment(productDetailsFragment);
    }

    @Override
    public void removeWishList(final Product product) {
        confirmRemovingWishList(product);
    }

    private void confirmRemovingWishList(final Product product){
        if(getContext() != null){
            MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(getContext())
                    .setTitle("Delete item ?")
                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            removeWishListConfirmed(product);
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

    private void removeWishListConfirmed(final Product product){
        showProgressDialog();
        Toast.makeText(getContext(), "remove", Toast.LENGTH_SHORT).show();
        HashMap<String, String> wishRemove = new HashMap<>();
        wishRemove.put("productId", product.get_id());
        wishRemove.put("customerId", mCustomerId);

        mViewModelHomeFragment.deleteCustomerWishList(wishRemove).observe(this, new Observer<OutLaw>() {
            @Override
            public void onChanged(OutLaw outLaw) {
                mViewModelHomeFragment.removeFromWishList(product);
                dismissProgressDialog();
            }
        });
    }

    private void showProgressBar(){
        if(getActivity() != null){
            findProgressBar().setVisibility(View.VISIBLE);
        }
    }

    private void hideProgressBar(){
        if(getActivity() != null){
            findProgressBar().setVisibility(View.GONE);
        }
    }

    private void showProgressDialog(){
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setTitle("Removing from wish list");
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

    private void loadWishListDetailsFragment(Fragment fragment){
        removePreviousWishListDetailsFameLayoutData();
        if(fragment != null && getActivity() != null){
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
            transaction.replace(R.id.containerWishListDetails, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    private void removePreviousWishListDetailsFameLayoutData(){
        if(getActivity() != null){
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentById(R.id.containerWishListDetails);
            if(fragment != null){
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .remove(fragment)
                        .commit();
            }
        }
    }

    private View findWishListScrollView(){ return getActivity().findViewById(R.id.scrollWishList); }
    private View findWishListFrameLayout(){ return getActivity().findViewById(R.id.containerWishList); }
    private View findWishListDetailsFrameLayout(){ return getActivity().findViewById(R.id.containerWishListDetails); }
    private ProgressBar findProgressBar(){ return getActivity().findViewById(R.id.indeterminateWishList); }
    private View findProductFrameLayout(){return getActivity().findViewById(R.id.containerProducts); }

    private View findProductDetailsFrameLayout(){ return getActivity().findViewById(R.id.containerProductDetails); }



}
