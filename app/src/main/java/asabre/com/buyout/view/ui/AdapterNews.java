package asabre.com.buyout.view.ui;

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
import asabre.com.buyout.service.model.News;
import asabre.com.buyout.view.callback.NewsCallback;

public class AdapterNews extends RecyclerView.Adapter<AdapterNews.ViewHolder> {

    private Context mContext;
    private List<News> mNewsList;
    private NewsCallback mNewsCallback;

    public AdapterNews(Context context, List<News> newsList, NewsCallback newsCallback) {
        mContext = context;
        mNewsList = newsList;
        mNewsCallback = newsCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.browse_news, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(mNewsList == null || mNewsList.size() == 0){
            // when data is not available
        } else {
            News news = mNewsList.get(position);
            Glide.with(mContext.getApplicationContext())
                    .load(news.getImg())
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(holder.newsImage);
            holder.newsDate.setText(news.getDate());
            holder.newsMsg.setText(news.getMsg());
            holder.newsSource.setText(news.getSource());
            holder.newsThumbsUpCount.setText(news.getThumbs()[0]);
            holder.newsThumbsDownCount.setText(news.getThumbs()[1]);

            // set listeners
            holder.newsThumbsUp.setOnClickListener(increaseQuantity(news.get_id(), holder));
            holder.newsThumbsDown.setOnClickListener(decreaseQuantity(news.get_id(), holder));
        }
    }

    @Override
    public int getItemCount() {
        return mNewsList != null && mNewsList.size() != 0 ? mNewsList.size() : 0;
    }

    public void loadNewData(List<News> list){
        mNewsList = list;
        notifyDataSetChanged();
    }

    public static  class ViewHolder extends RecyclerView.ViewHolder{
        TextView newsDate;
        ImageView newsImage;
        TextView newsMsg;
        TextView newsSource;
        ImageView newsThumbsDown;
        TextView newsThumbsDownCount;
        ImageView newsThumbsUp;
        TextView newsThumbsUpCount;

        ViewHolder(View itemView){
            super(itemView);
            this.newsDate = itemView.findViewById(R.id.newsDate);
            this.newsImage = itemView.findViewById(R.id.newsImage);
            this.newsMsg = itemView.findViewById(R.id.newsMsg);
            this.newsSource = itemView.findViewById(R.id.newsSource);
            this.newsThumbsDown = itemView.findViewById(R.id.newsThumbsDown);
            this.newsThumbsDownCount = itemView.findViewById(R.id.newsThumbsDownCount);
            this.newsThumbsUp = itemView.findViewById(R.id.newsThumbsUp);
            this.newsThumbsUpCount = itemView.findViewById(R.id.newsThumbsUpCount);
        }
    }

    private View.OnClickListener increaseQuantity(final String newsId, final ViewHolder holder){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNewsCallback.newsThumbsUp(newsId, holder);
            }
        };
    }

    private View.OnClickListener decreaseQuantity(final String newsId, final ViewHolder holder){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNewsCallback.newsThumbsDown(newsId, holder);
            }
        };
    }


}
