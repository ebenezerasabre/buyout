package asabre.com.buyout.view.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import asabre.com.buyout.R;
import asabre.com.buyout.service.model.Category;
import asabre.com.buyout.view.adapter.NestedCategoryAdapter;
import asabre.com.buyout.view.callback.NestedCategoryCallback;
import asabre.com.buyout.viewmodel.ViewModelHomeFragment;

public class CategoryFragment extends Fragment implements NestedCategoryCallback {
    private static final String TAG = CategoryFragment.class.getSimpleName();

    private NestedCategoryAdapter mNestedCategoryAdapter;
    private TextView theCategoriesTopWord;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        init(view);
        setCategoryViewModel();
        loadSubFromCategoryClick();
        return view;
    }

    // when a circular category is clicked open nestedFragments
    private void loadSubFromCategoryClick(){
        if(!CategoryParentFragment.selectedCategory.isEmpty()){
            loadCategorySubFragment();
        }
    }


    @Override
    public void nestedCategoryCallback(String category) {
        Toast.makeText(getContext(),category,Toast.LENGTH_SHORT).show();
        CategoryParentFragment.selectedCategory = category;
        loadCategorySubFragment();
    }

    private void loadCategorySubFragment(){
        CategorySubFragment categorySubFragment = new CategorySubFragment();
        if(getActivity() != null){
            // load fragment with animation
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
            transaction.replace(R.id.the_subcategory_part, categorySubFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    private void init(View view){
        theCategoriesTopWord = view.findViewById(R.id.theCategoriesTopWord);

        // for categories
        mNestedCategoryAdapter = new NestedCategoryAdapter(this.getContext(), new ArrayList<Category>(), this);
        RecyclerView recyclerView = view.findViewById(R.id.holderTheCategories);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setAdapter(mNestedCategoryAdapter);
    }

    private void setCategoryViewModel(){
        ViewModelHomeFragment viewModelHomeFragment = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(ViewModelHomeFragment.class);
        viewModelHomeFragment.getCategoriesObservable().observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(List<Category> categories) {
              int count = mNestedCategoryAdapter.loadNewData(categories);
                theCategoriesTopWord.setText(String.format("Select Category, (%s)", count));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


}
