package com.cheng.youthapartment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.cheng.youthapartment.adapter.FragmentAdapter;
import com.cheng.youthapartment.bean.BaseBean;
import com.cheng.youthapartment.bean.PageDataBean;
import com.cheng.youthapartment.bean.RoomBean;
import com.cheng.youthapartment.bean.UserBean;
import com.cheng.youthapartment.fragment.GroupFragment;
import com.cheng.youthapartment.fragment.MessageFragment;
import com.cheng.youthapartment.fragment.MyRoomFragment;
import com.cheng.youthapartment.fragment.SearchFragment;
import com.cheng.youthapartment.fragment.UserCenterFragment;
import com.cheng.youthapartment.util.OkHttpUtil;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private ViewPager2 mViewPager2;
    private FragmentAdapter fragmentAdapter;
    private BottomNavigationView mBottomNavigationView;
    private SharedPreferences spf;
    private SharedPreferences.Editor spEditor;
    private static final Gson gson = new Gson();
    private static final String TAG = "LeaseHome";
    private String token, nickname, avatarUrl;
    private PageDataBean<RoomBean> pageData;
    private List<RoomBean> roomList;
    private List<Fragment> fragments;

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        spf = this.getSharedPreferences("user_info", MODE_PRIVATE);
        isFirst();

        initView();
        setFragments();
        // getRoomList(1, 10);
    }

    private void isFirst() {
        boolean first_run = spf.getBoolean("firstOpen", true);
        if (first_run) {
            spf.edit().putBoolean("firstOpen", false).apply();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            token = spf.getString("token", "");
            nickname = spf.getString("nickname", "");
            avatarUrl = spf.getString("avatarUrl", null);
            // 检查登陆状态
            OkHttpUtil.getInstance().get("/app/info", token, (call, response) -> {
                BaseBean<UserBean> baseBean = gson.fromJson(response, new TypeToken<BaseBean<UserBean>>() {
                });
                if (baseBean.getCode() != 200) {
                    Toast.makeText(getApplicationContext(), "登录已过期，请重新登陆", Toast.LENGTH_SHORT).show();
                    spEditor = spf.edit();
                    spEditor.putString("token", "");
                    spEditor.putString("nickname", "");
                    spEditor.putString("avatarUrl", null);
                    spEditor.apply();

                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, this);
        }
    }

    private void initView() {
        mBottomNavigationView = findViewById(R.id.bottom_nv);
        mViewPager2 = findViewById(R.id.vp_home);
    }


    private void setFragments() {
        fragments = new ArrayList<>();
        SearchFragment searchFragment = new SearchFragment();
        GroupFragment groupFragment = new GroupFragment();
        MyRoomFragment myRoomFragment = new MyRoomFragment();
        MessageFragment messageFragment = new MessageFragment();
        UserCenterFragment userCenterFragment = new UserCenterFragment();
        fragments.add(searchFragment);
        fragments.add(groupFragment);
        fragments.add(myRoomFragment);
        fragments.add(messageFragment);
        fragments.add(userCenterFragment);

        // 设置适配器
        fragmentAdapter = new FragmentAdapter(this, fragments);
        mViewPager2.setAdapter(fragmentAdapter);

        // 设置滑动监听
        mViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                onPagerSelected(position);
            }
        });

        // 设置底部导航栏
        // 由于新版本缘故，无法在switch内使用R.id的方式获取id，可以改为if
        mBottomNavigationView.setOnItemSelectedListener(menuItem -> {
            int position = menuItem.getItemId();
            if (position == R.id.fragment_search) {
                mViewPager2.setCurrentItem(0);
            } else if (position == R.id.fragment_group) {
                mViewPager2.setCurrentItem(1);
            } else if (position == R.id.fragment_my_room) {
                mViewPager2.setCurrentItem(2);
            } else if (position == R.id.fragment_message) {
                mViewPager2.setCurrentItem(3);
            } else if (position == R.id.fragment_user_center) {
                mViewPager2.setCurrentItem(4);
            }
            return true;
        });
        /*// 设置消息
        BadgeDrawable badge = mBottomNavigationView.getOrCreateBadge(R.id.fragment_message);
        badge.setNumber(99999);
        // 当数字长度超过该数字时，显示99+
        badge.setMaxCharacterCount(3);*/
    }

    private void onPagerSelected(int position) {
        switch (position) {
            case 0:
                mBottomNavigationView.setSelectedItemId(R.id.fragment_search);
                break;
            case 1:
                mBottomNavigationView.setSelectedItemId(R.id.fragment_group);
                break;
            case 2:
                mBottomNavigationView.setSelectedItemId(R.id.fragment_my_room);
                break;
            case 3:
                mBottomNavigationView.setSelectedItemId(R.id.fragment_message);
                /*// 删除消息
                mBottomNavigationView.removeBadge(R.id.fragment_message);*/
                break;
            case 4:
                mBottomNavigationView.setSelectedItemId(R.id.fragment_user_center);
                break;
        }
    }

    private void getRoomList(int currentPage, int size) {
        Map<String, Object> map = new HashMap<>();
        map.put("current", currentPage);
        map.put("size", size);
        OkHttpUtil.getInstance().get("/app/room/pageItem", token, map, (call, response) -> {
            BaseBean<PageDataBean<RoomBean>> baseBean = gson.fromJson(response, new TypeToken<BaseBean<PageDataBean<RoomBean>>>() {
            }.getType());
            if (baseBean.getCode() == 200) {
                if (baseBean.getData() != null) {
                    pageData = baseBean.getData();
                    roomList = pageData.getRecords();
                    //                roomList.forEach(v -> {
                    //                    Log.i(TAG, "\nroom: " + v);
                    //                });
                }

            } else {
                Log.e(TAG, "Code: " + baseBean.getCode());
                Log.e(TAG, "baseBean: " + baseBean.getMessage());
            }
        }, this);
    }



}