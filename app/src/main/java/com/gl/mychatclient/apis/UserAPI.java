package com.gl.mychatclient.apis;

import com.gl.mychatclient.Declaration;
import com.gl.mychatclient.apis.models.FriendProfile;
import com.gl.mychatclient.apis.models.Notification;
import com.gl.mychatclient.utils.NetworkUtility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gl on 2016/2/3.
 */
public class UserAPI {
    public static boolean validateToken() {
        Map<String, String> params = new HashMap<>();
        params.put("action", "validatetoken");
        params.put("token", Declaration.configuration.getToken());
        String url = Declaration.SERVLET_URL + "/UserServlet?" + NetworkUtility.encodeParam(params);
        try {
            String response = NetworkUtility.sendGetRequest(url);
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getInt("status") == 1)
                return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<Notification> queryNotifications() {
        Map<String, String> params = new HashMap<>();
        params.put("action", "querynotification");
        params.put("token", Declaration.configuration.getToken());
        String url = Declaration.SERVLET_URL + "/UserServlet?" + NetworkUtility.encodeParam(params);
        List<Notification> notifications = new ArrayList<>();
        try {
            String response = NetworkUtility.sendGetRequest(url);
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getInt("status") == 0 || !jsonObject.has("notifications"))
                return null;
            JSONArray jsonArray = jsonObject.getJSONArray("notifications");
            for (int i = 0; i < jsonArray.length(); ++i) {
                jsonObject = jsonArray.getJSONObject(i);
                Notification notification = new Notification();
                notification.setNotificationId(jsonObject.getInt("notificationid"));
                notification.setNotification(jsonObject.getString("notification"));
                notifications.add(notification);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return notifications;
    }

    /*
    * 修改自己的名片
    * */
    public static void updateProfile(FriendProfile profile) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("action", "updateprofile");
        params.put("token", Declaration.configuration.getToken());
        params.put("nickname", profile.getNickname());
        params.put("gender", profile.getGender());
        params.put("region", profile.getRegion());
        params.put("description", profile.getDescription());
        params.put("phonenumber", profile.getPhoneNumber());
        String url = Declaration.SERVLET_URL + "/UserServlet?" + NetworkUtility.encodeParam(params);
        String response = NetworkUtility.sendPostRequest(url, profile.getPortrait());
        JSONObject jsonObject = new JSONObject(response);
        if (jsonObject.getInt("status") == 0)
            throw new Exception(jsonObject.getString("error"));
    }
}
