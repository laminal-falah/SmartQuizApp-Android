package com.kukitriplan.smartquizapp.skripsi.data.json;

import com.kukitriplan.smartquizapp.skripsi.data.model.Notifications;

public class NotificationJson {
    private Notifications notifications;
    private Notifications[] notificationsList;

    public Notifications getNotifications() {
        return notifications;
    }

    public void setNotifications(Notifications notifications) {
        this.notifications = notifications;
    }

    public Notifications[] getNotificationsList() {
        return notificationsList;
    }

    public void setNotificationsList(Notifications[] notificationsList) {
        this.notificationsList = notificationsList;
    }
}
