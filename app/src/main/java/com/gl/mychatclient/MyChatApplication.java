package com.gl.mychatclient;

import android.app.Application;

import com.gl.mychatclient.utils.Configuration;

/**
 * Created by gl on 2016/1/31.
 */
public class MyChatApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Declaration.configuration = Configuration.getInstance(this);
    }
}
