package com.kukitriplan.smartquizapp.skripsi.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.kukitriplan.smartquizapp.R;
import com.kukitriplan.smartquizapp.skripsi.data.model.HistoryKuis;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HistoryKuisAdapter extends RecyclerView.Adapter<HistoryKuisAdapter.ViewHistory> {

    private Context context;
    private ArrayList<HistoryKuis> histories;
    private BottomSheetDialog mBottomSheetDialog;

    public HistoryKuisAdapter(Context context, ArrayList<HistoryKuis> histories) {
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
        final HistoryKuis historyKuis = histories.get(i);
        viewHistory.id = historyKuis.getIdNilai();
        viewHistory.tvNomor.setText(historyKuis.getNomor());
        viewHistory.tvJumlah.setText(historyKuis.getNamaKuis());
        viewHistory.tvTgl.setText(historyKuis.getNilai());
        //viewHistory.setItemClickListener(this);
        viewHistory.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View bottomSheetLayout = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_dialog, null);
                Button b = bottomSheetLayout.findViewById(R.id.button_close);
                b.setVisibility(View.INVISIBLE);
                (bottomSheetLayout.findViewById(R.id.button_close)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBottomSheetDialog.dismiss();
                    }
                });
                (bottomSheetLayout.findViewById(R.id.button_ok)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBottomSheetDialog.dismiss();
                    }
                });
                TextView tvTitle = bottomSheetLayout.findViewById(R.id.tv_title_sheet);
                tvTitle.setText(historyKuis.getNamaKuis() + ", Tanggal : " + historyKuis.getTanggal());
                AppCompatRatingBar ratingBar = bottomSheetLayout.findViewById(R.id.ratingBarSheet);
                ratingBar.setRating(historyKuis.getRating());
                TextView tvDeskripsi = bottomSheetLayout.findViewById(R.id.tv_detail_sheet);
                String detail = "Nama Kuis : " + historyKuis.getNamaKuis() + "\n" +
                                "Harga : "  + historyKuis.getHarga() + "\n" +
                                "Jumlah Soal : " + historyKuis.getSoal() + "\n" +
                                "Durasi Kuis : " + historyKuis.getDurasi() + "\n" +
                                "Jawaban Benar : " + historyKuis.getBenar() + "\n" +
                                "Jawaban Salah : " + historyKuis.getSalah() + "\n" +
                                "Nilai : " + historyKuis.getNilai() + "\n";
                tvDeskripsi.setText(detail);
                mBottomSheetDialog = new BottomSheetDialog(context);
                mBottomSheetDialog.setContentView(bottomSheetLayout);
                mBottomSheetDialog.setCanceledOnTouchOutside(false);
                mBottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                mBottomSheetDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return histories.size() > 0 ? histories.size() : '0';
    }

    class ViewHistory extends RecyclerView.ViewHolder {
        @BindView(R.id.tvNomor) TextView tvNomor;
        @BindView(R.id.tvJumlah) TextView tvJumlah;
        @BindView(R.id.tvTgl) TextView tvTgl;
        String id;
        public ViewHistory(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}