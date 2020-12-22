package asabre.com.buyout.view.ui;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import asabre.com.buyout.R;
import asabre.com.buyout.service.model.Cart;
import asabre.com.buyout.service.model.ProductImage;
import asabre.com.buyout.view.adapter.ProductImageAdapter;
import asabre.com.buyout.view.callback.BaseFragment;
import asabre.com.buyout.viewmodel.ViewModelHomeFragment;

public class CartDetailsFragment extends Fragment implements BaseFragment {
    private static final String TAG = CartDetailsFragment.class.getSimpleName();


    private TextView cartExposeTopWord;
    private TextView exposeCartSize;
    private TextView exposeCartColor;
    private TextView exposeCartQuantity;
    private TextView exposeCartMaterial;
    private TextView exposeCartSubTotal;
//    private TextView exposeCartName;
    private ExtendedFloatingActionButton exposeCartCloseFAB;

    private ProductImageAdapter mProductImageAdapter;
    private Cart mCart = ViewModelHomeFragment.cartDetails;

    // show cart detail count
    private LinearLayout mDotsLayout;
    static TextView mDotsText[];

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_expse_cart, container, false);
        init(view);
        setCartDetails();
        return view;
    }

    private void init(View view){
        cartExposeTopWord = view.findViewById(R.id.cartExposeTopWord);
        exposeCartSize = view.findViewById(R.id.exposeCartSize);
        exposeCartColor = view.findViewById(R.id.exposeCartColor);
        exposeCartQuantity = view.findViewById(R.id.exposeCartQuantity);
        exposeCartMaterial = view.findViewById(R.id.exposeCartMaterial);
        exposeCartSubTotal = view.findViewById(R.id.exposeCartSubTotal);
//        exposeCartName = view.findViewById(R.id.exposeCartName);
        exposeCartCloseFAB = view.findViewById(R.id.exposeCartCloseFAB);
        mDotsLayout = view.findViewById(R.id.exposeCartImageCount);
        setRecyclerView(view);
    }

    private void setRecyclerView(View view){
        mProductImageAdapter = new ProductImageAdapter(getContext(), new ArrayList<ProductImage>());
        RecyclerView holderCartExposeImages = view.findViewById(R.id.holderExposeCartImages);
        holderCartExposeImages.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        holderCartExposeImages.setAdapter(mProductImageAdapter);
        setDots();
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

    private void setCartDetails(){
        ArrayList<ProductImage> photoList = new ArrayList<>();
        for(String str : mCart.getImages()){
            ProductImage pi = new ProductImage(str);
            photoList.add(pi);
        }
        mProductImageAdapter.loadNewData(photoList);
        setTextViews();
    }

    private void setTextViews(){
        cartExposeTopWord.setText(mCart.getProductName());
        exposeCartSize.setText(mCart.getSize());
        exposeCartColor.setText(mCart.getColor());
        exposeCartQuantity.setText(mCart.getQuantity());
        exposeCartMaterial.setText(mCart.getMaterial());
        exposeCartSubTotal.setText(String.format(Locale.US, "GH¢ %s",  mCart.getOrderValue()));
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
            Fragment fragment = fragmentManager.findFragmentById(R.id.containerCartDetails);
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
        exposeCartCloseFAB.setOnClickListener(goBackListener());
        cartExposeTopWord.setOnClickListener(goBackListener());
    }

    @Override
    public void onStop() {
        super.onStop();
        exposeCartCloseFAB.setOnClickListener(null);
        cartExposeTopWord.setOnClickListener(null);
    }

    @Override
    public void onBackPressed() {
        if(MainActivity.mTrackMain == MainActivity.TrackMain.CART){
            setVisibility();
        }
    }
}
