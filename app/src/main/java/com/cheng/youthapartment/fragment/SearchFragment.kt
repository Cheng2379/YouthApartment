package com.cheng.youthapartment.fragment

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
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
import com.cheng.youthapartment.util.Logger
import com.cheng.youthapartment.util.RetrofitUtil
import com.cheng.youthapartment.util.findButtonById
import com.cheng.youthapartment.util.findImageViewById
import com.cheng.youthapartment.util.findTextViewById
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    private val mProvinceBeanList = arrayListOf<ProvinceBean>()
    private val mCityBeanList = arrayListOf<CityBean>()
    private val mDistrictBeanList = arrayListOf<DistrictBean>()

    private var mToken: String = App.getToken()
    private var mCurrentRequestJob: Job? = null
    private var mCurrentPage = 1
    private var mPageSize = 6
    private var mLastRequestTime = 0L
    private val mDebounceTime = 1000L
    private var mTotalScrollDistance = 0 // 累计滑动距离
    private var mIsScrollingUp = false // 是否正在向上滑动
    private var isSelect = false

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

    /**
     * TODO 首页排序标签待处理
     */
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
        } else {
            mDataEmpty.visibility = TextView.GONE
            mRv.visibility = RecyclerView.VISIBLE
        }

        setSearchFilter()
    }

    private fun setSearchFilter() {
        val regionText = mView.findTextViewById(R.id.search_region_text)
        val regionImg = mView.findImageViewById(R.id.search_region_img)
        val priceText = mView.findTextViewById(R.id.search_price_text)
        val priceImg = mView.findImageViewById(R.id.search_price_img)
        val payTypeText = mView.findTextViewById(R.id.search_pay_type_text)
        val payTypeImg = mView.findImageViewById(R.id.search_pay_type_img)
        val sortText = mView.findTextViewById(R.id.search_sort_text)
        val sortImg = mView.findImageViewById(R.id.search_sort_img)

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
        mPriceView.setOnClickListener {
            setSelectHighlightColor(
                priceText,
                priceImg
            )
            setNoSelectDefaultColor(regionText, regionImg)
            setNoSelectDefaultColor(payTypeText, payTypeImg)
            setNoSelectDefaultColor(sortText, sortImg)
        }
        mPayTypeView.setOnClickListener {
            setSelectHighlightColor(
                payTypeText,
                payTypeImg
            )
            setNoSelectDefaultColor(regionText, regionImg)
            setNoSelectDefaultColor(priceText, priceImg)
            setNoSelectDefaultColor(sortText, sortImg)
        }
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

    @SuppressLint("InflateParams")
    private fun showLocationFilterPopup(view: View) {
        // 若已有窗口则关闭
        currentPopupWindow?.dismiss()

        // 获取菜单栏的位置与高度
        val filterBarLocation = IntArray(2)
        (view.parent as View).getLocationOnScreen(filterBarLocation)
        val filterBarHeight = (view.parent as View).height
        // 创捷遮蔽视图
        val maskView = View(mActivity).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor("#80000000".toColorInt())
            alpha = 0f
            y = (filterBarLocation[1] + filterBarHeight).toFloat()
            // 添加点击事件，消费点击事件防止穿透
            setOnClickListener {
                currentPopupWindow?.dismiss()
            }
        }
        // 添加到首页Activity的根部
        val rootView = mActivity.window.decorView.findViewById<ViewGroup>(android.R.id.content)
        rootView.addView(maskView)
        maskView.animate().alpha(1f).setDuration(300).start()

        val popupView =
            LayoutInflater.from(mActivity).inflate(R.layout.item_popup_filter_region, null)
        // 动画执行之前，设置视图高度为0
        popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val popupHeight = popupView.measuredHeight
        popupView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0)

        currentPopupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            // 外部点击可关闭
            isOutsideTouchable = true

            showAsDropDown(view.parent as View)
            // 自定义动画
            animationStyle = R.style.PopupAnimation
            popupView.post {
                val valueAnimator = ValueAnimator.ofInt(0, popupHeight)
                valueAnimator.interpolator = DecelerateInterpolator()
                valueAnimator.addUpdateListener {  animator ->
                    // 获取当前帧的动画值, 并赋值给popupView布局参数的高度
                    val value = animator.animatedValue as Int
                    popupView.layoutParams.height = value
                    // 通知系统重新布局popupView, 触发视图的测量和绘制过程, 使高度的变化立即生效
                    popupView.requestLayout()
                }
                valueAnimator.start()
            }

            // 监听窗口关闭删除遮蔽视图
            setOnDismissListener {
                maskView.animate().alpha(0f).setDuration(300)
                    .withEndAction {
                        rootView.removeView(maskView)
                    }
                    .start()
            }

            val cancelBtn = popupView.findButtonById(R.id.item_cancel_btn)
            val confirmBtn = popupView.findButtonById(R.id.item_confirm_btn)
            cancelBtn.setOnClickListener {
                this.dismiss()
            }
            confirmBtn.setOnClickListener {
                this.dismiss()
            }
        }

    }

    /**
     * 设置选中的菜单文本和图标高亮显示
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
    private fun getRoomList(isUpdate: Boolean, currentPage: Int, size: Int) {
        mCurrentRequestJob?.cancel()
        RetrofitUtil.get<RoomBean>(
            "/app/room/pageItem",
            mToken,
            mapOf("current" to currentPage, "size" to size)
        ) { _, response ->
            response?.let {
                val newData = it.roomRecords
                if (newData.isEmpty()) {
                    mRvAdapter?.setAllDataLoaded(true)
                } else {
                    mCurrentRequestJob = lifecycleScope.launch {
                        withContext(Dispatchers.Main) {
                            if (isUpdate) {
                                mRvAdapter?.updateDta(newData)
                            } else {
                                mRvAdapter?.addData(newData)
                            }
                            mRv.visibility = RecyclerView.VISIBLE
                            mDataEmpty.visibility = TextView.GONE
                            Logger.d("currentPage: $currentPage Response RoomListSize: ${newData.size}")
                        }
                    }
                }
            }
        }
    }

    fun getAllProvince() {
        RetrofitUtil.get<List<ProvinceBean>>(
            "/app/region/province/list",
            App.getToken(),
            null
        ) { _, response ->
            response?.let {
                mProvinceBeanList.addAll(it)
            }
        }
    }

    fun getCityByProvinceId(id: Long) {
        RetrofitUtil.get<List<CityBean>>(
            "/app/region/city/listByProvinceId",
            App.getToken(),
            mapOf("id" to id)
        ) { _, response ->
            response?.let {
                mCityBeanList.addAll(it)
            }
        }
    }

    fun getDistrictByCityId(id: Int) {
        RetrofitUtil.get<List<DistrictBean>>(
            "/app/region/district/listByCityId",
            App.getToken(),
            mapOf("id" to id)
        ) { _, response ->
            response?.let {
                mDistrictBeanList.addAll(it)
            }
        }
    }
}