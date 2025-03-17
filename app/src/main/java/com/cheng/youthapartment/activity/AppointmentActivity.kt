package com.cheng.youthapartment.activity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.cheng.youthapartment.bean.apartment.ApartmentItemVo
import com.cheng.youthapartment.databinding.ActivityAppointmentBinding
import com.cheng.youthapartment.util.Logger
import com.cheng.youthapartment.util.getYAParcelableExtra
import com.cheng.youthapartment.util.textChangedListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AppointmentActivity : BaseActivity() {
    private val mAppointBinding: ActivityAppointmentBinding by lazy {
        ActivityAppointmentBinding.inflate(layoutInflater)
    }
    private var mApartmentItemVo: ApartmentItemVo? = null
    private val mCalendar = Calendar.getInstance()
    private var mAppointmentTime: String? = null

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

        mAppointBinding.appointName.textChangedListener { charSequence, _, _, _ ->

        }


        // 日期
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val appointDateView = mAppointBinding.appointDate
        var selectDate = dateFormat.format(mCalendar.time)
        appointDateView.text = dateFormat.format(mCalendar.time)
        mAppointBinding.appointDateParent.setOnClickListener {
            val datePicker = DatePickerDialog(
                this, { _, year, month, dayOfMonth ->
                    mCalendar.set(year, month, dayOfMonth)
                    selectDate = dateFormat.format(mCalendar.time)
                    appointDateView.text = dateFormat.format(mCalendar.time)
                },
                mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        // 时间
        val defaultHour = mCalendar.get(Calendar.HOUR_OF_DAY)
        val defaultMinute = mCalendar.get(Calendar.MINUTE)
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val calender = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, defaultHour)
            set(Calendar.MINUTE, defaultMinute)
        }
        val appointTimeView = mAppointBinding.appointTime
        var selectTime = timeFormat.format(mCalendar.time)
        appointTimeView.text = timeFormat.format(calender.time)
        mAppointBinding.appointTimeParent.setOnClickListener {
            val datePicker = TimePickerDialog(
                this,
                { _, hour, minute ->
                    mCalendar.set(Calendar.HOUR_OF_DAY, hour)
                    mCalendar.set(Calendar.MINUTE, minute)
                    selectTime = timeFormat.format(mCalendar.time)
                    appointTimeView.text = timeFormat.format(mCalendar.time)
                },
                defaultHour,
                defaultMinute,
                true
            )
            datePicker.show()
        }
        mAppointmentTime = "$selectDate $selectTime"
    }
}