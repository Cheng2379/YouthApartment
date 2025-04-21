package com.cheng.youthapartment.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cheng.youthapartment.App
import com.cheng.youthapartment.R
import com.cheng.youthapartment.adapter.RvAdapter
import com.cheng.youthapartment.adapter.SquareCrop
import com.cheng.youthapartment.entity.BaseBean
import com.cheng.youthapartment.entity.lease.LeaseBean
import com.cheng.youthapartment.entity.enums.LeaseStatus
import com.cheng.youthapartment.databinding.ActivityMyLeaseBinding
import com.cheng.youthapartment.fragment.DialogFragment
import com.cheng.youthapartment.util.RetrofitUtil
import com.cheng.youthapartment.util.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

            val intent = Intent(this, LeaseInfoActivity::class.java)
            intent.putExtra("lease_id", leaseVo.id)

            //租约状态(1:签约待确认，2:已签约，3:已取消，4:已到期，5:退租待确认，6:已退租，7:续约待确认)
            val leaseId = leaseVo.id
            when (LeaseStatus.fromValue(leaseVo.leaseStatus)) {
                // 显示-确认按钮
                LeaseStatus.SIGN_AWAIT_CONFIRM -> {
                    status.text = this.getString(R.string.my_lease_default_status_1)
                    status.setBackgroundResource(R.drawable.shape_status_light_cyan)
                    reviseOrConfirm.visibility = Button.VISIBLE
                    reviseOrConfirm.text = "确认"
                    reviseOrConfirm.setOnClickListener {
                        intent.putExtra("status", "confirm")
                        startActivity(intent)
                    }
                }
                // 显示-续约按钮、提前退租按钮
                LeaseStatus.SIGNED -> {
                    status.text = this.getString(R.string.my_lease_default_status_2)
                    status.setBackgroundResource(R.drawable.shape_status_green)
                    renewal.visibility = Button.VISIBLE
                    refund.visibility = Button.VISIBLE

                    renewal.setOnClickListener {
                        intent.putExtra("status", "renewal")
                        startActivity(intent)
                    }
                    refund.setOnClickListener {
                        // dialog 弹窗
                        val dialogFragment = DialogFragment(this, R.layout.dialog_refund) { view, dialog ->
                            val cancelBtn = view.findViewById<Button>(R.id.btn_cancel)
                            val sureBtn = view.findViewById<Button>(R.id.btn_sure)

                            cancelBtn.setOnClickListener {
                                dialog.dismiss()
                            }

                            sureBtn.setOnClickListener {
                                updateStatusById(leaseId, 5)
                                dialog.dismiss()
                            }
                        }
                        dialogFragment.show(supportFragmentManager, "LeaseRefund")
                    }
                }

                LeaseStatus.CANCELED -> {
                    status.text = this.getString(R.string.my_lease_default_status_3)
                    status.setBackgroundResource(R.drawable.shape_status_grey)
                }

                LeaseStatus.EXPIRED -> {
                    status.text = this.getString(R.string.my_lease_default_status_4)
                    status.setBackgroundResource(R.drawable.shape_status_grey)
                }

                LeaseStatus.TERMINATION_AWAIT_CONFIRM -> {
                    status.text = this.getString(R.string.my_lease_default_status_5)
                    status.setBackgroundResource(R.drawable.shape_status_red)
                }

                LeaseStatus.TERMINATED -> {
                    status.text = this.getString(R.string.my_lease_default_status_6)
                    status.setBackgroundResource(R.drawable.shape_status_grey)
                }
                // 显示修改按钮
                LeaseStatus.RENEWAL_AWAIT_CONFIRM -> {
                    status.text = this.getString(R.string.my_lease_default_status_7)
                    status.setBackgroundResource(R.drawable.shape_status_light_cyan)
                    reviseOrConfirm.visibility = Button.VISIBLE
                    reviseOrConfirm.setOnClickListener {
                        intent.putExtra("status", "edit")
                        startActivity(intent)
                    }
                }
            }

            itemView.setOnClickListener {
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
        ) { _, response ->
            response?.let {
                mLeaseBeanList = it as ArrayList<LeaseBean>
                lifecycleScope.launch(Dispatchers.Main) {
                    mRvAdapter?.updateDta(mLeaseBeanList)
                }
            }
        }
    }

    private fun updateStatusById(id: Int, status: Int) {
        RetrofitUtil.post<BaseBean<Any>>(
            "/app/agreement/saveOrUpdate",
            App.getToken(),
            mapOf(
                "id" to id,
                "status" to status
            )
        ) { _, response ->
            if (response?.code == 200) {
                "操作成功!".showToast()
                getLeaseItemList()
            }
        }
    }

}