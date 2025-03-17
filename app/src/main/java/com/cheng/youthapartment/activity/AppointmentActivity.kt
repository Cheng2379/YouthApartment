package com.cheng.youthapartment.activity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.cheng.youthapartment.bean.apartment.ApartmentItemVo
import com.cheng.youthapartment.databinding.ActivityAppointmentBinding
import com.cheng.youthapartment.util.getYAParcelableExtra
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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

        // 日期
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val defaultDate = Calendar.getInstance().apply {
            // month从0开始计算
            set(2025, 5, 11)
        }
        val appointDate = mAppointBinding.appointDate
        appointDate.text = dateFormat.format(defaultDate.time)
        mAppointBinding.appointDateParent.setOnClickListener {
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, month, dayOfMonth)
                    appointDate.text = dateFormat.format(selectedDate.time)
                },
                defaultDate.get(Calendar.YEAR),
                defaultDate.get(Calendar.MONTH),
                defaultDate.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        // 时间
        val defaultHour = 14
        val defaultMinute = 30
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val calender = Calendar.getInstance().apply {
            // month从0开始计算
            set(Calendar.HOUR_OF_DAY, defaultHour)
            set(Calendar.MINUTE, defaultMinute)
        }
        val appointTime = mAppointBinding.appointTime
        appointTime.text = timeFormat.format(calender.time)
        mAppointBinding.appointTimeParent.setOnClickListener {
            val datePicker = TimePickerDialog(
                this,
                { _, hour, minute ->
                    val selectedTime = Calendar.getInstance()
                    selectedTime.set(Calendar.HOUR_OF_DAY, hour)
                    selectedTime.set(Calendar.MINUTE, minute)
                    appointTime.text = timeFormat.format(selectedTime.time)
                },
                defaultHour,
                defaultMinute,
                true
            )
            datePicker.show()
        }
    }
}