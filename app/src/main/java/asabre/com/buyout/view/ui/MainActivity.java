package asabre.com.buyout.view.ui;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import asabre.com.buyout.R;
import asabre.com.buyout.service.model.Customer;
import asabre.com.buyout.service.model.UserEntity;
import asabre.com.buyout.service.repository.DatabaseClient;
import asabre.com.buyout.view.callback.BaseFragment;
import asabre.com.buyout.viewmodel.ViewModelHomeFragment;

public class MainActivity extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    public static boolean allGranted;

    // tracking
    public enum TrackMain{HOME,WISHLIST,CART,ORDER,PROFILE}
    public static TrackMain mTrackMain = TrackMain.HOME;

    // tracking home
    public enum HomeTrack{HOME,PRODUCTS,PRODUCT_DETAILS,PRODUCT_REVIEW,EXTRA}
    public static HomeTrack mHomeTrack = HomeTrack.HOME;

    // true if productDetails is called from homePage
    // false if productDetails is called from productsFragment


    // tracking wishList
    public enum WishListTrack{WISH_LIST,WISH_LIST_DETAILS,EXTRA}
    public static WishListTrack mWishListTrack = WishListTrack.WISH_LIST;

    // tracking cart
    public enum CartTrack{CART,CART_DETAILS}
    public static CartTrack mCartTrack = CartTrack.CART;

    // tracking orders
    public enum OrderTrack{ORDER,ORDER_DETAIL}
    public static OrderTrack mOrderTrack = OrderTrack.ORDER;

    // tracking profile
    public enum ProfileTrack{PROFILE,PROFILE_DETAIL}
    public static ProfileTrack mProfileTrack = ProfileTrack.PROFILE;

    public static boolean stepOne = false;// true if home-productDetails else false
    public enum Steps{STEP_ONE,STEP_TWO,STEP_THREE}
    public static Steps mStepsTracking;
    // STEP_ONE from home to productDetails
    // STEP_TWO from home, products, productDetails
    // STEP_ONE from home, All categories, products, productDetails

    // track which see all was clicked
    public static String seeAll = "";
//    public static String searchHint = "";
    public static HashMap<String, String> search = new HashMap<>();
    public static String searchRef = "";
    public static String searchOther = "";

//    public static String nestFirstOrder = "";

    // save customer's data
//    public static UserEntity customerEntity = null;
    public static Customer customer;
    BottomNavigationView navigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called");
        setContentView(R.layout.activity_main);
        allGranted =  checkAndRequestPermissions();
        setBottomNavigation();
        loadFragments();
        restoreCurrentFragment(savedInstanceState);
    }

    private void restoreCurrentFragment(Bundle savedInstanceState){
        if(savedInstanceState != null){
            String mainString = savedInstanceState.getString("mainTrack");
            String homeString = savedInstanceState.getString("homeTrack");
            String mainTrack = null != mainString ? mainString : "";
            String homeTrack = null != homeString ? homeString : "";
            checkHomeTrack(homeTrack);
            checkMainTrack(mainTrack);
        }
    }

    private void checkMainTrack(String mainTrack){
        switch (mainTrack){
            case "HOME": mTrackMain = TrackMain.HOME; setHome(); break;
            case "WISHLIST": mTrackMain = TrackMain.WISHLIST; setWishList();break;
            case "CART": mTrackMain = TrackMain.CART; setCart();break;
            case "ORDER": mTrackMain = TrackMain.ORDER; setOrder();break;
            case "PROFILE": mTrackMain = TrackMain.PROFILE; setProfile();break;
            default:
                // do nothing
                break;
        }
    }

    private void checkHomeTrack(String homeTrack){
        switch (homeTrack){
            case "HOME": mHomeTrack = HomeTrack.HOME; break;
            case "PRODUCTS": mHomeTrack = HomeTrack.PRODUCTS; break;
            case "PRODUCT_DETAILS": mHomeTrack = HomeTrack.PRODUCT_DETAILS; break;
            default:
                // do nothing
                break;
        }
        setHome();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: called");
        Log.d(TAG, "onSaveInstanceState: the home tracking was " + mHomeTrack);

        outState.putString("homeTrack", String.valueOf(mHomeTrack));
        outState.putString("mainTrack", String.valueOf(mTrackMain));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
       return seVisibility(item);
    }

    private void setBottomNavigation(){
         navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(this);
    }

    private boolean seVisibility(MenuItem menuItem){
        switch (menuItem.getItemId()){
            case R.id.navigation_home: setHome(); break;
            case R.id.navigation_wishList: setWishList(); break;
            case R.id.navigation_cart: setCart(); break;
            case R.id.navigation_order: setOrder(); break;
            case R.id.navigation_profile: setProfile(); break;
            default: break; // do nothing
        }
        return true;
    }

