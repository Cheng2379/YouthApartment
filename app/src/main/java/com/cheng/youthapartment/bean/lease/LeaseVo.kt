package com.cheng.youthapartment.bean.lease


import com.cheng.youthapartment.bean.GraphVo
import com.google.gson.annotations.SerializedName

data class LeaseVo(
    @SerializedName("apartmentName")
    val apartmentName: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("leaseEndDate")
    val leaseEndDate: String,
    @SerializedName("leaseStartDate")
    val leaseStartDate: String,
    @SerializedName("leaseStatus")
    val leaseStatus: Int,
    @SerializedName("rent")
    val rent: Int,
    @SerializedName("roomGraphVoList")
    val graphVo: List<GraphVo>,
    @SerializedName("roomNumber")
    val roomNumber: String,
    @SerializedName("sourceType")
    val sourceType: Int
)