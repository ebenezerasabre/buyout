package asabre.com.buyout.view.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
import asabre.com.buyout.service.model.SubCategory;
import asabre.com.buyout.view.adapter.NestedSubCategoryAdapter;
import asabre.com.buyout.view.callback.NestedSubCategoryCallback;
import asabre.com.buyout.viewmodel.ViewModelHomeFragment;

public class CategorySubFragment extends Fragment implements NestedSubCategoryCallback {
    private static final String TAG = CategorySubFragment.class.getSimpleName();

    private TextView theSubCategoriesCategory;
    private TextView theSubCategoriesSubs;
    private NestedSubCategoryAdapter mNestedSubCategoryAdapter;
    private ProgressBar indeterminateTheSubCategories;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_sub, container, false);
        init(view);
        setSubCategoryViewModel(subObj());
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void nestedSubCategoryCallback(SubCategory subCategory) {
//        indeterminateTheSubCategories.setVisibility(View.VISIBLE);
        Toast.makeText(getContext(), subCategory.getSubCategory(),Toast.LENGTH_SHORT).show();

        // ref
        MainActivity.searchRef = "subCategory";
        // other
        MainActivity.searchOther = String
                .format(Locale.US, "%s,%s",
                subCategory.getCategory(),
                subCategory.getSubCategory());
        loadProducts();
    }

    private void loadProducts(){
        MainActivity.seeAll = "nested";
        MainActivity.mHomeTrack = MainActivity.HomeTrack.PRODUCTS;
        MainActivity.mStepsTracking = MainActivity.Steps.STEP_THREE;
        ProductsFragment productsFragment = new ProductsFragment();
        loadProductsFragment(productsFragment);

    }

    private void loadProductsFragment(Fragment fragment){
        removePreviousProductsFameLayoutData();

        if(fragment != null && getActivity() != null){
            findNesCatFrameLayout().setVisibility(View.GONE);
            findProductFrameLayout().setVisibility(View.VISIBLE);

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
            transaction.replace(R.id.containerProducts, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    private void removePreviousProductsFameLayoutData(){
        if(getActivity() != null){
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentById(R.id.containerProducts);
            if(fragment != null){
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .remove(fragment)
                        .commit();
            }
        }
    }



    private void init(View view){
        theSubCategoriesCategory = view.findViewById(R.id.theSubCategoriesCategory);
        theSubCategoriesCategory.setText(CategoryParentFragment.selectedCategory);
        theSubCategoriesSubs = view.findViewById(R.id.theSubCategoriesSubs);
        indeterminateTheSubCategories = view.findViewById(R.id.indeterminateTheSubCategories);

        indeterminateTheSubCategories.setVisibility(View.VISIBLE);
        // for recyclerView
        mNestedSubCategoryAdapter = new NestedSubCategoryAdapter(this.getContext(), new ArrayList<SubCategory>(), this);
        RecyclerView recyclerView = view.findViewById(R.id.holderTheSubCategories);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mNestedSubCategoryAdapter);

    }

    private List<HashMap<String, String>> subObj(){
        HashMap<String, String> obj = new HashMap<>();
        obj.put("category", CategoryParentFragment.selectedCategory.toLowerCase());
        List<HashMap<String, String>> list = new ArrayList<>();
        list.add(obj);
        return list;
    }

    private void setSubCategoryViewModel(List<HashMap<String, String>> body){
        ViewModelHomeFragment viewModelHomeFragment = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(ViewModelHomeFragment.class);
        viewModelHomeFragment.resetSubCategories();
        viewModelHomeFragment.initSubCategories(body);
        viewModelHomeFragment.getSubCategories().observe(this, new Observer<List<SubCategory>>() {
            @Override
            public void onChanged(List<SubCategory> subCategories) {
                Log.d(TAG, "onChanged: subCategories " + subCategories);
               int count = mNestedSubCategoryAdapter.loadNewData(subCategories);
                theSubCategoriesSubs.setText(String.format("Select SuCategory, (%s)", count));
                indeterminateTheSubCategories.setVisibility(View.GONE);
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

    private View findNesCatFrameLayout(){ return Objects.requireNonNull(getActivity()).findViewById(R.id.containerProductReview); }
    private View  findProductFrameLayout(){ return Objects.requireNonNull(getActivity()).findViewById(R.id.containerProducts); }




}
