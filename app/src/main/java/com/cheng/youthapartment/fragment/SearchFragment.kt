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
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cheng.youthapartment.App
import com.cheng.youthapartment.R
import com.cheng.youthapartment.activity.HomeActivity
import com.cheng.youthapartment.activity.RoomActivity
import com.cheng.youthapartment.adapter.RvAdapter
import com.cheng.youthapartment.adapter.SquareCrop
import com.cheng.youthapartment.decoration.grid_view.SpaceItemDecoration
import com.cheng.youthapartment.entity.enums.FilterType
import com.cheng.youthapartment.entity.enums.PayTypeId
import com.cheng.youthapartment.entity.region.CityBean
import com.cheng.youthapartment.entity.region.DistrictBean
import com.cheng.youthapartment.entity.region.ProvinceBean
import com.cheng.youthapartment.entity.room.RoomBean
import com.cheng.youthapartment.entity.room.RoomRecord
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

    // 筛选栏的四个筛选控件
    private lateinit var regionText: TextView
    private lateinit var regionImg: ImageView
    private lateinit var priceText: TextView
    private lateinit var priceImg: ImageView
    private lateinit var payTypeText: TextView
    private lateinit var payTypeImg: ImageView
    private lateinit var sortText: TextView
    private lateinit var sortImg: ImageView

    private var currentPopupWindow: PopupWindow? = null

    private var mToken: String = App.getToken()
    private var mCurrentRequestJob: Job? = null
    private var mCurrentPage = 1
    private var mPageSize = 6
    private var mLastRequestTime = 0L
    private val mDebounceTime = 1000L
    private var mTotalScrollDistance = 0 // 累计滑动距离
    private var mIsScrollingUp = false // 是否正在向上滑动

    // 选中的省份、城市、区县文本
    private var selectProvinceView: TextView? = null
    private var selectCityView: TextView? = null
    private var selectDistrictView: TextView? = null

    // 显示价格的区间，如0-1500, 第一个值为最小租金, 第二个值为最大租金, 数据来源于string.xml的price_ranges属性
    private var filterPriceList: List<Int>? = null

    // 添加筛选控件状态Map
    private val filterViewStateMap = mutableMapOf<LinearLayout, Boolean>()

    // 选中的视图Map(不包含地区)，根据筛选类型存储对应的选中视图
    private val selectedViewMap = mutableMapOf<FilterType, LinearLayout?>()

    private var provinceId = 0L
    private var cityId = 0L
    private var districtId = 0L

    private var filterPayTypeId = -1

    // 0代表高价优先(倒序), 1代表低价优先(正序), 默认-1不排序
    private var filterSortType = -1


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
     * 初始化滚动监听
     */
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
     * 设置搜索过滤器
     * TODO 重复点击控件时需要收起控件
     */
    private fun setSearchFilter() {
        regionText = mView.findTextViewById(R.id.search_region_text)
        regionImg = mView.findImageViewById(R.id.search_region_img)

        priceText = mView.findTextViewById(R.id.search_price_text)
        priceImg = mView.findImageViewById(R.id.search_price_img)

        payTypeText = mView.findTextViewById(R.id.search_pay_type_text)
        payTypeImg = mView.findImageViewById(R.id.search_pay_type_img)

        sortText = mView.findTextViewById(R.id.search_sort_text)
        sortImg = mView.findImageViewById(R.id.search_sort_img)

        // 初始化筛选控件状态
        filterViewStateMap[mLocationView] = false
        filterViewStateMap[mPriceView] = false
        filterViewStateMap[mPayTypeView] = false
        filterViewStateMap[mSortView] = false

        // 地区
        mLocationView.setOnClickListener {
            updateFilterViewStates(mLocationView)
            setFilterViewAppearance(regionText, regionImg)
            showLocationFilterPopup(it)
        }
        // 价格
        mPriceView.setOnClickListener {
            updateFilterViewStates(mPriceView)
            setFilterViewAppearance(priceText, priceImg)
            showFilterPopup(it, R.array.price_ranges, FilterType.PRICE)
        }
        // 支付方式
        mPayTypeView.setOnClickListener {
            updateFilterViewStates(mPayTypeView)
            setFilterViewAppearance(payTypeText, payTypeImg)
            showFilterPopup(it, R.array.payment_method, FilterType.PAY_TYPE)
        }
        // 排序
        mSortView.setOnClickListener {
            updateFilterViewStates(mSortView)
            setFilterViewAppearance(sortText, sortImg)
            showFilterPopup(it, R.array.sort, FilterType.SORT_TYPE)
        }
    }

    /**
     * 设置筛选项的选中状态
     */
    private fun setFilterPopupItemSelected(
        itemView: LinearLayout,
        textView: TextView,
        isSelected: Boolean
    ) {
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
    private fun updateFilterViewStates(selectedView: LinearLayout) {
        // 将所有控件状态设为false
        filterViewStateMap.keys.forEach { view ->
            filterViewStateMap[view] = (view == selectedView)
        }
    }

    /**
     * 设置筛选控件外观
     */
    private fun setFilterViewAppearance(textView: TextView, imageView: ImageView? = null) {
        // 先将所有控件设置为默认状态
        resetAllFilterViews()
        // 设置当前选中控件的高亮状态
        setSelectHighlightColor(textView, imageView)
    }

    /**
     * 重置所有筛选控件为默认状态
     */
    private fun resetAllFilterViews() {
        // 直接使用类级别的变量，不需要重新获取
        setNoSelectDefaultColor(regionText, regionImg)
        setNoSelectDefaultColor(priceText, priceImg)
        setNoSelectDefaultColor(payTypeText, payTypeImg)
        setNoSelectDefaultColor(sortText, sortImg)
    }

    /**
     * 设置选中的文本与图片为高亮颜色
     */
    private fun setSelectHighlightColor(textView: TextView, imageView: ImageView? = null) {
        textView.setTextColor(mActivity.getColor(R.color.light_cyan))
        imageView?.let {
            it.animate().rotation(180f).setDuration(300).start()
            it.backgroundTintList = ColorStateList.valueOf(
                ResourcesCompat.getColor(
                    resources,
                    R.color.light_cyan,
                    null
                )
            )
        }
    }

    /**
     * 设置非选中的文本与图片为默认颜色
     */
    private fun setNoSelectDefaultColor(textView: TextView, imageView: ImageView? = null) {
        textView.setTextColor(mActivity.getColor(R.color.icon_or_text))
        imageView?.let {
            it.animate().rotation(0f).setDuration(300).start()
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
                resetAllFilterViews()
            }
            findBtn.setOnClickListener {
                popupWindow.dismiss()
                resetAllFilterViews()
                getRoomList(true, mCurrentPage, mPageSize)
            }
        }
    }

    /**
     * 下拉筛选菜单展示
     * @param view 具体的筛选条件的View
     * @param stringArrayId string.xml文件内定义的固定字符串数组, 具体为每个筛选的条目值
     * @param filterType 筛选方式枚举类, 地区筛选在showLocationFilterPopup()方法单独处理
     */
    private fun showFilterPopup(view: View, stringArrayId: Int, filterType: FilterType) {
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
                R.layout.item_filter_text
            ) { holder, position ->
                val itemView = holder.itemView as LinearLayout
                val filterTextView = itemView.findTextViewById(R.id.filter_condition_text)

                filterTextView.text = stringArray[position]
                // 根据筛选类型获取上一次缓存的状态值, 若该值不为默认值或null, 那么就高亮该控件
                val isCurrentItemSelected = when (filterType) {
                    FilterType.PRICE -> {
                        filterPayTypeId = -1
                        filterSortType = -1
                        if (position == 0) {
                            // "不限"选项，当filterPriceList为null时应该高亮
                            filterPriceList == null
                        } else {
                            // 其他价格范围选项
                            val currentItemPriceList = stringArray[position].getNumber()
                            filterPriceList != null &&
                                    currentItemPriceList?.size == filterPriceList!!.size &&
                                    currentItemPriceList.zip(filterPriceList!!)
                                        .all { it.first == it.second }
                        }
                    }

                    FilterType.PAY_TYPE -> {
                        filterSortType = -1
                        filterPriceList = null
                        if (position == 0) {
                            // "默认排序"选项，当filterPayTypeId为-1时高亮
                            false
                        } else {
                            // 检查当前支付方式是否被选中
                            val payTypeName = stringArray[position]
                            val payTypeId = PayTypeId.getIdByType(payTypeName)
                            filterPayTypeId == payTypeId
                        }
                    }

                    FilterType.SORT_TYPE -> {
                        filterPayTypeId = -1
                        filterPriceList = null
                        if (position == 0) {
                            // "默认排序"选项，当filterSortType为-1时高亮
                            false
                        } else {
                            // 其他排序方式选项
                            filterSortType == position
                        }
                    }
                }
                // 设置当前项的选中控件高亮显示
                setFilterPopupItemSelected(itemView, filterTextView, isCurrentItemSelected)
                // 如果当前项是选中的，更新选中视图引用
                if (isCurrentItemSelected) {
                    selectedViewMap[filterType] = itemView
                }

                itemView.setOnClickListener {
                    when (filterType) {
                        FilterType.PRICE -> {
                            // 获取筛选的价格
                            filterPriceList =
                                if (position == 0) null else stringArray[position].getNumber()
                        }

                        FilterType.PAY_TYPE -> {
                            // 获取筛选的支付方式
                            filterPayTypeId = PayTypeId.getIdByType(stringArray[position])
                        }

                        FilterType.SORT_TYPE -> {
                            // 获取筛选的排序方式
                            if (position in 0..2) {
                                filterSortType = position
                                Logger.d("排序id: $filterSortType")
                            }
                        }
                    }
                    // 重置之前选中项的样式
                    selectedViewMap[filterType]?.let { oldSelectedView ->
                        val oldTextView = oldSelectedView.getChildAt(0) as TextView
                        setFilterPopupItemSelected(oldSelectedView, oldTextView, false)
                    }

                    // 设置当前选中项的样式
                    setFilterPopupItemSelected(itemView, filterTextView, true)

                    // 更新选中视图引用
                    selectedViewMap[filterType] = itemView
                }
            }

            // 取消与查找按钮
            val cancelBtn = popupView.findButtonById(R.id.item_cancel_btn)
            val findBtn = popupView.findButtonById(R.id.item_find_btn)
            cancelBtn.setOnClickListener {
                popupWindow.dismiss()
                resetAllFilterViews()
            }
            findBtn.setOnClickListener {
                popupWindow.dismiss()
                resetAllFilterViews()
                getRoomList(
                    true,
                    mCurrentPage,
                    mPageSize,
                    filterPriceList?.get(0),
                    filterPriceList?.get(1),
                    filterPayTypeId,
                    filterSortType
                )
            }
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
     * 获取房间列表
     * @param isUpdate: 是否更新视图，不更新则为添加
     * @param currentPage 当前页码
     * @param pageSize 每页数量
     * @param minRent 最小租金
     * @param maxRent 最大租金
     * @param paymentTypeId 支付方式ID
     * @param sortType 排序方式
     */
    private fun getRoomList(
        isUpdate: Boolean = false,
        currentPage: Int,
        pageSize: Int,
        minRent: Int? = null,
        maxRent: Int? = null,
        paymentTypeId: Int = -1,
        sortType: Int = -1
    ) {
        Logger.d("minRent: $minRent, maxRent: $maxRent, paymentTypeId: $paymentTypeId, sortType: $sortType")
        mCurrentRequestJob?.cancel()
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
                "paymentTypeId" to (if (paymentTypeId != -1) paymentTypeId else ""),
                "orderType" to (if (sortType == 0) "desc" else if (sortType == 1) "asc" else "")
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

    /**
     * 获取所有的省份列表
     */
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

    /**
     * 根据省份id获取所有的城市列表
     */
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

    /**
     * 根据城市id获取具体地区列表
     */
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