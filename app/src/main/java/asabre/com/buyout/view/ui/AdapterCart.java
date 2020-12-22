package asabre.com.buyout.view.ui;

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
import asabre.com.buyout.service.model.Cart;
import asabre.com.buyout.view.callback.CartCallback;
import asabre.com.buyout.viewmodel.ViewModelHomeFragment;

public class AdapterCart extends RecyclerView.Adapter<AdapterCart.ViewHolder> {

    private Context mContext;
    private List<Cart> mCartList;
    private CartCallback mCartCallback;


public AdapterCart(Context context, List<Cart> cartList, CartCallback cartCallback) {
        mContext = context;
        mCartList = cartList;
        mCartCallback = cartCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.browse_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        if(mCartList == null || mCartList.size() == 0){
            // when cartList is empty
            ViewModelHomeFragment.cartIsEmpty.setValue(true);

        } else {
            ViewModelHomeFragment.cartIsEmpty.setValue(false);
            final Cart cart = mCartList.get(position);
            Glide.with(mContext.getApplicationContext())
                    .load(cart.getImages()[0])
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(holder.cartImage);

            // set texts
            holder.cartName.setText(cart.getProductName());
            holder.cartMaterial.setText(cart.getMaterial());
            holder.orderValue.setText(String.format("GH¢ %s", cart.getOrderValue()));
            holder.cart_price_multiply.setText(cart.getQuantity());

            // set listeners
            holder.cartImage.setOnClickListener(productDetail(cart));
            holder.cartDesc.setOnClickListener(productDetail(cart));
            holder.cancel.setOnClickListener(removeCart(cart));

            holder.thumbsUp.setOnClickListener(increaseQuantity(cart, holder));
            holder.thumbsDown.setOnClickListener(decreaseQuantity(cart, holder));


        }
    }

    @Override
    public int getItemCount() {
        return mCartList != null && mCartList.size() != 0 ? mCartList.size() : 0;
    }

    public HashMap<String, String> loadNewData(List<Cart> list){
        mCartList = list;
        notifyDataSetChanged();

        int count = 0;
        double value = 0.0;
        for(Cart cart : mCartList){
            count += 1;
            value += Double.parseDouble(cart.getOrderValue());
        }

        HashMap<String, String> cartSum = new HashMap<>();
        cartSum.put("count", String.valueOf(count));
        cartSum.put("value", String.valueOf(value));
        return cartSum;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        LinearLayout cartDesc;
        ImageView cartImage;
        ImageView cancel;
        TextView cartName;
        TextView cartMaterial;

        TextView orderValue;
        TextView cart_price_multiply;
        ImageView thumbsUp;
        ImageView thumbsDown;

        private ViewHolder(View itemView){
            super(itemView);

            cartDesc = itemView.findViewById(R.id.cartDesc);
            cartImage = itemView.findViewById(R.id.cartImage);
            cancel = itemView.findViewById(R.id.cartCancel);
            cartName = itemView.findViewById(R.id.cartName);
            cartMaterial = itemView.findViewById(R.id.cartMaterial);

            orderValue = itemView.findViewById(R.id.cartPrice);
            cart_price_multiply = itemView.findViewById(R.id.cartPriceMultiply);
            thumbsUp = itemView.findViewById(R.id.cartUp);
            thumbsDown = itemView.findViewById(R.id.cartDown);
        }
    }

    private View.OnClickListener productDetail(final Cart cart){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCartCallback.productDetail(cart);
            }
        };
    }

    private View.OnClickListener removeCart(final Cart cart){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCartCallback.removeCart(cart);
            }
        };
    }

    private View.OnClickListener increaseQuantity(final Cart cart, final ViewHolder holder){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCartCallback.increaseQuantity(cart, holder);
            }
        };
    }

    private View.OnClickListener decreaseQuantity(final Cart cart, final ViewHolder holder){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCartCallback.decreaseQuantity(cart, holder);
            }
        };
    }


}
