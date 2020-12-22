package asabre.com.buyout.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import asabre.com.buyout.R;
import asabre.com.buyout.service.model.Order;
import asabre.com.buyout.view.callback.OrderCallback;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private Context mContext;
    private List<Order> mOrderList;
    private OrderCallback mOrderCallback;

    public OrderAdapter(Context context, List<Order> orderList, OrderCallback orderCallback) {
        mContext = context;
        mOrderList = orderList;
        mOrderCallback = orderCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.browse_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(mOrderList == null || mOrderList.size() == 0){
            // when orderList is empty
            // do something
        } else {
            Order order = mOrderList.get(position);
            // load image resource
            Glide.with(mContext.getApplicationContext())
                    .load(order.getImages()[0])
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(holder.orderImage);

            // set texts
            holder.orderName.setText(order.getProductName());
            holder.orderStatus.setText(order.getStatus());
            holder.orderValue.setText(String.format(Locale.US,"GH¢ %.2f", Double.parseDouble(order.getOrderValue())));

            // set listeners
            holder.orderDesc.setOnClickListener(orderDetails(order));
            holder.orderImage.setOnClickListener(orderDetails(order));
            holder.orderReview.setOnClickListener(leaveReview(order.getProductId(), order.getCustomerId()));

        }
    }

    @Override
    public int getItemCount() {
        return mOrderList != null && mOrderList.size() != 0 ? mOrderList.size() : 0;
    }

    public HashMap<String, String> loadNewData(List<Order> list){
        mOrderList = list;
        notifyDataSetChanged();

        int count = 0;
        double value = 0.0;
        for(Order order : list){
            count += 1;
            value += Double.parseDouble(order.getOrderValue());
        }

        HashMap<String, String> orderSum = new HashMap<>();
        orderSum.put("count", String.valueOf(count));
        orderSum.put("value", String.valueOf(value));
        return orderSum;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout orderDesc;
        ImageView orderImage;
        TextView orderName;
        ImageView orderReview;
        TextView orderStatus;
        TextView orderValue;

        private ViewHolder(View itemView){
            super(itemView);
            orderDesc = itemView.findViewById(R.id.orderDesc);
            this.orderImage = itemView.findViewById(R.id.orderImage);
            this.orderName = itemView.findViewById(R.id.orderName);
            this.orderReview = itemView.findViewById(R.id.orderReview);
            this.orderStatus = itemView.findViewById(R.id.orderStatus);
            this.orderValue = itemView.findViewById(R.id.orderValue);
        }
    }

    private View.OnClickListener orderDetails(final Order order){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOrderCallback.orderDetails(order);
            }
        };
    }

    private View.OnClickListener leaveReview(final String productId, final String customerId){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOrderCallback.leaveReview(productId, customerId);
            }
        };
    }

}
