package com.gl.mychatclient.adapters;

import com.gl.mychatclient.apis.models.Application;

/**
 * Created by gl on 2016/2/10.
 */
public class ApplicationItem extends Application{
    private byte[] portrait;
    private String nickname;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public byte[] getPortrait() {
        return portrait;
    }

    public void setPortrait(byte[] portrait) {
        this.portrait = portrait;
    }
}
