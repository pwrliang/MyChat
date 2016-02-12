package com.gl.mychatclient.apis.models;

/**
 * Created by gl on 2016/2/1.
 * 好友详细信息
 */
public class FriendProfile {
    private byte[] portrait;//nullable
    private String username;
    private String nickname;
    private String gender;
    private String region;
    private String description;
    private String registDate;
    private String phoneNumber;
    private String friendNote;//好友备注,

    public String getFriendNote() {
        return friendNote;
    }

    public String getRegistDate() {
        return registDate;
    }

    public void setRegistDate(String registDate) {
        this.registDate = registDate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setFriendNote(String friendNote) {
        this.friendNote = friendNote;
    }

    public byte[] getPortrait() {
        return portrait;
    }

    public void setPortrait(byte[] portrait) {
        this.portrait = portrait;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
