package com.cheng.youthapartment.entity.region

import com.google.gson.annotations.SerializedName

/**
 * 省份信息
 * @author CHENG
 * @since 2025/4/7
 */
data class ProvinceBean (
    @SerializedName("id")
    val id: Long,
    @SerializedName("name")
    val name: String
)