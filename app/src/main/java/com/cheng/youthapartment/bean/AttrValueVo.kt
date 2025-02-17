package com.cheng.youthapartment.bean


import com.google.gson.annotations.SerializedName

data class AttrValueVo(
    @SerializedName("attrKeyId")
    val attrKeyId: Int,
    @SerializedName("attrKeyName")
    val attrKeyName: Any,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)