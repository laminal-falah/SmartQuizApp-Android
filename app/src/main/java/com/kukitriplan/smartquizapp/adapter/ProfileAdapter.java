package com.kukitriplan.smartquizapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kukitriplan.smartquizapp.R;
import com.kukitriplan.smartquizapp.data.model.Account;
import com.kukitriplan.smartquizapp.data.shared.SharedLoginManager;
import com.kukitriplan.smartquizapp.utils.SnackBarUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolderProfile> {

    private ArrayList<Account> accounts;
    private Context context;
    private SharedLoginManager prefManager;

    public ProfileAdapter(Context context, ArrayList<Account> accounts) {
        this.context = context;
        this.accounts = accounts;
        prefManager = new SharedLoginManager(context);
    }

    @NonNull
    @Override
    public ViewHolderProfile onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolderProfile(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_profile, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderProfile viewHolderProfile, int i) {
        final Account account = accounts.get(i);
        viewHolderProfile.tvTitle.setText(account.getTitle());
        viewHolderProfile.tvDesc.setText(account.getDescription());
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }

    class ViewHolderProfile extends RecyclerView.ViewHolder {
        @BindView(R.id.tvTitleListProfile) TextView tvTitle;
        @BindView(R.id.tvDescListProfile) TextView tvDesc;

        public ViewHolderProfile(@NonNull final View itemView) {
            super(itemView);
            ButterKnife.bind(this ,itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (prefManager.getSpLevel().equals("author")) {
                        Snackbar.make(v, accounts.get(getAdapterPosition()).getTitle() + " : " +
                                accounts.get(getAdapterPosition()).getDescription(), Snackbar.LENGTH_SHORT).show();
                    } else {
                        SnackBarUtils.SnackBarUtils(v, accounts.get(getAdapterPosition()).getTitle() + " : " +
                                accounts.get(getAdapterPosition()).getDescription(), Snackbar.LENGTH_SHORT);
                    }
                }
            });
        }
    }
}
