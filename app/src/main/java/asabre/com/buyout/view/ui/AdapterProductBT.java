package asabre.com.buyout.view.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import asabre.com.buyout.R;
import asabre.com.buyout.service.model.Cart;
import asabre.com.buyout.service.model.Product;
import asabre.com.buyout.view.callback.AddFpToWishListCallback;
import asabre.com.buyout.view.callback.CallCartAddFromHomeCallback;
import asabre.com.buyout.view.callback.ProductClickCallback;

public class AdapterProductBT extends RecyclerView.Adapter<AdapterProductBT.ViewHolder> {
    private Context mContext;
    List<Product> mProductList;
    private ProductClickCallback mProductClickCallback;
    private AddBTToWishListCallback mAddBTToWishListCallback;
    private CallCartAddFromHomeCallback mCallCartAddFromHomeCallback;

    public AdapterProductBT(Context context,
                            List<Product> productList,
                            ProductClickCallback productClickCallback,
                            AddBTToWishListCallback addBTToWishListCallback,
                            CallCartAddFromHomeCallback callCartAddFromHomeCallback) {
        mContext = context;
        mProductList = productList;
        mProductClickCallback = productClickCallback;
        mAddBTToWishListCallback = addBTToWishListCallback;
        mCallCartAddFromHomeCallback = callCartAddFromHomeCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.browse_product_together, parent, false);
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
            holder.homeFpAddCart.setOnClickListener(callCartDialog(product));
            holder.homeFpAddWishList.setOnClickListener(addProductToWishList(product, holder));
        }
    }

    @Override
    public int getItemCount() {
        return mProductList != null && mProductList.size() != 0 ? mProductList.size() : 0;
    }

    public HashMap<String, String> loadNewData(List<Product> list){
        mProductList = list;
        notifyDataSetChanged();

        int count = 0;
        double value = 0.0;
        StringBuilder productIds = new StringBuilder();
        for(Product p : list){
            count += 1;
            value += newPrice(p);
            productIds.append(String.format(Locale.US, "%s,", p.get_id()));
        }

        HashMap<String, String> cartSum = new HashMap<>();
        cartSum.put("count", String.valueOf(count));
        cartSum.put("value", String.valueOf(value));
        cartSum.put("productIds", productIds.toString());
        return cartSum;
    }


    private double newPrice(Product product){
        // get only number from discount
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(product.getDiscount());
        String num = "";
        while(m.find()){ num = m.group(); }

        // get the new price
        double oldPrice = Double.parseDouble(product.getPrice());
        double disD = Double.parseDouble(num);
        return  (100.00 - disD) * oldPrice / 100.00; // new price
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView productImage;
        TextView productDiscount;
        TextView productName;
        TextView productNewPrice;
        ImageView homeFpAddCart;
        ImageView homeFpAddWishList;

        ViewHolder(View itemView){
            super(itemView);
            this.productImage = itemView.findViewById(R.id.productImageBT);
            this.productDiscount = itemView.findViewById(R.id.productDiscountBT);
            this.productName = itemView.findViewById(R.id.productNameBT);
            this.productNewPrice = itemView.findViewById(R.id.productNewPriceBT);
            this.homeFpAddCart = itemView.findViewById(R.id.homeFpAddCartBT);
            this.homeFpAddWishList = itemView.findViewById(R.id.homeFpAddWishListBT);
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

    private View.OnClickListener callCartDialog(final Product product){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallCartAddFromHomeCallback.callCartAddDialog(product);
            }
        };
    }

    private View.OnClickListener addProductToWishList(final Product product, final ViewHolder holder){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddBTToWishListCallback.addBTToWishList(product, holder);
            }
        };
    }


}
