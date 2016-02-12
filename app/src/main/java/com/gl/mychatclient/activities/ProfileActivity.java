package com.gl.mychatclient.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gl.mychatclient.Declaration;
import com.gl.mychatclient.R;
import com.gl.mychatclient.apis.FriendAPI;
import com.gl.mychatclient.db.FriendsDB;
import com.gl.mychatclient.db.MessageDB;
import com.gl.mychatclient.db.SessionDB;
import com.gl.mychatclient.utils.LogUtil;

/**
 * Created by gl on 2016/2/1.
 */
public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView ivPortrait;
    private ImageView ivGender;
    private TextView tvNote;
    private TextView tvUsername;
    private TextView tvNickname;
    private TextView tvRegion;
    private TextView tvDescription;
    private Button btnFunction;
    private Intent intent;
    private final String TAG = getClass().getSimpleName();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.ap_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.profile);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ivPortrait = (ImageView) findViewById(R.id.ap_iv_portrait);
        ivGender = (ImageView) findViewById(R.id.ap_iv_gender);
        tvNote = (TextView) findViewById(R.id.ap_tv_note);
        tvUsername = (TextView) findViewById(R.id.ap_tv_username);
        tvNickname = (TextView) findViewById(R.id.ap_tv_nickname);
        tvRegion = (TextView) findViewById(R.id.ap_tv_region);
        tvDescription = (TextView) findViewById(R.id.ap_tv_description);
        btnFunction = (Button) findViewById(R.id.ap_btn_function);
        intent = getIntent();

        if (intent.hasExtra("portrait")) {
            byte[] bytes = intent.getByteArrayExtra("portrait");
            Bitmap portrait = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            ivPortrait.setImageBitmap(portrait);
        }
        if (intent.hasExtra("gender")) {
            if (intent.getStringExtra("gender").equals("MALE")) {
                ivGender.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.male));
            } else {
                ivGender.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.female));
            }
        }
        //有备注，第一个TextView显示备注，第三个显示昵称，无备注，第一个显示昵称，第三个隐藏
        if (intent.hasExtra("friendnote")) {
            tvNote.setText(intent.getStringExtra("friendnote"));
            tvNickname.setText("昵称：" + intent.getStringExtra("nickname"));
        } else {
            tvNote.setText(intent.getStringExtra("nickname"));
            tvNickname.setVisibility(View.GONE);
        }
        tvUsername.setText("账号：" + intent.getStringExtra("username"));
        if (intent.hasExtra("region")) {
            tvRegion.setText(intent.getStringExtra("region"));
        }
        if (intent.hasExtra("description")) {
            tvDescription.setText(intent.getStringExtra("description"));
        }
        if (intent.getBooleanExtra("isfriend", false)) {
            btnFunction.setText(R.string.send_message);
        } else {
            btnFunction.setText(R.string.add_to_contacts);
        }
        if (intent.getStringExtra("username").equals(Declaration.configuration.getUsername())) {//自己查看自己
            btnFunction.setEnabled(false);
        }
        btnFunction.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //是好友才显示删除、加入黑名单菜单
        if (intent.getBooleanExtra("isfriend", false))
            getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete_friend) {//删除好友
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String username = intent.getStringExtra("username");
                        FriendAPI.deleteFriend(username);
                        MessageDB.getInstance(ProfileActivity.this).deleteMessages(username);
                        SessionDB.getInstance(ProfileActivity.this).deleteSession(username);
                        FriendsDB.getInstance(ProfileActivity.this).deleteFriend(username);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (btnFunction.getText().equals(getString(R.string.add_to_contacts))) {
            LogUtil.i(TAG, "add to friend");
            final EditText edtContent = new EditText(this);
            edtContent.setSingleLine();
            edtContent.setMaxEms(20);
            edtContent.setHint(R.string.please_input_note);
            AlertDialog.Builder adBuilder = new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle(getString(R.string.require_add_friend))
                    .setView(edtContent);
            //申请好友
            adBuilder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (FriendAPI.applyFriend(intent.getStringExtra("username"), edtContent.getText().toString())) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Snackbar.make(btnFunction, R.string.require_add_to_contract_success, Snackbar.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } catch (final Exception e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Snackbar.make(btnFunction, "错误：" + e.toString(), Snackbar.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }).start();
                }
            });
            adBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            final AlertDialog ad = adBuilder.create();
            ad.show();
        } else {
            //启动发消息界面
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("username", this.intent.getStringExtra("username"));
            if (this.intent.hasExtra("friendnote")) {
                intent.putExtra("title", this.intent.getStringExtra("friendnote"));
            } else
                intent.putExtra("title", this.intent.getStringExtra("nickname"));
            startActivity(intent);
        }
    }
}
