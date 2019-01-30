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
import com.kukitriplan.smartquizapp.skripsi.data.model.Notifications;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolderNotification> {

    private Context mContext;
    private ArrayList<Notifications> notifications;

    public NotificationAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public ArrayList<Notifications> getNotifications() {
        return notifications;
    }

    public void setNotifications(ArrayList<Notifications> list) {
        this.notifications = list;
    }

    @NonNull
    @Override
    public ViewHolderNotification onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolderNotification(LayoutInflater.from(mContext).inflate(R.layout.list_notification, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderNotification viewHolderNotification, int i) {
        try {
            viewHolderNotification.tvSubtitleNotif.setText(getNotifications().get(i).getSubtitle());
            viewHolderNotification.tvTitleNotif.setText(getNotifications().get(i).getTitle());
            viewHolderNotification.tvMessageNotif.setText(getNotifications().get(i).getMessage());
            viewHolderNotification.itemView.setOnClickListener(new CustomOnItemClickListener(i, new OnItemClickCallback() {
                @Override
                public void onItemClicked(View view, int position) {
                    Toast.makeText(view.getContext(), "Hapus Notifikasi ? ", Toast.LENGTH_SHORT).show();
                }
            }));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return notifications.size() > 0 ? notifications.size() : 0;
    }

    class ViewHolderNotification extends RecyclerView.ViewHolder {
        @BindView(R.id.tvSubtitleNotif) TextView tvSubtitleNotif;
        @BindView(R.id.tvTitleNotif) TextView tvTitleNotif;
        @BindView(R.id.tvMessageNotif) TextView tvMessageNotif;

        public ViewHolderNotification(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class CustomOnItemClickListener implements View.OnClickListener {
        private int position;
        private OnItemClickCallback onItemClickCallback;

        public CustomOnItemClickListener(int position, OnItemClickCallback onItemClickCallback) {
            this.position = position;
            this.onItemClickCallback = onItemClickCallback;
        }

        @Override
        public void onClick(View v) {
            onItemClickCallback.onItemClicked(v, position);
        }
    }

    public interface OnItemClickCallback {
        void onItemClicked(View view, int position);
    }
}
