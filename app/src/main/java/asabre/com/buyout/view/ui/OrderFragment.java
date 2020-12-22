package asabre.com.buyout.view.ui;

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
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

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
import asabre.com.buyout.service.model.Order;
import asabre.com.buyout.view.adapter.OrderAdapter;
import asabre.com.buyout.view.callback.OrderCallback;
import asabre.com.buyout.viewmodel.ViewModelHomeFragment;

public class OrderFragment extends Fragment implements OrderCallback {
    private static final String TAG = OrderFragment.class.getSimpleName();

    private TextView ordersTopWord;
    private RecyclerView mRecyclerViewOrders;
    private ExtendedFloatingActionButton ordersFAB;
    private ViewModelHomeFragment mViewModelHomeFragment;
    private OrderAdapter mOrderAdapter;

    private HashMap<String, String> orderSum = new HashMap<>();

    String mCustomerId = ViewModelHomeFragment.userEntity == null ?
            "" : ViewModelHomeFragment.userEntity.getCustomerId();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        Log.d(TAG, "onCreateView: orderFragment onCreateView called");
        init(view);
        setOrderViewModel();
        return view;
    }

    private void init(View view){
        ordersTopWord = view.findViewById(R.id.ordersTopWord);
        ordersFAB = view.findViewById(R.id.ordersFAB);

        // for orders
        mOrderAdapter = new OrderAdapter(getContext(), new ArrayList<Order>(), this);
        mRecyclerViewOrders = view.findViewById(R.id.holderOrders);
        mRecyclerViewOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerViewOrders.setAdapter(mOrderAdapter);
    }

    private List<HashMap<String, String>> cusOrderObj(){

        HashMap<String, String> orderObj = new HashMap<>();
        orderObj.put("sub", "customer");
        orderObj.put("subId", mCustomerId);
        List<HashMap<String, String>> list = new ArrayList<>();
        list.add(orderObj);
        Log.d(TAG, "cusOrderObj: the customerId is " +  mCustomerId);
        return list;
    }

    private void setOrderViewModel(){
        if(getActivity() != null){
            mViewModelHomeFragment = ViewModelProviders.of(getActivity()).get(ViewModelHomeFragment.class);
            mViewModelHomeFragment.init();
            mViewModelHomeFragment.initOrders(cusOrderObj());

            mViewModelHomeFragment.getCustomerOrders().observe(this, new Observer<List<Order>>() {
                @Override
                public void onChanged(List<Order> list) {
                    Log.d(TAG, "onChanged: observing orders");
                    orderSum = mOrderAdapter.loadNewData(list);
                    navigationBadge();
                }
            });
        }
    }


    private void navigationBadge(){
        if (getActivity() != null){

            BottomNavigationView navigationView = getActivity().findViewById(R.id.navigation);
            BottomNavigationMenuView bottomNavigationMenuView = (BottomNavigationMenuView) navigationView.getChildAt(0);
            View v = bottomNavigationMenuView.getChildAt(3);
            BottomNavigationItemView itemView = (BottomNavigationItemView) v;

            View badge = LayoutInflater.from(getContext())
                    .inflate(R.layout.nav_count_num, bottomNavigationMenuView, false);
            TextView tv = badge.findViewById(R.id.notification_badge);

            View badge1 = LayoutInflater.from(getContext())
                    .inflate(R.layout.nav_count_num1, bottomNavigationMenuView, false);
            TextView tv1 = badge1.findViewById(R.id.notification_badge1);

            String countWish = orderSum.get("count");
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
    public void orderDetails(Order order) {
        ViewModelHomeFragment.orderDetails = order;
//        loadOrderDetailsDialog();
        loadOrderDetailsFragment();
    }

    @Override
    public void leaveReview(String productId, String customerId) {
        ViewModelHomeFragment.productReviewId = productId;
//        loadProductRatingDialog();
        loadProductRatingFragment();
    }

    private void loadProductRatingDialog(){
        FragmentManager fm = getFragmentManager();
        ProductRatingDialog pd = ProductRatingDialog.getInstance();
        pd.setTargetFragment(OrderFragment.this, 300);
        assert fm != null;
        pd.show(fm, "Product Rating Dialog");

    }

    private void loadOrderDetailsDialog(){
        FragmentManager fm = getFragmentManager();
        OrderDetailsDialog od = OrderDetailsDialog.getInstance();
        od.setTargetFragment(OrderFragment.this, 300);
        assert fm != null;
        od.show(fm, "OrderDetails Dialog");
    }

    private void loadOrderDetailsFragment(){
        OrderDetailsFragment orderDetailsFragment = new OrderDetailsFragment();
        if(getActivity() != null){
            getActivity().findViewById(R.id.containerOrder).setVisibility(View.GONE);
            getActivity().findViewById(R.id.containerOrderDetails).setVisibility(View.VISIBLE);
            MainActivity.mOrderTrack = MainActivity.OrderTrack.ORDER_DETAIL;

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
            transaction.replace(R.id.containerOrderDetails, orderDetailsFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    private void loadProductRatingFragment(){
        ProductRatingFragment productRatingFragment = new ProductRatingFragment();
        if(getActivity() != null){
            getActivity().findViewById(R.id.containerOrder).setVisibility(View.GONE);
            getActivity().findViewById(R.id.containerOrderDetails).setVisibility(View.VISIBLE);
            MainActivity.mOrderTrack = MainActivity.OrderTrack.ORDER_DETAIL;

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
            transaction.replace(R.id.containerOrderDetails, productRatingFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: orderFragment started");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: called");
    }

    private View findProgressBar(){ return Objects.requireNonNull(getActivity()).findViewById(R.id.indeterminateOrders);}
}
