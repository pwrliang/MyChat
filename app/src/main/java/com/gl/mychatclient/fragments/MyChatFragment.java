package com.gl.mychatclient.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gl.mychatclient.R;
import com.gl.mychatclient.activities.ChatActivity;
import com.gl.mychatclient.adapters.SessionAdapter;
import com.gl.mychatclient.apis.models.FriendProfile;
import com.gl.mychatclient.apis.models.Session;
import com.gl.mychatclient.db.FriendsDB;
import com.gl.mychatclient.db.MessageDB;
import com.gl.mychatclient.db.SessionDB;
import com.gl.mychatclient.utils.LogUtil;

import java.util.List;

/**
 * Created by gl on 2016/1/31.
 */
public class MyChatFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private final String TAG = getClass().getSimpleName();
    private ListView lvSession;
    private List<Session> sessionList;
    private SessionAdapter sessionAdapter;
    private LocalReceiver localReceiver;
    private LocalBroadcastManager localBroadcastManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mychat, container, false);
        lvSession = (ListView) view.findViewById(R.id.fm_lv_session);
        lvSession.setOnItemClickListener(this);
        lvSession.setOnItemLongClickListener(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.gl.mychatclient.BROADCAST_UPDATE_SESSION");
        localReceiver = new LocalReceiver();
        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);
        loadSession();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        localBroadcastManager.unregisterReceiver(localReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSession();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Session session = sessionList.get(position);
        FriendsDB friendsDB = FriendsDB.getInstance(getContext());
        FriendProfile friendProfile = friendsDB.findFriend(session.getUsername());
        Intent newIntent = new Intent(getContext(), ChatActivity.class);
        newIntent.putExtra("username", friendProfile.getUsername());
        if (!TextUtils.isEmpty(friendProfile.getFriendNote())) {
            newIntent.putExtra("title", friendProfile.getFriendNote());
        } else
            newIntent.putExtra("title", friendProfile.getNickname());
        startActivity(newIntent);
    }

    private class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadSession();
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        //长按列表项，删除会话
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.delete_session));
        builder.setMessage(getString(R.string.delete_session_or_not));
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Session session = sessionList.remove(position);
                SessionDB sessionDB = SessionDB.getInstance(getContext());
                sessionDB.deleteSession(session.getUsername());
                MessageDB messageDB = MessageDB.getInstance(getContext());
                messageDB.deleteMessages(session.getUsername());
                sessionAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
        return true;
    }

    //此事件，别的Fragment切换到该Fragment时触发
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //在CreateView之前被调用，需要判断是否加载了布局
            if (lvSession != null)
                loadSession();
            LogUtil.i(TAG, "setUserVisibleHint");
        }
    }

    public void loadSession() {
        SessionDB sessionDB = SessionDB.getInstance(getContext());
        sessionList = sessionDB.readSession();
        if (sessionList == null) return;
        sessionAdapter = new SessionAdapter(getActivity(), R.layout.session_item, sessionList);
        lvSession.setAdapter(sessionAdapter);
        sessionAdapter.notifyDataSetChanged();
    }
}
