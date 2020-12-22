package asabre.com.buyout.view.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
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
import asabre.com.buyout.view.adapter.AboutUsAdapter;
import asabre.com.buyout.view.callback.BaseFragment;
import asabre.com.buyout.viewmodel.ViewModelHomeFragment;

public class AboutUsFragment extends Fragment implements BaseFragment {
    private static final String TAG = AboutUsFragment.class.getSimpleName();

    private TextView aboutUsTopWord;
    private ExtendedFloatingActionButton aboutUsFAB;
    private AboutUsAdapter mAboutUsAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_us, container, false);
        init(view);
        setAboutUsViewModel();
        return view;
    }

    private void init(View view){
        aboutUsTopWord = view.findViewById(R.id.aboutUsTopWord);
        aboutUsFAB = view.findViewById(R.id.aboutUsFAB);

        RecyclerView recyclerView = view.findViewById(R.id.holderAboutUs);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAboutUsAdapter = new AboutUsAdapter(this.getContext(), new ArrayList<AboutUs>());
        recyclerView.setAdapter(mAboutUsAdapter);
    }

    private void setAboutUsViewModel(){
        if(getActivity() != null){
            ViewModelHomeFragment viewModelHomeFragment = ViewModelProviders.of(getActivity()).get(ViewModelHomeFragment.class);
            viewModelHomeFragment.getAboutUs().observe(this, new Observer<List<AboutUs>>() {
                @Override
                public void onChanged(List<AboutUs> aboutUses) {
                    mAboutUsAdapter.loadNewData(aboutUses);
                    if(getActivity() != null){
                        getActivity().findViewById(R.id.indeterminateAboutUsFragment).setVisibility(View.GONE);
                    }
                }
            });
        }
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
    // tenase koom dwene woho na aba so

    @Override
    public void onStart() {
        super.onStart();
        aboutUsTopWord.setOnClickListener(goBackListener());
        aboutUsFAB.setOnClickListener(goBackListener());
    }

    @Override
    public void onStop() {
        super.onStop();
        aboutUsTopWord.setOnClickListener(null);
        aboutUsFAB.setOnClickListener(null);
    }

    @Override
    public void onBackPressed() {
        if(MainActivity.mHomeTrack == MainActivity.HomeTrack.PRODUCTS){
            Log.d(TAG, "onBackPressed: called");
            setVisibility();
        }
    }


}
