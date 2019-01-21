package com.kukitriplan.smartquizapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kukitriplan.smartquizapp.R;
import com.kukitriplan.smartquizapp.data.model.Kategori;
import com.kukitriplan.smartquizapp.ui.home.KategoriActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class KategoriAdapter extends RecyclerView.Adapter<KategoriAdapter.ViewHolderKategori> {

    private final ArrayList<Kategori> kategoris;
    private final Context context;

    public KategoriAdapter(ArrayList<Kategori> kategoris, Context context) {
        this.kategoris = kategoris;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolderKategori onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolderKategori(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_kategori, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderKategori viewHolderKategori, int i) {
        final Kategori kategori = kategoris.get(i);
        final String id = kategori.getId();
        viewHolderKategori.title.setText(kategori.getTitle());
        try {
            Picasso.with(context).load(kategori.getIcon()).resize(140,120).into(viewHolderKategori.iv_icon);
        } catch (Exception e) {
            e.printStackTrace();
        }
        viewHolderKategori.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context,kategori.getTitle().trim(), Toast.LENGTH_LONG).show();
                v.getContext().startActivity(new Intent(v.getContext(), KategoriActivity.class)
                        .putExtra("id", id)
                        .putExtra("nama", kategori.getTitle())
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
    }

    @Override
    public int getItemCount() {
        return kategoris.size();
    }

    class ViewHolderKategori extends RecyclerView.ViewHolder {
        private final TextView title;
        private final ImageView iv_icon;

        ViewHolderKategori(@NonNull final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            title = itemView.findViewById(R.id.tv_title_sheet);
            iv_icon = itemView.findViewById(R.id.iv_icon);
        }
    }
}
