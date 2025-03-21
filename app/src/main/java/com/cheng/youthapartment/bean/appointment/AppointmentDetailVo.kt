package com.cheng.youthapartment.bean.appointment

import com.cheng.youthapartment.bean.apartment.ApartmentDetailVo
import com.google.gson.annotations.SerializedName

/**
 * 预约详情页数据
 */
data class AppointmentDetailVo(
    @SerializedName("id")
    val id: Int,
    @SerializedName("userId")
    val userId: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("apartmentId")
    val apartmentId: Int,
    @SerializedName("appointmentTime")
    val appointmentTime: String,
    @SerializedName("additionalInfo")
    val additionalInfo: String,
    @SerializedName("appointmentStatus")
    val appointmentStatus: Int,
    @SerializedName("apartmentItemVo")
    val apartmentItemVo: ApartmentDetailVo
)