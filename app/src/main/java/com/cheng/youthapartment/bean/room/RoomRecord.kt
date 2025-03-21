package com.cheng.youthapartment.bean.room


import com.cheng.youthapartment.bean.properties.GraphBean
import com.cheng.youthapartment.bean.properties.LabelInfoBean
import com.cheng.youthapartment.bean.apartment.ApartmentBean
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class RoomRecord(
    @SerializedName("apartmentInfo")
    val apartmentBean: ApartmentBean,
    @SerializedName("graphVoList")
    val graphVoList: List<GraphBean>,
    @SerializedName("id")
    val id: Int,
    @SerializedName("labelInfoList")
    val labelInfoList: List<LabelInfoBean>,
    @SerializedName("rent")
    val rent: BigDecimal,
    @SerializedName("roomNumber")
    val roomNumber: String
)