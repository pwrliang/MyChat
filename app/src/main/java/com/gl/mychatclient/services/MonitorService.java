package com.gl.mychatclient.services;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.gl.mychatclient.activities.ChatActivity;
import com.gl.mychatclient.activities.MainActivity;
import com.gl.mychatclient.apis.MessageAPI;
import com.gl.mychatclient.apis.NotifiactionAPI;
import com.gl.mychatclient.apis.models.FriendProfile;
import com.gl.mychatclient.apis.models.Message;
import com.gl.mychatclient.apis.models.Notification;
import com.gl.mychatclient.apis.models.Session;
import com.gl.mychatclient.db.FriendsDB;
import com.gl.mychatclient.db.MessageDB;
import com.gl.mychatclient.db.SessionDB;
import com.gl.mychatclient.utils.LogUtil;
import com.gl.mychatclient.utils.MyChatUtility;

import java.util.List;


/**
 * Created by gl on 2016/2/3.
 * 向服务器查询通知、消息
 */
public class MonitorService extends IntentService {
    private final String TAG = getClass().getSimpleName();
    private LocalBroadcastManager localBroadcastManager;

    public MonitorService() {
        super("MonitorService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        while (true) {
            try {
                Thread.sleep(2000);
                fetchMessages();
                Thread.sleep(2000);
                fetchNotifications();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取消息
     * 若有聊天窗口，则通过广播发送
     * 否则保存到数据库
     * */
    private void fetchMessages() {
        try {
            List<Message> messages = MessageAPI.queryMessages();
            if (messages == null) return;
            MessageDB messageDB = MessageDB.getInstance(this);
            for (Message message : messages) {
                FriendsDB friendsDB = FriendsDB.getInstance(this);
                FriendProfile friendProfile = friendsDB.findFriend(message.getUsername());
                Session session = new Session();
                session.setUsername(friendProfile.getUsername());
                if (message.getType() == Message.VALUE_LEFT_TEXT) {
                    session.setLastWord(message.getText());
                } else if (message.getType() == Message.VALUE_LEFT_IMAGE) {
                    session.setLastWord("(图片)");
                }
                LogUtil.i(TAG,"收到！！！！！！！！");
                session.setTime(System.currentTimeMillis());
                SessionDB sessionDB = SessionDB.getInstance(this);
                sessionDB.saveSession(session); //保存会话
                messageDB.saveMessage(message, "UNREAD");//保存消息,未读
                //当前是主Activity，更新session
                if (MyChatUtility.getRunningActivityName(this).equals("com.gl.mychatclient.activities.MainActivity")) {
                    Intent intent = new Intent("com.gl.mychatclient.BROADCAST_UPDATE_SESSION");
                    localBroadcastManager.sendBroadcast(intent);
                }
                if (MyChatUtility.getRunningActivityName(this).
                        equals("com.gl.mychatclient.activities.ChatActivity")) {//当前是聊天Activity
                    Intent intent = new Intent("com.gl.mychatclient.BROADCAST_RECEIVE_MESSAGE");
                    localBroadcastManager.sendBroadcast(intent);
                } else { //没开启聊天窗口，则发送通知
                    if (message.getType() == Message.VALUE_LEFT_TEXT) {
                        Intent intent = new Intent(this, ChatActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (friendProfile.getFriendNote() != null)
                            intent.putExtra("title", friendProfile.getFriendNote());
                        else
                            intent.putExtra("title", friendProfile.getNickname());
                        intent.putExtra("username", message.getUsername());
                        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        int sessionId = sessionDB.findSessionId(friendProfile.getUsername());
                        if (friendProfile.getFriendNote() != null) {
                            MyChatUtility.sendNotification(this, sessionId, friendProfile.getFriendNote(), message.getText(), pendingIntent);
                        } else {
                            MyChatUtility.sendNotification(this, sessionId, friendProfile.getNickname(), message.getText(), pendingIntent);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchNotifications() {
        try {
            List<Notification> notifications = NotifiactionAPI.fetchNotifications();
            if (notifications == null)
                return;
            for (Notification notification : notifications) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                MyChatUtility.sendNotification(this, notification.getNotificationId(), "新通知", notification.getNotification(), pendingIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}




