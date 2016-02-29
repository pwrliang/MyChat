package com.gl.mychatclient;

import android.app.Application;
import android.os.Environment;

import com.gl.mychatclient.utils.Configuration;
import com.gl.mychatclient.utils.LogUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by gl on 2016/1/31.
 */
public class MyChatApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Declaration.configuration = Configuration.getInstance(this);
        try {
            File file = new File("/sdcard/ip.txt");
            if (!file.exists())
                return;
            FileInputStream in = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
            String ip = reader.readLine();
            Declaration.SERVLET_URL = "http://" + ip + "/MyChatServer/servlet";
            LogUtil.i("MyChatApplication", Declaration.SERVLET_URL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