// aniuonyam a 3ni abakos3m no 3yaa 3ni nyinaso


    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: called");
        Log.d(TAG, "onStop: tracking home " + mHomeTrack);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: called");
        Log.d(TAG, "onDestroy: tracking home " + mHomeTrack);
    }

    @Override
    public void onBackPressed() {
        if(mTrackMain == TrackMain.HOME && mHomeTrack == HomeTrack.HOME){
            super.onBackPressed();
        }
        TellFragmentsBackIsPressed();
    }

    private void TellFragmentsBackIsPressed(){
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for(Fragment f : fragments){
            if(f instanceof BaseFragment){
                ((BaseFragment)f).onBackPressed();
            }
        }
    }

    private void loadFragments(){
        loadHomeFragment();
        // other fragments are loaded from homeFragment
    }
    private void loadHomeFragment(){
        HomeFragment homeFragment = new HomeFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.containerHomeFragment, homeFragment)
                .commit();
    }

    // set visibility
    private void setHome(){
        Log.d(TAG, "setHome: " + mHomeTrack);
        mTrackMain = TrackMain.HOME;
        switch (mHomeTrack){
            case HOME: setHomeVisible(); break;
            case PRODUCTS: setProductsVisible(); break;
            case PRODUCT_DETAILS: setProductDetailsVisible(); break;
            case PRODUCT_REVIEW: setProductReviewVisible(); break;
            case EXTRA: setHomeExtra();break;
            default: break;
        }
    }
    private void setWishList(){
        Log.d(TAG, "setWishList: " + mWishListTrack);
        mTrackMain = TrackMain.WISHLIST;
        switch (mWishListTrack){
            case WISH_LIST: setWishListVisible(); break;
            case WISH_LIST_DETAILS: setWishListDetailsVisible(); break;
            case EXTRA: setWishListExtra();break;
            default: break;
        }
    }
    private void setCart(){
        Log.d(TAG, "setCart: " + mCartTrack);
        mTrackMain = TrackMain.CART;
        switch (mCartTrack){
            case CART: setCartVisible(); break;
            case CART_DETAILS: setCartDetailsVisible(); break;
            default: break;
        }
    }
    private void setOrder(){
        Log.d(TAG, "setOrder: " + mOrderTrack);
        mTrackMain = TrackMain.ORDER;
        switch (mOrderTrack){
            case ORDER: setOrderVisible(); break;
            case ORDER_DETAIL: setOrderDetailsVisible(); break;
            default: break;
        }
    }
    private void setProfile(){
        Log.d(TAG, "setProfile: " + mProfileTrack);
        mTrackMain = TrackMain.PROFILE;
        switch (mProfileTrack){
            case PROFILE: setProfileVisible(); break;
            case PROFILE_DETAIL: setProfileDetailsVisible(); break;
            default: break;
        }
    }

    private void setProfileDetailsVisible(){
        setVisibility(findProfileDetailsFrameLayout(),
                findCartFrameLayout(),
                findWishListDetailsFrameLayout(),
                findWishListFrameLayout(),
                findProductReviewFrameLayout(),
                findProductDetailsFrameLayout(),
                findProductsFrameLayout(),
                findHomeFrameLayout(),
                findCartDetailsFrameLayout(),
                findOrderFrameLayout(),
                findOrderDetailsFrameLayout(),
                findProfileFrameLayout(),
                findHomeExtra(),
                findWishListExtra());
    }

    private void setProfileVisible(){
        setVisibility(findProfileFrameLayout(),
                findCartFrameLayout(),
                findWishListDetailsFrameLayout(),
                findWishListFrameLayout(),
                findProductReviewFrameLayout(),
                findProductDetailsFrameLayout(),
                findProductsFrameLayout(),
                findHomeFrameLayout(),
                findCartDetailsFrameLayout(),
                findOrderFrameLayout(),
                findOrderDetailsFrameLayout(),
                findProfileDetailsFrameLayout(),
                findHomeExtra(),
                findWishListExtra());
    }

    private void setOrderDetailsVisible(){
        setVisibility(findOrderDetailsFrameLayout(),
                findCartFrameLayout(),
                findWishListDetailsFrameLayout(),
                findWishListFrameLayout(),
                findProductReviewFrameLayout(),
                findProductDetailsFrameLayout(),
                findProductsFrameLayout(),
                findHomeFrameLayout(),
                findCartDetailsFrameLayout(),
                findOrderFrameLayout(),
                findProfileFrameLayout(),
                findProfileDetailsFrameLayout(),
                findHomeExtra(),
                findWishListExtra());
    }

    private void setOrderVisible(){
        setVisibility(findOrderFrameLayout(),
                findCartFrameLayout(),
                findWishListDetailsFrameLayout(),
                findWishListFrameLayout(),
                findProductReviewFrameLayout(),
                findProductDetailsFrameLayout(),
                findProductsFrameLayout(),
                findHomeFrameLayout(),
                findCartDetailsFrameLayout(),
                findOrderDetailsFrameLayout(),
                findProfileFrameLayout(),
                findProfileDetailsFrameLayout(),
                findHomeExtra(),
                findWishListExtra());
    }

    private void setCartDetailsVisible(){
        setVisibility(findCartDetailsFrameLayout(),
                findCartFrameLayout(),
                findWishListDetailsFrameLayout(),
                findWishListFrameLayout(),
                findProductReviewFrameLayout(),
                findProductDetailsFrameLayout(),
                findProductsFrameLayout(),
                findHomeFrameLayout(),
                findOrderFrameLayout(),
                findOrderDetailsFrameLayout(),
                findProfileFrameLayout(),
                findProfileDetailsFrameLayout(),
                findHomeExtra(),
                findWishListExtra());
    }

    private void setCartVisible(){
        setVisibility(findCartFrameLayout(),
                findWishListDetailsFrameLayout(),
                findWishListFrameLayout(),
                findProductReviewFrameLayout(),
                findProductDetailsFrameLayout(),
                findProductsFrameLayout(),
                findHomeFrameLayout(),
                findCartDetailsFrameLayout(),
                findOrderFrameLayout(),
                findOrderDetailsFrameLayout(),
                findProfileFrameLayout(),
                findProfileDetailsFrameLayout(),
                findHomeExtra(),
                findWishListExtra());
    }

    private void setWishListExtra(){
        setVisibility(findWishListExtra(),
                findHomeExtra(),
                findCartFrameLayout(),
                findWishListDetailsFrameLayout(),
                findWishListFrameLayout(),
                findProductReviewFrameLayout(),
                findProductDetailsFrameLayout(),
                findProductsFrameLayout(),
                findHomeFrameLayout(),
                findCartDetailsFrameLayout(),
                findOrderFrameLayout(),
                findOrderDetailsFrameLayout(),
                findProfileFrameLayout(),
                findProfileDetailsFrameLayout());
    }

    private void setWishListDetailsVisible(){
        setVisibility(findWishListDetailsFrameLayout(),
                findWishListFrameLayout(),
                findProductReviewFrameLayout(),
                findProductDetailsFrameLayout(),
                findProductsFrameLayout(),
                findHomeFrameLayout(),
                findCartFrameLayout(),
                findCartDetailsFrameLayout(),
                findOrderFrameLayout(),
                findOrderDetailsFrameLayout(),
                findProfileFrameLayout(),
                findProfileDetailsFrameLayout(),
                findHomeExtra(),
                findWishListExtra());
    }

    private void setWishListVisible(){
        setVisibility(findWishListFrameLayout(),
                findProductReviewFrameLayout(),
                findProductDetailsFrameLayout(),
                findProductsFrameLayout(),
                findHomeFrameLayout(),
                findWishListDetailsFrameLayout(),
                findCartFrameLayout(),
                findCartDetailsFrameLayout(),
                findOrderFrameLayout(),
                findOrderDetailsFrameLayout(),
                findProfileFrameLayout(),
                findProfileDetailsFrameLayout(),
                findHomeExtra(),
                findWishListExtra());
    }

    private void setHomeExtra(){
        setVisibility(findHomeExtra(),
                findWishListExtra(),
                findCartFrameLayout(),
                findWishListDetailsFrameLayout(),
                findWishListFrameLayout(),
                findProductReviewFrameLayout(),
                findProductDetailsFrameLayout(),
                findProductsFrameLayout(),
                findHomeFrameLayout(),
                findCartDetailsFrameLayout(),
                findOrderFrameLayout(),
                findOrderDetailsFrameLayout(),
                findProfileFrameLayout(),
                findProfileDetailsFrameLayout());
    }

    private void setProductReviewVisible(){
        setVisibility(findProductReviewFrameLayout(),
                findProductDetailsFrameLayout(),
                findProductsFrameLayout(),
                findHomeFrameLayout(),
                findWishListFrameLayout(),
                findWishListDetailsFrameLayout(),
                findCartFrameLayout(),
                findCartDetailsFrameLayout(),
                findOrderFrameLayout(),
                findOrderDetailsFrameLayout(),
                findProfileFrameLayout(),
                findProfileDetailsFrameLayout(),
                findHomeExtra(),
                findWishListExtra());
    }

    private void setProductDetailsVisible(){
        setVisibility(findProductDetailsFrameLayout(),
                findProductsFrameLayout(),
                findHomeFrameLayout(),
                findProductReviewFrameLayout(),
                findWishListFrameLayout(),
                findWishListDetailsFrameLayout(),
                findCartFrameLayout(),
                findCartDetailsFrameLayout(),
                findOrderFrameLayout(),
                findOrderDetailsFrameLayout(),
                findProfileFrameLayout(),
                findProfileDetailsFrameLayout(),
                findHomeExtra(),
                findWishListExtra());
    }

    private void setProductsVisible(){
        setVisibility(findProductsFrameLayout(),
                findHomeFrameLayout(),
                findProductDetailsFrameLayout(),
                findProductReviewFrameLayout(),
                findWishListFrameLayout(),
                findWishListDetailsFrameLayout(),
                findCartFrameLayout(),
                findCartDetailsFrameLayout(),
                findOrderFrameLayout(),
                findOrderDetailsFrameLayout(),
                findProfileFrameLayout(),
                findProfileDetailsFrameLayout(),
                findHomeExtra(),
                findWishListExtra());
    }

    private void setHomeVisible(){
        setVisibility(findHomeFrameLayout(),
                findProductsFrameLayout(),
                findProductDetailsFrameLayout(),
                findProductReviewFrameLayout(),
                findWishListFrameLayout(),
                findWishListDetailsFrameLayout(),
                findCartFrameLayout(),
                findCartDetailsFrameLayout(),
                findOrderFrameLayout(),
                findOrderDetailsFrameLayout(),
                findProfileFrameLayout(),
                findProfileDetailsFrameLayout(),
                findHomeExtra(),
                findWishListExtra());
    }

    private void setVisibility(View v1,View v2,View v3,View v4,View v5,View v6,
                               View v7,View v8,View v9,View v10,View v11,View v12,View v13,View v14) {
        View[] arr = {v1,v2,v3,v4,v5,v6, v7,v8,v9,v10,v11,v12, v13, v14};
        for(View v : arr){ v.setVisibility(View.GONE); }
        v1.setVisibility(View.VISIBLE);
    }

    public  View findHomeFrameLayout(){ return findViewById(R.id.containerHomeFragment);}
    private View findProductsFrameLayout(){ return findViewById(R.id.containerProducts);}
    private View findProductDetailsFrameLayout(){ return findViewById(R.id.containerProductDetails);}
    private View findProductReviewFrameLayout(){ return findViewById(R.id.containerProductReview);}
    private View findHomeExtra(){ return findViewById(R.id.containerHomeExtra); }

    private View findWishListFrameLayout(){ return findViewById(R.id.containerWishList);}
    private View findWishListDetailsFrameLayout(){ return findViewById(R.id.containerWishListDetails);}
    private View findWishListExtra(){ return findViewById(R.id.containerWishListExtra); }

    private View findCartFrameLayout(){ return findViewById(R.id.containerCart);}
    private View findCartDetailsFrameLayout(){ return findViewById(R.id.containerCartDetails);}

    private View findOrderFrameLayout(){ return findViewById(R.id.containerOrder);}
    private View findOrderDetailsFrameLayout(){ return findViewById(R.id.containerOrderDetails);}

    private View findProfileFrameLayout(){ return findViewById(R.id.containerProfile);}
    private View findProfileDetailsFrameLayout(){ return findViewById(R.id.containerProfileDetails);}



    // request for all necessary permissions
    private boolean checkAndRequestPermissions(){
        int permissionFineLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCoarseLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionReadExternalStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionWriteExternalStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        int permissionForeGroundService = ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE);
//        int permissionNetorkState = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE);

        List<String> listPermissionsNeeded = new ArrayList<>();

        // check build version
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // only for gingerbread and newer versions
            int permissionForegroundService = ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE);
            if(permissionForegroundService != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.FOREGROUND_SERVICE);
            }
        }

        if(permissionFineLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if(permissionCoarseLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if(permissionReadExternalStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            ViewModelHomeFragment.mediaPermission.setValue(false);
        }

        if(permissionWriteExternalStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            ViewModelHomeFragment.mediaPermission.setValue(false);
        }

        // check build version
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // only for gingerbread and newer versions
            int permissionForegroundService = ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE);

            if(permissionForegroundService != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.FOREGROUND_SERVICE);
            }

        }


        if(!listPermissionsNeeded.isEmpty()){
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        ViewModelHomeFragment.mediaPermission.setValue(true);
        return true;
    }


}
