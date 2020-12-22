package asabre.com.buyout.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import asabre.com.buyout.R;
import asabre.com.buyout.service.model.Product;
import asabre.com.buyout.view.callback.AddTrendingToWishListCallback;
import asabre.com.buyout.view.callback.CallCartAddFromHomeCallback;
import asabre.com.buyout.view.callback.ProductClickCallback;

public class FeaturedProductsAdapter extends RecyclerView.Adapter<FeaturedProductsAdapter.ViewHolder> {
    private Context mContext;
    List<Product> mProductList;
    private ProductClickCallback mProductClickCallback;
    private AddTrendingToWishListCallback mAddWishListFromHomeCallback;
    private CallCartAddFromHomeCallback mCallCartAddFromHomeCallback;

    public FeaturedProductsAdapter(Context context,
                                   List<Product> productList,
                                   ProductClickCallback productClickCallback,
                                   AddTrendingToWishListCallback addWishListFromHomeCallback,
                                   CallCartAddFromHomeCallback callCartAddFromHomeCallback) {
        mContext = context;
        mProductList = productList;
        mProductClickCallback = productClickCallback;
        mAddWishListFromHomeCallback = addWishListFromHomeCallback;
        mCallCartAddFromHomeCallback = callCartAddFromHomeCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.browse_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(mProductList == null || mProductList.size() == 0){
            // so something when data is not available
        } else {
            Product product = mProductList.get(position);
            Glide.with(mContext.getApplicationContext())
                    .load(product.getImages()[0])
                    .placeholder(R.drawable.filter_body)
                    .into(holder.productImage);

            holder.productDiscount.setText(product.getDiscount());
            holder.productName.setText(product.getName());

            // get only number from discount
            Pattern p = Pattern.compile("\\d+");
            Matcher m = p.matcher(product.getDiscount());
            String num = "";
            while(m.find()){ num = m.group(); }

            // get the new price
            double oldPrice = Double.parseDouble(product.getPrice());
            double disD = Double.parseDouble(num);
            double newPrice = (100.00 - disD) * oldPrice / 100.00;
            holder.productNewPrice.setText(String.format(Locale.US,"GH¢%.2f", newPrice));

            holder.productImage.setOnClickListener(featuredProductClick(product));
            holder.productName.setOnClickListener(featuredProductClick(product));
            holder.productDiscount.setOnClickListener(featuredProductClick(product));
        }
    }

    @Override
    public int getItemCount() {
        return mProductList != null && mProductList.size() != 0 ? mProductList.size() : 0;
    }

    public void loadNewData(List<Product> list){
        mProductList = list;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView productImage;
        TextView productDiscount;
        TextView productName;
        TextView productNewPrice;
        ImageView homeFpAddCart;
        ImageView homeFpAddWishList;


        ViewHolder(View itemView){
            super(itemView);
            this.productImage = itemView.findViewById(R.id.productImage);
            this.productDiscount = itemView.findViewById(R.id.productDiscount);
            this.productName = itemView.findViewById(R.id.productName);
            this.productNewPrice = itemView.findViewById(R.id.productNewPrice);
            this.homeFpAddCart = itemView.findViewById(R.id.homeFpAddCart);
            this.homeFpAddWishList = itemView.findViewById(R.id.homeFpAddWishList);
        }
    }

    private View.OnClickListener featuredProductClick(final Product product){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProductClickCallback.productClickCallback(product);
            }
        };
    }


}
