package com.cheng.youthapartment.entity.appointment

import com.cheng.youthapartment.entity.apartment.ApartmentDetailBean
import com.google.gson.annotations.SerializedName

/**
 * 预约详情页数据
 */
data class AppointmentDetailBean(
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
    val apartmentDetailBean: ApartmentDetailBean
)