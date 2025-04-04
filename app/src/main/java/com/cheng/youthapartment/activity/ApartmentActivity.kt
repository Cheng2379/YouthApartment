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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MarkerOptions
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cheng.youthapartment.App
import com.cheng.youthapartment.R
import com.cheng.youthapartment.adapter.BannerAdapter
import com.cheng.youthapartment.adapter.RvAdapter
import com.cheng.youthapartment.adapter.SquareCrop
import com.cheng.youthapartment.bean.properties.GraphBean
import com.cheng.youthapartment.bean.apartment.ApartmentDetailBean
import com.cheng.youthapartment.bean.room.RoomBean
import com.cheng.youthapartment.bean.room.RoomRecord
import com.cheng.youthapartment.databinding.ActivityApartmentBinding
import com.cheng.youthapartment.decoration.grid_view.LabelSpaceDecoration
import com.cheng.youthapartment.decoration.grid_view.SpaceItemDecoration
import com.cheng.youthapartment.util.DataUtil
import com.cheng.youthapartment.util.Logger
import com.cheng.youthapartment.util.RetrofitUtil
import com.cheng.youthapartment.util.ViewUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

    private var mRvAdapter: RvAdapter<RoomRecord>? = null
    private var mRoomList = ArrayList<RoomRecord>()
    private var mApartmentId = 0

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

    //声明AMapLocationClient类对象
    private var mLocationClient: AMapLocationClient? = null

    //声明定位回调监听器
    private var mLocationListener: AMapLocationListener = AMapLocationListener { }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(mApartmentBinding.root)

        // 初始化地图
        mApartmentBinding.apartmentMap.onCreate(savedInstanceState)
        setUpViewPager()
        getApartmentById()
        getRoomItemByApartmentId()
    }

    /**
     * 设置地图
     * @param [longitude] 精度
     * @param [latitude]: 维度
     */
    private fun setMap(longitude: String, latitude: String) {
        val map = mApartmentBinding.apartmentMap.map ?: return
        val isNight = ViewUtil.isNightModel()
        //初始化定位
        try {
            val lng = longitude.toDoubleOrNull() ?: 0.0
            val lat = latitude.toDoubleOrNull() ?: 0.0
            val latLng = LatLng(lat, lng)
            // 15f为缩放级别
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            map.addMarker(
                MarkerOptions().position(latLng)
                    .title("房源位置")
            )
            map.mapType = if (isNight) AMap.MAP_TYPE_NIGHT else AMap.MAP_TYPE_NORMAL
        } catch (e: Exception) {
            Logger.e("map initialization fail")
        }

        mLocationClient = AMapLocationClient(this)
        mLocationClient?.let { client ->
            //设置定位回调监听
            client.setLocationListener(mLocationListener)
            client.setReGeoLocationCallback {
                Logger.d("amapLocation: $it")
            }

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getApartmentById() {
        mApartmentId = intent.getIntExtra("apartment_id", 0)

        lifecycleScope.launch(Dispatchers.IO) {
            RetrofitUtil.get<ApartmentDetailBean>(
                "/app/apartment/getDetailById",
                App.getToken(),
                mapOf("id" to mApartmentId)
            ) { _, response ->
                response?.let {
                    mGraphList.addAll(it.graphVoList)
                    runOnUiThread {
                        initIndicators()
                        initView(it)
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initView(apartmentDetailBean: ApartmentDetailBean) {
        mAdapter = BannerAdapter(mGraphList, this)
        mViewPager.adapter = mAdapter

        mApartmentBinding.apartmentName.text = apartmentDetailBean.name

        val labelSpanCount = minOf(6, apartmentDetailBean.labelInfoList.size ?: 0)
        val labelList = ArrayList(apartmentDetailBean.labelInfoList)
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
        mApartmentBinding.apartmentRent.text = "￥${apartmentDetailBean.minRent}/月起"

        // 社区介绍
        mApartmentBinding.apartmentBaseInfo.text = apartmentDetailBean.introduction

        // 配套说明
        val facilitySpanCount = minOf(6, apartmentDetailBean.facilityInfoList.size)
        val facilityList = ArrayList(apartmentDetailBean.facilityInfoList)
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
            DataUtil.setFacility(this, facilityInfoText.text, facilityInfoImage)
        }

        // 位置详情
        mApartmentBinding.apartmentLocation.text = apartmentDetailBean.addressDetail
        setMap(apartmentDetailBean.longitude, apartmentDetailBean.latitude)

        // 可选房间列表
        mRvRoom.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mRvAdapter = RvAdapter(
            this,
            mRoomList,
            R.layout.item_room
        ) { holder, position ->
            val itemView = holder.itemView
            val roomImg: ImageView = itemView.findViewById(R.id.room_img)
            val roomName: TextView = itemView.findViewById(R.id.room_name)
            val roomLocation: TextView = itemView.findViewById(R.id.room_location)
            val roomRent: TextView = itemView.findViewById(R.id.search_item_room_rent)

            val roomItem = mRoomList[position]
            val graphVoList = roomItem.graphVoList

            if (graphVoList.isNotEmpty()) {
                Glide.with(this)
                    .load(graphVoList[0].url)
                    .apply(
                        RequestOptions.bitmapTransform(SquareCrop(20))
                    )
                    .error(R.drawable.img_fail)
                    .into(roomImg)
            }
            roomName.text =
                roomItem.apartmentBean.name + " " + roomItem.roomNumber + "号房间"
            roomLocation.text =
                roomItem.apartmentBean.provinceName + "  " + roomItem.apartmentBean.cityName + "  " + roomItem.apartmentBean.districtName
            roomRent.text =
                "￥ " + roomItem.rent.stripTrailingZeros().toPlainString() + "/月"

            itemView.setOnClickListener {
                val intent = Intent(this, RoomActivity::class.java)
                intent.putExtra("room_id", roomItem.id)
                startActivity(intent)
            }
        }
        mRvRoom.adapter = mRvAdapter

        // 预约看房
        mApartmentBinding.btnReserveHouse.setOnClickListener {
            val intent = Intent(this, AppointmentInfoActivity::class.java)
            intent.putExtra("appoint_apartment", apartmentDetailBean)
            startActivity(intent)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getRoomItemByApartmentId() {
        Logger.d("id: $mApartmentId")
        lifecycleScope.launch {
            RetrofitUtil.get<RoomBean>(
                "/app/room/pageItemByApartmentId",
                App.getToken(),
                mapOf(
                    "current" to 1,
                    "size" to 999,
                    "id" to mApartmentId
                )
            ) { _, response ->
                response?.let {
                    mRoomList = it.roomRecords as ArrayList<RoomRecord>
                    mRvAdapter?.updateDta(mRoomList)
                    mApartmentBinding.dataLoadingCompleted.visibility = TextView.VISIBLE
                }
            }
        }
    }

    private fun setUpViewPager() {
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
        for (index in mGraphList.indices) {
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