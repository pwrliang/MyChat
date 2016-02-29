package com.gl.mychatclient.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.gl.mychatclient.Declaration;
import com.gl.mychatclient.R;
import com.gl.mychatclient.apis.FriendAPI;
import com.gl.mychatclient.apis.models.FriendProfile;
import com.gl.mychatclient.db.FriendsDB;
import com.gl.mychatclient.db.MessageDB;
import com.gl.mychatclient.db.SessionDB;
import com.gl.mychatclient.fragments.DiscoveryFragment;
import com.gl.mychatclient.fragments.FriendFragment;
import com.gl.mychatclient.fragments.MeFragment;
import com.gl.mychatclient.fragments.MyChatFragment;
import com.gl.mychatclient.services.MonitorService;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private String TAG = getClass().getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.am_toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.am_vp_container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.am_tl_tabs);
        tabLayout.setupWithViewPager(mViewPager);
        startService(new Intent(this, MonitorService.class));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_friend:
                final EditText edtUsername = new EditText(this);
                edtUsername.setSingleLine();
                AlertDialog.Builder ab = new AlertDialog.Builder(this);
                ab.setTitle(getString(R.string.search_user));
                ab.setView(edtUsername);
                ab.setPositiveButton("搜索", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    FriendsDB friendsDB = FriendsDB.getInstance(MainActivity.this);
                                    FriendProfile friendProfile = friendsDB.findFriend(edtUsername.getText().toString());//本地数据库找不到，说明不是好友，就从网络找
                                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                                    if (friendProfile == null) {
                                        friendProfile = FriendAPI.fetchFriendProfile(edtUsername.getText().toString());
                                        intent.putExtra("isfriend", false);
                                    } else
                                        intent.putExtra("isfriend", true);
                                    if (friendProfile.getPortrait() != null) {
                                        intent.putExtra("portrait", friendProfile.getPortrait());
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
                                    startActivity(intent);
                                } catch (Exception e) {
                                    e.printStackTrace();

                                    Snackbar.make(mViewPager, "错误：" + e.toString(), Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        }).start();
                    }
                });
                ab.create().show();
                return true;
            case R.id.action_logout:
                Declaration.configuration.setToken(null);
                Declaration.configuration.setUsername(null);
                Declaration.configuration.setPassword(null);
                SessionDB.getInstance(this).deleteSessions();
                MessageDB.getInstance(this).deleteMessages();
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new MyChatFragment();
                case 1:
                    return new FriendFragment();
//                case 2:
//                    return new DiscoveryFragment();
                case 2:
                    return new MeFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "我信";
                case 1:
                    return "通讯录";
//                case 2:
//                    return "发现";
                case 2:
                    return "我";
            }
            return null;
        }
    }
}
