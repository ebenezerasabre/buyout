package asabre.com.buyout.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import asabre.com.buyout.R;
import asabre.com.buyout.service.model.ProductImage;

public class ProductImageAdapter extends RecyclerView.Adapter<ProductImageAdapter.ViewHolder> {

    private Context mContext;
    private List<ProductImage> mProductImageList;

    public ProductImageAdapter(Context context, List<ProductImage> productImageList) {
        mContext = context;
        mProductImageList = productImageList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.browse_product_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(mProductImageList == null || mProductImageList.size() == 0){
            // do something when no data is available
        } else {
            ProductImage productImage = mProductImageList.get(position);
            Glide.with(mContext.getApplicationContext())
                    .load(productImage.getImg())
                    .placeholder(R.drawable.filter_body)
                    .into(holder.productDetailsImages);
        }
    }

    @Override
    public int getItemCount() {
        return mProductImageList != null && mProductImageList.size() != 0 ? mProductImageList.size() : 0;
    }

    public void loadNewData(List<ProductImage> list){
        mProductImageList = list;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView productDetailsImages;
        ViewHolder(View itemView){
            super(itemView);
            this.productDetailsImages = itemView.findViewById(R.id.productDetailsImages);
        }
    }

}
