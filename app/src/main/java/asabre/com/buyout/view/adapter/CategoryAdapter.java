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
import asabre.com.buyout.view.callback.HomeFragmentCallback;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private Context mContext;
    private List<Category> mCategoryList;
    private HomeFragmentCallback mHomeFragmentCallback;

    public CategoryAdapter(Context context, List<Category> categoryList, HomeFragmentCallback homeFragmentCallback) {
        mContext = context;
        mCategoryList = categoryList;
        mHomeFragmentCallback = homeFragmentCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.browse_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(mCategoryList == null || mCategoryList.size() == 0){
            // do something when data is not available
        } else {
            Category category = mCategoryList.get(position);
            String categoryName = category.getName();
            Glide.with(mContext)
                    .load(category.getImage())
//                    .centerCrop()
                    .placeholder(R.drawable.filter_body)
                    .into(holder.categoryImage);

            holder.categoryName.setText(categoryName);
            holder.categoryWord.setText("new");
            holder.categoryImage.setOnClickListener(categoryClick(categoryName));
            holder.categoryName.setOnClickListener(categoryClick(categoryName));
            holder.categoryWord.setOnClickListener(categoryClick(categoryName));
        }
    }

    @Override
    public int getItemCount() {
        return mCategoryList != null && mCategoryList.size() != 0 ? mCategoryList.size() : 0;
    }

    public void loadNewData(List<Category> list){
        mCategoryList = list;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView categoryImage;
        TextView categoryWord;
        TextView categoryName;

        ViewHolder(View itemView){
            super(itemView);
            categoryImage = itemView.findViewById(R.id.categoryImage);
            categoryWord = itemView.findViewById(R.id.categoryWord);
            categoryName = itemView.findViewById(R.id.categoryName);
        }
    }

    private View.OnClickListener categoryClick(final String category){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHomeFragmentCallback.onCategoryClick(category);
            }
        };
    }

}
