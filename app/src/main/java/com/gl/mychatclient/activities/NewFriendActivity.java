package com.gl.mychatclient.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.gl.mychatclient.R;
import com.gl.mychatclient.adapters.ApplicationAdapter;
import com.gl.mychatclient.adapters.ApplicationItem;
import com.gl.mychatclient.apis.FriendAPI;
import com.gl.mychatclient.apis.models.Application;
import com.gl.mychatclient.apis.models.FriendProfile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gl on 2016/2/10.
 */
public class NewFriendActivity extends AppCompatActivity {
    private ListView lvRequest;
    private ApplicationAdapter applicationAdapter;
    private List<ApplicationItem> applicationItemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newfriend);
        Toolbar toolbar = (Toolbar) findViewById(R.id.an_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.new_friends);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        lvRequest = (ListView) findViewById(R.id.an_lv);
        applicationAdapter = new ApplicationAdapter(this, R.layout.applicant_item, applicationItemList);
        lvRequest.setAdapter(applicationAdapter);
        loadApplication();
    }

    private void loadApplication() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Application不含头像，ApplicationItem含头像
                    List<Application> applicationList = FriendAPI.queryApplications();
                    if (applicationList == null)
                        return;
                    for (Application application : applicationList) {
                        ApplicationItem applicationItem = new ApplicationItem();
                        FriendProfile friendProfile = FriendAPI.fetchFriendProfile(application.getApplicant());//获取详细信息，主要是头像什么的
                        applicationItem.setPortrait(friendProfile.getPortrait());//头像
                        applicationItem.setNickname(friendProfile.getNickname());//昵称
                        applicationItem.setApplicant(application.getApplicant());//申请人用户名
                        applicationItem.setDescription(application.getDescription());//申请时备注
                        applicationItemList.add(applicationItem);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            applicationAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

