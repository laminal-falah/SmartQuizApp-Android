package com.kukitriplan.smartquizapp.adapter;

import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kukitriplan.smartquizapp.R;
import com.kukitriplan.smartquizapp.data.model.Kuis;
import com.kukitriplan.smartquizapp.ui.dashboard.BuatSoalActivity;
import com.kukitriplan.smartquizapp.ui.dashboard.ListSoalActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListKuisAuthorAdapter extends RecyclerView.Adapter<ListKuisAuthorAdapter.ViewHistory> {

    private Context context;
    public ArrayList<Kuis> kuis;
    private BottomSheetDialog mBottomSheetDialog;

    public ListKuisAuthorAdapter(Context context, ArrayList<Kuis> kuis) {
        this.context = context;
        this.kuis = kuis;
    }

    @NonNull
    @Override
    public ViewHistory onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHistory(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_kuis_author, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHistory viewHistory, int i) {
        final Kuis kuis1 = kuis.get(i);
        viewHistory.id = kuis1.getId_kuis();
        viewHistory.tvNomor.setText(kuis1.getNomor());
        viewHistory.tvJudul.setText(kuis1.getJudul());
        viewHistory.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View bottomSheetLayout = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_kuis, null);
                (bottomSheetLayout.findViewById(R.id.btnTmbhSoal)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.getContext().startActivity(new Intent(v.getContext(), BuatSoalActivity.class)
                                .putExtra("slugKuis", kuis1.getSlug())
                                .putExtra("judulKuis", kuis1.getJudul())
                                .putExtra("nomorSoal", kuis1.getNomorSoal()));;
                    }
                });
                (bottomSheetLayout.findViewById(R.id.btnListSoal)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.getContext().startActivity(new Intent(v.getContext(), ListSoalActivity.class)
                                .putExtra("slugKuis", kuis1.getSlug())
                                .putExtra("judulKuis", kuis1.getJudul()));
                    }
                });
                TextView tvTitle = bottomSheetLayout.findViewById(R.id.tv_title_sheet);
                tvTitle.setText(kuis1.getJudul());
                AppCompatRatingBar ratingBar = bottomSheetLayout.findViewById(R.id.ratingBarSheet);
                ratingBar.setRating(kuis1.getRating());
                ImageView cover = bottomSheetLayout.findViewById(R.id.coverKuis);
                Picasso.with(context).load(kuis1.getCover()).fit().into(cover);
                TextView tvDeskripsi = bottomSheetLayout.findViewById(R.id.tvDeskripsiKuis);
                String str = "Jumlah Soal : " + kuis1.getSoal() + " Item \n" +
                             "Durasi Kuis : " + kuis1.getDurasi() + " Menit \n" +
                             "Status Kuis : " + kuis1.getStatus() + "\n" +
                             "Harga Kuis : " + kuis1.getHarga() + "\n" +
                             "Author : " + kuis1.getAuthor() + "\n" +
                             "Kategori : " + kuis1.getNm_kategori() + "\n" +
                             "Mata Pelajaran : " + kuis1.getNm_mapel() + "\n" +
                             "Deskripsi : " + kuis1.getDeskripsi() + "\n";
                tvDeskripsi.setText(str);
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
        return kuis.size() > 0 ? kuis.size() : '0';
    }

    class ViewHistory extends RecyclerView.ViewHolder {
        @BindView(R.id.tvNomor) TextView tvNomor;
        @BindView(R.id.tvJudulKuisAuthor) TextView tvJudul;
        String id;
        public ViewHistory(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}