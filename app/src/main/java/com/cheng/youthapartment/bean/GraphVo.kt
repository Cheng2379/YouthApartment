package com.cheng.youthapartment.bean


import com.google.gson.annotations.SerializedName

data class GraphVo(
    @SerializedName("name")
    val name: String,
    @SerializedName("url")
    val url: String
)