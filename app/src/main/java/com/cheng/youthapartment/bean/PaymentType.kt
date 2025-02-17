package com.cheng.youthapartment.bean


import com.google.gson.annotations.SerializedName

data class PaymentType(
    @SerializedName("additionalInfo")
    val additionalInfo: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("payMonthCount")
    val payMonthCount: String
)