package com.cheng.youthapartment.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.cheng.youthapartment.R
import com.cheng.youthapartment.util.Logger
import com.cheng.youthapartment.util.getYAParcelableExtra

/**
 * 公寓详情页
 * @author CHENG
 * @since 2025/3/20
 */
class ApartmentActivity : BaseActivity() {
    private var mApartmentId: Int = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_apartment)

        initView()
    }

    private fun initView() {
        mApartmentId = intent.getIntExtra("apartment_id", -1)
        Logger.d("mApartmentId: $mApartmentId")
    }
}