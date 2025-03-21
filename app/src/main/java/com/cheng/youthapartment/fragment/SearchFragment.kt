package com.cheng.youthapartment.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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
import com.cheng.youthapartment.bean.room.RoomRecord
import com.cheng.youthapartment.bean.room.RoomBean
import com.cheng.youthapartment.util.Logger
import com.cheng.youthapartment.util.RetrofitUtil
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
    private val mSR: SwipeRefreshLayout by lazy { mView.findViewById(R.id.refresh_layout) }
    private val mRv: RecyclerView by lazy { mView.findViewById(R.id.rv_search_home) }
    private val mDataEmpty: TextView by lazy { mView.findViewById(R.id.data_empty) }
    private var mRvAdapter: RvAdapter<RoomRecord>? = null
    private var mRoomList = ArrayList<RoomRecord>()

    private var mToken: String = App.getToken()
    private var mCurrentRequestJob: Job? = null
    private var mCurrentPage = 1
    private var mPageSize = 6
    private var mLastRequestTime = 0L
    private val mDebounceTime = 1000L
    private var mTotalScrollDistance = 0 // 累计滑动距离
    private var mIsScrollingUp = false // 是否正在向上滑动

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
                val mRoomImg: ImageView = itemView.findViewById(R.id.room_img)
                val mRoomName: TextView = itemView.findViewById(R.id.room_name)
                val mRoomLocation: TextView = itemView.findViewById(R.id.room_location)
                val mRoomRent: TextView = itemView.findViewById(R.id.search_item_room_rent)

                val roomItem = mRoomList[position]
                val graphVoList = roomItem.graphVoList
                if (graphVoList.isNotEmpty()) {
                    Glide.with(this)
                        .load(graphVoList[0].url)
                        .apply(
                            RequestOptions.bitmapTransform(SquareCrop(20))
                        )
                        .error(R.drawable.img_fail)
                        .into(mRoomImg)
                }
                mRoomName.text = roomItem.apartmentBean.name + " " + roomItem.roomNumber + "号房间"
                mRoomLocation.text =
                    roomItem.apartmentBean.provinceName + "  " + roomItem.apartmentBean.cityName + "  " + roomItem.apartmentBean.districtName
                mRoomRent.text = "$ " + roomItem.rent.stripTrailingZeros().toPlainString() + "/月"

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
}