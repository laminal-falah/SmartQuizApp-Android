package com.kukitriplan.smartquizapp.skripsi.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kukitriplan.smartquizapp.R;
import com.kukitriplan.smartquizapp.skripsi.data.model.HistoryWithDraw;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HistoryWithDrawAdapter extends RecyclerView.Adapter<HistoryWithDrawAdapter.ViewHolderWithDraw> {

    private Context context;
    private ArrayList<HistoryWithDraw> historyWithDraws;
    private BottomSheetDialog mBottomSheetDialog;

    public HistoryWithDrawAdapter(Context context, ArrayList<HistoryWithDraw> historyWithDraws) {
        this.context = context;
        this.historyWithDraws = historyWithDraws;
    }

    @NonNull
    @Override
    public ViewHolderWithDraw onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolderWithDraw(LayoutInflater.from(context).inflate(R.layout.list_history_top_up, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderWithDraw viewHolder, int i) {
        final HistoryWithDraw withDraw = historyWithDraws.get(i);
        viewHolder.tvNomor.setText(historyWithDraws.get(i).getNomor());
        viewHolder.tvJumlah.setText(historyWithDraws.get(i).getTitle());
        viewHolder.tvTgl.setText(historyWithDraws.get(i).getTanggal());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View bottomSheetLayout = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_wd, null);
                (bottomSheetLayout.findViewById(R.id.button_ok)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBottomSheetDialog.dismiss();
                    }
                });
                TextView tvTitle = bottomSheetLayout.findViewById(R.id.tv_title_sheet);
                tvTitle.setText("History WithDraw");
                TextView tvDeskripsi = bottomSheetLayout.findViewById(R.id.tv_detail_sheet);
                String detail = withDraw.getTitle() + "\n" +
                                "Status Withdraw : " + withDraw.getStatus() + "\n" +
                                "Pada tanggal : " + withDraw.getTanggal() + "\n" +
                                "Dengan Nama Bank : " + withDraw.getNamaBank() + "\n" +
                                "No. Rekening : " + withDraw.getRekening() + "\n" +
                                "Dengan Atas Nama : " + withDraw.getAtasNama();
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
        return historyWithDraws.size() > 0 ? historyWithDraws.size() : 0;
    }

    class ViewHolderWithDraw extends RecyclerView.ViewHolder {
        @BindView(R.id.tvNomor) TextView tvNomor;
        @BindView(R.id.tvJumlah) TextView tvJumlah;
        @BindView(R.id.tvTgl) TextView tvTgl;

        public ViewHolderWithDraw(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
