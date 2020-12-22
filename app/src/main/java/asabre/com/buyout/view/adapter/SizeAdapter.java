package asabre.com.buyout.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import asabre.com.buyout.R;
import asabre.com.buyout.service.model.ProductImage;
import asabre.com.buyout.view.callback.SizeCallback;

public class SizeAdapter extends RecyclerView.Adapter<SizeAdapter.ViewHolder> {
    private Context mContext;
    private List<ProductImage> mSizeList;
    private SizeCallback mSizeCallback;

    public SizeAdapter(Context context, List<ProductImage> sizeList, SizeCallback sizeCallback) {
        mContext = context;
        mSizeList = sizeList;
        mSizeCallback = sizeCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.browse_size, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(mSizeList == null || mSizeList.size() == 0){
            // do something when data is not available
        } else {
            final ProductImage productImage = mSizeList.get(position);
            final String sizeNumber = productImage.getImg();
            holder.selectSize.setText(sizeNumber);
            holder.selectSize.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSizeCallback.selectSize(sizeNumber);
                }
            });
        }
    }

//    yourView.setBackgroundColor(Color.parseColor("#ffffff"));

    @Override
    public int getItemCount() {
        return mSizeList != null && mSizeList.size() != 0 ? mSizeList.size() : 0;
    }

    public void loadNewData(List<ProductImage> list){
        mSizeList = list;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView selectSize;

        ViewHolder(View itemView){
            super(itemView);
            this.selectSize = itemView.findViewById(R.id.selectSize);
        }
    }
}
