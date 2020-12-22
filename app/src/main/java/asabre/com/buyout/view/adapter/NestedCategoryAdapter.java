package asabre.com.buyout.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import asabre.com.buyout.R;
import asabre.com.buyout.service.model.Category;
import asabre.com.buyout.view.callback.NestedCategoryCallback;

public class NestedCategoryAdapter extends
        RecyclerView.Adapter<NestedCategoryAdapter.ViewHolder> {

    private Context mContext;
    private List<Category> mCategoryList;
    private NestedCategoryCallback mNestedCategoryCallback;

    public NestedCategoryAdapter(Context context,
                                 List<Category> categoryList,
                                 NestedCategoryCallback nestedCategoryCallback) {
        mContext = context;
        mCategoryList = categoryList;
        mNestedCategoryCallback = nestedCategoryCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.browse_nested_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(mCategoryList == null || mCategoryList.size() == 0){
            // when data is not available
        } else {
            Category category = mCategoryList.get(position);
            Glide.with(mContext.getApplicationContext())
                    .load(category.getImage())
                    .placeholder(R.drawable.filter_body)
                    .into(holder.nestedCatImage);
            holder.nestedCatName.setText(category.getName());
            holder.itemView.setOnClickListener(catClickListener(category.getName()));
        }
    }

    @Override
    public int getItemCount() {
        return mCategoryList != null && mCategoryList.size() != 0 ? mCategoryList.size() : 0;
    }

    public int loadNewData(List<Category> list){
        mCategoryList = list;
        notifyDataSetChanged();
        // get total number of categories
        int count = 0;
        for(Category c : list){
            count += 1;
        }
        return count;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView nestedCatImage;
        TextView nestedCatName;

        ViewHolder(View itemView){
            super(itemView);
            this.nestedCatImage = itemView.findViewById(R.id.nestedCatImage);
            this.nestedCatName = itemView.findViewById(R.id.nestedCatName);
        }
    }

    private View.OnClickListener catClickListener(final String category) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNestedCategoryCallback.nestedCategoryCallback(category);
            }
        };
    }

}
