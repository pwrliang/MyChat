package com.gl.mychatclient.activities;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.gl.mychatclient.Declaration;
import com.gl.mychatclient.R;
import com.gl.mychatclient.adapters.MessageAdapter;
import com.gl.mychatclient.apis.FriendAPI;
import com.gl.mychatclient.apis.MessageAPI;
import com.gl.mychatclient.apis.models.FriendProfile;
import com.gl.mychatclient.apis.models.Message;
import com.gl.mychatclient.apis.models.Session;
import com.gl.mychatclient.db.MessageDB;
import com.gl.mychatclient.db.SessionDB;
import com.gl.mychatclient.utils.LogUtil;
import com.gl.mychatclient.utils.MyChatUtility;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gl on 2016/2/2.
 */
public class ChatActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {
    private ListView lvMessage;
    private EditText edtInput;
    private Button btnSend;
    private List<Message> messageList = new ArrayList<>();
    private MessageAdapter messageAdapter;
    private final String TAG = getClass().getSimpleName();
    private String username;
    private LocalBroadcastManager localBroadcastManager;
    private LocalReceiver localReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        final Intent intent = getIntent();
        username = intent.getStringExtra("username");
        Toolbar toolbar = (Toolbar) findViewById(R.id.ac_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(intent.getStringExtra("title"));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        lvMessage = (ListView) findViewById(R.id.ac_lv_message);
        edtInput = (EditText) findViewById(R.id.ac_edt_input);
        btnSend = (Button) findViewById(R.id.ac_btn_send);
        btnSend.setOnClickListener(this);
        btnSend.setOnLongClickListener(this);
        btnSend.setEnabled(false);
        new AsyncTask<Void, Void, Void>() {
            private FriendProfile myProfile;
            private FriendProfile friendProfile;
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    myProfile = FriendAPI.fetchFriendProfile(Declaration.configuration.getUsername());
                    friendProfile = FriendAPI.fetchFriendProfile(username);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ChatActivity.this, "获取信息失败：" + e.toString(), Toast.LENGTH_SHORT).show();
                    finish();
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                btnSend.setEnabled(true);
                byte[] bytes = friendProfile.getPortrait();
                Bitmap bitmapLeft = BitmapFactory.decodeResource(getResources(), R.drawable.portrait_default);
                if (bytes != null)
                    bitmapLeft = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                bytes = myProfile.getPortrait();
                Bitmap bitmapRight = BitmapFactory.decodeResource(getResources(), R.drawable.portrait_default);
                if (bytes != null)
                    bitmapRight = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                messageAdapter = new MessageAdapter(ChatActivity.this, messageList, bitmapLeft, bitmapRight);//两个头像
                lvMessage.setAdapter(messageAdapter);
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("com.gl.mychatclient.BROADCAST_RECEIVE_MESSAGE");
                localReceiver = new LocalReceiver();
                localBroadcastManager = LocalBroadcastManager.getInstance(ChatActivity.this);
                localBroadcastManager.registerReceiver(localReceiver, intentFilter);
                showMessage();
            }
        }.execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(localReceiver);
    }

    private void showMessage() {
        messageList.clear();
        MessageDB messageDB = MessageDB.getInstance(this);
        List<Message> messages = messageDB.readMessages(username);
        if (messages == null) return;
        for (Message message : messages) {
            messageList.add(message);
        }
        messageAdapter.notifyDataSetChanged();
        lvMessage.setSelection(messageList.size());
    }

    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //收到广播，有新消息
            LogUtil.i(TAG, "收到消息了");
            MessageDB messageDB = MessageDB.getInstance(ChatActivity.this);
            List<Message> messages = messageDB.readMessages(username, "UNREAD");
            if (messages == null) return;
            for (Message message : messages) {
                messageList.add(message);
            }
            messageAdapter.notifyDataSetChanged();
            lvMessage.setSelection(messageList.size());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            try {
                ContentResolver resolver = getContentResolver();
                //照片的原始资源地址
                Uri originalUri = data.getData();
                //使用ContentProvider通过URI获取原始图片
                Bitmap photo = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                if (photo != null) {
                    //为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
                    int SCALE = 5;
                    Bitmap smallBitmap = MyChatUtility.zoomBitmap(photo, photo.getWidth() / SCALE, photo.getHeight() / SCALE);
                    //释放原始图片占用的内存，防止out of memory异常发生
                    photo.recycle();
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    smallBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
                    final Message message = new Message();
                    message.setUsername(username);
                    message.setType(Message.VALUE_RIGHT_IMAGE);
                    message.setImage(outputStream.toByteArray());
                    MessageDB messageDB = MessageDB.getInstance(this);
                    messageDB.saveMessage(message, "READ");
                    Session session = new Session();
                    session.setUsername(username);
                    session.setLastWord("(图片)");
                    session.setTime(System.currentTimeMillis());
                    SessionDB sessionDB = SessionDB.getInstance(this);
                    sessionDB.saveSession(session); //保存会话
                    messageList.add(message);
                    messageAdapter.notifyDataSetChanged();
                    lvMessage.setSelection(messageList.size());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                MessageAPI.sendMessage(message);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        lvMessage.setSelection(messageList.size());
        if (TextUtils.isEmpty(edtInput.getText()))
            return;
        new AsyncTask<String, Void, Void>() {
            Message message;

            @Override
            protected void onPreExecute() {
                btnSend.setEnabled(false);
                edtInput.setEnabled(false);
                message = new Message();
                message.setType(Message.VALUE_RIGHT_TEXT);
                message.setText(edtInput.getText().toString());
                message.setUsername(username);
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                btnSend.setEnabled(true);
                edtInput.setEnabled(true);
                edtInput.setText("");
                MessageDB messageDB = MessageDB.getInstance(ChatActivity.this);
                messageDB.saveMessage(message, "READ");
                Session session = new Session();//更新Session;
                session.setUsername(username);
                session.setLastWord(message.getText());
                session.setTime(System.currentTimeMillis());
                SessionDB sessionDB = SessionDB.getInstance(ChatActivity.this);
                sessionDB.saveSession(session); //保存会话
                messageList.add(message);
                messageAdapter.notifyDataSetChanged();
                lvMessage.setSelection(messageList.size());
            }

            @Override
            protected Void doInBackground(String... params) {
                try {
                    //检查是否拉黑
                    if (FriendAPI.isAvailable(username)) {
                        MessageAPI.sendMessage(message);
                    } else {
                        throw new Exception(getString(R.string.friend_unavailable));
                    }
                } catch (final Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(btnSend, "错误：" + e.toString(), Snackbar.LENGTH_SHORT).show();
                        }
                    });
                }
                return null;
            }
        }.execute();
        //失去焦点后阻止软键盘关闭
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(edtInput, InputMethodManager.SHOW_FORCED);
        edtInput.requestFocus();
    }

    @Override
    public boolean onLongClick(View v) {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        intent.putExtra("crop", true);
        intent.putExtra("scale", true);
        startActivityForResult(intent, 0);
        return true;
    }
}
