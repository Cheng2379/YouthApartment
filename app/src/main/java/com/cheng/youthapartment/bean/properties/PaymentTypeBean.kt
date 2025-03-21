package com.cheng.youthapartment.bean.properties


import com.google.gson.annotations.SerializedName

data class PaymentTypeBean(
    @SerializedName("additionalInfo")
    val additionalInfo: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("payMonthCount")
    val payMonthCount: String
)