package com.gl.mychatclient.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.gl.mychatclient.Declaration;
import com.gl.mychatclient.R;
import com.gl.mychatclient.apis.UserAPI;
import com.gl.mychatclient.utils.NetworkUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gl on 2016/1/31.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout llProgress;
    private Button btnLogin;
    private Button btnRegistration;
    private EditText edtUsername;
    private EditText edtPassword;
    private final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        llProgress = (LinearLayout) findViewById(R.id.al_ll_progress);
        btnLogin = (Button) findViewById(R.id.al_btn_login);
        btnRegistration = (Button) findViewById(R.id.al_btn_registration);
        edtUsername = (EditText) findViewById(R.id.al_edt_username);
        edtPassword = (EditText) findViewById(R.id.al_edt_password);
        btnLogin.setOnClickListener(this);
        btnRegistration.setOnClickListener(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.al_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.user_login);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                //在线验证token有效性
                if (Declaration.configuration.getToken() != null && UserAPI.validateToken()) {//token有效,启动主窗口
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }
            }
        }).start();
    }

    private class GetData extends AsyncTask<String, Void, Void> {
        String response;

        @Override
        protected void onPreExecute() {
            llProgress.setVisibility(View.VISIBLE);
            btnLogin.setEnabled(false);
            btnRegistration.setEnabled(false);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            llProgress.setVisibility(View.GONE);
            btnLogin.setEnabled(true);
            btnRegistration.setEnabled(true);
            if (response == null) {
                Snackbar.make(llProgress, R.string.no_response, Snackbar.LENGTH_SHORT).show();
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(response);
                int status = jsonObject.getInt("status");
                if (status == 1) {
                    Declaration.configuration.setToken(jsonObject.getString("token"));
                    Declaration.configuration.setUsername(edtUsername.getText().toString());
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {//返回错误
                    String error = jsonObject.getString("error");
                    Snackbar.make(llProgress, "错误：" + error, Snackbar.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                response = NetworkUtility.sendGetRequest(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.al_btn_login:
                Map<String, String> params = new HashMap<String, String>();
                params.put("action", "login");
                params.put("username", edtUsername.getText().toString());
                params.put("password", edtPassword.getText().toString());
                String url = Declaration.SERVLET_URL + "/UserServlet?" + NetworkUtility.encodeParam(params);
                new GetData().execute(url);
                break;
            case R.id.al_btn_registration:
                Intent intent = new Intent(this, RegistrationActivity.class);
                startActivity(intent);
                break;
        }
    }
}
