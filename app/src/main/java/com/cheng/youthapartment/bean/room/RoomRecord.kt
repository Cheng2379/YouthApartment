package com.cheng.youthapartment.bean.room


import com.cheng.youthapartment.bean.GraphVo
import com.cheng.youthapartment.bean.LabelInfo
import com.cheng.youthapartment.bean.apartment.ApartmentVo
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class RoomRecord(
    @SerializedName("apartmentInfo")
    val apartmentVo: ApartmentVo,
    @SerializedName("graphVoList")
    val graphVoList: List<GraphVo>,
    @SerializedName("id")
    val id: Int,
    @SerializedName("labelInfoList")
    val labelInfoList: List<LabelInfo>,
    @SerializedName("rent")
    val rent: BigDecimal,
    @SerializedName("roomNumber")
    val roomNumber: String
)