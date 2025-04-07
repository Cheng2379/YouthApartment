package com.cheng.youthapartment.bean.region

import com.google.gson.annotations.SerializedName

/**
 * 城市信息
 * @author CHENG
 * @since 2025/4/7
 */
data class CityBean (
    @SerializedName("id")
    val id: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("provinceId")
    val provinceId: Long
)