package com.gl.mychatclient.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by gl on 2016/1/31.
 */
public class NetworkUtility {
    public static String encodeParam(Map<String, String> keyValue) {
        StringBuilder result = new StringBuilder();
        Set<String> keys = keyValue.keySet();
        Iterator<String> iterator = keys.iterator();
        try {
            while (iterator.hasNext()) {
                String key = iterator.next();
                if (keyValue.get(key) != null) {
                    result.append(key).append('=')
                            .append(URLEncoder.encode(keyValue.get(key), "UTF-8"))
                            .append('&');
                }else {
                    LogUtil.i("NetworkUtility",key+"为空");
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        return result.toString().substring(0, result.length() - 1);
    }

    public static String sendGetRequest(final String address) throws IOException {
        BufferedReader reader = null;
        HttpURLConnection connection = null;
        URL url = new URL(address);
        connection = (HttpURLConnection) url.openConnection();
        connection.setUseCaches(false);
//        connection.setRequestMethod("GET");
//        connection.setDoOutput(true);
        connection.connect();
        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {//请求失败
            return null;
        }
        reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
        String line = null;
        StringBuilder response = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        if (reader != null)
            reader.close();
        if (connection != null)
            connection.disconnect();
        return response.toString();
    }

    public static String sendPostRequest(String address, byte[] bytes) throws IOException {
        URL url = new URL(address);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        connection.setUseCaches(false);
        connection.setRequestMethod("POST");
        connection.connect();
        if (bytes != null) {
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(bytes);
            outputStream.flush();
        }
        connection.connect();
        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {//请求失败
            return null;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
        String tmp;
        StringBuilder response = new StringBuilder();
        while ((tmp = reader.readLine()) != null) {
            response.append(tmp);
        }
        reader.close();
        connection.disconnect();
        return response.toString();
    }
}
