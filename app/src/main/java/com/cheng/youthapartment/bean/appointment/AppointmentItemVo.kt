package com.cheng.youthapartment.bean.appointment


import android.os.Parcelable
import com.cheng.youthapartment.bean.GraphVo
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * 我的预约页面item数据
 */
@Parcelize
data class AppointmentItemVo(
    @SerializedName("id")
    val id: Int,
    @SerializedName("apartmentName")
    val apartmentName: String,
    @SerializedName("graphVoList")
    val graphVoList: List<GraphVo>,
    @SerializedName("appointmentTime")
    val appointmentTime: String,
    @SerializedName("appointmentStatus")
    val appointmentStatus: Int
): Parcelable