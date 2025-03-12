package com.cheng.youthapartment.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.cheng.youthapartment.bean.apartment.ApartmentItemVo
import com.cheng.youthapartment.databinding.ActivityAppointmentBinding
import com.cheng.youthapartment.util.getYAParcelableExtra

class AppointmentActivity : BaseActivity() {
    private val mAppointBinding: ActivityAppointmentBinding by lazy {
        ActivityAppointmentBinding.inflate(layoutInflater)
    }
    private var mApartmentItemVo: ApartmentItemVo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(mAppointBinding.root)

        getApartmentItemVo()
        initView()
    }

    private fun getApartmentItemVo() {
        mApartmentItemVo = intent.getYAParcelableExtra("appoint_apartment")
    }

    fun initView() {
        mApartmentItemVo?.let {
            mAppointBinding.apartmentAppoint.setData(it)
        }


    }
}