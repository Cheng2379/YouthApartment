package com.cheng.youthapartment.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cheng.youthapartment.App
import com.cheng.youthapartment.activity.HomeActivity
import com.cheng.youthapartment.R
import com.cheng.youthapartment.activity.RoomActivity
import com.cheng.youthapartment.adapter.RvAdapter
import com.cheng.youthapartment.adapter.SquareCrop
import com.cheng.youthapartment.bean.region.CityBean
import com.cheng.youthapartment.bean.region.DistrictBean
import com.cheng.youthapartment.bean.region.ProvinceBean
import com.cheng.youthapartment.bean.room.RoomRecord
import com.cheng.youthapartment.bean.room.RoomBean
import com.cheng.youthapartment.decoration.grid_view.SpaceItemDecoration
import com.cheng.youthapartment.util.Logger
import com.cheng.youthapartment.util.RetrofitUtil
import com.cheng.youthapartment.util.findButtonById
import com.cheng.youthapartment.util.findImageViewById
import com.cheng.youthapartment.util.findRecyclerViewById
import com.cheng.youthapartment.util.findTextViewById
import com.cheng.youthapartment.util.getNumber
import com.cheng.youthapartment.view.DropDownFilterViewUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.math.min

/**
 *
 * @author Cheng
 * @since 2024/12/20
 */
class SearchFragment : Fragment() {
    private lateinit var mView: View
    private val mActivity by lazy { requireActivity() as HomeActivity }

    private val mLocationView: LinearLayout by lazy { mView.findViewById(R.id.search_region) }
    private val mPriceView: LinearLayout by lazy { mView.findViewById(R.id.search_price) }
    private val mPayTypeView: LinearLayout by lazy { mView.findViewById(R.id.search_pay_type) }
    private val mSortView: LinearLayout by lazy { mView.findViewById(R.id.search_sort) }
    private val mSR: SwipeRefreshLayout by lazy { mView.findViewById(R.id.search_refresh_layout) }
    private val mRv: RecyclerView by lazy { mView.findViewById(R.id.search_rv_home) }
    private val mDataEmpty: TextView by lazy { mView.findViewById(R.id.search_data_empty) }
    private var mRvAdapter: RvAdapter<RoomRecord>? = null
    private var mRoomList = ArrayList<RoomRecord>()

    private var currentPopupWindow: PopupWindow? = null

    private var mToken: String = App.getToken()
    private var mCurrentRequestJob: Job? = null
    private var mCurrentPage = 1
    private var mPageSize = 6
    private var mLastRequestTime = 0L
    private val mDebounceTime = 1000L
    private var mTotalScrollDistance = 0 // 累计滑动距离
    private var mIsScrollingUp = false // 是否正在向上滑动
    private var isSelect = false

    // 选中的省份、城市、区县文本
    private var selectProvinceView: TextView? = null
    private var selectCityView: TextView? = null
    private var selectDistrictView: TextView? = null

    // 选中的价格、付款方式、排序
    private var selectPriceView: LinearLayout? = null
    private var selectPayTypeView: TextView? = null
    private var selectSortView: TextView? = null

    private var provinceId = 0L
    private var cityId = 0L
    private var districtId = 0L
    // 显示价格的区间，如0-1500, 第一个值为最小租金, 第二个值为最大租金, 数据来源于string.xml的price_ranges属性
    private var filterPriceList: List<Int>? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mView = inflater.inflate(R.layout.fragment_search, container, false)
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initScrollListener()
        getRoomList(false, mCurrentPage, mPageSize)
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        mDataEmpty.visibility = TextView.GONE
        mRvAdapter =
            RvAdapter(requireContext(), mRoomList, R.layout.item_room) { holder, position ->
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
                roomName.text = roomItem.apartmentBean.name + " " + roomItem.roomNumber + "号房间"
                roomLocation.text =
                    roomItem.apartmentBean.provinceName + "  " + roomItem.apartmentBean.cityName + "  " + roomItem.apartmentBean.districtName
                roomRent.text = "￥ " + roomItem.rent.stripTrailingZeros().toPlainString() + "/月"

                itemView.setOnClickListener {
                    val intent = Intent(mActivity, RoomActivity::class.java)
                    intent.putExtra("room_id", roomItem.id)
                    startActivity(intent)
                }
            }
        mRv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mRv.adapter = mRvAdapter
        if (mRoomList.isEmpty()) {
            mDataEmpty.visibility = TextView.VISIBLE
            mRv.visibility = RecyclerView.GONE
            mSR.visibility = SwipeRefreshLayout.GONE
        } else {
            mDataEmpty.visibility = TextView.GONE
            mRv.visibility = RecyclerView.VISIBLE
            mSR.visibility = SwipeRefreshLayout.VISIBLE
        }

