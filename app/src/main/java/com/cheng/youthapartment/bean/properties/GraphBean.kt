package com.cheng.youthapartment.bean.properties


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class GraphBean(
    @SerializedName("name")
    val name: String,
    @SerializedName("url")
    val url: String
): Parcelable