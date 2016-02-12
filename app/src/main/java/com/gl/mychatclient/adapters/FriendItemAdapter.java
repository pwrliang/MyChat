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

import java.util.List;

/**
 * Created by gl on 2016/2/1.
 */
public class FriendItemAdapter extends ArrayAdapter<FriendItem> {
    private int resourceId;

    public FriendItemAdapter(Context context, int resourceId, List<FriendItem> firendItems) {
        super(context, resourceId, firendItems);
        this.resourceId = resourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.ivPortrait = (ImageView) view.findViewById(R.id.fi_iv_portrait);
            viewHolder.tvNickname = (TextView) view.findViewById(R.id.fi_tv_nickname);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        FriendItem contact = getItem(position);
        if (contact.getPortrait() != null) {
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inPurgeable = true;
            Bitmap portrait = BitmapFactory.decodeByteArray(contact.getPortrait(), 0, contact.getPortrait().length);
            viewHolder.ivPortrait.setImageBitmap(portrait);
        }
        viewHolder.tvNickname.setText(contact.getNickname());
        return view;
    }

    private class ViewHolder {
        ImageView ivPortrait;
        TextView tvNickname;
    }
}
