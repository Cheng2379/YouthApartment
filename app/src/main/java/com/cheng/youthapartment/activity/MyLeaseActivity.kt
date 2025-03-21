package com.cheng.youthapartment.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cheng.youthapartment.App
import com.cheng.youthapartment.R
import com.cheng.youthapartment.adapter.RvAdapter
import com.cheng.youthapartment.adapter.SquareCrop
import com.cheng.youthapartment.bean.lease.LeaseBean
import com.cheng.youthapartment.databinding.ActivityMyLeaseBinding
import com.cheng.youthapartment.util.Logger
import com.cheng.youthapartment.util.RetrofitUtil

/**
 * 我的租约页面
 * @author CHENG
 * @since 2025/3/20
 */
class MyLeaseActivity : BaseActivity() {
    private val mLeaseBinding: ActivityMyLeaseBinding by lazy {
        ActivityMyLeaseBinding.inflate(layoutInflater)
    }

    private val mRvView: RecyclerView by lazy { mLeaseBinding.myLeaseRv }
    private var mRvAdapter: RvAdapter<LeaseBean>? = null

    private var mLeaseBeanList = ArrayList<LeaseBean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(mLeaseBinding.root)

        initView()
        getLeaseItemList()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        mLeaseBinding.backBtn.setOnClickListener {
            finish()
        }
        mRvView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mRvAdapter = RvAdapter(this, mLeaseBeanList, R.layout.item_my_lease) { holder, position ->
            val itemView = holder.itemView
            val image = itemView.findViewById<ImageView>(R.id.my_lease_img)
            val name = itemView.findViewById<TextView>(R.id.my_lease_name)
            val date = itemView.findViewById<TextView>(R.id.my_lease_date)
            val status = itemView.findViewById<TextView>(R.id.my_lease_status)
            val rent = itemView.findViewById<TextView>(R.id.my_lease_rent)
            // 续约
            val renewal = itemView.findViewById<TextView>(R.id.my_lease_renewal)
            // 提前退租
            val refund = itemView.findViewById<TextView>(R.id.my_lease_refund)
            // 修改或确认
            val reviseOrConfirm = itemView.findViewById<TextView>(R.id.my_lease_revise_or_confirm)

            val leaseVo = mLeaseBeanList[position]

            leaseVo.graphVo.takeIf { it.isNotEmpty() }?.let {
                Glide.with(this)
                    .load(it[0].url)
                    .apply(RequestOptions.bitmapTransform(SquareCrop(20)))
                    .error(R.drawable.img_fail)
                    .into(image)
            }
            name.text = leaseVo.apartmentName + " " + leaseVo.roomNumber + "房间"
            date.text = leaseVo.leaseStartDate + " 至 " + leaseVo.leaseEndDate
            rent.text = "￥${leaseVo.rent}/月"

            // TODO 点击事件后续处理
            //租约状态(1:签约待确认，2:已签约，3:已取消，4:已到期，5:退租待确认，6:已退租，7:续约待确认)
            when (leaseVo.leaseStatus) {
                // 显示-确认按钮
                1 -> {
                    status.text = this.getString(R.string.my_lease_default_status_1)
                    status.setBackgroundResource(R.drawable.shape_status_light_cyan)
                    reviseOrConfirm.visibility = Button.VISIBLE
                    reviseOrConfirm.text = "确认"
                    reviseOrConfirm.setOnClickListener {
                        Logger.d("确认")
                    }
                }
                // 显示-续约按钮、提前退租按钮
                2 -> {
                    status.text = this.getString(R.string.my_lease_default_status_2)
                    status.setBackgroundResource(R.drawable.shape_status_green)
                    renewal.visibility = Button.VISIBLE
                    refund.visibility = Button.VISIBLE

                    renewal.setOnClickListener {
                        Logger.d("续约")
                    }
                    refund.setOnClickListener {
                        Logger.d("提前退租")
                    }
                }

                3 -> {
                    status.text = this.getString(R.string.my_lease_default_status_3)
                    status.setBackgroundResource(R.drawable.shape_status_grey)
                }

                4 -> {
                    status.text = this.getString(R.string.my_lease_default_status_4)
                    status.setBackgroundResource(R.drawable.shape_status_grey)
                }

                5 -> {
                    status.text = this.getString(R.string.my_lease_default_status_5)
                    status.setBackgroundResource(R.drawable.shape_status_red)
                }

                6 -> {
                    status.text = this.getString(R.string.my_lease_default_status_6)
                    status.setBackgroundResource(R.drawable.shape_status_grey)
                }
                // 显示修改按钮
                7 -> {
                    status.text = this.getString(R.string.my_lease_default_status_7)
                    status.setBackgroundResource(R.drawable.shape_status_light_cyan)
                    reviseOrConfirm.visibility = Button.VISIBLE
                    reviseOrConfirm.setOnClickListener {
                        Logger.d("修改")
                    }
                }
            }

            itemView.setOnClickListener {
                val intent = Intent(this, LeaseInfoActivity::class.java)
                intent.putExtra("lease_id", leaseVo.id)
                startActivity(intent)
            }
        }
        mRvView.adapter = mRvAdapter
    }

    private fun getLeaseItemList() {
        RetrofitUtil.get<List<LeaseBean>>(
            "/app/agreement/listItem",
            App.getToken(),
            null
        ) { call, response ->
            response?.let {
                mLeaseBeanList = it as ArrayList<LeaseBean>
                mRvAdapter?.updateDta(mLeaseBeanList)
                Logger.d("mLeaseVoList: $mLeaseBeanList")
            }
        }
    }

}