package com.cheng.youthapartment.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.view.setMargins
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationListener
import com.amap.api.location.IReGeoLocationCallback
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MarkerOptions
import com.cheng.youthapartment.App
import com.cheng.youthapartment.R
import com.cheng.youthapartment.adapter.BannerAdapter
import com.cheng.youthapartment.adapter.RvAdapter
import com.cheng.youthapartment.bean.properties.GraphBean
import com.cheng.youthapartment.bean.room.RoomDetailBean
import com.cheng.youthapartment.databinding.ActivityRoomBinding
import com.cheng.youthapartment.decoration.grid_view.GridLayoutStyle
import com.cheng.youthapartment.decoration.grid_view.LabelSpaceDecoration
import com.cheng.youthapartment.decoration.grid_view.SpaceItemDecoration
import com.cheng.youthapartment.util.DataUtil
import com.cheng.youthapartment.util.Logger
import com.cheng.youthapartment.util.RetrofitUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.abs


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
    private var mGraphBeanList = mutableListOf<GraphBean>()
    private var mIsUserScrolling = false

    private val mScrollDelay = 3000L
    private val mHandler = Handler(Looper.getMainLooper())
    private val mAutoScrollRunnable = object : Runnable {
        override fun run() {
            if (mGraphBeanList.size > 1 && !mIsUserScrolling) {
                val currentItem = mViewPager.currentItem
                // 改为向右滑动逻辑
                val nextItem = if (currentItem == mGraphBeanList.size - 1) 0 else currentItem + 1
                mViewPager.setCurrentItem(nextItem, true)
            }
            mHandler.postDelayed(this, mScrollDelay)
        }
    }

    //声明AMapLocationClient类对象
    private var mLocationClient: AMapLocationClient? = null

    //声明定位回调监听器
    private var mLocationListener: AMapLocationListener = AMapLocationListener { }

    private var mLastX = 0f
    private var mLastY = 0f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(mRoomBinding.root)

        // 初始化地图
        mRoomBinding.roomMap.onCreate(savedInstanceState)
        setUpViewPager()
        getRoomById()
        setMapSlidingConflict()
    }

    /**
     * 设置地图
     * @param [longitude] 精度
     * @param [latitude]: 维度
     */
    private fun setMap(longitude: String, latitude: String) {
        //初始化定位
        try {
            val map = mRoomBinding.roomMap.map ?: return
            val lng = longitude.toDoubleOrNull() ?: 0.0
            val lat = latitude.toDoubleOrNull() ?: 0.0
            val latLng = LatLng(lat, lng)
            // 15f为缩放级别
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            map.addMarker(
                MarkerOptions().position(latLng)
                    .title("房源位置")
            )
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
    private fun getRoomById() {
        mAdapter = BannerAdapter(mGraphBeanList, this)
        mViewPager.adapter = mAdapter

        lifecycleScope.launch(Dispatchers.IO) {
            RetrofitUtil.get<RoomDetailBean>(
                "/app/room/getDetailById",
                App.getToken(),
                mapOf("id" to intent.getIntExtra("room_id", 0))
            ) { _, response ->
                response?.let {
                    mRoomDetailBean = response
                    mGraphBeanList.addAll(mRoomDetailBean!!.graphVoList)
                    runOnUiThread {
                        mAdapter.notifyDataSetChanged()
                        mViewPager.setCurrentItem(0, false)
                        initIndicators()
                        initView()
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        // 初始化指示器
        initIndicators()
        // xxx号房间
        mRoomBinding.roomName.text =
            mRoomDetailBean?.apartmentDetailBean?.name + " " + mRoomDetailBean?.roomNumber + "号房间"
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
        mRoomBinding.roomPageRent.text = "￥${mRoomDetailBean?.rent.toString()}/月"

        // 基本信息
        val attrList = mutableListOf<String>()
        mRoomDetailBean?.attrValueList?.forEach {
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
            DataUtil.setFacility(facilityInfoText.text, facilityInfoImage)
        }

        // 显示地址数据
        mRoomDetailBean?.apartmentDetailBean?.let {
            mRoomBinding.roomLocation.text = it.addressDetail
            setMap(it.longitude, it.latitude)
        }


        // 费用明细
        val freeMap = mutableMapOf<String, String>()
        mRoomDetailBean?.feeValueList?.forEach {
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
        mRoomDetailBean?.apartmentDetailBean?.let { itemVo ->
            mRoomBinding.roomByApartment.setData(itemVo)
            mRoomBinding.roomByApartment.setOnClickListener {
                val intent = Intent(this, ApartmentActivity::class.java)
                intent.putExtra("apartment_id", itemVo.id)
                startActivity(intent)
            }
        }

        // 预约看房button
        mRoomBinding.btnReserveHouse.setOnClickListener {
            val intent = Intent(this, AppointmentInfoActivity::class.java)
            mRoomDetailBean?.apartmentDetailBean?.facilityInfoList = emptyList()
            intent.putExtra("appoint_apartment", mRoomDetailBean?.apartmentDetailBean)
            startActivity(intent)
        }
    }

    /**
     * 监听滑动图片
     */
    private fun setUpViewPager() {
        mViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateIndicators(position)
            }
        })
    }

    /**
     * 处理地图控件与父组件的滑动冲突
     * TODO: 滑动冲突依旧存在，待解决
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun setMapSlidingConflict() {
        mRoomBinding.roomMap.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    mLastX = event.x
                    mLastY = event.y
                    // 请求父View不拦截触摸事件
                    mRoomBinding.root.requestDisallowInterceptTouchEvent(true)
                }

                MotionEvent.ACTION_MOVE -> {
                    // 计算绝对值
                    val deltaX = abs(event.x - mLastX)
                    val deltaY = abs(event.y - mLastY)
                    // 当y坐标偏移量大于x坐标偏移量，且deltaY大于阈值(10像素), 则为垂直滑动，交给父View处理
                    if (deltaY > deltaX && deltaY > 10) {
                        mRoomBinding.root.requestDisallowInterceptTouchEvent(false)
                    } else {
                        mRoomBinding.root.requestDisallowInterceptTouchEvent(true)
                    }
                    mLastX = event.x
                    mLastY = event.y
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // 手指抬起时, 恢复父View拦截
                    mRoomBinding.root.requestDisallowInterceptTouchEvent(false)
                }
            }
            false
        }
    }

    /**
     * 初始化指示器
     */
    private fun initIndicators() {
        mIndicator.removeAllViews()
        for (index in mGraphBeanList.indices) {
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