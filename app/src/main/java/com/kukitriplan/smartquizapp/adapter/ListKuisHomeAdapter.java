package com.kukitriplan.smartquizapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kukitriplan.smartquizapp.R;
import com.kukitriplan.smartquizapp.data.model.Kuis;
import com.kukitriplan.smartquizapp.ui.home.DetailKuisActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class ListKuisHomeAdapter extends RecyclerView.Adapter<ListKuisHomeAdapter.ViewHolderKuis> {

    private final ArrayList<Kuis> kuisList;
    private final Context context;

    public ListKuisHomeAdapter(ArrayList<Kuis> kuis, Context context) {
        this.kuisList = kuis;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolderKuis onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolderKuis(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_list_kuis, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderKuis viewHolderKuis, int i) {
        final Kuis kuis = kuisList.get(i);
        viewHolderKuis.tvTitle.setText(kuis.getJudul());
        viewHolderKuis.tvHrg.setText(kuis.getHarga());
        viewHolderKuis.tvSoal.setText(context.getResources().getString(R.string.txtSoalKuis,kuis.getSoal()));
        viewHolderKuis.tvDurasi.setText(context.getResources().getString(R.string.txtDurasiKuis,kuis.getDurasi()));
        Picasso.with(context).load(kuis.getCover()).fit().into(viewHolderKuis.ivCover);
        viewHolderKuis.ratingBar.setRating(kuis.getRating());
        viewHolderKuis.tvAuthor.setText(kuis.getAuthor());
        viewHolderKuis.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.getContext().startActivity(new Intent(context, DetailKuisActivity.class)
                        .putExtra("slug", kuis.getSlug())
                        .putExtra("tipe","home")
                        .putExtra("nama", kuis.getJudul())
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
    }

    @Override
    public int getItemCount() {
        return kuisList.size() > 0 ? kuisList.size() : 0;
    }

    class ViewHolderKuis extends RecyclerView.ViewHolder {
        private final TextView tvTitle;
        private final TextView tvHrg;
        private final TextView tvSoal;
        private final TextView tvDurasi;
        private final TextView tvAuthor;
        private final ImageView ivCover;
        private final AppCompatRatingBar ratingBar;

        ViewHolderKuis(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            tvTitle = itemView.findViewById(R.id.tv_title_kuis);
            tvHrg = itemView.findViewById(R.id.tv_hrg_kuis);
            tvSoal = itemView.findViewById(R.id.tv_soal_kuis);
            tvDurasi = itemView.findViewById(R.id.tv_durasi_kuis);
            ivCover = itemView.findViewById(R.id.iv_cover);
            ratingBar = itemView.findViewById(R.id.ratingBar_kuis);
            tvAuthor = itemView.findViewById(R.id.tv_author_kuis);
        }
    }
}
