package com.cheng.youthapartment.bean.apartment


import com.cheng.youthapartment.bean.FacilityInfo
import com.cheng.youthapartment.bean.GraphVo
import com.cheng.youthapartment.bean.LabelInfo
import com.google.gson.annotations.SerializedName

data class ApartmentBean(
    @SerializedName("addressDetail")
    val addressDetail: String,
    @SerializedName("cityId")
    val cityId: Int,
    @SerializedName("cityName")
    val cityName: String,
    @SerializedName("districtId")
    val districtId: Int,
    @SerializedName("districtName")
    val districtName: String,
    @SerializedName("facilityInfoList")
    val facilityInfoList: List<FacilityInfo>,
    @SerializedName("graphVoList")
    val graphVoList: List<GraphVo>,
    @SerializedName("id")
    val id: Int,
    @SerializedName("introduction")
    val introduction: String,
    @SerializedName("isRelease")
    val isRelease: Int,
    @SerializedName("labelInfoList")
    val labelInfoList: List<LabelInfo>,
    @SerializedName("latitude")
    val latitude: String,
    @SerializedName("longitude")
    val longitude: String,
    @SerializedName("minRent")
    val minRent: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("provinceId")
    val provinceId: Int,
    @SerializedName("provinceName")
    val provinceName: String
)