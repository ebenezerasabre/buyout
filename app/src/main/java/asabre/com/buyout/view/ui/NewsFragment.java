package asabre.com.buyout.view.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import asabre.com.buyout.R;
import asabre.com.buyout.service.model.AboutUs;
import asabre.com.buyout.service.model.News;
import asabre.com.buyout.view.callback.BaseFragment;
import asabre.com.buyout.view.callback.NewsCallback;
import asabre.com.buyout.viewmodel.ViewModelHomeFragment;

public class NewsFragment extends Fragment implements BaseFragment, NewsCallback {
    private static final String TAG = NewsFragment.class.getSimpleName();

    TextView newsTopWord;
    private ExtendedFloatingActionButton newsCloseFAB;
    private AdapterNews mAdapterNews;
    private ViewModelHomeFragment mViewModelHomeFragment;
    private ProgressBar indeterminateNewsFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        init(view);
        setNewsViewModel();
        return view;
    }

    @Override
    public void newsThumbsUp(String newsId, AdapterNews.ViewHolder holder) {
        likeNews(newsId, holder);
    }

    @Override
    public void newsThumbsDown(String newsId, AdapterNews.ViewHolder holder) {
        hateNews(newsId, holder);
    }

    private void likeNews(String newsId, AdapterNews.ViewHolder holder){
        TextView thumbsUpCount = holder.newsThumbsUpCount;
        String str = thumbsUpCount.getText().toString();
        int previousQuantity = str.isEmpty() ? 0 : Integer.parseInt(str);
        int newQuantity = previousQuantity + 1;
        thumbsUpCount.setText(String.valueOf(newQuantity));
        setThumbsViewModel("thumbsUp", newsId);
    }

    private void hateNews(String newsId, AdapterNews.ViewHolder holder){
        TextView thumbsDownCount = holder.newsThumbsDownCount;
        String str = thumbsDownCount.getText().toString();
        int previousQuantity = str.isEmpty() ? 0 : Integer.parseInt(str);
        int newQuantity = previousQuantity + 1;
        thumbsDownCount.setText(String.valueOf(newQuantity));
        setThumbsViewModel("thumbsDown", newsId);
    }

    private void setThumbsViewModel(String sub, String subId){
        HashMap<String, String> obj = new HashMap<>();
        obj.put("sub", sub);
        obj.put("subId", subId);
        mViewModelHomeFragment.newsThumbs(obj);
    }

    @Override
    public void onStart() {
        super.onStart();
        newsTopWord.setOnClickListener(goBackListener());
        newsCloseFAB.setOnClickListener(goBackListener());
    }

    @Override
    public void onStop() {
        super.onStop();
        newsTopWord.setOnClickListener(null);
        newsCloseFAB.setOnClickListener(null);
    }

    @Override
    public void onBackPressed() {
        if(MainActivity.mHomeTrack == MainActivity.HomeTrack.PRODUCTS){
            setVisibility();
        }
    }

    private void setNewsViewModel(){
        if(getActivity() != null){
            mViewModelHomeFragment = ViewModelProviders.of(getActivity()).get(ViewModelHomeFragment.class);
            mViewModelHomeFragment.init();
            mViewModelHomeFragment.getNews().observe(this, new Observer<List<News>>() {
                @Override
                public void onChanged(List<News> news) {
                    mAdapterNews.loadNewData(news);
                    indeterminateNewsFragment.setVisibility(View.GONE);
                }
            });

        }
    }


    private void init(View view){
        newsTopWord = view.findViewById(R.id.newsTopWord);
        newsCloseFAB = view.findViewById(R.id.newCloseFAB);
        indeterminateNewsFragment = view.findViewById(R.id.indeterminateNewsFragment);

        // for recyclerView
        RecyclerView recyclerView = view.findViewById(R.id.holderNews);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapterNews = new AdapterNews(this.getContext(), new ArrayList<News>(), this);
        recyclerView.setAdapter(mAdapterNews);

    }

    private void setVisibility(){
        if(getActivity() != null){
            MainActivity.mHomeTrack = MainActivity.HomeTrack.HOME;
            getActivity().findViewById(R.id.containerProducts).setVisibility(View.GONE);
            getActivity().findViewById(R.id.containerHomeFragment).setVisibility(View.VISIBLE);
        }
        removeThisFragment();
    }

    private void removeThisFragment(){
        if(getActivity() != null){
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentById(R.id.containerProducts);
            if(fragment != null){
                getActivity().getSupportFragmentManager().beginTransaction()
                        .remove(fragment)
                        .commit();
                Log.d(TAG, "removeThisFragment: called");
            }
        }
    }

    private View.OnClickListener goBackListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibility();
            }
        };
    }





}
