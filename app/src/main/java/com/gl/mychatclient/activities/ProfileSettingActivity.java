package com.gl.mychatclient.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.gl.mychatclient.Declaration;
import com.gl.mychatclient.R;
import com.gl.mychatclient.apis.FriendAPI;
import com.gl.mychatclient.apis.UserAPI;
import com.gl.mychatclient.apis.models.FriendProfile;

import java.io.ByteArrayOutputStream;

/**
 * Created by gl on 2016/2/11.
 */
public class ProfileSettingActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout llPortrait;
    private ImageView ivPortrait;
    private TextView tvUsername;
    private TextView tvRegDate;
    private EditText edtNickname;
    private EditText edtRegion;
    private EditText edtPhoneNumber;
    private RadioButton rbMale;
    private RadioButton rbFemale;
    private EditText edtDescription;
    private Button btnSave;
    private boolean changedPortrait = false;//标记，当修改过头像时，该值为true

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.aps_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.my_profile);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        llPortrait = (LinearLayout) findViewById(R.id.aps_ll_portrait);
        ivPortrait = (ImageView) findViewById(R.id.aps_iv_portrait);
        tvUsername = (TextView) findViewById(R.id.aps_tv_username);
        tvRegDate = (TextView) findViewById(R.id.aps_tv_reg_date);
        edtNickname = (EditText) findViewById(R.id.aps_edt_nickname);
        edtRegion = (EditText) findViewById(R.id.aps_edt_region);
        edtPhoneNumber = (EditText) findViewById(R.id.aps_edt_phone_number);
        rbMale = (RadioButton) findViewById(R.id.aps_rb_male);
        rbFemale = (RadioButton) findViewById(R.id.aps_rb_female);
        edtDescription = (EditText) findViewById(R.id.aps_edt_description);
        btnSave = (Button) findViewById(R.id.aps_btn_save);
        btnSave.setOnClickListener(this);
        llPortrait.setOnClickListener(this);
        new GetData().execute(Declaration.configuration.getUsername());//载入数据
    }

    private class GetData extends AsyncTask<String, Void, Void> {
        FriendProfile profile;

        @Override
        protected void onPostExecute(Void aVoid) {
            if (profile.getPortrait() != null) {
                byte[] bytes = profile.getPortrait();
                ivPortrait.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            }
            if (profile.getUsername() != null)
                tvUsername.setText(profile.getUsername());
            if (profile.getRegistDate() != null)
                tvRegDate.setText(profile.getRegistDate());
            if (profile.getNickname() != null)
                edtNickname.setText(profile.getNickname());
            if (profile.getRegion() != null)
                edtRegion.setText(profile.getRegion());
            if (profile.getPhoneNumber() != null)
                edtPhoneNumber.setText(profile.getPhoneNumber());
            if (TextUtils.equals(profile.getGender(), "MALE"))
                rbMale.setChecked(true);
            else if (TextUtils.equals(profile.getGender(), "FEMALE"))
                rbFemale.setChecked(true);
            if (profile.getDescription() != null)
                edtDescription.setText(profile.getDescription());
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                profile = FriendAPI.fetchFriendProfile(params[0]);
            } catch (Exception e) {
                e.printStackTrace();
                Snackbar.make(btnSave, "错误：" + e.toString(), Snackbar.LENGTH_SHORT).show();
            }
            return null;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap photo = extras.getParcelable("data");
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.JPEG, 75, outputStream);
                byte[] bytes = outputStream.toByteArray();
                ivPortrait.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                changedPortrait = true;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.aps_ll_portrait:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.putExtra("crop", "true");
                intent.putExtra("outputX", 200);
                intent.putExtra("outputY", 200);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("noFaceDetection", true);
                intent.putExtra("return-data", true);
                try {
                    startActivityForResult(intent, 0);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.aps_btn_save:
                new AsyncTask<Void, Void, Void>() {
                    private FriendProfile profile;

                    @Override
                    protected void onPreExecute() {
                        profile = new FriendProfile();
                        if (changedPortrait) {//修改过头像
                            BitmapDrawable bitmapDrawable = (BitmapDrawable) ivPortrait.getDrawable();
                            Bitmap photo = bitmapDrawable.getBitmap();
                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                            photo.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                            profile.setPortrait(outputStream.toByteArray());
                        }
                        if (!TextUtils.isEmpty(edtNickname.getText()))
                            profile.setNickname(edtNickname.getText().toString());
                        if (!TextUtils.isEmpty(edtRegion.getText()))
                            profile.setRegion(edtRegion.getText().toString());
                        if (!TextUtils.isEmpty(edtPhoneNumber.getText()))
                            profile.setPhoneNumber(edtPhoneNumber.getText().toString());
                        if (rbMale.isChecked())
                            profile.setGender("MALE");
                        else if (rbFemale.isChecked())
                            profile.setGender("FEMALE");
                        if (!TextUtils.isEmpty(edtDescription.getText()))
                            profile.setDescription(edtDescription.getText().toString());
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        finish();
                    }

                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            UserAPI.updateProfile(profile);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }.execute();
                break;
        }
    }
}
