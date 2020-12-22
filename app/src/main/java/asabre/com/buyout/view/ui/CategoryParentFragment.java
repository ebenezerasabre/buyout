package asabre.com.buyout.view.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import asabre.com.buyout.R;
import asabre.com.buyout.view.callback.BaseFragment;

public class CategoryParentFragment extends Fragment implements BaseFragment {

    private TextView categoryParentTopWord;
    public static String selectedCategory = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_parent, container, false);
        init(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadCategoryFragment();
    }

    private void loadCategoryFragment(){
        CategoryFragment categoryFragment = new CategoryFragment();
        if(getActivity() != null){
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
            transaction.replace(R.id.the_category_part, categoryFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    private void init(View view){
        categoryParentTopWord = view.findViewById(R.id.categoryParentTopWord);
    }

    private View.OnClickListener removeListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeNestedFragments();
            }
        };
    }

    private void removeNestedFragments(){
        setVisibility();
        removeSubCategory();
        removeCategory();
        removeParent();
    }

    private void setVisibility(){
        Objects.requireNonNull(getActivity()).findViewById(R.id.containerHomeFragment).setVisibility(View.VISIBLE);
        MainActivity.mHomeTrack = MainActivity.HomeTrack.HOME;
        getActivity().findViewById(R.id.containerProductReview).setVisibility(View.GONE);
    }

    private void removeCategory(){
        FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        Fragment cat = fragmentManager.findFragmentById(R.id.the_category_part);
        if(cat != null){
            getActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .remove(cat)
                    .commit();
        }
    }
    private void removeSubCategory(){
        FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        Fragment subCat = fragmentManager.findFragmentById(R.id.the_subcategory_part);
        if(subCat != null){
            getActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .remove(subCat)
                    .commit();
        }
    }
    private void removeParent(){
        FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        Fragment parentCat = fragmentManager.findFragmentById(R.id.containerProductReview);
        assert parentCat != null;
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .remove(parentCat)
                .commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        categoryParentTopWord.setOnClickListener(removeListener());
    }

    @Override
    public void onStop() {
        super.onStop();
        categoryParentTopWord.setOnClickListener(null);
    }

    @Override
    public void onBackPressed() {
        if(MainActivity.mHomeTrack == MainActivity.HomeTrack.PRODUCT_REVIEW ){
            removeNestedFragments();
        }
    }




}
