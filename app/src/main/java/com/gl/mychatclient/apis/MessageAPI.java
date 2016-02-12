package com.gl.mychatclient.apis;

import android.util.Base64;

import com.gl.mychatclient.Declaration;
import com.gl.mychatclient.apis.models.Message;
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
public class MessageAPI {

    public static void sendMessage(Message message) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("action", "sendmessage");
        params.put("token", Declaration.configuration.getToken());
        params.put("receiver", message.getUsername());
        if (message.getType() == Message.VALUE_RIGHT_TEXT)
            params.put("contenttype", "text");
        else if (message.getType() == Message.VALUE_RIGHT_IMAGE)
            params.put("contenttype", "picture");

        String url = Declaration.SERVLET_URL + "/MessageServlet?" + NetworkUtility.encodeParam(params);
        byte[] bytes = null;
        if (message.getType() == Message.VALUE_RIGHT_TEXT) {//文字消息
            bytes = message.getText().getBytes("UTF-8");
        } else if (message.getType() == Message.VALUE_RIGHT_IMAGE) {//图片消息
            bytes = message.getImage();
        }
        String response = NetworkUtility.sendPostRequest(url, bytes);//内容转换为字节
        JSONObject jsonObject = new JSONObject(response);
        int status = jsonObject.getInt("status");
        if (status == 0) {//返回错误
            throw new Exception(jsonObject.getString("error"));
        }
    }

    public static List<Message> queryMessages() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("action", "receivemessage");
        params.put("token", Declaration.configuration.getToken());
        String url = Declaration.SERVLET_URL + "/MessageServlet?" + NetworkUtility.encodeParam(params);
        List<Message> messages = new ArrayList<>();
        try {
            String response = NetworkUtility.sendGetRequest(url);
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getInt("status") == 0)
                throw new Exception(jsonObject.getString("error"));
            if (jsonObject.has("messages")) {
                JSONArray msgArray = jsonObject.getJSONArray("messages");
                for (int i = 0; i < msgArray.length(); ++i) {
                    JSONObject msgObj = msgArray.getJSONObject(i);
                    Message msg = new Message();
                    if (msgObj.getString("contenttype").equals("TEXT")) {
                        msg.setType(Message.VALUE_LEFT_TEXT);
                        msg.setText(msgObj.getString("content"));
                    }
                    else if (msgObj.getString("contenttype").equals("PICTURE")) {//传来的图片用Base64编码过
                        msg.setType(Message.VALUE_LEFT_IMAGE);
                        msg.setImage(Base64.decode(msgObj.getString("content"), Base64.DEFAULT));//解码
                    }
                    msg.setUsername(msgObj.getString("sender"));
                    messages.add(msg);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (messages.size() == 0) return null;
        return messages;
    }
}
