package com.gl.mychatclient.apis.models;

/**
 * Created by gl on 2016/2/2.
 */
public class Message {
    public static final int VALUE_TIME_TIP = 0;// 7种不同的布局
    public static final int VALUE_LEFT_TEXT = 1;
    public static final int VALUE_LEFT_IMAGE = 2;
    public static final int VALUE_LEFT_AUDIO = 3;
    public static final int VALUE_RIGHT_TEXT = 4;
    public static final int VALUE_RIGHT_IMAGE = 5;
    public static final int VALUE_RIGHT_AUDIO = 6;

    private String username;
    private String text;
    private byte[] image;
    private byte[] audio;
    private String status;
    private int type;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public byte[] getAudio() {
        return audio;
    }

    public void setAudio(byte[] audio) {
        this.audio = audio;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
