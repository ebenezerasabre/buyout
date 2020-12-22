package asabre.com.buyout.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import asabre.com.buyout.R;
import asabre.com.buyout.service.model.Product;
import asabre.com.buyout.view.callback.WishListCallback;

public class WishListAdapter extends RecyclerView.Adapter<WishListAdapter.ViewHolder> {
    private static final String TAG = WishListAdapter.class.getSimpleName();

    private Context mContext;
    private List<Product> mProductList;
    private WishListCallback mWishListCallback;

    public WishListAdapter(Context context, List<Product> productList, WishListCallback wishListCallback) {
        mContext = context;
        mProductList = productList;
        mWishListCallback = wishListCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.browse_wish_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(mProductList == null || mProductList.size() == 0){
            // do something when wishList is empty
        } else {

            Product product = mProductList.get(position);
            // load image resource
            Glide.with(mContext.getApplicationContext())
                    .load(product.getImages()[0])
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(holder.wishList_image);

            // set texts
            holder.wishList_name.setText(product.getName());
            holder.wishList_material.setText(product.getMaterial());
            holder.wishList_price.setText(String.format("GH¢ %s", product.getPrice()));

            // set listeners
            holder.wishList_image.setOnClickListener(productDetails(product));
            holder.wishListDesc.setOnClickListener(productDetails(product));
            holder.wishList_cancel.setOnClickListener(removeWishList(product));

        }
    }

    @Override
    public int getItemCount() {
        return mProductList != null && mProductList.size() != 0 ? mProductList.size() : 0;
        // changing 0 to 1 so that if there are no items to display
        // the recyclerViewAdapter will display the default photo and text message
    }

    public HashMap<String, String> loadNewData(List<Product> products){
        mProductList = products;
        notifyDataSetChanged();

        // return total wishList count and value
        int count = 0;
        double value = 0.0;
        for(Product pr : products){
            count += 1;
            value = Double.parseDouble(pr.getPrice());
        }
        HashMap<String, String> wishSum = new HashMap<>();
        wishSum.put("count", String.valueOf(count));
        wishSum.put("value", String.valueOf(value));
        return wishSum;
    }


    static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout wishListDesc;
        ImageView wishList_image;
        TextView wishList_name;
        TextView wishList_material;
        TextView wishList_price;
        ImageView wishList_cancel;

        private ViewHolder(View itemView){
            super(itemView);
            wishListDesc = itemView.findViewById(R.id.wishListDesc);
            wishList_image = itemView.findViewById(R.id.wishListImage);
            wishList_name = itemView.findViewById(R.id.wishListName);
            wishList_material = itemView.findViewById(R.id.wishListMaterial);
            wishList_price = itemView.findViewById(R.id.wishListPrice);
            wishList_cancel = itemView.findViewById(R.id.wishListCancel);
        }
    }

    private View.OnClickListener productDetails(final Product product){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWishListCallback.productDetail(product);
            }
        };
    }

    private View.OnClickListener removeWishList(final Product product){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWishListCallback.removeWishList(product);
            }
        };
    }

}
