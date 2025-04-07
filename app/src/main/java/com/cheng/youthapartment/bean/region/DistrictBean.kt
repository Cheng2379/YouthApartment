package com.cheng.youthapartment.bean.region

import com.google.gson.annotations.SerializedName

/**
 * 区域信息
 * @author CHENG
 * @since 2025/4/7
 */
data class DistrictBean (
    @SerializedName("id")
    val id: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("cityId")
    val cityId: Long
)