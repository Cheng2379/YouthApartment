package com.cheng.youthapartment.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cheng.youthapartment.App
import com.cheng.youthapartment.R
import com.cheng.youthapartment.adapter.SquareCrop
import com.cheng.youthapartment.bean.BaseBean
import com.cheng.youthapartment.bean.lease.LeaseDetailBean
import com.cheng.youthapartment.bean.properties.LeaseStatus
import com.cheng.youthapartment.bean.properties.LeaseTermId
import com.cheng.youthapartment.bean.properties.PayTypeId
import com.cheng.youthapartment.databinding.ActivityLeaseInfoBinding
import com.cheng.youthapartment.util.DataUtil
import com.cheng.youthapartment.util.Logger
import com.cheng.youthapartment.util.RetrofitUtil
import com.cheng.youthapartment.util.showToast
import com.cheng.youthapartment.view.BottomActionSheet

class LeaseInfoActivity : AppCompatActivity() {
    private val mLeaseInfoBinding: ActivityLeaseInfoBinding by lazy {
        ActivityLeaseInfoBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(mLeaseInfoBinding.root)

        val leaseId = intent.getIntExtra("lease_id", 0)
        val status = intent.getStringExtra("status")

        getLeaseDetailById(leaseId, status)
    }

    @SuppressLint("SetTextI18n")
    private fun getLeaseDetailById(id: Int, extraStatus: String? = null) {
        RetrofitUtil.get<LeaseDetailBean>(
            "/app/agreement/getDetailById",
            App.getToken(),
            mapOf("id" to id)
        ) { _, response ->
            response?.let { leaseDetailBean ->
                Logger.d("response leaseDetailBean: $leaseDetailBean")

                leaseDetailBean.apartmentGraphVoList.takeIf { it.isNotEmpty() }?.let {
                    Glide.with(this)
                        .load(it[0].url)
                        .apply(RequestOptions.bitmapTransform(SquareCrop(20)))
                        .error(R.drawable.img_fail)
                        .into(mLeaseInfoBinding.leaseInfoApartmentImg)
                }

                leaseDetailBean.roomGraphVoList.takeIf { it.isNotEmpty() }?.let {
                    Glide.with(this)
                        .load(it[0].url)
                        .apply(RequestOptions.bitmapTransform(SquareCrop(20)))
                        .error(R.drawable.img_fail)
                        .into(mLeaseInfoBinding.leaseInfoRoomImg)
                }

                mLeaseInfoBinding.leaseInfoApartment.setOnClickListener {
                    Logger.d("apartmentId: ${leaseDetailBean.apartmentId}")
                    val intent = Intent(this, ApartmentActivity::class.java)
                    intent.putExtra("apartment_id", leaseDetailBean.apartmentId)
                    startActivity(intent)
                }

                mLeaseInfoBinding.leaseInfoRoom.setOnClickListener {
                    Logger.d("room_id: ${leaseDetailBean.roomId}")
                    val intent = Intent(this, RoomActivity::class.java)
                    intent.putExtra("room_id", leaseDetailBean.roomId)
                    startActivity(intent)
                }

                mLeaseInfoBinding.leaseInfoApartmentName.text = leaseDetailBean.apartmentName
                mLeaseInfoBinding.leaseInfoRoomName.text = "${leaseDetailBean.roomNumber}房间"

                mLeaseInfoBinding.leaseInfoName.text = leaseDetailBean.apartmentName
                mLeaseInfoBinding.leaseInfoPhone.text = leaseDetailBean.phone
                mLeaseInfoBinding.leaseInfoIdNumber.text = leaseDetailBean.identificationNumber

                mLeaseInfoBinding.leaseInfoLeasePeriodText.text =
                    "${leaseDetailBean.leaseTermMonthCount}月"

                mLeaseInfoBinding.leaseInfoDate.text =
                    leaseDetailBean.leaseStartDate + " 至 " + leaseDetailBean.leaseEndDate

                mLeaseInfoBinding.leaseInfoRent.text = "${leaseDetailBean.rent}元/月"
                mLeaseInfoBinding.leaseInfoDeposit.text = "${leaseDetailBean.deposit}元"

                mLeaseInfoBinding.leaseInfoPayTypeText.text = leaseDetailBean.paymentTypeName

                val btn = mLeaseInfoBinding.leaseInfoBtnSave

                extraStatus?.let {
                    btn.visibility = Button.VISIBLE
                    when (it) {
                        "renewal" -> {
                            // 设置月份、支付方式
                            setMonthAndPayType(leaseDetailBean)

                            btn.text = "确认续约"
                            // 修改为续约待确认
                            leaseDetailBean.status = LeaseStatus.RENEWAL_AWAIT_CONFIRM.value
                            // 设置新租约时间段
                            leaseDetailBean.leaseStartDate = leaseDetailBean.leaseEndDate
                            leaseDetailBean.leaseEndDate = DataUtil.addMonthsHandlingEndOfMonth(
                                leaseDetailBean.leaseEndDate,
                                leaseDetailBean.leaseTermMonthCount
                            )
                            mLeaseInfoBinding.leaseInfoDate.text =
                                leaseDetailBean.leaseStartDate + " 至 " + leaseDetailBean.leaseEndDate
                        }

                        "edit" -> {
                            // 设置月份、支付方式
                            setMonthAndPayType(leaseDetailBean)

                            btn.text = "保存"
                            // 设置新租约时间段
                            leaseDetailBean.leaseStartDate = leaseDetailBean.leaseEndDate
                            leaseDetailBean.leaseEndDate = DataUtil.addMonthsHandlingEndOfMonth(
                                leaseDetailBean.leaseEndDate,
                                leaseDetailBean.leaseTermMonthCount
                            )
                            mLeaseInfoBinding.leaseInfoDate.text =
                                leaseDetailBean.leaseStartDate + " 至 " + leaseDetailBean.leaseEndDate
                        }

                        "confirm" -> {
                            btn.text = "确认签约"
                            // 修改为已签约
                            leaseDetailBean.status = LeaseStatus.SIGNED.value
                        }
                    }
                    btn.setOnClickListener {
                        val paymentTypeMonthCount = when (leaseDetailBean.paymentTypeName) {
                            "月付" -> 1
                            "季付" -> 3
                            "半年付" -> 6
                            "年付" -> 12
                            else -> 0
                        }

                        if (leaseDetailBean.leaseTermMonthCount < paymentTypeMonthCount) {
                            "租期时长不能小于支付方式月份长度!".showToast()
                            return@setOnClickListener
                        }
                        // TODO 后台有个问题。支付方式修改为年付的情况下，会造成数据无法读取, 目前定位到问题可能出现在支付方式id和租期id
                        Logger.d("submit leaseDetailBean: $leaseDetailBean")
                        saveOrUpdate(leaseDetailBean)
                    }
                } ?: run {
                    btn.visibility = Button.GONE
                }
            }
        }
    }

