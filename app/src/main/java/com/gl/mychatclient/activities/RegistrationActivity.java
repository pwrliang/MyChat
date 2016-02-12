package com.gl.mychatclient.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gl.mychatclient.Declaration;
import com.gl.mychatclient.R;
import com.gl.mychatclient.utils.NetworkUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gl on 2016/1/31.
 */
public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout llProgress;
    private EditText edtNickname;
    private EditText edtPhoneNumber;
    private EditText edtUsername;
    private EditText edtPassword;
    private EditText edtConfirmPassword;
    private Button btnRegistration;
    private final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.ar_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.registration);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        llProgress = (LinearLayout) findViewById(R.id.ar_ll_progress);
        edtNickname = (EditText) findViewById(R.id.ar_edt_nickname);
        edtPhoneNumber = (EditText) findViewById(R.id.ar_edt_phone_number);
        edtUsername = (EditText) findViewById(R.id.ar_edt_username);
        edtPassword = (EditText) findViewById(R.id.ar_edt_password);
        edtConfirmPassword = (EditText) findViewById(R.id.ar_edt_confirm_password);
        btnRegistration = (Button) findViewById(R.id.ar_btn_registration);
        btnRegistration.setOnClickListener(this);
    }

    private class GetData extends AsyncTask<String, Void, Void> {
        String response;

        @Override
        protected void onPreExecute() {
            llProgress.setVisibility(View.VISIBLE);
            btnRegistration.setEnabled(false);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            llProgress.setVisibility(View.GONE);
            btnRegistration.setEnabled(true);
            if (response == null) {
                Snackbar.make(llProgress, R.string.no_response, Snackbar.LENGTH_SHORT).show();
                return;
            }
            try {
                final JSONObject jsonObject = new JSONObject(response);
                int status = jsonObject.getInt("status");
                if (status == 1) {//注册成功
                    Toast.makeText(RegistrationActivity.this, R.string.registration_successful, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {//返回错误
                    Snackbar.make(llProgress, "错误：" + jsonObject.getString("error"), Snackbar.LENGTH_SHORT).show();

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

    //注册按钮事件
    public void onClick(View view) {
        //校验表单
        if (TextUtils.isEmpty(edtNickname.getText())) {
            Snackbar.make(view, R.string.null_nickname, Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(edtPhoneNumber.getText())) {
            Snackbar.make(view, R.string.null_phone_number, Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(edtUsername.getText())) {
            Snackbar.make(view, R.string.null_username, Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(edtPassword.getText())) {
            Snackbar.make(view, R.string.null_password, Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (!TextUtils.equals(edtPassword.getText(), edtConfirmPassword.getText())) {
            Snackbar.make(view, R.string.different_password, Snackbar.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("action", "regist");
        params.put("username", edtUsername.getText().toString());
        params.put("password", edtPassword.getText().toString());
        params.put("nickname", edtNickname.getText().toString());
        params.put("phonenumber", edtPhoneNumber.getText().toString());
        String url = Declaration.SERVLET_URL + "/UserServlet?" + NetworkUtility.encodeParam(params);
        new GetData().execute(url);
    }
}
