package asabre.com.buyout.view.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import asabre.com.buyout.R;
import asabre.com.buyout.service.model.Order;
import asabre.com.buyout.service.model.OutLaw;
import asabre.com.buyout.service.model.ProductImage;
import asabre.com.buyout.view.adapter.ProductImageAdapter;
import asabre.com.buyout.view.callback.BaseFragment;
import asabre.com.buyout.view.callback.OrderDetailsCallback;
import asabre.com.buyout.viewmodel.ViewModelHomeFragment;

public class OrderDetailsFragment extends Fragment implements BaseFragment, OrderDetailsCallback {
    private static final String TAG = OrderDetailsFragment.class.getSimpleName();

    private TextView orderDetailsTopWord;
//    private RecyclerView holderOrderDetailsImages;
//    private TextView orderDetailsName;
    private TextView orderDetailsCustomerName;
    private TextView orderDetailsCustomerPhone;

    private TextView orderDetailsCustomerAddress;
    private TextView orderDetailsSize;
    private TextView orderDetailsQuantity;
    private TextView orderDetailsColor;
    private TextView orderDetailsStatus;

    private TextView orderDetailsDeliveryDate;
    private TextView orderDetailsOrderId;
    private TextView orderDetailsOrderValue;
    private ExtendedFloatingActionButton orderDetailsCloseFAB;
    private ExtendedFloatingActionButton orderDetailsDeleteOrderFAB;


    private ProductImageAdapter mProductImageAdapter;
    private ViewModelHomeFragment mViewModelHomeFragment;

    // show product detail count
    private LinearLayout mDotsLayout;
    static TextView[] mDotsText;

