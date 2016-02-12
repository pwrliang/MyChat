package com.gl.mychatclient.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.gl.mychatclient.R;
import com.gl.mychatclient.activities.NewFriendActivity;
import com.gl.mychatclient.activities.ProfileActivity;
import com.gl.mychatclient.adapters.FriendItem;
import com.gl.mychatclient.adapters.FriendItemAdapter;
import com.gl.mychatclient.apis.FriendAPI;
import com.gl.mychatclient.apis.models.FriendBrief;
import com.gl.mychatclient.apis.models.FriendProfile;
import com.gl.mychatclient.db.FriendsDB;
import com.gl.mychatclient.db.MessageDB;
import com.gl.mychatclient.db.SessionDB;
import com.gl.mychatclient.utils.LogUtil;
import com.gl.mychatclient.utils.MyChatUtility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gl on 2016/1/31.
 */
public class FriendFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private final String TAG = getClass().getSimpleName();
    private ListView lvFriends;
    private FriendItemAdapter friendAdapter;
    private List<FriendItem> friendItems = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        lvFriends = (ListView) view.findViewById(R.id.ff_lv_friends);
        friendAdapter = new FriendItemAdapter(getContext(), R.layout.contact_item, friendItems);
        lvFriends.setAdapter(friendAdapter);
        lvFriends.setOnItemClickListener(this);
        lvFriends.setOnItemLongClickListener(this);
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            LogUtil.i(TAG, "setUserVisibleHint");
            loadFriendList();
        }
    }

    //FriendBrief服务器传来的数据
    //FriendItem通讯录数据
    //Friend查看个人档案详细数据
    private void loadFriendList() {
        LogUtil.i(TAG, "更新好友列表");
        new AsyncTask<Void, Void, Void>() {
            FriendsDB friendsDB;
            List<FriendProfile> friendProfiles;

            @Override
            protected Void doInBackground(Void... params) {
                //遍历本地数据库，过滤拉黑，被删好友
                try {
                    //清理本地数据库中被删除、拉黑的好友
                    for (FriendProfile friendProfile : friendProfiles) {
                        if (!FriendAPI.isAvailable(friendProfile.getUsername())) {
                            MessageDB.getInstance(getContext()).deleteMessages(friendProfile.getUsername());
                            SessionDB.getInstance(getContext()).deleteSession(friendProfile.getUsername());
                            friendsDB.deleteFriend(friendProfile.getUsername());
                        }
                    }
                    List<FriendBrief> briefList = FriendAPI.queryAllFriend();//查询所有好友
                    if (briefList == null)
                        return null;
                    for (FriendBrief brief : briefList) {
                        FriendProfile friendProfile = FriendAPI.fetchFriendProfile(brief.getUsername());//查询详细信息
                        final FriendItem item = new FriendItem();
                        item.setPortrait(friendProfile.getPortrait());
                        //好友备注非空，用备注替换昵称
                        if (brief.getFriendNote() != null) {
                            friendProfile.setFriendNote(brief.getFriendNote());
                            item.setNickname(brief.getFriendNote());
                        } else
                            item.setNickname(friendProfile.getNickname());
                        item.setUsername(friendProfile.getUsername());//添加用户名，不显示，在查看详细信息时使用
                        friendsDB.saveFriend(friendProfile);//保存到本地数据库
                        friendItems.add(item);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                friendAdapter.notifyDataSetChanged();
            }

            @Override
            protected void onPreExecute() {
                friendsDB = FriendsDB.getInstance(getContext());
                friendProfiles = friendsDB.findAllFriend();
                friendItems.clear();
                Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.new_friends);
                friendItems.add(new FriendItem(MyChatUtility.getBytesFromDrawable(drawable), getString(R.string.new_friends)));
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.black_list);
                friendItems.add(new FriendItem(MyChatUtility.getBytesFromDrawable(drawable), getString(R.string.black_list)));
                friendAdapter.notifyDataSetChanged();
            }
        }.execute();
    }


    //点击联系人，查看详细信息
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            startActivity(new Intent(getContext(), NewFriendActivity.class));
        } else if (position == 1) {
        } else {
            FriendItem item = friendItems.get(position);
            FriendsDB friendsDB = FriendsDB.getInstance(getContext());
            FriendProfile friendProfile = friendsDB.findFriend(item.getUsername());
            Intent intent = new Intent(getContext(), ProfileActivity.class);
            if (friendProfile.getPortrait() != null) {
                intent.putExtra("portrait", friendProfile.getPortrait());
                LogUtil.i(TAG,"从数据库读 头像非空");
            }
            if (!TextUtils.isEmpty(friendProfile.getGender())) {
                intent.putExtra("gender", friendProfile.getGender());
            }
            if (!TextUtils.isEmpty(friendProfile.getFriendNote())) {
                intent.putExtra("friendnote", friendProfile.getFriendNote());
            }
            intent.putExtra("username", friendProfile.getUsername());
            intent.putExtra("nickname", friendProfile.getNickname());
            if (!TextUtils.isEmpty(friendProfile.getRegion())) {
                intent.putExtra("region", friendProfile.getRegion());
            }
            if (!TextUtils.isEmpty(friendProfile.getDescription())) {
                intent.putExtra("description", friendProfile.getDescription());
            }
            intent.putExtra("isfriend", true);
            startActivity(intent);
        }
    }

    //长按修改备注
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (position < 2)
            return false;
        final FriendItem friendItem = friendItems.get(position);
        final EditText edtFriendNote = new EditText(getContext());
        edtFriendNote.setHint(R.string.please_input_friend_note);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.mod_friend_note));
        builder.setView(edtFriendNote);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FriendAPI.updateFriendNote(friendItem.getUsername(), edtFriendNote.getText().toString());
                            Thread.sleep(1000);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loadFriendList();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                dialog.dismiss();
            }
        });
        builder.create().show();
        return true;
    }
}
