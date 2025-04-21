package com.cheng.youthapartment.entity.properties


import com.google.gson.annotations.SerializedName

data class LeaseTermBean(
    @SerializedName("id")
    val id: Int,
    @SerializedName("monthCount")
    val monthCount: Int,
    @SerializedName("unit")
    val unit: String
)