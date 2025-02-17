package com.cheng.youthapartment.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.cheng.youthapartment.App
import com.cheng.youthapartment.R
import com.cheng.youthapartment.adapter.FragmentAdapter
import com.cheng.youthapartment.fragment.GroupFragment
import com.cheng.youthapartment.fragment.MessageFragment
import com.cheng.youthapartment.fragment.MyRoomFragment
import com.cheng.youthapartment.fragment.SearchFragment
import com.cheng.youthapartment.fragment.UserCenterFragment
import com.cheng.youthapartment.util.Logger
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 首页
 * @author Cheng
 * @since 2024/12/09
 */
class HomeActivity : BaseActivity() {
    private val mViewPager2: ViewPager2 by lazy { findViewById(R.id.vp_home) }
    private val mBottomNavigationView: BottomNavigationView by lazy { findViewById(R.id.bottom_nv) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        isFirst()
        setFragments()
    }

    private fun isFirst() {
        val firstRun = getSharedPreferences("lease", MODE_PRIVATE).getBoolean("firstOpen", true)
        if (firstRun) {
            getSharedPreferences("lease", MODE_PRIVATE).edit {
                putBoolean("firstOpen", false)
            }
            startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
            finish()
        } else {
            // 检查token是否过期
            val token = App.getToken()
            Logger.d("App token: $token")
            lifecycleScope.launch(Dispatchers.Main) {
                val resultUser = getLoginUserInfo(token, Gson())
                if (resultUser == null) {
                    ActivityCollector.finishAll()
                    startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
                }
            }
        }
    }

    private fun setFragments() {
        val fragments = listOf(
            SearchFragment(),
            GroupFragment(),
            MyRoomFragment(),
            MessageFragment(),
            UserCenterFragment()
        )
        // 设置ViewPager2
        val fragmentAdapter = FragmentAdapter(this, fragments)
        mViewPager2.adapter = fragmentAdapter
        mViewPager2.offscreenPageLimit = 5 // 设置缓存页面数

        // 滑动监听
        mViewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                onPagerSelected(position)
            }
        })

        // 设置底部导航栏
        // 由于新版本缘故，无法在switch内使用R.id的方式获取id，可以改为if
        mBottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.fragment_search -> mViewPager2.currentItem = 0
                R.id.fragment_group -> mViewPager2.currentItem = 1
                R.id.fragment_my_room -> mViewPager2.currentItem = 2
                R.id.fragment_message -> mViewPager2.currentItem = 3
                R.id.fragment_user_center -> mViewPager2.currentItem = 4
            }
            true
        }
        /*// 设置消息
        val badge = mBottomNavigationView.getOrCreateBadge(R.id.fragment_message);
        badge.setNumber(99999);
        // 当数字长度超过该数字时，显示99+
        badge.maxCharacterCount = 3;*/
    }

    private fun onPagerSelected(position: Int) {
        when (position) {
            0 -> mBottomNavigationView.selectedItemId = R.id.fragment_search
            1 -> mBottomNavigationView.selectedItemId = R.id.fragment_group
            2 -> mBottomNavigationView.selectedItemId = R.id.fragment_my_room
            3 -> {
                mBottomNavigationView.selectedItemId = R.id.fragment_message
                /*// 删除消息
                mBottomNavigationView.removeBadge(R.id.fragment_message);*/
            }

            4 -> mBottomNavigationView.selectedItemId = R.id.fragment_user_center
        }
    }
}
