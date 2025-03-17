package com.cheng.youthapartment.activity

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.cheng.youthapartment.App
import com.cheng.youthapartment.bean.BaseBean
import com.cheng.youthapartment.bean.apartment.ApartmentItemVo
import com.cheng.youthapartment.databinding.ActivityAppointmentBinding
import com.cheng.youthapartment.util.Logger
import com.cheng.youthapartment.util.RetrofitUtil
import com.cheng.youthapartment.util.getYAParcelableExtra
import com.cheng.youthapartment.util.showToast
import com.cheng.youthapartment.util.textChangedListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.regex.Pattern

/**
 * 预约详情页
 * @author Cheng
 * @since 2025/03/14
 */
class AppointmentActivity : BaseActivity() {
    private val mAppointBinding: ActivityAppointmentBinding by lazy {
        ActivityAppointmentBinding.inflate(layoutInflater)
    }
    private var mApartmentItemVo: ApartmentItemVo? = null

    private var mSubmitName: String = ""
    private var mSubmitPhone: String = ""
    private var mSubmitRemark: String = ""

    private var mSelectDate: String = ""
    private var mSelectTime: String = ""
    private val mCalendar = Calendar.getInstance()

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

    @SuppressLint("SetTextI18n")
    fun initView() {
        mApartmentItemVo?.let {
            mAppointBinding.apartmentAppoint.setData(it)
        }

        mAppointBinding.appointName.textChangedListener { text, _, _, _ ->
            mSubmitName = text?.toString() ?: ""
        }

        mAppointBinding.appointPhone.textChangedListener { text, _, _, _ ->
            mSubmitPhone = text?.toString() ?: ""
        }

        mAppointBinding.appointRemark.textChangedListener { text, _, _, _ ->
            mSubmitRemark = text?.toString() ?: ""
            mAppointBinding.appointRemarkNumber.text = "${mSubmitRemark.length}/50"
        }

        // 日期
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val appointDateView = mAppointBinding.appointDate
        mSelectDate = dateFormat.format(mCalendar.time)
        appointDateView.text = dateFormat.format(mCalendar.time)
        mAppointBinding.appointDateParent.setOnClickListener {
            val datePicker = DatePickerDialog(
                this, { _, year, month, dayOfMonth ->
                    mCalendar.set(year, month, dayOfMonth)
                    mSelectDate = dateFormat.format(mCalendar.time)
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
        mSelectTime = timeFormat.format(mCalendar.time)
        appointTimeView.text = timeFormat.format(calender.time)
        mAppointBinding.appointTimeParent.setOnClickListener {
            val datePicker = TimePickerDialog(
                this,
                { _, hour, minute ->
                    mCalendar.set(Calendar.HOUR_OF_DAY, hour)
                    mCalendar.set(Calendar.MINUTE, minute)
                    mSelectTime = timeFormat.format(mCalendar.time)
                    appointTimeView.text = timeFormat.format(mCalendar.time)
                },
                defaultHour,
                defaultMinute,
                true
            )
            datePicker.show()
        }

        mAppointBinding.btnReserveHouse.setOnClickListener {
            if (mSubmitName.isEmpty()) {
                "请输入姓名".showToast()
                return@setOnClickListener
            } else if (mSubmitPhone.isEmpty()) {
                "请输入手机号".showToast()
                return@setOnClickListener
            } else {
                val pattern =
                    Pattern.compile("^(13[0-9]|15[012356789]|17[013678]|18[0-9]|14[57]|19[89]|166)[0-9]{8}")
                val matcher = pattern.matcher(mSubmitPhone)
                if (!matcher.matches()) {
                    "请输入正确的手机号".showToast()
                    return@setOnClickListener
                }
            }
            saveOrUpdate()
        }
    }

    fun saveOrUpdate() {
        // todo 后续根据传入的id更新
        Logger.d("mSubmitName: $mSubmitName, mSubmitPhone: $mSubmitPhone, mSubmitRemark: $mSubmitRemark, time: ${"$mSelectDate $mSelectTime"}")
        lifecycleScope.launch(Dispatchers.IO) {
            RetrofitUtil.post<BaseBean<Any>>(
                "/app/appointment/saveOrUpdate",
                App.getToken(),
                mapOf(
                    "id" to "",
                    "name" to mSubmitName,
                    "phone" to mSubmitPhone,
                    "appointmentTime" to "$mSelectDate $mSelectTime",
                    "date" to mSelectDate,
                    "time" to mSelectTime,
                    "additionalInfo" to mSubmitRemark,
                    "apartmentId" to mApartmentItemVo!!.id,
                    "appointmentStatus" to 1
                )

            ) { _, response ->
                if (response?.code == 200) {
                    Logger.d("Submit success")
                    startActivity(
                        Intent(
                            this@AppointmentActivity,
                            MyAppointmentActivity::class.java
                        )
                    )
                }
            }
        }
    }


}