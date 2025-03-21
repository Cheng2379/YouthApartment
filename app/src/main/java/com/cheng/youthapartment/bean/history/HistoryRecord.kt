package com.cheng.youthapartment.bean.history

import com.cheng.youthapartment.bean.properties.GraphBean
import java.math.BigDecimal

data class HistoryRecord(
    val id: Int,
    val userId: Int,
    val roomId: Int,
    val browseTime: String,
    val roomNumber: String,
    val rent: BigDecimal,
    val roomGraphVoList: List<GraphBean>,
    val apartmentName: String,
    val provinceName: String,
    val cityName: String,
    val districtName: String,
)