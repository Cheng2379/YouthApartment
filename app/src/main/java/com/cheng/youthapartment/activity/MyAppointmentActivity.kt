package com.cheng.youthapartment.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cheng.youthapartment.App
import com.cheng.youthapartment.R
import com.cheng.youthapartment.adapter.RvAdapter
import com.cheng.youthapartment.adapter.SquareCrop
import com.cheng.youthapartment.bean.appointment.AppointmentItemVo
import com.cheng.youthapartment.databinding.ActivityMyAppointmentBinding
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
    private val mRvView: RecyclerView by lazy { mMyAppointmentBinding.myAppointRv }
    private var mRvAdapter: RvAdapter<AppointmentItemVo>? = null

    private var mAppointmentVoList = ArrayList<AppointmentItemVo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(mMyAppointmentBinding.root)

        initView()
        getAllAppointment()
    }

    private fun getAllAppointment() {
        lifecycleScope.launch {
            RetrofitUtil.get<List<AppointmentItemVo>>(
                "/app/appointment/listItem", App.getToken(), null
            ) { _, response ->
                response?.let {
                    mAppointmentVoList = it as ArrayList<AppointmentItemVo>
                    mRvAdapter?.updateDta(mAppointmentVoList)
                }
            }
        }
    }

    private fun initView() {
        mMyAppointmentBinding.backBtn.setOnClickListener {
            finish()
        }
        mRvView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mRvAdapter =
            RvAdapter(this, mAppointmentVoList, R.layout.item_my_appointment) { holder, position ->
                val itemView = holder.itemView
                val imageView = itemView.findViewById<ImageView>(R.id.my_appoint_img)
                val name = itemView.findViewById<TextView>(R.id.my_appoint_name)
                val label = itemView.findViewById<TextView>(R.id.my_appoint_label)
                val time = itemView.findViewById<TextView>(R.id.my_appoint_time)

                val itemAppointment = mAppointmentVoList[position]
                itemAppointment.graphVoList.takeIf { it.isNotEmpty() }?.let {
                    Glide.with(this)
                        .load(it[0].url)
                        .apply(RequestOptions.bitmapTransform(SquareCrop(20)))
                        .error(R.drawable.img_fail)
                        .into(imageView)
                }

                name.text = itemAppointment.apartmentName
                when (itemAppointment.appointmentStatus) {
                    1 -> {
                        label.text = "待看房"
                        label.setTextColor(Color.WHITE)
                        label.setBackgroundResource(R.drawable.shape_status_green)
                    }

                    2 -> {
                        label.text = "已带看"
                        label.setTextColor(Color.WHITE)
                        label.setBackgroundResource(R.drawable.shape_status_grey)
                    }

                    3 -> {
                        label.text = "已取消"
                        label.setTextColor(Color.WHITE)
                        label.setBackgroundResource(R.drawable.shape_status_grey)
                    }
                }
                time.text = itemAppointment.appointmentTime


                itemView.setOnClickListener {
                    val intent = Intent(this, AppointmentActivity::class.java)
                    intent.putExtra("appoint_item", itemAppointment)
                    startActivity(intent)
                }
            }
        mRvView.adapter = mRvAdapter
    }

}