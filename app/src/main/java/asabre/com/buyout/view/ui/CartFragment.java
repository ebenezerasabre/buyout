package asabre.com.buyout.view.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import asabre.com.buyout.R;
import asabre.com.buyout.service.model.Cart;
import asabre.com.buyout.service.model.Product;
import asabre.com.buyout.view.callback.CartCallback;
import asabre.com.buyout.viewmodel.ViewModelHomeFragment;

//public class CartFragment extends Fragment implements CartAdapter.CartThumbs {
public class CartFragment extends Fragment implements CartCallback {
    private static final String TAG = CartFragment.class.getSimpleName();

    private AdapterCart mCartAdapter;
    private ExtendedFloatingActionButton mCartFAB;
    private ViewModelHomeFragment mViewModelHomeFragment;
    private RecyclerView mRecyclerViewCart;
    private TextView cartTopWord;
    TextView cartTotal;

    private HashMap<String, String> cartSum = new HashMap<>();
    private ProgressDialog mProgressDialog;


    String mCustomerId = ViewModelHomeFragment.userEntity == null ?
            "" : ViewModelHomeFragment.userEntity.getCustomerId();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        Log.d(TAG, "onCreateView: cart onCreateView called");
        init(view);
        setCartViewModel();
        return view;
    }

    private void init(View view){
        cartTopWord = view.findViewById(R.id.cartTopWord);
        mCartFAB = view.findViewById(R.id.cartFAB);
        cartTotal = view.findViewById(R.id.cartTotal);

        // for cart
        mCartAdapter = new AdapterCart(getContext(), new ArrayList<Cart>(), this);
        mRecyclerViewCart = view.findViewById(R.id.holderCart);
        mRecyclerViewCart.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerViewCart.setAdapter(mCartAdapter);

    }

    private List<HashMap<String, String>> cusCartObj(){

        HashMap<String, String> cartObj = new HashMap<>();
        cartObj.put("customerId", mCustomerId);
        List<HashMap<String, String>> list = new ArrayList<>();
        list.add(cartObj);
        Log.d(TAG, "cusCartObj: the customerId is " + mCustomerId);
        return list;
    }

    private List<HashMap<String, String>> cusIdForAddress(){
        HashMap<String, String> obj = new HashMap<>();
        obj.put("customerId", mCustomerId);
        List<HashMap<String, String>> list = new ArrayList<>();
        list.add(obj);
        return list;
    }

    private void setCartViewModel(){

            // initialize viewModel
            mViewModelHomeFragment = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(ViewModelHomeFragment.class);
            mViewModelHomeFragment.init();

            // get customer cart
            mViewModelHomeFragment.initCart(cusCartObj());
            mViewModelHomeFragment.getCustomerCart().observe(this, new Observer<List<Cart>>() {
                @Override
                public void onChanged(List<Cart> carts) {
                    Log.d(TAG, "onChanged: observing cart");
                   cartSum = mCartAdapter.loadNewData(carts);
                   cartTotal.setText(String.format(Locale.US, "GH¢ %s", cartSum.get("value")) );
                   navigationBadge();
                }
            });

            // load customer address but observe in LocationDialog
            mViewModelHomeFragment.initCustomerAddress(cusIdForAddress());

    }



    private void navigationBadge(){
        if (getActivity() != null){
            BottomNavigationView navigationView = getActivity().findViewById(R.id.navigation);
            BottomNavigationMenuView bottomNavigationMenuView = (BottomNavigationMenuView) navigationView.getChildAt(0);
            View v = bottomNavigationMenuView.getChildAt(2);
            BottomNavigationItemView itemView = (BottomNavigationItemView) v;

            View badge = LayoutInflater.from(getContext())
                    .inflate(R.layout.nav_count_num, bottomNavigationMenuView, false);
            TextView tv = badge.findViewById(R.id.notification_badge);

            View badge1 = LayoutInflater.from(getContext())
                    .inflate(R.layout.nav_count_num1, bottomNavigationMenuView, false);
            TextView tv1 = badge1.findViewById(R.id.notification_badge1);

            String countWish = cartSum.get("count");
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
    public void productDetail(Cart cart) {
        ViewModelHomeFragment.cartDetails = cart;
//        loadCartDetailDialog();
        loadCartDetailsFragment();
    }

    private void loadCartDetailDialog(){
        FragmentManager fm = getFragmentManager();
        CartExposeDialog cd = CartExposeDialog.getInstance();
        cd.setTargetFragment(CartFragment.this, 300);
        assert fm != null;
        cd.show(fm, "Cart Details dialog");
    }

    private void loadCartDetailsFragment(){
        removePreviousCartDetailsData();
        CartDetailsFragment cartDetailsFragment = new CartDetailsFragment();
        if(getActivity() != null){
            getActivity().findViewById(R.id.containerCart).setVisibility(View.GONE);
            getActivity().findViewById(R.id.containerCartDetails).setVisibility(View.VISIBLE);
            MainActivity.mCartTrack = MainActivity.CartTrack.CART_DETAILS;

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
            transaction.replace(R.id.containerCartDetails, cartDetailsFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    private void removePreviousCartDetailsData(){
        if(getActivity() != null){
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentById(R.id.containerCartDetails);
            if(fragment != null){
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .remove(fragment)
                        .commit();
            }
        }
    }

    @Override
    public void removeCart(final Cart cart) {
       confirmRemovingCart(cart);
    }


    private void confirmRemovingCart(final Cart cart){
        if(getContext() != null){
            MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(getContext())
                    .setTitle("Delete item ?")
                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            removeCartConfirmed(cart);
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

    private void removeCartConfirmed(final Cart cart){
        showProgressDialog();
        Toast.makeText(getContext(), "remove", Toast.LENGTH_SHORT).show();

        HashMap<String, String> cartRemove = new HashMap<>();
        cartRemove.put("customerId", mCustomerId);
        cartRemove.put("_id", cart.get_id());
        List<HashMap<String, String>> list = new ArrayList<>();
        list.add(cartRemove);

        Log.d(TAG, "removeCart: customerId " + mCustomerId);
        Log.d(TAG, "removeCart: _id " + cart.get_id());

        mViewModelHomeFragment.deleteCustomerCart(list).observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                Log.d(TAG, "onChanged: deleting cart msg from server " + strings);
                mViewModelHomeFragment.removeFromCart(cart);
                dismissProgressDialog();
            }
        });
    }


    @Override
    public void increaseQuantity(Cart cart, AdapterCart.ViewHolder viewHolder) {
        cartQuantityIncrease(cart, viewHolder);
    }

    @Override
    public void decreaseQuantity(Cart cart, AdapterCart.ViewHolder viewHolder) {
        cartQuantityDecrease(cart, viewHolder);
    }


    private void thumbs(String cartId, String thumb){

        HashMap<String, String> obj = new HashMap<>();
        obj.put("sub", thumb);
        obj.put("cartId", cartId);
        obj.put("customerId", mCustomerId);
        mViewModelHomeFragment.cartThumbs(obj);
    }

    private void cartQuantityIncrease(Cart cart, AdapterCart.ViewHolder viewHolder) {
        TextView cartQuantity = viewHolder.cart_price_multiply;
        TextView cartOrderValue = viewHolder.orderValue;

        int prevQuantity = cartQuantity.getText().toString().isEmpty() ? 0 : Integer.parseInt(cartQuantity.getText().toString());
        int newQuantity = prevQuantity + 1;
        cartQuantity.setText(String.valueOf(newQuantity));
        thumbs(cart.get_id(), "thumbsUp");

        // increase the cartPrice accordingly(client & api)
        String priceGH = cartOrderValue.getText().toString();
        Pattern p = Pattern.compile("\\d+\\.\\d+");
        Matcher m = p.matcher(priceGH);
        String sPrice = "";
        while (m.find()){ sPrice = m.group(); }

        double dPrice = Double.parseDouble(sPrice);
        double unitPrice = dPrice / prevQuantity ;
        String sUnitPrice = String.format(Locale.US, "GH¢ %.2f", unitPrice * newQuantity);
        cartOrderValue.setText(sUnitPrice);

        // increase the cart total accordingly
        String cartTotalValue = cartTotal.getText().toString();
        m = p.matcher(cartTotalValue);
        String tPrice = "";
        while (m.find()){ tPrice = m.group(); }

        double tdPrice = Double.parseDouble(tPrice);
        tdPrice -= dPrice;
        tdPrice += (unitPrice * newQuantity);
        cartTotal.setText(String.format(Locale.US, "GH¢ %.2f", tdPrice));

    }


    private void cartQuantityDecrease(Cart cart, AdapterCart.ViewHolder viewHolder) {
        TextView cartQuantity = viewHolder.cart_price_multiply;
        TextView cartOrderValue = viewHolder.orderValue;

        int prevQuantity = cartQuantity.getText().toString().isEmpty() ? 0 : Integer.parseInt(cartQuantity.getText().toString());
        if (prevQuantity > 1) {
            int newQuantity = prevQuantity - 1;
            cartQuantity.setText(String.valueOf(newQuantity));
            thumbs(cart.get_id(), "thumbsDown");

            // decrease the cartPrice accordingly(client & api)
            String priceGH = cartOrderValue.getText().toString();
            Pattern p = Pattern.compile("\\d+\\.\\d+");
            Matcher m = p.matcher(priceGH);
            String sPrice = "";
            while (m.find()) {
                sPrice = m.group();
            }

            double dPrice = Double.parseDouble(sPrice);
            double unitPrice = dPrice / prevQuantity;
            String sUnitPrice = String.format(Locale.US, "GH¢ %.2f", unitPrice * newQuantity);
            cartOrderValue.setText(sUnitPrice);


            // increase the cart total accordingly
            String cartTotalValue = cartTotal.getText().toString();
            m = p.matcher(cartTotalValue);
            String tPrice = "";
            while (m.find()){ tPrice = m.group(); }

            double tdPrice = Double.parseDouble(tPrice);
            tdPrice -= dPrice;
            tdPrice += (unitPrice * newQuantity);
            cartTotal.setText(String.format(Locale.US, "GH¢ %.2f", tdPrice));

        }
    }



    private View.OnClickListener addLocationListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!cartIsEmpty()){
//                   loadLocationDialog();
                    loadLocationFragment();
                } else {
                    Toast.makeText(getContext(), "Cart is empty",Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private boolean cartIsEmpty(){
        if(null != mViewModelHomeFragment.getCustomerCart().getValue()){
            return mViewModelHomeFragment.getCustomerCart().getValue().isEmpty();
        }
        return true;
    }

    private void loadLocationDialog(){
        FragmentManager fm = getFragmentManager();
        LocationDialog ld = LocationDialog.getInstance();
        ld.setTargetFragment(CartFragment.this, 300);
        assert fm != null;
        ld.show(fm, "LocationDialog");
    }

    private void loadLocationFragment(){
        LocationFragment locationFragment = new LocationFragment();
        if(getActivity() != null){
            getActivity().findViewById(R.id.containerCart).setVisibility(View.GONE);
            getActivity().findViewById(R.id.containerCartDetails).setVisibility(View.VISIBLE);
            MainActivity.mCartTrack = MainActivity.CartTrack.CART_DETAILS;

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
            transaction.replace(R.id.containerCartDetails, locationFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: cartFragment started");

        mCartFAB.setOnClickListener(addLocationListener());
    }


    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: called");
        mCartFAB.setOnClickListener(null);
    }

    private View findProgressBar(){ return Objects.requireNonNull(getActivity()).findViewById(R.id.indeterminateCart);}


    private void showProgressDialog(){
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setTitle("Removing from cart");
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
