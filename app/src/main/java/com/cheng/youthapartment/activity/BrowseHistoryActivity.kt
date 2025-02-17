package com.cheng.youthapartment.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cheng.youthapartment.App
import com.cheng.youthapartment.R
import com.cheng.youthapartment.adapter.RvAdapter
import com.cheng.youthapartment.adapter.SquareCrop
import com.cheng.youthapartment.bean.history.HistoryRecord
import com.cheng.youthapartment.bean.history.HistoryBean
import com.cheng.youthapartment.util.RetrofitUtil

class BrowseHistoryActivity : BaseActivity() {
    private val mBack: Button by lazy { findViewById(R.id.back_btn) }
    private val mRv: RecyclerView by lazy { findViewById(R.id.browse_history_rv) }
    private var mRvAdapter: RvAdapter<HistoryRecord>? = null
    private var mHistoryList = ArrayList<HistoryRecord>()
    private var mToken: String = App.getToken()
    private var mCurrentPage = 1
    private var mPageSize = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browse_history)

        initView()
        getHistory(mCurrentPage, mPageSize)
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        mBack.setOnClickListener {
            finish()
        }
        mRvAdapter = RvAdapter(this, mHistoryList, R.layout.rv_search_home) { holder, position ->
            val itemView = holder.itemView
            val mRoomImg: ImageView = itemView.findViewById(R.id.room_img)
            val mRoomName: TextView = itemView.findViewById(R.id.room_name)
            val mRoomLocation: TextView = itemView.findViewById(R.id.room_location)
            val mRoomTime: TextView = itemView.findViewById(R.id.room_history_time)
            val mRoomRent: TextView = itemView.findViewById(R.id.room_rent)

            val history = mHistoryList[position]
            val graphVoList = history.roomGraphVoList
            if (graphVoList.isNotEmpty()) {
                Glide.with(this)
                    .load(graphVoList[0].url)
                    .apply(
                        RequestOptions.bitmapTransform(SquareCrop(20))
                    )
                    .error(R.drawable.img_fail)
                    .into(mRoomImg)
            }
            mRoomName.text = history.roomNumber + " " + "号房间"
            mRoomLocation.text =
                history.provinceName + "  " + history.cityName + "  " + history.districtName
            mRoomTime.visibility = View.VISIBLE
            mRoomTime.text = history.browseTime
            mRoomRent.text = "$ " + history.rent.stripTrailingZeros().toPlainString()

            itemView.setOnClickListener {
                // TODO: 跳转到房间详情页面
            }
        }
        mRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mRv.adapter = mRvAdapter
    }

    private fun getHistory(currentPage: Int, size: Int) {
        RetrofitUtil.get<HistoryBean>(
            "/app/history/pageItem",
            mToken,
            mapOf("current" to currentPage, "size" to size)
        ) { _, response ->
            response?.let {
                mHistoryList = it.records as ArrayList<HistoryRecord>
                mRvAdapter?.updateDta(mHistoryList)
            }
        }
    }

}