        setSearchFilter()
    }

    /**
     * 设置搜索过滤器
     * TODO 简化逻辑，筛选栏部分的控件需要设置选中状态(选中颜色与图标方向)
     */
    private fun setSearchFilter() {
        val regionText = mView.findTextViewById(R.id.search_region_text)
        val regionImg = mView.findImageViewById(R.id.search_region_img)
        val priceText = mView.findTextViewById(R.id.search_price_text)
        val priceImg = mView.findImageViewById(R.id.search_price_img)
        val payTypeText = mView.findTextViewById(R.id.search_pay_type_text)
        val payTypeImg = mView.findImageViewById(R.id.search_pay_type_img)
        val sortText = mView.findTextViewById(R.id.search_sort_text)
        val sortImg = mView.findImageViewById(R.id.search_sort_img)

        // 地区
        mLocationView.setOnClickListener {
            setSelectHighlightColor(
                regionText,
                regionImg
            )
            setNoSelectDefaultColor(priceText, priceImg)
            setNoSelectDefaultColor(payTypeText, payTypeImg)
            setNoSelectDefaultColor(sortText, sortImg)
            showLocationFilterPopup(it)
        }
        // 价格
        mPriceView.setOnClickListener {
            setSelectHighlightColor(
                priceText,
                priceImg
            )
            setNoSelectDefaultColor(regionText, regionImg)
            setNoSelectDefaultColor(payTypeText, payTypeImg)
            setNoSelectDefaultColor(sortText, sortImg)
            showPriceFilterPopup(it, R.array.price_ranges)
        }
        // 支付方式
        mPayTypeView.setOnClickListener {
            setSelectHighlightColor(
                payTypeText,
                payTypeImg
            )
            setNoSelectDefaultColor(regionText, regionImg)
            setNoSelectDefaultColor(priceText, priceImg)
            setNoSelectDefaultColor(sortText, sortImg)
            // TODO
            //showPayTypeFilterPopup(it, R.array.payment_method)
        }
        // 排序
        mSortView.setOnClickListener {
            setSelectHighlightColor(
                sortText,
                sortImg
            )
            setNoSelectDefaultColor(regionText, regionImg)
            setNoSelectDefaultColor(priceText, priceImg)
            setNoSelectDefaultColor(payTypeText, payTypeImg)
        }
    }

    private fun initScrollListener() {
        mSR.setOnRefreshListener {
            lifecycleScope.launch {
                delay(1000)
                mCurrentPage = 1
                mTotalScrollDistance = 0
                getRoomList(true, mCurrentPage, mPageSize)
                mSR.isRefreshing = false
                mRvAdapter?.setAllDataLoaded(false)
            }
        }
        mSR.setOnChildScrollUpCallback { _, _ ->
            // 仅在 RecyclerView 滚动到顶部时允许下拉刷新
            mRv.canScrollVertically(-1)
        }

        mRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            private val loadMoreThreshold = 100 // 滑动距离阈值（单位：像素）

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                mIsScrollingUp = dy > 0
                if (dy > 0) {
                    mTotalScrollDistance += dy
                }

                // 获取布局管理器
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                // 判断是否接近底部
                val isNearBottom = lastVisibleItemPosition >= totalItemCount - 1

                // 滑动到底部且累计滑动距离超过阈值
                val shouldLoadMore =
                    isNearBottom && mIsScrollingUp && mTotalScrollDistance >= loadMoreThreshold && !scrollStabilization()

                if (!shouldLoadMore) return

                // 若数据加载完毕，返回
                if (mRvAdapter?.getAllDataLoaded() == true) return

                mCurrentPage++
                Logger.d("上滑加载触发, 当前页码: $mCurrentPage")
                getRoomList(false, mCurrentPage, mPageSize)
                mTotalScrollDistance = 0

            }
        })
    }

    /**
     * 筛选地区
     */
    private fun showLocationFilterPopup(view: View) {
        // 若已有窗口则关闭
        currentPopupWindow?.dismiss()

        currentPopupWindow = DropDownFilterViewUtil.createDropDownPopupWindow(
            mActivity,
            view,
            R.layout.item_popup_filter_region,
            R.style.PopupAnimation
        ) { popupView, popupWindow ->
            // 设置地区RecyclerView
            setupProvinceRecyclerView(
                popupView.findRecyclerViewById(R.id.item_rv_province),
                popupView.findRecyclerViewById(R.id.item_rv_city),
                popupView.findRecyclerViewById(R.id.item_rv_district)
            )

            // 取消与查找按钮
            val cancelBtn = popupView.findButtonById(R.id.item_cancel_btn)
            val findBtn = popupView.findButtonById(R.id.item_find_btn)
            cancelBtn.setOnClickListener {
                popupWindow.dismiss()
            }
            findBtn.setOnClickListener {
                popupWindow.dismiss()
                getRoomList(true, mCurrentPage, mPageSize)
            }
        }
    }

    /**
     * 筛选价格
     */
    private fun showPriceFilterPopup(view: View, stringArrayId: Int) {
        currentPopupWindow?.dismiss()
        val stringArray = resources.getStringArray(stringArrayId).toCollection(ArrayList())

        currentPopupWindow = DropDownFilterViewUtil.createDropDownPopupWindow(
            mActivity,
            view,
            R.layout.item_popup_filter_item
        ) { popupView, popupWindow ->
            val rv = popupView.findRecyclerViewById(R.id.filter_item_rv)

            rv.layoutManager = GridLayoutManager(requireContext(), 3)
            rv.addItemDecoration(SpaceItemDecoration(3, 20, true))
            rv.adapter = RvAdapter(
                requireContext(),
                stringArray,
                R.layout.filter_item_text
            ) { holder, position ->
                val itemView = holder.itemView as LinearLayout
                val filterTextView = itemView.findTextViewById(R.id.filter_condition_text)

                filterTextView.text = stringArray[position]
                // 检查当前项是否是之前选中的价格范围，如果是则高亮显示
                val isCurrentItemSelected = if (position == 0) {
                    // "不限"选项，当filterPriceList为null时应该高亮
                    filterPriceList == null
                } else {
                    // 其他价格范围选项
                    val currentItemPriceList = stringArray[position].getNumber()
                    filterPriceList != null &&
                            currentItemPriceList?.size == filterPriceList!!.size &&
                            currentItemPriceList.zip(filterPriceList!!).all { it.first == it.second }
                }
                // 设置当前项的选中状态
                setPriceItemSelected(itemView, filterTextView, isCurrentItemSelected)
                // 如果当前项是选中的，更新选中视图引用
                if (isCurrentItemSelected) {
                    selectPriceView = itemView
                }

                itemView.setOnClickListener {
                    // 获取筛选的价格
                    filterPriceList = stringArray[position].getNumber()
                    // 重置之前选中项的样式
                    selectPriceView?.let { oldSelectedView ->
                        val oldTextView = oldSelectedView.getChildAt(0) as TextView
                        setPriceItemSelected(oldSelectedView, oldTextView, false)
                    }

                    // 设置当前选中项的样式
                    setPriceItemSelected(itemView, filterTextView, true)

                    // 更新选中视图引用
                    selectPriceView = itemView
                }
            }

            // 取消与查找按钮
            val cancelBtn = popupView.findButtonById(R.id.item_cancel_btn)
            val findBtn = popupView.findButtonById(R.id.item_find_btn)
            cancelBtn.setOnClickListener {
                popupWindow.dismiss()
            }
            findBtn.setOnClickListener {
                popupWindow.dismiss()
                getRoomList(
                    true,
                    mCurrentPage,
                    mPageSize,
                    filterPriceList?.get(0),
                    filterPriceList?.get(1)
                )
            }
        }
    }

    /**
     * 设置价格筛选项的选中状态
     */
    private fun setPriceItemSelected(itemView: LinearLayout, textView: TextView, isSelected: Boolean) {
        if (isSelected) {
            // 设置选中样式
            itemView.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.shape_item_selector_filter
            )
            textView.setTextColor(Color.WHITE)
        } else {
            // 设置未选中样式
            itemView.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.shape_item_filter
            )
            textView.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.icon_or_text
                )
            )
        }
    }

    /**
     * 设置选中的筛选栏文本和图标高亮显示
     */
    private fun setSelectHighlightColor(textView: TextView, imageView: ImageView? = null) {
        if (!isSelect) {
            textView.setTextColor(mActivity.getColor(R.color.light_cyan))
            imageView?.let {
                it.animate().rotationBy(180f).setDuration(500).start()
                it.backgroundTintList = ColorStateList.valueOf(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.light_cyan,
                        null
                    )
                )
            }
            isSelect = true
        } else {
            textView.setTextColor(mActivity.getColor(R.color.icon_or_text))
            imageView?.let {
                it.animate().rotationBy(-180f).setDuration(500).start()
                it.backgroundTintList = ColorStateList.valueOf(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.filter_icon,
                        null
                    )
                )
            }
            isSelect = false
        }
    }

    /**
     * 设置未选中的菜单文本和图标恢复默认颜色
     */
    private fun setNoSelectDefaultColor(textView: TextView, imageView: ImageView? = null) {
        textView.setTextColor(mActivity.getColor(R.color.icon_or_text))
        imageView?.let {
            it.animate().rotationBy(-180f).setDuration(500).start()
            it.backgroundTintList = ColorStateList.valueOf(
                ResourcesCompat.getColor(
                    resources,
                    R.color.filter_icon,
                    null
                )
            )
        }
    }

    /**
     * 设置省份 RecyclerView
     */
    private fun setupProvinceRecyclerView(
        rvProvince: RecyclerView,
        rvCity: RecyclerView,
        rvDistrict: RecyclerView
    ) {
        // 设置数据之前，先重置下这三个id
        provinceId = 0
        cityId = 0
        districtId = 0
        lifecycleScope.launch(Dispatchers.IO) {
            val allProvinceList = getAllProvince()
            // 设置省份
            withContext(Dispatchers.Main) {
                rvProvince.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                rvProvince.adapter = RvAdapter(
                    requireContext(),
                    ArrayList(allProvinceList),
                    R.layout.item_region_text
                ) { holder, position ->
                    val provinceTextView = holder.itemView.findTextViewById(R.id.item_region_text)
                    provinceTextView.text = allProvinceList[position].name
                    holder.itemView.setOnClickListener {
                        // 点击时触发获取id
                        provinceId = allProvinceList[position].id
                        // 更新选中省份的UI
                        selectProvinceView?.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.icon_or_text
                            )
                        )
                        provinceTextView.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.light_cyan
                            )
                        )
                        // 更新当前选中的TextView
                        selectProvinceView = provinceTextView

                        // 设置城市之前重置下城市、区县的RecyclerView
                        rvCity.adapter = null
                        rvDistrict.adapter = null
                        // 设置城市 RecyclerView
                        setupCityRecyclerView(
                            rvCity,
                            rvDistrict,
                            provinceId
                        )
                    }
                }
            }
        }
    }


    /**
     * 设置城市 RecyclerView
     */
    private fun setupCityRecyclerView(
        rvCity: RecyclerView,
        rvDistrict: RecyclerView,
        provinceId: Long
    ) {
        lifecycleScope.launch(Dispatchers.IO) {
            val cityBeanList = getCityByProvinceId(provinceId)
            withContext(Dispatchers.Main) {
                rvCity.layoutManager =
                    LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                rvCity.adapter = RvAdapter(
                    requireContext(),
                    ArrayList(cityBeanList),
                    R.layout.item_region_text
                ) { holder, position ->
                    val cityTextView =
                        holder.itemView.findTextViewById(R.id.item_region_text)
                    cityTextView.text = cityBeanList[position].name
                    holder.itemView.setOnClickListener {
                        // 点击时触发获取id
                        cityId = cityBeanList[position].id
                        // 更新选中城市的UI
                        selectCityView?.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.icon_or_text
                            )
                        )
                        cityTextView.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.light_cyan
                            )
                        )
                        // 更新当前选中的TextView
                        selectCityView = cityTextView

                        rvDistrict.adapter = null
                        // 设置区/县 RecyclerView
                        setupDistrictRecyclerView(
                            rvDistrict,
                            cityId
                        )
                    }
                }
            }
        }
    }

    /**
     * 设置区/县 RecyclerView
     */
    private fun setupDistrictRecyclerView(
        rvDistrict: RecyclerView,
        cityId: Long,
    ) {
        lifecycleScope.launch(Dispatchers.IO) {
            val districtList = getDistrictByCityId(cityId.toInt())
            withContext(Dispatchers.Main) {
                rvDistrict.layoutManager = LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.VERTICAL,
                    false
                )
                rvDistrict.adapter = RvAdapter(
                    requireContext(),
                    ArrayList(districtList),
                    R.layout.item_region_text
                ) { holder, position ->
                    val districtView = holder.itemView.findTextViewById(R.id.item_region_text)
                    districtView.text = districtList[position].name
                    holder.itemView.setOnClickListener {
                        // 点击时获取id
                        districtId = districtList[position].id
                        // 更新选中区/县的UI
                        selectDistrictView?.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.icon_or_text
                            )
                        )
                        districtView.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.light_cyan
                            )
                        )
                        // 更新当前选中的TextView
                        selectDistrictView = districtView
                    }
                }
            }
        }
    }

    /**
     * 忽略快速刷新加载，防抖
     */
    private fun scrollStabilization(): Boolean {
        val currentTime = System.currentTimeMillis()
        if (currentTime - mLastRequestTime < mDebounceTime) {
            return true
        }
        mLastRequestTime = currentTime
        return false
    }

    /**
     * @param isUpdate: 是否更新视图，不更新则为添加
     */
    private fun getRoomList(
        isUpdate: Boolean,
        currentPage: Int,
        pageSize: Int,
        minRent: Int? = null,
        maxRent: Int? = null,
        paymentTypeId: Int? = null
    ) {
        mCurrentRequestJob?.cancel()
        Logger.d("maxxx: $minRent, $maxRent")
        RetrofitUtil.get<RoomBean>(
            "/app/room/pageItem",
            mToken,
            mapOf(
                "current" to currentPage,
                "size" to pageSize,
                "provinceId" to if (provinceId != 0L) provinceId else "",
                "cityId" to if (cityId != 0L) cityId else "",
                "districtId" to if (districtId != 0L) districtId else "",
                "minRent" to (minRent ?: ""),
                "maxRent" to (maxRent ?: ""),
                "paymentTypeId" to (paymentTypeId ?: "")
            )
        ) { _, response ->
            response?.let {
                val newData = it.roomRecords
                if (newData.isEmpty()) {
                    mCurrentRequestJob = lifecycleScope.launch(Dispatchers.Main) {
                        mRvAdapter?.updateDta(emptyList())
                        mSR.visibility = SwipeRefreshLayout.GONE
                        mRv.visibility = RecyclerView.GONE
                        mDataEmpty.visibility = TextView.VISIBLE
                    }
                } else {
                    mCurrentRequestJob = lifecycleScope.launch {
                        withContext(Dispatchers.Main) {
                            if (isUpdate) {
                                mRvAdapter?.updateDta(newData)
                            } else {
                                mRvAdapter?.addData(newData)
                            }
                            mSR.visibility = SwipeRefreshLayout.VISIBLE
                            mRv.visibility = RecyclerView.VISIBLE
                            mDataEmpty.visibility = TextView.GONE
                            Logger.d("currentPage: $currentPage Response RoomListSize: ${newData.size}")
                        }
                    }
                }
            }
        }
    }


    suspend fun getAllProvince(): List<ProvinceBean> =
        suspendCancellableCoroutine { continuation ->
            RetrofitUtil.get<List<ProvinceBean>>(
                "/app/region/province/list",
                App.getToken(),
                null
            ) { _, response ->
                response?.let {
                    continuation.resume(it)
                } ?: run {
                    continuation.resume(emptyList())
                }
            }
        }

    suspend fun getCityByProvinceId(id: Long): List<CityBean> =
        suspendCancellableCoroutine { continuation ->
            RetrofitUtil.get<List<CityBean>>(
                "/app/region/city/listByProvinceId",
                App.getToken(),
                mapOf("id" to id)
            ) { _, response ->
                response?.let {
                    continuation.resume(it)
                } ?: run {
                    continuation.resume(emptyList())
                }
            }
        }

    suspend fun getDistrictByCityId(id: Int): List<DistrictBean> =
        suspendCancellableCoroutine { continuation ->
            RetrofitUtil.get<List<DistrictBean>>(
                "/app/region/district/listByCityId",
                App.getToken(),
                mapOf("id" to id)
            ) { _, response ->
                response?.let {
                    continuation.resume(it)
                } ?: run {
                    continuation.resume(emptyList())
                }
            }
        }
}