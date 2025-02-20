package com.cheng.youthapartment.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.core.view.setMargins
import androidx.viewpager2.widget.ViewPager2
import com.cheng.youthapartment.App
import com.cheng.youthapartment.R
import com.cheng.youthapartment.adapter.ViewPagerAdapter
import com.cheng.youthapartment.bean.GraphVo
import com.cheng.youthapartment.bean.room.RoomDetailBean
import com.cheng.youthapartment.databinding.ActivityRoomBinding
import com.cheng.youthapartment.util.Logger
import com.cheng.youthapartment.util.RetrofitUtil

class RoomActivity : BaseActivity() {
    private lateinit var mRoomBinding: ActivityRoomBinding
    private lateinit var mAdapter: ViewPagerAdapter
    private val mViewPager: ViewPager2 by lazy { mRoomBinding.roomViewpager }
    private val mIndicator: LinearLayout by lazy { mRoomBinding.indicator }

    private var mRoomDetailBean: RoomDetailBean? = null
    private var mGraphVoList: MutableList<GraphVo> = mutableListOf()
    private var mIsUserScrolling = false

    private val mScrollDelay = 3000L
    private val mHandler = Handler(Looper.getMainLooper())
    private val mAutoScrollRunnable = object : Runnable {
        override fun run() {
            if (mGraphVoList.size > 1 && !mIsUserScrolling) {
                val currentItem = mViewPager.currentItem
                // 改为向右滑动逻辑
                val nextItem = if (currentItem == mGraphVoList.size - 1) 0 else currentItem + 1
                mViewPager.setCurrentItem(nextItem, true)
            }
            mHandler.postDelayed(this, mScrollDelay)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mRoomBinding = ActivityRoomBinding.inflate(layoutInflater)
        setContentView(mRoomBinding.root)

        getApartmentById()
        setupViewPager()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getApartmentById() {
        mAdapter = ViewPagerAdapter(mGraphVoList, this)
        mViewPager.adapter = mAdapter
        RetrofitUtil.get<RoomDetailBean>(
            "/app/room/getDetailById",
            App.getToken(),
            mapOf("id" to intent.getIntExtra("room_id", 0))
        ) { _, response ->
            response?.let {
                mRoomDetailBean = response
                mRoomDetailBean!!.graphVoList.let { mGraphVoList.addAll(it) }
                Logger.d("apartment: $response")
                mViewPager.adapter = mAdapter
                runOnUiThread {
                    mAdapter.notifyDataSetChanged()
                    mViewPager.setCurrentItem(0, false)
                    initIndicators()
                }
            }
        }.let {
            initView()
        }
    }

    private fun initView() {

    }

    /**
     * 监听滑动图片
     */
    private fun setupViewPager() {
        mViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateIndicators(position)
            }
        })
    }

    /**
     * 初始化指示器
     */
    private fun initIndicators() {
        mIndicator.removeAllViews()
        for (index in mGraphVoList.indices) {
            val dot = ImageView(this)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(8)
            }
            dot.layoutParams = params
            dot.setImageResource(
                if (index == 0) R.drawable.indicator_dot_selected
                else R.drawable.indicator_dot_default
            )
            mIndicator.addView(dot)
        }
    }

    /**
     * 更新指示器
     */
    private fun updateIndicators(position: Int) {
        for (index in 0 until mIndicator.childCount) {
            val dot = mIndicator.getChildAt(index) as ImageView
            dot.setImageResource(
                if (index == position) R.drawable.indicator_dot_selected
                else R.drawable.indicator_dot_default
            )
        }
    }

    private fun startAutoScroll() {
        mHandler.postDelayed(mAutoScrollRunnable, mScrollDelay)
    }

    private fun stopAUtoScroll() {
        mHandler.removeCallbacks(mAutoScrollRunnable)
    }


    override fun onResume() {
        super.onResume()
        startAutoScroll()
    }

    override fun onPause() {
        super.onPause()
        stopAUtoScroll()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAUtoScroll()
    }


}