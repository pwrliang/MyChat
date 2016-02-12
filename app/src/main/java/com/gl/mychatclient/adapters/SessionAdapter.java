package com.gl.mychatclient.adapters;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gl.mychatclient.R;
import com.gl.mychatclient.apis.models.FriendProfile;
import com.gl.mychatclient.apis.models.Session;
import com.gl.mychatclient.db.FriendsDB;
import com.gl.mychatclient.utils.LogUtil;

import java.util.Calendar;
import java.util.List;

/**
 * Created by gl on 2016/2/1.
 */
public class SessionAdapter extends ArrayAdapter<Session> {
    private int resourceId;

    public SessionAdapter(Context context, int resourceId, List<Session> sessionList) {
        super(context, resourceId, sessionList);
        this.resourceId = resourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.ivPortrait = (ImageView) view.findViewById(R.id.si_iv_portrait);
            viewHolder.tvNickname = (TextView) view.findViewById(R.id.si_tv_nickname);
            viewHolder.tvLastWord = (TextView) view.findViewById(R.id.si_tv_lastword);
            viewHolder.tvTime = (TextView) view.findViewById(R.id.si_tv_time);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        Session session = getItem(position);
        FriendsDB friendsDB = FriendsDB.getInstance(getContext());
        FriendProfile friendProfile = friendsDB.findFriend(session.getUsername());
        if(friendProfile ==null){
            LogUtil.e("SessionAdapter","ERROR!!!!!!!!!");
            return view;
        }
        if (friendProfile.getPortrait() != null) {
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inPurgeable = true;
            Bitmap portrait = BitmapFactory.decodeByteArray(friendProfile.getPortrait(), 0, friendProfile.getPortrait().length);
            viewHolder.ivPortrait.setImageBitmap(portrait);
        }
        //有备注用备注
        if (friendProfile.getFriendNote() != null)
            viewHolder.tvNickname.setText(friendProfile.getFriendNote());
        else
            viewHolder.tvNickname.setText(friendProfile.getNickname());

        viewHolder.tvLastWord.setText(session.getLastWord());
        Calendar lastCalendar = Calendar.getInstance();
        lastCalendar.setTimeInMillis(session.getTime());
        Calendar nowCalendar = Calendar.getInstance();
        long amount = (nowCalendar.getTimeInMillis() - lastCalendar.getTimeInMillis()) / 1000 / 60 / 60;
        if (amount <= 24) {
            String hour = lastCalendar.get(Calendar.HOUR_OF_DAY) + "";
            String minute = lastCalendar.get(Calendar.MINUTE) + "";
            if (hour.length() == 1) hour = 0 + hour;
            if (minute.length() == 1) minute = 0 + minute;
            viewHolder.tvTime.setText(hour + ":" + minute);
        } else if (amount > 24 && amount <= 48) {
            viewHolder.tvTime.setText("昨天");
        } else {
            viewHolder.tvTime.setText(lastCalendar.get(Calendar.MONTH) + "月" + lastCalendar.get(Calendar.DAY_OF_MONTH) + "日");
        }
        return view;
    }

    private class ViewHolder {
        ImageView ivPortrait;
        TextView tvNickname;
        TextView tvLastWord;
        TextView tvTime;
    }
}
