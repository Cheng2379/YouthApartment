package com.cheng.youthapartment.bean.appointment


import com.cheng.youthapartment.bean.GraphVo
import com.google.gson.annotations.SerializedName

data class AppointmentItemVoItemVo(
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
)