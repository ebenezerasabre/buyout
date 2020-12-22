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

public class TrendingProductsAdapter extends RecyclerView.Adapter<TrendingProductsAdapter.ViewHolder> {
    private Context mContext;
    private List<Product> mProductList;
    private ProductClickCallback mProductClickCallback;
    private AddTrendingToWishListCallback mAddWishListFromHomeCallback;
    private CallCartAddFromHomeCallback mCallCartAddFromHomeCallback;

    public TrendingProductsAdapter(Context context,
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
                .inflate(R.layout.browse_product_plus, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(mProductList == null || mProductList.size() == 0){
            // when data is not available
        } else {
            Product product = mProductList.get(position);
            Glide.with(mContext.getApplicationContext())
                    .load(product.getImages()[0])
                    .placeholder(R.drawable.filter_body)
                    .into(holder.trendingImage);
            holder.trendingName.setText(product.getName());

            // get only number from discount
            Pattern p = Pattern.compile("\\d+");
            Matcher m = p.matcher(product.getDiscount());
            String num = "";
            while(m.find()){ num = m.group(); }

            double oldPrice = Double.parseDouble(product.getPrice());
            double disD = Double.parseDouble(num);
            double newPrice = (100.00 - disD) * oldPrice / 100.00;

            holder.trendingNewPrice.setText(String.format(Locale.US,"GH¢%.2f", newPrice));
            holder.trendingImage.setOnClickListener(trendingProductClick(product));
            holder.trendingName.setOnClickListener(trendingProductClick(product));
            holder.homeTrendAddCart.setOnClickListener(callCartDialog(product));
            holder.homeTrendAddWishList.setOnClickListener(addTrendingToWishList(product));
        }
    }

    @Override
    public int getItemCount() {
        return mProductList != null && mProductList.size() != 0 ? mProductList.size() : 0;
    }

    public void loadNewData(List<Product> products){
        mProductList = products;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView trendingImage;
        TextView trendingName;
        TextView trendingNewPrice;
        ImageView homeTrendAddCart;
        ImageView homeTrendAddWishList;


        ViewHolder(View itemView){
            super(itemView);
            this.trendingImage = itemView.findViewById(R.id.trendingImage);
            this.trendingName = itemView.findViewById(R.id.trendingName);
            this.trendingNewPrice = itemView.findViewById(R.id.trendingNewPrice);
            homeTrendAddCart = itemView.findViewById(R.id.homeTrendAddCart);
            homeTrendAddWishList = itemView.findViewById(R.id.homeTrendAddWishList);
        }
    }

    private View.OnClickListener trendingProductClick(final Product product){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProductClickCallback.productClickCallback(product);
            }
        };
    }

    private View.OnClickListener addTrendingToWishList(final Product product){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mAddWishListFromHomeCallback.addWishListFromHome(product);
            }
        };
    }

    private View.OnClickListener callCartDialog(final Product product){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallCartAddFromHomeCallback.callCartAddDialog(product);
            }
        };
    }


}
