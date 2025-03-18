package com.cheng.youthapartment.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.cheng.youthapartment.App
import com.cheng.youthapartment.adapter.RvAdapter
import com.cheng.youthapartment.bean.appointment.AppointmentItemVoItemVo
import com.cheng.youthapartment.databinding.ActivityMyAppointmentBinding
import com.cheng.youthapartment.util.Logger
import com.cheng.youthapartment.util.RetrofitUtil
import kotlinx.coroutines.launch

/**
 * 我的预约
 * @author Cheng
 * @since 2025/03/18
 */
class MyAppointmentActivity : BaseActivity() {
    private val mMyAppointmentBinding: ActivityMyAppointmentBinding by lazy {
        ActivityMyAppointmentBinding.inflate(layoutInflater)
    }
    private val mRvView: RecyclerView by lazy { mMyAppointmentBinding.root }
    private var mRvAdapter: RvAdapter<AppointmentItemVoItemVo>? = null

    private var mAppointmentItemVo = ArrayList<AppointmentItemVoItemVo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(mMyAppointmentBinding.root)

        getAllAppointment()
        initView()
    }

    private fun getAllAppointment() {
        lifecycleScope.launch {
            RetrofitUtil.get<List<AppointmentItemVoItemVo>>("/app/appointment/listItem",
                App.getToken(),
                mapOf()
            ) { _, response ->
                response?.let {
                    mAppointmentItemVo = it as ArrayList<AppointmentItemVoItemVo>
                    Logger.d("result: $mAppointmentItemVo")
                }
            }
        }
    }

    private fun initView() {
        //mRvView.adapter =
        //    RvAdapter(this, mAppointmentItemVo, R.layout.item_my_appointment) { holder, position ->
        //        holder.itemView
        //    }
    }
}