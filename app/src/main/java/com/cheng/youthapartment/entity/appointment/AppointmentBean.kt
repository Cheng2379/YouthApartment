package com.cheng.youthapartment.entity.appointment


import android.os.Parcelable
import com.cheng.youthapartment.entity.properties.GraphBean
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * 我的预约页面item数据
 */
@Parcelize
data class AppointmentBean(
    @SerializedName("id")
    val id: Int,
    @SerializedName("apartmentName")
    val apartmentName: String,
    @SerializedName("graphVoList")
    val graphVoList: List<GraphBean>,
    @SerializedName("appointmentTime")
    val appointmentTime: String,
    @SerializedName("appointmentStatus")
    val appointmentStatus: Int
): Parcelable