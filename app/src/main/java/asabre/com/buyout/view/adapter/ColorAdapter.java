package asabre.com.buyout.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import asabre.com.buyout.R;
import asabre.com.buyout.service.model.ProductImage;
import asabre.com.buyout.view.callback.ColorCallback;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ViewHolder> {
    private Context mContext;
    private List<ProductImage> mColorList;
    private ColorCallback mColorCallback;

    public ColorAdapter(Context context, List<ProductImage> colorList, ColorCallback colorCallback) {
        mContext = context;
        mColorList = colorList;
        mColorCallback = colorCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.browse_color, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(mColorList == null || mColorList.size() == 0) {
            // do something when data is not available
        } else {
            final ProductImage productImage = mColorList.get(position);
            String colorName = productImage.getImg();
            holder.selectColor.setText(colorName);
//            holder.selectColor.setBackgroundColor(Color.parseColor(colorName));
//            holder.selectColor.setText(colorName);
//            holder.itemView.setBackgroundColor(Color.parseColor(colorName));

            holder.selectColor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mColorCallback.selectColor(productImage.getImg());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mColorList != null && mColorList.size() != 0 ? mColorList.size() : 0;
    }

    public void loadNewData(List<ProductImage> list){
        mColorList = list;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView selectColor;
        ViewHolder(View itemView){
            super(itemView);
            this.selectColor = itemView.findViewById(R.id.selectColor);
        }
    }
}
