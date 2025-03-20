package com.cheng.youthapartment.activity

import android.annotation.SuppressLint
import android.content.Intent
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
import com.cheng.youthapartment.adapter.BannerAdapter
import com.cheng.youthapartment.bean.GraphVo
import com.cheng.youthapartment.bean.room.RoomDetailBean
import com.cheng.youthapartment.databinding.ActivityRoomBinding
import com.cheng.youthapartment.util.Logger
import com.cheng.youthapartment.util.RetrofitUtil
import com.cheng.youthapartment.decoration.grid_view.GridLayoutStyle
import com.cheng.youthapartment.decoration.grid_view.SpaceItemDecoration
import com.cheng.youthapartment.decoration.grid_view.LabelSpaceDecoration
import kotlin.collections.ArrayList

/**
 * 房间详情页
 * @author Cheng
 * @since 2025/03/05
 * todo 公寓详情基于此页面进行修改
 */
class RoomActivity : BaseActivity() {
    private val mRoomBinding: ActivityRoomBinding by lazy {
        ActivityRoomBinding.inflate(layoutInflater)
    }
    private lateinit var mAdapter: BannerAdapter
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
        setContentView(mRoomBinding.root)

        getApartmentById()
        setupViewPager()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getApartmentById() {
        mAdapter = BannerAdapter(mGraphVoList, this)
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
        mRoomBinding.roomRvLabel.layoutManager = GridLayoutManager(this, labelSpanCount)
        val labelSpacing = resources.getDimensionPixelSize(R.dimen.label_grid_space)
        // 网格间距装饰器
        mRoomBinding.roomRvLabel.addItemDecoration(
            LabelSpaceDecoration(
                spanCount = labelSpanCount,
                rightSpacing = labelSpacing,
                bottomSpacing = labelSpacing
            )
        )
        mRoomBinding.roomRvLabel.adapter =
            RvAdapter(
                this,
                labelList,
                R.layout.item_text_label
            ) { holder, position ->
                val labelText: TextView = holder.itemView.findViewById(R.id.item_label)
                labelText.text = labelList[position]?.name ?: ""
                labelText.textSize = 15f
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
            SpaceItemDecoration(
                spanCount = facilitySpanCount,
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

        // 位置详情 todo 后续接入高德SDK

        // 费用明细
        // todo 缺少配套标签，后续优化
        val freeMap = mutableMapOf<String, String>()
        mRoomDetailBean?.feeValueVoList?.forEach {
            freeMap[it.feeKeyName] = "￥${it.feeKeyId}${it.unit}"
        }
        Logger.d("freeMap: $freeMap")
        mRoomBinding.roomFeeValueInfo.setGridLayoutStyle(GridLayoutStyle.OTHER_STYLE)
        mRoomBinding.roomFeeValueInfo.setData(dataMap = freeMap)

        // 可选支付方式
        val payTypeMap = mutableMapOf<String, String>()
        mRoomDetailBean?.paymentTypeList?.forEach {
            payTypeMap[it.name] = it.additionalInfo
        }
        mRoomBinding.roomPayType.setGridLayoutStyle(GridLayoutStyle.OTHER_STYLE)
        mRoomBinding.roomPayType.setData(dataMap = payTypeMap)

        // 可选租期
        val leaseTermMap = mutableMapOf<String, String>()
        mRoomDetailBean?.leaseTermList?.forEach {
            leaseTermMap[it.monthCount.toString() + it.unit] = "到期可续"
        }
        mRoomBinding.roomLeaseTerm.setGridLayoutStyle(GridLayoutStyle.OTHER_STYLE)
        mRoomBinding.roomLeaseTerm.setData(dataMap = leaseTermMap)

        // 所属公寓
        mRoomDetailBean?.apartmentItemVo?.let { itemVo ->
            mRoomBinding.roomByApartment.setData(itemVo)
            mRoomBinding.roomByApartment.setOnClickListener {
                val intent = Intent(this, ApartmentActivity::class.java)
                intent.putExtra("apartment_id", itemVo.id)
                startActivity(intent)
            }
        }

        // 预约看房button
        mRoomBinding.btnReserveHouse.setOnClickListener {
            val intent = Intent(this, AppointmentActivity::class.java)
            intent.putExtra("appoint_apartment", mRoomDetailBean?.apartmentItemVo)
            startActivity(intent)
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