package com.kukitriplan.smartquizapp.skripsi.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kukitriplan.smartquizapp.R;
import com.kukitriplan.smartquizapp.skripsi.data.model.Soal;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListJawabSoalAdapter extends RecyclerView.Adapter<ListJawabSoalAdapter.ViewHolderSoal> {

    private static final String TAG = ListJawabSoalAdapter.class.getSimpleName();

    public setOnItemSelector mSetOnItemSelector;

    private Context context;
    public ArrayList<Soal> soals;

    private RadioButton radioButton;

    public ListJawabSoalAdapter(Context context, ArrayList<Soal> soals) {
        this.context = context;
        this.soals = soals;
    }

    @NonNull
    @Override
    public ViewHolderSoal onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolderSoal(LayoutInflater.from(context).inflate(R.layout.card_soal, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderSoal viewHolderSoal, int i) {
        //final Soal soal = soals.get(i);
        viewHolderSoal.idSoal = soals.get(i).getIdSoal();
        viewHolderSoal.idKuis = soals.get(i).getIdKuis();
        viewHolderSoal.tvJudulSoal.setText(soals.get(i).getJudulSoal());
        viewHolderSoal.rbPilihanA.setText(context.getResources().getString(R.string.txtPilihanA, soals.get(i).getA()));
        viewHolderSoal.rbPilihanB.setText(context.getResources().getString(R.string.txtPilihanB, soals.get(i).getB()));
        viewHolderSoal.rbPilihanC.setText(context.getResources().getString(R.string.txtPilihanC, soals.get(i).getC()));
        viewHolderSoal.rbPilihanD.setText(context.getResources().getString(R.string.txtPilihanD, soals.get(i).getD()));
        viewHolderSoal.rbPilihanE.setText(context.getResources().getString(R.string.txtPilihanE, soals.get(i).getE()));
        viewHolderSoal.btnJawabSoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolderSoal.rgPilihan.getCheckedRadioButtonId() != -1) {
                    int jwb = viewHolderSoal.rgPilihan.getCheckedRadioButtonId();
                    radioButton = viewHolderSoal.itemView.findViewById(jwb);
                    String jawaban = radioButton.getText().toString();
                    String[] parts = jawaban.split(". ");
                    String pilihan = parts[0];
                    mSetOnItemSelector.onItemClick(viewHolderSoal.idKuis, viewHolderSoal.idSoal, pilihan);
                } else {
                    Toast.makeText(v.getContext(), "Pilih Jawaban ", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return soals.size() > 0 ? soals.size() : 0;
    }

    public class ViewHolderSoal extends RecyclerView.ViewHolder {
        public String idSoal;
        public String idKuis;
        @BindView(R.id.tvJudulSoal) public TextView tvJudulSoal;
        @BindView(R.id.rgPilihan) public RadioGroup rgPilihan;
        @BindView(R.id.rbPilihanA) public RadioButton rbPilihanA;
        @BindView(R.id.rbPilihanB) public RadioButton rbPilihanB;
        @BindView(R.id.rbPilihanC) public RadioButton rbPilihanC;
        @BindView(R.id.rbPilihanD) public RadioButton rbPilihanD;
        @BindView(R.id.rbPilihanE) public RadioButton rbPilihanE;
        @BindView(R.id.btnJawabSoal) public Button btnJawabSoal;

        public ViewHolderSoal(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void SetOnItemClickListener(final setOnItemSelector mSetOnItemSelector) {
        this.mSetOnItemSelector = mSetOnItemSelector;
    }

    public interface setOnItemSelector {
        void onItemClick(String a, String b, String c);
    }
}
