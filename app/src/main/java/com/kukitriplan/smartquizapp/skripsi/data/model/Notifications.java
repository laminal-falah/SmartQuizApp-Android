package com.kukitriplan.smartquizapp.skripsi.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Notifications implements Parcelable {
    private int id;
    private String title;
    private String subtitle;
    private String message;
    private String date;

    public Notifications() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeString(this.subtitle);
        dest.writeString(this.message);
        dest.writeString(this.date);
    }

    protected Notifications(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.subtitle = in.readString();
        this.message = in.readString();
        this.date = in.readString();
    }

    public static final Parcelable.Creator<Notifications> CREATOR = new Creator<Notifications>() {
        @Override
        public Notifications createFromParcel(Parcel source) {
            return new Notifications(source);
        }

        @Override
        public Notifications[] newArray(int size) {
            return new Notifications[size];
        }
    };
}
