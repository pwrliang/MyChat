package com.gl.mychatclient.adapters;

/**
 * Created by gl on 2016/2/3.
 */
public class FriendItem {

    private byte[] portrait;
    private String username;

    public FriendItem(){}
    public FriendItem(byte[] portrait,String nickname){
        this.portrait = portrait;
        this.nickname = nickname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private String nickname;

    public byte[] getPortrait() {
        return portrait;
    }

    public void setPortrait(byte[] portrait) {
        this.portrait = portrait;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