    Order mOrder = ViewModelHomeFragment.orderDetails;
    String PRODUCT_ID = mOrder.getProductId();
    private OrderDetailsCallback mOrderDetailsCallback = this;
    private ProgressDialog mProgressDialog;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_details, container, false);
        init(view);
        setOrderDetails();
        return view;
    }

    @Override
    public void deleteOrder(Order order) {
//        setOrderDeleteViewModel(order);

        confirmDeletingOrder(order);
    }

    private void confirmDeletingOrder(final Order order){
        if(getContext() != null){
            MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(getContext())
                    .setTitle("Delete order ?")
                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            removeOrderConfirmed(order);
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

    private void removeOrderConfirmed(Order order){
        setOrderDeleteViewModel(order);
    }

    private void init(View view){

        orderDetailsTopWord = view.findViewById(R.id.orderDetailsTopWord);
//        holderOrderDetailsImages = view.findViewById(R.id.holderOrderDetailsImages);
//        orderDetailsName = view.findViewById(R.id.orderDetailsName);
        orderDetailsCustomerName = view.findViewById(R.id.orderDetailsCustomerName);
        orderDetailsCustomerPhone = view.findViewById(R.id.orderDetailsCustomerPhone);

        orderDetailsCustomerAddress = view.findViewById(R.id.orderDetailsCustomerAddress);
        orderDetailsSize = view.findViewById(R.id.orderDetailsSize);
        orderDetailsQuantity = view.findViewById(R.id.orderDetailsQuantity);
        orderDetailsColor = view.findViewById(R.id.orderDetailsColor);
        orderDetailsStatus = view.findViewById(R.id.orderDetailsStatus);

        orderDetailsDeliveryDate = view.findViewById(R.id.orderDetailsDeliveryDate);
        orderDetailsOrderId = view.findViewById(R.id.orderDetailsOrderId);
        orderDetailsOrderValue = view.findViewById(R.id.orderDetailsOrderValue);
        orderDetailsCloseFAB = view.findViewById(R.id.orderDetailsCloseFAB);
        orderDetailsDeleteOrderFAB = view.findViewById(R.id.orderDetailsDeleteOrderFAB);

        setRecyclerView(view);
    }


    private void setRecyclerView(View view){
        mProductImageAdapter = new ProductImageAdapter(getContext(), new ArrayList<ProductImage>());
        RecyclerView holderOrderDetailsImages = view.findViewById(R.id.holderOrderDetailsImages);
        holderOrderDetailsImages.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        holderOrderDetailsImages.setAdapter(mProductImageAdapter);

        mDotsLayout = view.findViewById(R.id.orderDetailsImageCount);
        setDots();
    }

    private HashMap<String, String> delObj(){
        HashMap<String, String> obj = new HashMap<>();
        obj.put("_id", mOrder.get_id());
        return obj;
    }

    private void setOrderDeleteViewModel(final Order order){
        showProgressDialog();
        mViewModelHomeFragment = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(ViewModelHomeFragment.class);
        mViewModelHomeFragment.init();
        mViewModelHomeFragment.deleteCustomerOrder(delObj()).observe(this, new Observer<OutLaw>() {
            @Override
            public void onChanged(OutLaw outLaw) {
                dismissProgressDialog();
                mViewModelHomeFragment.removeFromOrder(order);
                setVisibility();
            }
        });
    }

    private void setOrderDetails(){
        ArrayList<ProductImage> photoList = new ArrayList<>();
        for(String str : mOrder.getImages()){
            ProductImage pi = new ProductImage(str);
            photoList.add(pi);
        }
        mProductImageAdapter.loadNewData(photoList);
        setTextViews();
    }

    private void setTextViews(){
        orderDetailsTopWord.setText(mOrder.getProductName());
//        orderDetailsName.setText(mOrder.getProductName());
        orderDetailsCustomerName.setText(mOrder.getCustomerName());
        orderDetailsCustomerPhone.setText(mOrder.getCustomerPhoneNumber());
        orderDetailsCustomerAddress.setText(mOrder.getCustomerLocation());

        orderDetailsSize.setText(mOrder.getSize());
        orderDetailsQuantity.setText(mOrder.getQuantity());
        orderDetailsColor.setText(mOrder.getColor());
        orderDetailsStatus.setText(mOrder.getStatus());

        orderDetailsDeliveryDate.setText(mOrder.getDelivery());
        String orderId = mOrder.get_id();
        orderDetailsOrderId.setText(String.format(Locale.US, "%s", orderId.substring(orderId.length() - 6)));
        orderDetailsOrderValue.setText(String.format(Locale.US, "GH¢ %.2f", Double.parseDouble(mOrder.getOrderValue())));

    }

    private void setDots(){
        mDotsText = new TextView[3];  // set dots
        for(int z = 0; z< 3; z++){    // here we get the dots
            mDotsText[z] = new TextView(getContext());
            mDotsText[z].setText(". ");
            mDotsText[z].setTextSize(35);
            mDotsText[z].setTypeface(null, Typeface.BOLD);
            mDotsText[z].setTextColor(Color.parseColor("#909090"));
            mDotsLayout.addView(mDotsText[z]);
        }
    }

    private void showProgressDialog(){
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setTitle("Deleting order");
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



    private View.OnClickListener deleteOrderListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOrderDetailsCallback.deleteOrder(mOrder);
            }
        };
    }

    private void setVisibility(){
        if(MainActivity.mOrderTrack == MainActivity.OrderTrack.ORDER_DETAIL){
            MainActivity.mOrderTrack = MainActivity.OrderTrack.ORDER;
            if (getActivity() != null) {
                getActivity().findViewById(R.id.containerOrderDetails).setVisibility(View.GONE);
                getActivity().findViewById(R.id.containerOrder).setVisibility(View.VISIBLE);
                removeThisFragment();
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

    private void removeThisFragment(){
        if(getActivity() != null){
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentById(R.id.containerOrderDetails);
            if(fragment != null){
                getActivity().getSupportFragmentManager().beginTransaction()
                        .remove(fragment)
                        .commit();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        orderDetailsDeleteOrderFAB.setOnClickListener(deleteOrderListener());
        orderDetailsCloseFAB.setOnClickListener(goBackListener());
        orderDetailsTopWord.setOnClickListener(goBackListener());
    }

    @Override
    public void onStop() {
        super.onStop();
        orderDetailsCloseFAB.setOnClickListener(null);
        orderDetailsDeleteOrderFAB.setOnLongClickListener(null);
    }

    @Override
    public void onBackPressed() {
        if(MainActivity.mTrackMain == MainActivity.TrackMain.ORDER){
            setVisibility();
        }
    }
}
