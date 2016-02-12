package com.gl.mychatclient.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.gl.mychatclient.R;
import com.gl.mychatclient.activities.ProfileSettingActivity;

/**
 * Created by gl on 2016/1/31.
 */
public class MeFragment extends Fragment implements View.OnClickListener {
    private final String TAG = getClass().getSimpleName();
    private LinearLayout llProfile;
    private LinearLayout llSetting;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        llProfile = (LinearLayout)view.findViewById(R.id.fm_ll_profile);
        llSetting= (LinearLayout) view.findViewById(R.id.fm_ll_setting);
        llProfile.setOnClickListener(this);
        llSetting.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fm_ll_profile:
                startActivity(new Intent(getContext(), ProfileSettingActivity.class));
                break;
            case R.id.fm_ll_setting:
                break;
        }
    }
}

