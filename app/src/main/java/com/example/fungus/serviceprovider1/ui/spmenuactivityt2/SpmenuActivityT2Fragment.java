package com.example.fungus.serviceprovider1.ui.spmenuactivityt2;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fungus.serviceprovider1.R;

public class SpmenuActivityT2Fragment extends Fragment {

    private SpmenuActivityT2ViewModel mViewModel;

    public static SpmenuActivityT2Fragment newInstance() {
        return new SpmenuActivityT2Fragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.spmenu_activity_t2_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(SpmenuActivityT2ViewModel.class);
        // TODO: Use the ViewModel
    }

}
