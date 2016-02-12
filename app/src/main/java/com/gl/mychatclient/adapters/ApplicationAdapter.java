package com.gl.mychatclient.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gl.mychatclient.R;
import com.gl.mychatclient.apis.FriendAPI;

import java.util.List;

/**
 * Created by gl on 2016/2/10.
 */
public class ApplicationAdapter extends ArrayAdapter<ApplicationItem> {
    private int resourceId;

    public ApplicationAdapter(Context context, int resourceId, List<ApplicationItem> applicationList) {
        super(context, resourceId, applicationList);
        this.resourceId = resourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ApplicationItem applicationItem = getItem(position);
        View view = null;
        final ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.ivPortrait = (ImageView) view.findViewById(R.id.ai_iv_portrait);
            viewHolder.tvNickname = (TextView) view.findViewById(R.id.ai_tv_nickname);
            viewHolder.tvDescription = (TextView) view.findViewById(R.id.ai_tv_description);
            viewHolder.btnAccept = (Button) view.findViewById(R.id.ai_btn_accept);
            viewHolder.btnReject = (Button) view.findViewById(R.id.ai_btn_reject);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        if (applicationItem.getPortrait() != null)
            viewHolder.ivPortrait.setImageBitmap(BitmapFactory.decodeByteArray(applicationItem.getPortrait(), 0, applicationItem.getPortrait().length));
        viewHolder.tvNickname.setText(applicationItem.getNickname());
        viewHolder.tvDescription.setText(applicationItem.getDescription());
        viewHolder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FriendAPI.responseApplication(applicationItem.getApplicant(), "accept");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                viewHolder.btnAccept.setEnabled(false);
                viewHolder.btnReject.setEnabled(false);
            }
        });
        viewHolder.btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FriendAPI.responseApplication(applicationItem.getApplicant(), "reject");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                viewHolder.btnAccept.setEnabled(false);
                viewHolder.btnReject.setEnabled(false);
            }
        });

        return view;
    }

    private class ViewHolder {
        ImageView ivPortrait;
        TextView tvNickname;
        TextView tvDescription;
        Button btnAccept;
        Button btnReject;
    }
}
