package com.kukitriplan.smartquizapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.rubensousa.gravitysnaphelper.GravityPagerSnapHelper;
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;
import com.kukitriplan.smartquizapp.R;
import com.kukitriplan.smartquizapp.data.model.SnapModel;

import java.util.ArrayList;

public class PilihanAdapter extends RecyclerView.Adapter<PilihanAdapter.ViewHolderPilihan> implements GravitySnapHelper.SnapListener {
    private static final int VERTICAL = 0;
    private static final int HORIZONTAL = 1;

    private final Context context;
    private final ArrayList<SnapModel> snapModels;

    public PilihanAdapter(Context context) {
        this.context = context;
        snapModels = new ArrayList<>();
    }

    public void addSnap(SnapModel snapModel) {
        snapModels.add(snapModel);
    }

    @Override
    public int getItemViewType(int position) {
        SnapModel snapModel = snapModels.get(position);
        switch (snapModel.getGravity()) {
            case Gravity.CENTER_VERTICAL:
                return VERTICAL;
            case Gravity.CENTER_HORIZONTAL:
                return HORIZONTAL;
            case Gravity.START:
                return HORIZONTAL;
            case Gravity.TOP:
                return VERTICAL;
            case Gravity.END:
                return HORIZONTAL;
            case Gravity.BOTTOM:
                return VERTICAL;
        }
        return HORIZONTAL;
    }

    @NonNull
    @Override
    public ViewHolderPilihan onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolderPilihan(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_pilihan, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderPilihan viewHolderPilihan, int i) {
        SnapModel snapModel = snapModels.get(i);
        viewHolderPilihan.snapTitle.setText(snapModel.getText());
        if (snapModel.getGravity() == Gravity.START || snapModel.getGravity() == Gravity.END) {
            viewHolderPilihan.snapKuis.setLayoutManager(new LinearLayoutManager(viewHolderPilihan.snapKuis.getContext(), LinearLayoutManager.HORIZONTAL, false));
            new GravitySnapHelper(snapModel.getGravity(), false, this).attachToRecyclerView(viewHolderPilihan.snapKuis);
        }
        else if (snapModel.getGravity() == Gravity.CENTER_HORIZONTAL || snapModel.getGravity() == Gravity.CENTER_VERTICAL) {
            viewHolderPilihan.snapKuis.setLayoutManager(new LinearLayoutManager(viewHolderPilihan.snapKuis.getContext(),
                    snapModel.getGravity() == Gravity.CENTER_HORIZONTAL ? LinearLayoutManager.HORIZONTAL : LinearLayoutManager.VERTICAL, false));
            new LinearSnapHelper().attachToRecyclerView(viewHolderPilihan.snapKuis);
        }
        else if (snapModel.getGravity() == Gravity.CENTER) {
            viewHolderPilihan.snapKuis.setLayoutManager(new LinearLayoutManager(viewHolderPilihan.snapKuis.getContext(), LinearLayoutManager.HORIZONTAL, false));
            new GravityPagerSnapHelper(Gravity.START).attachToRecyclerView(viewHolderPilihan.snapKuis);
        }
        else {
            viewHolderPilihan.snapKuis.setLayoutManager(new LinearLayoutManager(viewHolderPilihan.snapKuis.getContext()));
            new GravitySnapHelper(snapModel.getGravity()).attachToRecyclerView(viewHolderPilihan.snapKuis);
        }

        viewHolderPilihan.snapKuis.setAdapter(new KuisAdapter(context,snapModel.getGravity() == Gravity.START
                || snapModel.getGravity() == Gravity.END
                || snapModel.getGravity() == Gravity.CENTER_HORIZONTAL,
                snapModel.getGravity() == Gravity.CENTER, snapModel.getKuisList()));

    }

    @Override
    public void onSnap(int position) {

    }

    @Override
    public int getItemCount() {
        return snapModels.size() > 0 ? snapModels.size() : 0;
    }

    class ViewHolderPilihan extends RecyclerView.ViewHolder {
        private final TextView snapTitle;
        private final RecyclerView snapKuis;

        ViewHolderPilihan(@NonNull View itemView) {
            super(itemView);
            snapTitle = itemView.findViewById(R.id.tv_pilihan);
            snapKuis  = itemView.findViewById(R.id.rv_list_kuis);
        }
    }
}
