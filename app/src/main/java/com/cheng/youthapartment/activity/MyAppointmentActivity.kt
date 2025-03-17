package com.cheng.youthapartment.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cheng.youthapartment.R

/**
 * 我的预约
 * @author Cheng
 * @since 2025/03/18
 */
class MyAppointmentActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_my_appointment)

        initView()
    }

    private fun initView() {

    }
}