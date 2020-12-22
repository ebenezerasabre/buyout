package asabre.com.buyout.view.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import asabre.com.buyout.R;
import asabre.com.buyout.view.callback.BaseFragment;

public class HelpAndTutorials extends Fragment implements BaseFragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help_and_tutotrials, container, false);
        init(view);
        setHelpAndTutorialsViewModel();
        return view;
    }

    private void init(View view){

    }

    private void setHelpAndTutorialsViewModel(){


    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {

    }
}