    /**
     * 保存新租约
     */
    private fun saveOrUpdate(detailBean: LeaseDetailBean) {
        RetrofitUtil.post<BaseBean<Any>>(
            "/app/agreement/saveOrUpdate",
            App.getToken(),
            mapOf(
                "id" to detailBean.id,
                "phone" to detailBean.phone,
                "name" to detailBean.name,
                "identificationNumber" to detailBean.identificationNumber,
                "apartmentId" to detailBean.apartmentId,
                "roomId" to detailBean.roomId,
                "leaseStartDate" to detailBean.leaseStartDate,
                "leaseEndDate" to detailBean.leaseEndDate,
                "leaseTermId" to detailBean.leaseTermId,
                "rent" to detailBean.rent,
                "deposit" to detailBean.deposit,
                "paymentTypeId" to detailBean.paymentTypeId,
                "status" to detailBean.status,
                "sourceType" to detailBean.sourceType,
                "additionalInfo" to detailBean.additionalInfo,
            )
        ) { _, response ->
            if (response?.code == 200) {
                Logger.d("responseData: $response")
                finish()
                startActivity(Intent(this, MyLeaseActivity::class.java))
            }
        }
    }

    private fun setMonthAndPayType(leaseDetailBean: LeaseDetailBean) {
        mLeaseInfoBinding.leaseInfoLeasePeriodImg.visibility = ImageView.VISIBLE
        mLeaseInfoBinding.leaseInfoPayTypeImg.visibility = ImageView.VISIBLE

        // 底部月份分段选择器
        mLeaseInfoBinding.leaseInfoLeasePeriod.setOnClickListener {
            showPicker(
                leaseDetailBean.leaseTermMonthCount.toString(),
                listOf("1月", "3月", "6月", "12月"),
                leaseDetailBean,
                true
            )
        }
        // 底部支付方式分段选择器
        mLeaseInfoBinding.leaseInfoPayType.setOnClickListener {
            showPicker(
                leaseDetailBean.paymentTypeName,
                listOf("月付", "季付", "半年付", "年付"),
                leaseDetailBean,
                false
            )
        }
    }

    /**
     * TODO: 分段选择处理器
     */
    private fun showPicker(
        default: String,
        options: List<String>,
        leaseDetailBean: LeaseDetailBean,
        isPeriod: Boolean
    ) {
        // 获取当前选中项的位置
        val initialPosition = options.indexOf(default)

        // 创建并显示底部控件
        BottomActionSheet(this)
            .setTitle(if (options[0].contains("月")) "租期" else "支付方式")
            .setOptions(options)
            .setInitialSelection(initialPosition)
            .setActionListener(object : BottomActionSheet.OnActionListener {
                @SuppressLint("SetTextI18n")
                override fun onConfirm(position: Int, item: String) {
                    if (isPeriod) {
                        // 处理租期选择
                        mLeaseInfoBinding.leaseInfoLeasePeriodText.text = item
                        // 更新租约结束日期
                        val months = when (item) {
                            "1月" -> 1
                            "3月" -> 3
                            "6月" -> 6
                            "12月" -> 12
                            else -> 1
                        }

                        // 从起始日期开始计算结束日期
                        val startDate =
                            mLeaseInfoBinding.leaseInfoDate.text.toString().split(" 至 ")[0]
                        val endDate = DataUtil.addMonthsHandlingEndOfMonth(startDate, months)

                        mLeaseInfoBinding.leaseInfoDate.text = "$startDate 至 $endDate"
                        leaseDetailBean.leaseStartDate = startDate
                        leaseDetailBean.leaseEndDate = endDate

                        leaseDetailBean.leaseTermMonthCount = months
                        leaseDetailBean.leaseTermId = LeaseTermId.getIdByType(item)
                    } else {
                        // 处理支付方式选择
                        mLeaseInfoBinding.leaseInfoPayTypeText.text = item
                        leaseDetailBean.paymentTypeId = PayTypeId.getIdByType(item)
                        leaseDetailBean.paymentTypeName = item
                    }
                }

                override fun onCancel() {

                }
            })
            .show()
    }

}