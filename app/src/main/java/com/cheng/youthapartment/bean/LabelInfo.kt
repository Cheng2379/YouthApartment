package com.cheng.youthapartment.bean


import com.google.gson.annotations.SerializedName

data class LabelInfo(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("type")
    val type: Int
)