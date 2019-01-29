package com.kukitriplan.smartquizapp.skripsi.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kukitriplan.smartquizapp.R;
import com.kukitriplan.smartquizapp.skripsi.data.model.Soal;
import com.kukitriplan.smartquizapp.skripsi.ui.dashboard.ListSoalActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListSoalAdapter extends RecyclerView.Adapter<ListSoalAdapter.ViewHolderSoal> {

    private Context context;
    public ArrayList<Soal> soals;
    private BottomSheetDialog mBottomSheetDialog;
    private ListSoalActivity soalActivity = new ListSoalActivity();

    public ListSoalAdapter(Context context, ArrayList<Soal> soals) {
        this.context = context;
        this.soals = soals;
    }

    @NonNull
    @Override
    public ViewHolderSoal onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolderSoal(LayoutInflater.from(context).inflate(R.layout.card_list_soal, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderSoal viewHolderSoal, int i) {
        final Soal soal = soals.get(i);
        viewHolderSoal.tvNomorSoal.setText(viewHolderSoal.itemView.getContext().getResources().getString(R.string.txtNomorSoal, soal.getNomorSoal()));
        viewHolderSoal.tvJudulSoal.setText(soal.getJudulSoal());
        viewHolderSoal.tvJawaban.setText(viewHolderSoal.itemView.getContext().getResources().getString(R.string.txtKunciJawaban, soal.getKunciJawaban()));
        viewHolderSoal.tvPilihanA.setText(viewHolderSoal.itemView.getContext().getResources().getString(R.string.txtPilihanA, soal.getA()));
        viewHolderSoal.tvPilihanB.setText(viewHolderSoal.itemView.getContext().getResources().getString(R.string.txtPilihanB, soal.getB()));
        viewHolderSoal.tvPilihanC.setText(viewHolderSoal.itemView.getContext().getResources().getString(R.string.txtPilihanC, soal.getC()));
        viewHolderSoal.tvPilihanD.setText(viewHolderSoal.itemView.getContext().getResources().getString(R.string.txtPilihanD, soal.getD()));
        viewHolderSoal.tvPilihanE.setText(viewHolderSoal.itemView.getContext().getResources().getString(R.string.txtPilihanE, soal.getE()));
        viewHolderSoal.id = soal.getIdSoal();
    }

    @Override
    public int getItemCount() {
        return soals.size() > 0 ? soals.size() : 0;
    }

    class ViewHolderSoal extends RecyclerView.ViewHolder {
        String id;
        @BindView(R.id.tvNomorSoal) TextView tvNomorSoal;
        @BindView(R.id.tvJudulSoal) TextView tvJudulSoal;
        @BindView(R.id.tvPilihanA) TextView tvPilihanA;
        @BindView(R.id.tvPilihanB) TextView tvPilihanB;
        @BindView(R.id.tvPilihanC) TextView tvPilihanC;
        @BindView(R.id.tvPilihanD) TextView tvPilihanD;
        @BindView(R.id.tvPilihanE) TextView tvPilihanE;
        @BindView(R.id.tvJawaban) TextView tvJawaban;
        public ViewHolderSoal(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
