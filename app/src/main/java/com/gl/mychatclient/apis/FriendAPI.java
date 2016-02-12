package com.gl.mychatclient.apis;

import android.util.Base64;

import com.gl.mychatclient.Declaration;
import com.gl.mychatclient.apis.models.Application;
import com.gl.mychatclient.apis.models.FriendBrief;
import com.gl.mychatclient.apis.models.FriendProfile;
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
public class FriendAPI {
    public static List<FriendBrief> queryAllFriend() throws Exception {
        List<FriendBrief> briefList = new ArrayList<>();
        Map<String, String> params = new HashMap<>();
        params.put("action", "queryfriend");
        params.put("token", Declaration.configuration.getToken());
        String url = Declaration.SERVLET_URL + "/FriendServlet?" + NetworkUtility.encodeParam(params);
        String response = NetworkUtility.sendGetRequest(url);
        JSONObject jsonObject = new JSONObject(response);
        if (jsonObject.getInt("status") == 0)
            throw new Exception(jsonObject.getString("error"));
        if (!jsonObject.has("friends"))
            return null;
        JSONArray friendsObj = jsonObject.getJSONArray("friends");
        for (int i = 0; i < friendsObj.length(); ++i) {
            JSONObject friendObj = friendsObj.getJSONObject(i);
            FriendBrief brief = new FriendBrief();
            brief.setUsername(friendObj.getString("username"));
            if (friendObj.has("friendnote"))
                brief.setFriendNote(friendObj.getString("friendnote"));
            briefList.add(brief);
        }

        if (briefList.size() == 0)
            return null;
        return briefList;
    }

    public static FriendProfile fetchFriendProfile(String username) throws Exception {
        FriendProfile friendProfile = new FriendProfile();
        Map<String, String> params = new HashMap<String, String>();
        params.put("action", "queryprofilebyusername");
        params.put("token", Declaration.configuration.getToken());
        params.put("targetusername", username);
        String url = Declaration.SERVLET_URL + "/UserServlet?" + NetworkUtility.encodeParam(params);
        String response = NetworkUtility.sendGetRequest(url);
        JSONObject jsonObject = new JSONObject(response);
        if (jsonObject.getInt("status") == 0)
            throw new Exception(jsonObject.getString("error"));
        if (jsonObject.has("portrait"))
            friendProfile.setPortrait(Base64.decode(jsonObject.getString("portrait"), Base64.DEFAULT));
        friendProfile.setUsername(jsonObject.getString("username"));//非空
        friendProfile.setNickname(jsonObject.getString("nickname"));//非空
        if (jsonObject.has("gender"))
            friendProfile.setGender(jsonObject.getString("gender"));
        if (jsonObject.has("region"))
            friendProfile.setRegion(jsonObject.getString("region"));
        if (jsonObject.has("description"))
            friendProfile.setDescription(jsonObject.getString("description"));
        if(jsonObject.has("registdate"))
            friendProfile.setRegistDate(jsonObject.getString("registdate"));
        if(jsonObject.has("phonenumber"))
            friendProfile.setPhoneNumber(jsonObject.getString("phonenumber"));
        return friendProfile;
    }

    /*
    * 申请好友
    * @param username 被申请人用户名
    * @param description 备注
    * @return 成功返回true
    * */
    public static boolean applyFriend(String username, String description) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("action", "apply");
        params.put("token", Declaration.configuration.getToken());
        params.put("targetusername", username);
        params.put("description", description);
        String url = Declaration.SERVLET_URL + "/FriendServlet?" + NetworkUtility.encodeParam(params);
        String response = NetworkUtility.sendGetRequest(url);
        JSONObject jsonObject = new JSONObject(response);
        if (jsonObject.getInt("status") == 0) {
            throw new Exception(jsonObject.getString("error"));
        }
        return true;
    }

    /*
    * 查询好友申请
    * */
    public static List<Application> queryApplications() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("action", "queryapply");
        params.put("token", Declaration.configuration.getToken());
        String url = Declaration.SERVLET_URL + "/FriendServlet?" + NetworkUtility.encodeParam(params);
        String response = NetworkUtility.sendGetRequest(url);
        JSONObject jsonObject = new JSONObject(response);
        List<Application> applications = new ArrayList<>();
        if (jsonObject.getInt("status") == 0)
            throw new Exception(jsonObject.getString("error"));
        if (jsonObject.has("application")) {
            JSONArray jsonArray = jsonObject.getJSONArray("application");
            for (int i = 0; i < jsonArray.length(); ++i) {
                Application application = new Application();
                jsonObject = jsonArray.getJSONObject(i);
                application.setApplicant(jsonObject.getString("applicant"));
                if (jsonObject.has("description"))
                    application.setDescription(jsonObject.getString("description"));
                applications.add(application);
            }
        }
        if (applications.size() == 0)
            return null;
        return applications;
    }

    /*
    * 同意或拒绝好友请求
    * */
    public static void responseApplication(String applicant, String action) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("action", action.toLowerCase());
        params.put("token", Declaration.configuration.getToken());
        params.put("applicant", applicant);
        String url = Declaration.SERVLET_URL + "/FriendServlet?" + NetworkUtility.encodeParam(params);
        String response = NetworkUtility.sendGetRequest(url);
        JSONObject jsonObject = new JSONObject(response);
        if (jsonObject.getInt("status") == 0)
            throw new Exception("error");
    }

    /*
    * 查询是否是好友，以及被拉黑
    * */
    public static boolean isAvailable(String targetUsername) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("action", "isavailable");
        params.put("token", Declaration.configuration.getToken());
        params.put("targetusername", targetUsername);
        String url = Declaration.SERVLET_URL + "/FriendServlet?" + NetworkUtility.encodeParam(params);
        String response = NetworkUtility.sendGetRequest(url);
        JSONObject jsonObject = new JSONObject(response);
        if (jsonObject.getInt("status") == 0)
            throw new Exception("error");
        return jsonObject.getBoolean("available");
    }

    /*
    * 双向删除好友
    * */
    public static void deleteFriend(String targetUsername) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("action", "delete");
        params.put("token", Declaration.configuration.getToken());
        params.put("targetusername", targetUsername);
        String url = Declaration.SERVLET_URL + "/FriendServlet?" + NetworkUtility.encodeParam(params);
        String response = NetworkUtility.sendGetRequest(url);
        JSONObject jsonObject = new JSONObject(response);
        if (jsonObject.getInt("status") == 0)
            throw new Exception("error");
    }

    /*
    * 修改好友备注
    * */
    public static void updateFriendNote(String targetUsername, String friendNote) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("action", "updatefriendnote");
        params.put("token", Declaration.configuration.getToken());
        params.put("targetusername", targetUsername);
        params.put("friendnote", friendNote);
        String url = Declaration.SERVLET_URL + "/FriendServlet?" + NetworkUtility.encodeParam(params);
        String response = NetworkUtility.sendGetRequest(url);
        JSONObject jsonObject = new JSONObject(response);
        if (jsonObject.getInt("status") == 0)
            throw new Exception("error");
    }
}
