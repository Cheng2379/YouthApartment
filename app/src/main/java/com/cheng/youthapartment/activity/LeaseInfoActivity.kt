package com.cheng.youthapartment.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cheng.youthapartment.App
import com.cheng.youthapartment.R
import com.cheng.youthapartment.adapter.SquareCrop
import com.cheng.youthapartment.bean.lease.LeaseDetailVo
import com.cheng.youthapartment.databinding.ActivityLeaseInfoBinding
import com.cheng.youthapartment.util.RetrofitUtil

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
        RetrofitUtil.get<LeaseDetailVo>(
            "/app/agreement/getDetailById",
            App.getToken(),
            mapOf("id" to id)
        ) { _, response ->
            response?.let { leaseDetailVo ->

                leaseDetailVo.apartmentGraphVoList.takeIf { it.isNotEmpty() }?.let {
                    Glide.with(this)
                        .load(it[0].url)
                        .apply(RequestOptions.bitmapTransform(SquareCrop(20)))
                        .error(R.drawable.img_fail)
                        .into(mLeaseInfoBinding.leaseInfoApartmentImg)
                }

                leaseDetailVo.roomGraphVoList.takeIf { it.isNotEmpty() }?.let {
                    Glide.with(this)
                        .load(it[0].url)
                        .apply(RequestOptions.bitmapTransform(SquareCrop(20)))
                        .error(R.drawable.img_fail)
                        .into(mLeaseInfoBinding.leaseInfoRoomImg)
                }
                mLeaseInfoBinding.leaseInfoApartmentName.text = leaseDetailVo.apartmentName
                mLeaseInfoBinding.leaseInfoRoomName.text = "${leaseDetailVo.roomNumber}房间"

                mLeaseInfoBinding.leaseInfoName.text = leaseDetailVo.apartmentName
                mLeaseInfoBinding.leaseInfoPhone.text = leaseDetailVo.phone
                mLeaseInfoBinding.leaseInfoIdNumber.text = leaseDetailVo.identificationNumber
                mLeaseInfoBinding.leaseInfoLeasePeriod.text = "${leaseDetailVo.leaseTermMonthCount}月"
                mLeaseInfoBinding.leaseInfoDate.text =
                    leaseDetailVo.leaseStartDate + " 至 " + leaseDetailVo.leaseEndDate
                mLeaseInfoBinding.leaseInfoRent.text = "${leaseDetailVo.rent}元/月"
                mLeaseInfoBinding.leaseInfoDeposit.text = "${leaseDetailVo.deposit}元"
                mLeaseInfoBinding.leaseInfoPayType.text = leaseDetailVo.paymentTypeName
            }
        }
    }

}