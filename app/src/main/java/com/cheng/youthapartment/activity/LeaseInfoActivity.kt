package com.cheng.youthapartment.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cheng.youthapartment.App
import com.cheng.youthapartment.R
import com.cheng.youthapartment.adapter.SquareCrop
import com.cheng.youthapartment.bean.lease.LeaseInfoVo
import com.cheng.youthapartment.databinding.ActivityLeaseInfoBinding
import com.cheng.youthapartment.util.RetrofitUtil
import com.cheng.youthapartment.util.getYAParcelableExtra

class LeaseInfoActivity : AppCompatActivity() {
    private val mLeaseInfoBinding: ActivityLeaseInfoBinding by lazy {
        ActivityLeaseInfoBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(mLeaseInfoBinding.root)

        getLeaseDetailById(intent.getIntExtra("lease_id", 0))
    }

    @SuppressLint("SetTextI18n")
    private fun getLeaseDetailById(id: Int) {
        RetrofitUtil.get<LeaseInfoVo>(
            "/app/agreement/getDetailById",
            App.getToken(),
            mapOf("id" to id)
        ) { _, response ->
            response?.let { leaseInfoVo ->

                leaseInfoVo.apartmentGraphVoList.takeIf { it.isNotEmpty() }?.let {
                    Glide.with(this)
                        .load(it[0].url)
                        .apply(RequestOptions.bitmapTransform(SquareCrop(20)))
                        .error(R.drawable.img_fail)
                        .into(mLeaseInfoBinding.leaseInfoApartmentImg)
                }

                leaseInfoVo.roomGraphVoList.takeIf { it.isNotEmpty() }?.let {
                    Glide.with(this)
                        .load(it[0].url)
                        .apply(RequestOptions.bitmapTransform(SquareCrop(20)))
                        .error(R.drawable.img_fail)
                        .into(mLeaseInfoBinding.leaseInfoRoomImg)
                }
                mLeaseInfoBinding.leaseInfoApartmentName.text = leaseInfoVo.apartmentName
                mLeaseInfoBinding.leaseInfoRoomName.text = "${leaseInfoVo.roomNumber}房间"

                mLeaseInfoBinding.leaseInfoName.text = leaseInfoVo.apartmentName
                mLeaseInfoBinding.leaseInfoPhone.text = leaseInfoVo.phone
                mLeaseInfoBinding.leaseInfoIdNumber.text = leaseInfoVo.identificationNumber
                mLeaseInfoBinding.leaseInfoLeasePeriod.text = "${leaseInfoVo.leaseTermMonthCount}月"
                mLeaseInfoBinding.leaseInfoDate.text =
                    leaseInfoVo.leaseStartDate + " 至 " + leaseInfoVo.leaseEndDate
                mLeaseInfoBinding.leaseInfoRent.text = "${leaseInfoVo.rent}元/月"
                mLeaseInfoBinding.leaseInfoDeposit.text = "${leaseInfoVo.deposit}元"
                mLeaseInfoBinding.leaseInfoPayType.text = leaseInfoVo.paymentTypeName
            }
        }
    }

}