package com.cheng.youthapartment.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.cheng.youthapartment.R
import com.cheng.youthapartment.databinding.ActivityApartmentBinding
import com.cheng.youthapartment.util.Logger
import com.cheng.youthapartment.util.getYAParcelableExtra

/**
 * 公寓详情页
 * @author CHENG
 * @since 2025/3/20
 */
class ApartmentActivity : BaseActivity() {
    private val mApartmentBinding by lazy {
        ActivityApartmentBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(mApartmentBinding.root)

        initView()
    }

    private fun initView() {
        intent.getIntExtra("apartment_id", 0).let {
            Logger.d("apartmentId: $it")

        }
    }
}