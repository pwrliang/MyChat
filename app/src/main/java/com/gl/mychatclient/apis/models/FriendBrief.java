package com.gl.mychatclient.apis.models;

/**
 * Created by gl on 2016/2/3.
 * 好友简述，查看好友列表使用
 * 调用queryfriend
 */
public class FriendBrief {
    private String username;
    private String friendNote;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFriendNote() {
        return friendNote;
    }

    public void setFriendNote(String friendNote) {
        this.friendNote = friendNote;
    }
}
