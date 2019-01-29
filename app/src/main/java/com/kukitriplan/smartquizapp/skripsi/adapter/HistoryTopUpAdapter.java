package com.kukitriplan.smartquizapp.skripsi.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kukitriplan.smartquizapp.R;
import com.kukitriplan.smartquizapp.skripsi.data.model.HistoryTopUp;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HistoryTopUpAdapter extends RecyclerView.Adapter<HistoryTopUpAdapter.ViewHistory> {

    private Context context;
    private ArrayList<HistoryTopUp> histories;

    public HistoryTopUpAdapter(Context context, ArrayList<HistoryTopUp> histories) {
        this.context = context;
        this.histories = histories;
    }

    @NonNull
    @Override
    public ViewHistory onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHistory(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_history_top_up, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHistory viewHistory, int i) {
        final HistoryTopUp historyTopUp = histories.get(i);
        viewHistory.tvNomor.setText(historyTopUp.getNomor());
        viewHistory.tvJumlah.setText(historyTopUp.getJumlah());
        viewHistory.tvTgl.setText(historyTopUp.getTanggal());
    }

    @Override
    public int getItemCount() {
        return histories.size() > 0 ? histories.size() : '0';
    }

    class ViewHistory extends RecyclerView.ViewHolder {
        @BindView(R.id.tvNomor) TextView tvNomor;
        @BindView(R.id.tvJumlah) TextView tvJumlah;
        @BindView(R.id.tvTgl) TextView tvTgl;

        public ViewHistory(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), histories.get(getAdapterPosition()).getJumlah() + " Tanggal " +
                            histories.get(getAdapterPosition()).getTanggal(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
