package com.cheng.youthapartment.bean.room


import com.cheng.youthapartment.bean.GraphVo
import com.cheng.youthapartment.bean.LabelInfo
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class Record(
    @SerializedName("apartmentInfo")
    val apartmentInfo: ApartmentInfo,
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