package com.cheng.youthapartment.bean


import com.google.gson.annotations.SerializedName

data class FeeValueVo(
    @SerializedName("feeKeyId")
    val feeKeyId: Int,
    @SerializedName("feeKeyName")
    val feeKeyName: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("unit")
    val unit: String
)