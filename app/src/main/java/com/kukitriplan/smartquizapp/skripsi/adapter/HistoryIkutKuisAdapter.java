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
import com.kukitriplan.smartquizapp.skripsi.data.model.HistoryIkutKuis;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HistoryIkutKuisAdapter extends RecyclerView.Adapter<HistoryIkutKuisAdapter.ViewHolderIkut> {

    private Context context;
    private ArrayList<HistoryIkutKuis> historyIkutKuis;
    private BottomSheetDialog mBottomSheetDialog;

    public HistoryIkutKuisAdapter(Context context, ArrayList<HistoryIkutKuis> historyIkutKuis) {
        this.context = context;
        this.historyIkutKuis = historyIkutKuis;
    }

    @NonNull
    @Override
    public ViewHolderIkut onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolderIkut(LayoutInflater.from(context).inflate(R.layout.list_history_top_up, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderIkut viewHolderIkut, int i) {
        final HistoryIkutKuis ikutKuis = historyIkutKuis.get(i);
        viewHolderIkut.tvNomor.setText(ikutKuis.getNomor() + " " + ikutKuis.getJudul());
        viewHolderIkut.tvJumlah.setText(" "+ikutKuis.getPemain());
        viewHolderIkut.tvTgl.setText(ikutKuis.getTanggal());
        viewHolderIkut.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View bottomSheetLayout = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_dialog, null);
                Button b = bottomSheetLayout.findViewById(R.id.button_close);
                b.setVisibility(View.INVISIBLE);
                (bottomSheetLayout.findViewById(R.id.button_ok)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBottomSheetDialog.dismiss();
                    }
                });
                TextView tvTitle = bottomSheetLayout.findViewById(R.id.tv_title_sheet);
                tvTitle.setText(ikutKuis.getJudul());
                AppCompatRatingBar ratingBar = bottomSheetLayout.findViewById(R.id.ratingBarSheet);
                ratingBar.setRating(ikutKuis.getRating());
                TextView tvDeskripsi = bottomSheetLayout.findViewById(R.id.tv_detail_sheet);
                String detail = "Judul Kuis : " + ikutKuis.getJudul() + "\n" +
                                "Author Kuis : " + ikutKuis.getAuthor() + "\n" +
                                "Pemain : " + ikutKuis.getPemain() + "\n" +
                                "Jawaban yang benar : " + ikutKuis.getBenar() + "\n" +
                                "Jawaban yang salah : " + ikutKuis.getSalah() + "\n" +
                                "Nilai : " + ikutKuis.getNilai() + "\n" +
                                "Pada tanggal : " + ikutKuis.getTanggal();
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
        return historyIkutKuis.size() > 0 ? historyIkutKuis.size() : 0;
    }

    class ViewHolderIkut extends RecyclerView.ViewHolder {
        @BindView(R.id.tvNomor) TextView tvNomor;
        @BindView(R.id.tvJumlah) TextView tvJumlah;
        @BindView(R.id.tvTgl) TextView tvTgl;

        public ViewHolderIkut(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
