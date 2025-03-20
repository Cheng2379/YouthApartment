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
import com.cheng.youthapartment.bean.appointment.AppointmentDetailVo
import com.cheng.youthapartment.bean.appointment.AppointmentItemVo
import com.cheng.youthapartment.databinding.ActivityAppointmentBinding
import com.cheng.youthapartment.util.DataCheckUtil
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

/**
 * 预约详情页
 * @author Cheng
 * @since 2025/03/14
 */
class AppointmentActivity : BaseActivity() {
    private val mAppointBinding: ActivityAppointmentBinding by lazy {
        ActivityAppointmentBinding.inflate(layoutInflater)
    }
    private val mAppointName by lazy { mAppointBinding.appointName }
    private val mAppointPhone by lazy { mAppointBinding.appointPhone }
    private val mAppointRemark by lazy { mAppointBinding.appointRemark }
    private val mAppointRemarkNumber by lazy { mAppointBinding.appointRemarkNumber }
    private val mAppointDate by lazy { mAppointBinding.appointDate }
    private val mAppointTime by lazy { mAppointBinding.appointTime }
    private val mApartmentAppoint by lazy { mAppointBinding.apartmentAppoint }
    private val mBtnReserveHouse by lazy { mAppointBinding.btnReserveHouse }

    private var mApartmentItemVo: ApartmentItemVo? = null
    private var mAppointmentItemVo: AppointmentItemVo? = null

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
        mAppointmentItemVo = intent.getYAParcelableExtra("appoint_item")
        mAppointmentItemVo?.let {
            lifecycleScope.launch {
                getDetailById(it.id)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun initView() {
        mApartmentItemVo?.let {
            mApartmentAppoint.setData(it)
        }

        mAppointName.textChangedListener { text, _, _, _ ->
            mSubmitName = text?.toString() ?: ""
        }

        mAppointPhone.textChangedListener { text, _, _, _ ->
            mSubmitPhone = text?.toString() ?: ""
        }

        mAppointRemark.textChangedListener { text, _, _, _ ->
            mSubmitRemark = text?.toString() ?: ""
            mAppointRemarkNumber.text = "${mSubmitRemark.length}/50"
        }

        // 日期
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        mSelectDate = dateFormat.format(mCalendar.time)
        mAppointDate.text = dateFormat.format(mCalendar.time)
        mAppointBinding.appointDateParent.setOnClickListener {
            val datePicker = DatePickerDialog(
                this, { _, year, month, dayOfMonth ->
                    mCalendar.set(year, month, dayOfMonth)
                    mSelectDate = dateFormat.format(mCalendar.time)
                    mAppointDate.text = dateFormat.format(mCalendar.time)
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
        mSelectTime = timeFormat.format(mCalendar.time)
        mAppointTime.text = timeFormat.format(calender.time)
        mAppointBinding.appointTimeParent.setOnClickListener {
            val datePicker = TimePickerDialog(
                this,
                { _, hour, minute ->
                    mCalendar.set(Calendar.HOUR_OF_DAY, hour)
                    mCalendar.set(Calendar.MINUTE, minute)
                    mSelectTime = timeFormat.format(mCalendar.time)
                    mAppointTime.text = timeFormat.format(mCalendar.time)
                },
                defaultHour,
                defaultMinute,
                true
            )
            datePicker.show()
        }

        mBtnReserveHouse.setOnClickListener {
            val map = mapOf(
                mSubmitName to "请输入姓名",
                mSubmitPhone to "请输入手机号",
            )
            if (!DataCheckUtil.checkSubmit(map, mSubmitPhone)) return@setOnClickListener
            saveOrUpdate()
        }
    }

    private fun saveOrUpdate() {
        Logger.d(
            "mSubmitName: $mSubmitName, mSubmitPhone: $mSubmitPhone, " +
                    "mSubmitRemark: $mSubmitRemark, time: ${"$mSelectDate $mSelectTime"}, ApartmentId: ${mApartmentItemVo!!.id}"
        )
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
                    "预约成功".showToast()
                    finish()
                    if (mAppointmentItemVo == null) {
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

    @SuppressLint("SetTextI18n")
    private fun getDetailById(id: Int) {
        RetrofitUtil.get<AppointmentDetailVo>(
            "/app/appointment/getDetailById",
            App.getToken(),
            mapOf("id" to id)
        ) { _, response ->
            response?.let {
                mApartmentItemVo = it.apartmentItemVo
                mAppointName.setText(it.name)
                mAppointPhone.setText(it.phone)
                mAppointRemark.setText(it.additionalInfo)
                mAppointRemarkNumber.text = "${mAppointRemark.text.length}/50"
                val split = it.appointmentTime.split(" ")
                mAppointDate.text = split[0]
                mAppointTime.text = split[1]
                mApartmentAppoint.setData(it.apartmentItemVo)
                mBtnReserveHouse.text = "重新预约"
            }
        }
    }

}