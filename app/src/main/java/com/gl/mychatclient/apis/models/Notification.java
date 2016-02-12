package com.gl.mychatclient.apis.models;

/**
 * Created by gl on 2016/2/3.
 */
public class Notification {
    private int notificationId;
    private String notification;

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }
}
