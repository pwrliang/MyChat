package com.gl.mychatclient.apis;

import com.gl.mychatclient.Declaration;
import com.gl.mychatclient.apis.models.Notification;
import com.gl.mychatclient.utils.NetworkUtility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gl on 2016/2/5.
 */
public class NotifiactionAPI {
    public static List<Notification> fetchNotifications() {
        List<Notification> notifications = new ArrayList<>();
        Map<String, String> params = new HashMap<String, String>();
        params.put("action", "querynotification");
        params.put("token", Declaration.configuration.getToken());
        String url = Declaration.SERVLET_URL + "/UserServlet?" + NetworkUtility.encodeParam(params);
        try {
            String response = NetworkUtility.sendGetRequest(url);
            if (response == null) return null;
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getInt("status") == 0)
                throw new Exception(jsonObject.getString("error"));
            if (jsonObject.has("notifications")) {
                JSONArray friendsJSON = jsonObject.getJSONArray("notifications");
                for (int i = 0; i < friendsJSON.length(); ++i) {
                    Notification notification = new Notification();
                    jsonObject = friendsJSON.getJSONObject(i);
                    notification.setNotificationId(jsonObject.getInt("notificationid"));
                    notification.setNotification(jsonObject.getString("notification"));
                    notifications.add(notification);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (notifications.size() == 0) return null;
        return notifications;
    }
}
