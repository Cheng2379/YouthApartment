package com.cheng.youthapartment.entity.properties


import com.google.gson.annotations.SerializedName

data class AttrValueBean(
    @SerializedName("attrKeyId")
    val attrKeyId: Int,
    @SerializedName("attrKeyName")
    val attrKeyName: Any,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)