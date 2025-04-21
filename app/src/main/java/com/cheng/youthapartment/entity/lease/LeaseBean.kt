package com.cheng.youthapartment.entity.lease


import com.cheng.youthapartment.entity.properties.GraphBean
import com.google.gson.annotations.SerializedName

data class LeaseBean(
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
    val graphVo: List<GraphBean>,
    @SerializedName("roomNumber")
    val roomNumber: String,
    @SerializedName("sourceType")
    val sourceType: Int
)