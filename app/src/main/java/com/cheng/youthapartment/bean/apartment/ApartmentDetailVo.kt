package com.cheng.youthapartment.bean.apartment

import android.os.Parcelable
import com.cheng.youthapartment.bean.GraphVo
import com.cheng.youthapartment.bean.LabelInfo
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * 公寓详情数据, 内容比ApartmentInfo详细
 */
@Parcelize
data class ApartmentDetailVo(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("introduction")
    val introduction: String,
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
    // 公寓图片列表
    @SerializedName("graphVoList")
    val graphVoList: List<GraphVo>,
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
    @SerializedName("phone")
    val phone: String,
    @SerializedName("provinceId")
    val provinceId: Int,
    @SerializedName("provinceName")
    val provinceName: String
) : Parcelable