package asabre.com.buyout.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import asabre.com.buyout.R;
import asabre.com.buyout.service.model.AboutUs;

public class AboutUsAdapter extends RecyclerView.Adapter<AboutUsAdapter.ViewHolder> {
    private Context mContext;
    private List<AboutUs> mAboutUsList;

    public AboutUsAdapter(Context context, List<AboutUs> aboutUsList) {
        mContext = context;
        mAboutUsList = aboutUsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.browse_about_us, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(mAboutUsList == null || mAboutUsList.size() == 0){
            // when data is not available
        } else {
            AboutUs aboutUs = mAboutUsList.get(position);
            holder.aboutUsTitle.setText(aboutUs.getTitle());
            holder.aboutUsMsg.setText(aboutUs.getMsg());
        }
    }

    @Override
    public int getItemCount() {
        return mAboutUsList != null && mAboutUsList.size() != 0 ? mAboutUsList.size() : 0;
    }

    public void loadNewData(List<AboutUs> list){
        mAboutUsList = list;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView aboutUsTitle;
        TextView aboutUsMsg;
        ViewHolder(View itemView){
            super(itemView);
            this.aboutUsTitle = itemView.findViewById(R.id.aboutUsTitle);
            this.aboutUsMsg = itemView.findViewById(R.id.aboutUsMsg);
        }
    }
}
