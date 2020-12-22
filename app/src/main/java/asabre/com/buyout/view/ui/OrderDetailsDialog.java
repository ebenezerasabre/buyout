package asabre.com.buyout.view.ui;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import asabre.com.buyout.R;
import asabre.com.buyout.service.model.Order;
import asabre.com.buyout.service.model.OutLaw;
import asabre.com.buyout.service.model.ProductImage;
import asabre.com.buyout.view.adapter.ProductImageAdapter;
import asabre.com.buyout.view.callback.OrderDetailsCallback;
import asabre.com.buyout.viewmodel.ViewModelHomeFragment;

public class OrderDetailsDialog extends DialogFragment implements OrderDetailsCallback {
    private static final String TAG = OrderDetailsDialog.class.getSimpleName();

    private TextView orderDetailsTopWord;
    private TextView orderDetailsName;
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
    private RecyclerView mHolderOrderDetailsImages;
    private ViewModelHomeFragment mViewModelHomeFragment;

    // show product detail count
    private LinearLayout mDotsLayout;
    static TextView mDotsText[];

    Order mOrder = ViewModelHomeFragment.orderDetails;
    String PRODUCT_ID = mOrder.getProductId();
    private OrderDetailsCallback mOrderDetailsCallback = this;
    private ProgressDialog mProgressDialog;

    public static OrderDetailsDialog getInstance(){
        return new OrderDetailsDialog();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        setOrderDetails();
    }

    // from orderDetailsCallback
    @Override
    public void deleteOrder(Order order) {
        setOrderViewModel(order);
    }

    private void init(View view){
        orderDetailsTopWord = view.findViewById(R.id.orderDetailsTopWord);
        orderDetailsName = view.findViewById(R.id.orderDetailsName);
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
        mHolderOrderDetailsImages = view.findViewById(R.id.holderOrderDetailsImages);
        mHolderOrderDetailsImages.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mHolderOrderDetailsImages.setAdapter(mProductImageAdapter);

        mDotsLayout = view.findViewById(R.id.orderDetailsImageCount);
        setDots();
    }

    private HashMap<String, String> delObj(){
        HashMap<String, String> obj = new HashMap<>();
        obj.put("_id", mOrder.get_id());
        return obj;
    }

    private void setOrderViewModel(final Order order){
        showProgressDialog();
        mViewModelHomeFragment = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(ViewModelHomeFragment.class);
        mViewModelHomeFragment.init();
        mViewModelHomeFragment.deleteCustomerOrder(delObj()).observe(this, new Observer<OutLaw>() {
            @Override
            public void onChanged(OutLaw outLaw) {
                dismissProgressDialog();
                mViewModelHomeFragment.removeFromOrder(order);
                dismiss();
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
        orderDetailsName.setText(mOrder.getProductName());
        orderDetailsCustomerName.setText(mOrder.getCustomerName());
        orderDetailsCustomerPhone.setText(mOrder.getCustomerPhoneNumber());
        orderDetailsCustomerAddress.setText(mOrder.getCustomerLocation());

        orderDetailsSize.setText(mOrder.getSize());
        orderDetailsQuantity.setText(mOrder.getQuantity());
        orderDetailsColor.setText(mOrder.getColor());
        orderDetailsStatus.setText(mOrder.getStatus());

        orderDetailsDeliveryDate.setText(mOrder.getDelivery());
        orderDetailsOrderId.setText(mOrder.get_id());
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

    @Override
    public void onResume() {
//         Full screen dialog
        // Get existing layout params for the window
        WindowManager.LayoutParams params = Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).getAttributes();
        // Assign window properties to fill the parent
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        Objects.requireNonNull(getDialog().getWindow()).setAttributes(params);

        super.onResume();
    }

    private View.OnClickListener deleteOrderListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOrderDetailsCallback.deleteOrder(mOrder);
            }
        };
    }

    private View.OnClickListener closeOrderListener(){
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
        orderDetailsDeleteOrderFAB.setOnClickListener(deleteOrderListener());
        orderDetailsCloseFAB.setOnClickListener(closeOrderListener());
        orderDetailsTopWord.setOnClickListener(closeOrderListener());
    }

    @Override
    public void onStop() {
        super.onStop();
        orderDetailsDeleteOrderFAB.setOnLongClickListener(null);
        orderDetailsCloseFAB.setOnClickListener(null);

    }
}
