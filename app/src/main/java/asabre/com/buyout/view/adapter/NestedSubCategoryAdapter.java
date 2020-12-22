package asabre.com.buyout.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import asabre.com.buyout.R;
import asabre.com.buyout.service.model.SubCategory;
import asabre.com.buyout.view.callback.NestedSubCategoryCallback;

public class NestedSubCategoryAdapter extends
        RecyclerView.Adapter<NestedSubCategoryAdapter.ViewHolder> {
    private Context mContext;
    private List<SubCategory> mSubCategoryList;
    private NestedSubCategoryCallback mNestedSubCategoryCallback;

    public NestedSubCategoryAdapter(Context context,
                                    List<SubCategory> subCategoryList,
                                    NestedSubCategoryCallback nestedSubCategoryCallback) {
        mContext = context;
        mSubCategoryList = subCategoryList;
        mNestedSubCategoryCallback = nestedSubCategoryCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.browse_nested_sub_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(mSubCategoryList == null || mSubCategoryList.size() == 0){
            // when data is not available
        } else {
            SubCategory subCategory = mSubCategoryList.get(position);
            holder.nestedSubCatName.setText(subCategory.getSubCategory());
            holder.itemView.setOnClickListener(subCatClickListener(subCategory));
        }
    }

    @Override
    public int getItemCount() {
        return mSubCategoryList != null && mSubCategoryList.size() != 0 ? mSubCategoryList.size() : 0;
    }

    public int loadNewData(List<SubCategory> list){
        mSubCategoryList = list;
        notifyDataSetChanged();
        // get total number of subCategories of a category
        int count = 0;
        for(SubCategory sc : list){
            count += 1;
        }
        return count;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView nestedSubCatName;
        ImageView nestedSubCatGetProducts;

        ViewHolder(View itemView){
            super(itemView);
            this.nestedSubCatName = itemView.findViewById(R.id.nestedSubCatName);
            this.nestedSubCatGetProducts = itemView.findViewById(R.id.nestedSubCatGetProducts);
        }
    }

    private View.OnClickListener subCatClickListener(final SubCategory subCategory){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNestedSubCategoryCallback.nestedSubCategoryCallback(subCategory);
            }
        };
    }

}
