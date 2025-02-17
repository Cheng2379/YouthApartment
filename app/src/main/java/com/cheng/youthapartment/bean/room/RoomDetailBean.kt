package com.cheng.youthapartment.bean.room

import com.cheng.youthapartment.bean.AttrValueVo
import com.cheng.youthapartment.bean.FacilityInfo
import com.cheng.youthapartment.bean.FeeValueVo
import com.cheng.youthapartment.bean.GraphVo
import com.cheng.youthapartment.bean.LabelInfo
import com.cheng.youthapartment.bean.LeaseTerm
import com.cheng.youthapartment.bean.PaymentType
import com.cheng.youthapartment.bean.apartment.ApartmentItemVo
import com.google.gson.annotations.SerializedName

data class RoomDetailBean(
    @SerializedName("id")
    val id: Int,
    @SerializedName("roomNumber")
    val roomNumber: String,
    @SerializedName("rent")
    val rent: Int,
    @SerializedName("apartmentId")
    val apartmentId: Int,
    @SerializedName("isRelease")
    val isRelease: Int,
    @SerializedName("apartmentItemVo")
    val apartmentItemVo: ApartmentItemVo,
    // 房间图片列表
    @SerializedName("graphVoList")
    val graphVoList: List<GraphVo>,
    @SerializedName("attrValueVoList")
    val attrValueVoList: List<AttrValueVo>,
    @SerializedName("facilityInfoList")
    val facilityInfoList: List<FacilityInfo>,
    @SerializedName("labelInfoList")
    val labelInfoList: List<LabelInfo>,
    @SerializedName("paymentTypeList")
    val paymentTypeList: List<PaymentType>,
    @SerializedName("feeValueVoList")
    val feeValueVoList: List<FeeValueVo>,
    @SerializedName("leaseTermList")
    val leaseTermList: List<LeaseTerm>,
)