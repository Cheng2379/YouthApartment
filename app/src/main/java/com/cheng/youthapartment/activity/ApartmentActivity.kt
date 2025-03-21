package com.cheng.youthapartment.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.cheng.youthapartment.App
import com.cheng.youthapartment.R
import com.cheng.youthapartment.adapter.BannerAdapter
import com.cheng.youthapartment.adapter.RvAdapter
import com.cheng.youthapartment.bean.properties.GraphBean
import com.cheng.youthapartment.bean.apartment.ApartmentDetailBean
import com.cheng.youthapartment.databinding.ActivityApartmentBinding
import com.cheng.youthapartment.decoration.grid_view.LabelSpaceDecoration
import com.cheng.youthapartment.decoration.grid_view.SpaceItemDecoration
import com.cheng.youthapartment.util.DataUtil
import com.cheng.youthapartment.util.Logger
import com.cheng.youthapartment.util.RetrofitUtil

/**
 * 公寓详情页
 * @author CHENG
 * @since 2025/3/20
 */
class ApartmentActivity : BaseActivity() {
    private val mApartmentBinding by lazy {
        ActivityApartmentBinding.inflate(layoutInflater)
    }
    private lateinit var mAdapter: BannerAdapter
    private val mViewPager by lazy { mApartmentBinding.apartmentViewpager }
    private val mIndicator by lazy { mApartmentBinding.indicator }
    private val mRvRoom by lazy { mApartmentBinding.apartmentRvRoom }

    private var mGraphList = mutableListOf<GraphBean>()

    private var mIsUserScrolling = false

    private val mScrollDelay = 3000L
    private val mHandler = Handler(Looper.getMainLooper())
    private val mAutoScrollRunnable = object : Runnable {
        override fun run() {
            if (mGraphList.size > 1 && !mIsUserScrolling) {
                val currentItem = mViewPager.currentItem
                // 改为向右滑动逻辑
                val nextItem = if (currentItem == mGraphList.size - 1) 0 else currentItem + 1
                mViewPager.setCurrentItem(nextItem, true)
            }
            mHandler.postDelayed(this, mScrollDelay)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(mApartmentBinding.root)

        getApartmentById()
        setupViewPager()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getApartmentById() {
        mAdapter = BannerAdapter(mGraphList, this)
        mViewPager.adapter = mAdapter
        RetrofitUtil.get<ApartmentDetailBean>(
            "/app/apartment/getDetailById",
            App.getToken(),
            mapOf("id" to intent.getIntExtra("apartment_id", 0))
        ) { _, response ->
            response?.let {
                Logger.d("response: $it")
                mGraphList.addAll(it.graphVoList)
                initView(it)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initView(apartmentBean: ApartmentDetailBean) {
        // TODO 轮播图指示标不显示，待解决
        mApartmentBinding.apartmentName.text = apartmentBean.name
        val labelSpanCount = minOf(6, apartmentBean.labelInfoList.size ?: 0)
        val labelList = ArrayList(apartmentBean.labelInfoList)
        mApartmentBinding.apartmentRvLabel.layoutManager = GridLayoutManager(this, labelSpanCount)
        val labelSpacing = resources.getDimensionPixelSize(R.dimen.label_grid_space)
        // 网格间距装饰器
        mApartmentBinding.apartmentRvLabel.addItemDecoration(
            LabelSpaceDecoration(
                spanCount = labelSpanCount,
                rightSpacing = labelSpacing,
                bottomSpacing = labelSpacing
            )
        )
        mApartmentBinding.apartmentRvLabel.adapter =
            RvAdapter(
                this,
                labelList,
                R.layout.item_text_label
            ) { holder, position ->
                val labelText: TextView = holder.itemView.findViewById(R.id.item_label)
                labelText.text = labelList[position]?.name ?: ""
                labelText.textSize = 15f
            }
        mApartmentBinding.apartmentRent.text = "$${apartmentBean.minRent}/月起"

        // 社区介绍
        mApartmentBinding.apartmentBaseInfo.text = apartmentBean.introduction

        // 配套说明
        val facilitySpanCount = minOf(6, apartmentBean.facilityInfoList.size)
        val facilityList = ArrayList(apartmentBean.facilityInfoList)
        mApartmentBinding.apartmentRvFacilityInfo.layoutManager =
            GridLayoutManager(this, facilitySpanCount)
        mApartmentBinding.apartmentRvFacilityInfo.addItemDecoration(
            SpaceItemDecoration(
                spanCount = facilitySpanCount,
                spacing = labelSpacing,
                includeEdge = false
            )
        )
        mApartmentBinding.apartmentRvFacilityInfo.adapter = RvAdapter(
            this,
            facilityList,
            R.layout.item_icon_facility_info
        ) { holder, position ->
            val facilityInfoImage = holder.itemView.findViewById<ImageView>(R.id.facility_img)
            val facilityInfoText = holder.itemView.findViewById<TextView>(R.id.facility_text)
            facilityInfoText.text = facilityList[position]?.name ?: ""
            DataUtil.setFacility(facilityInfoText.text, facilityInfoImage)
        }

        // 位置详情

        // 可选房间列表
    }

    private fun setupViewPager() {
        mViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateIndicators(position)
            }
        })
    }

    /**
     * 更新指示器
     */
    private fun updateIndicators(position: Int) {
        for (index in 0 until mIndicator.childCount) {
            val dot = mIndicator.getChildAt(index) as ImageView
            dot.setImageResource(
                if (index == position) R.drawable.shape_indicator_selected
                else R.drawable.shape_indicator_default
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