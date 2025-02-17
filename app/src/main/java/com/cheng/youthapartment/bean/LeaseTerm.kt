package com.cheng.youthapartment.bean


import com.google.gson.annotations.SerializedName

data class LeaseTerm(
    @SerializedName("id")
    val id: Int,
    @SerializedName("monthCount")
    val monthCount: Int,
    @SerializedName("unit")
    val unit: String
)