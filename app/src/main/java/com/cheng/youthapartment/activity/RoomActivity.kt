package com.cheng.youthapartment.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.view.setMargins
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.cheng.youthapartment.App
import com.cheng.youthapartment.R
import com.cheng.youthapartment.adapter.RvAdapter
import com.cheng.youthapartment.adapter.ViewPagerAdapter
import com.cheng.youthapartment.bean.GraphVo
import com.cheng.youthapartment.bean.room.RoomDetailBean
import com.cheng.youthapartment.databinding.ActivityRoomBinding
import com.cheng.youthapartment.decoration.GridSpaceItemDecoration
import com.cheng.youthapartment.util.RetrofitUtil
import com.cheng.youthapartment.view.GridLayoutStyle
import kotlin.collections.ArrayList

// todo 写错了，该页面逻辑属于公寓详情，不是房间详情，后续更改
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
                mViewPager.adapter = mAdapter
                runOnUiThread {
                    mAdapter.notifyDataSetChanged()
                    mViewPager.setCurrentItem(0, false)
                    initIndicators()
                    initView()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        // xxx号房间
        mRoomBinding.roomName.text =
            mRoomDetailBean?.apartmentItemVo?.name + " " + mRoomDetailBean?.roomNumber + "号房间"
        val labelSpanCount = minOf(6, mRoomDetailBean?.labelInfoList?.size ?: 0)
        val labelList = ArrayList(mRoomDetailBean?.labelInfoList.orEmpty())
        val labelSpacing = resources.getDimensionPixelSize(R.dimen.label_grid_space)
        mRoomBinding.roomRvLabel.layoutManager = GridLayoutManager(this, labelSpanCount)
        // 添加网格间距装饰器（处理首尾无间距）
        mRoomBinding.roomRvLabel.addItemDecoration(
            GridSpaceItemDecoration(
                spanCount = 6,
                spacing = labelSpacing,
                includeEdge = false
            )
        )
        mRoomBinding.roomRvLabel.adapter =
            RvAdapter(
                this,
                labelList,
                R.layout.item_text_label
            ) { holder, position ->
                val labelText: TextView = holder.itemView.findViewById(R.id.room_label)
                labelText.text = labelList[position]?.name ?: ""
            }
        mRoomBinding.roomPageRent.text = "$${mRoomDetailBean?.rent.toString()}/月"

        // 基本信息
        val attrList = mutableListOf<String>()
        mRoomDetailBean?.attrValueVoList?.forEach {
            attrList.add(it.name)
        }
        mRoomBinding.roomBaseInfo.setData(attrList)


        // 配套说明
        val facilitySpanCount = minOf(6, mRoomDetailBean?.facilityInfoList?.size ?: 0)
        val facilityList = ArrayList(mRoomDetailBean?.facilityInfoList.orEmpty())
        mRoomBinding.roomRvFacilityInfo.layoutManager = GridLayoutManager(this, facilitySpanCount)
        mRoomBinding.roomRvFacilityInfo.addItemDecoration(
            GridSpaceItemDecoration(
                spanCount = 6,
                spacing = labelSpacing,
                includeEdge = false
            )
        )
        mRoomBinding.roomRvFacilityInfo.adapter = RvAdapter(
            this,
            facilityList,
            R.layout.item_icon_facility_info
        ) { holder, position ->
            val facilityInfoImage = holder.itemView.findViewById<ImageView>(R.id.facility_img)
            val facilityInfoText = holder.itemView.findViewById<TextView>(R.id.facility_text)
            facilityInfoText.text = facilityList[position]?.name ?: ""
            when (facilityInfoText.text) {
                "空调" -> {
                    facilityInfoImage.setImageResource(R.drawable.svg_air_conditioner)
                }

                "洗衣机" -> {
                    facilityInfoImage.setImageResource(R.drawable.svg_washing_machine)
                }

                "冰箱" -> {
                    facilityInfoImage.setImageResource(R.drawable.svg_icebox)
                }

                "书桌" -> {
                    facilityInfoImage.setImageResource(R.drawable.svg_desk)
                }

                "WIFI" -> {
                    facilityInfoImage.setImageResource(R.drawable.svg_wifi)
                }

                "床" -> {
                    facilityInfoImage.setImageResource(R.drawable.svg_bed)
                }

                "沙发" -> {
                    facilityInfoImage.setImageResource(R.drawable.svg_sofa)
                }

                "微波炉" -> {
                    facilityInfoImage.setImageResource(R.drawable.svg_microwave_oven)
                }

                "油烟机" -> {
                    facilityInfoImage.setImageResource(R.drawable.svg_range_hood)
                }

                "热水器" -> {
                    facilityInfoImage.setImageResource(R.drawable.svg_water_heater)
                }

                "衣柜" -> {
                    facilityInfoImage.setImageResource(R.drawable.svg_closet)
                }

                "电视机" -> {
                    facilityInfoImage.setImageResource(R.drawable.svg_tv_set)
                }

                else -> {
                    facilityInfoImage.setImageResource(R.drawable.svg_position)
                }
            }
        }

        // 位置详情

        // 费用明细
        //val attrList = mutableListOf<String>()
        //mRoomDetailBean?.feeValueVoList?.forEach {
        //    attrList.add(it.name)
        //}
        //mRoomBinding.roomBaseInfo.setStyle(GridLayoutStyle.OTHER_STYLE)
        //mRoomBinding.roomBaseInfo.setData(attrList)

        // 可选支付方式

        // 预约看房button
        mRoomBinding.btnReserveHouse.setOnClickListener {

        }
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
                if (index == 0) R.drawable.shape_indicator_selected
                else R.drawable.shape_indicator_default
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