package com.kukitriplan.smartquizapp.skripsi.ui.dashboard.navigation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kukitriplan.smartquizapp.R;
import com.kukitriplan.smartquizapp.skripsi.utils.ProgressUtils;

public class DashboardFragment extends Fragment {

    private View view;
    private ProgressUtils progressUtils;

    public DashboardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        progressUtils = new ProgressUtils(getContext());
        progressUtils.hide();
        return view;
    }

}
