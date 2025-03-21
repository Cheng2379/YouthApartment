package com.cheng.youthapartment.bean.room

import com.cheng.youthapartment.bean.AttrValueVo
import com.cheng.youthapartment.bean.FacilityInfo
import com.cheng.youthapartment.bean.FeeValueVo
import com.cheng.youthapartment.bean.GraphVo
import com.cheng.youthapartment.bean.LabelInfo
import com.cheng.youthapartment.bean.LeaseTerm
import com.cheng.youthapartment.bean.PaymentType
import com.cheng.youthapartment.bean.apartment.ApartmentDetailVo
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
    val apartmentDetailVo: ApartmentDetailVo,
    // 房间图片列表
    @SerializedName("graphVoList")
    val graphVoList: List<GraphVo>,
    // 基本信息
    @SerializedName("attrValueVoList")
    val attrValueVoList: List<AttrValueVo>,
    // 配套信息
    @SerializedName("facilityInfoList")
    val facilityInfoList: List<FacilityInfo>,
    // 标签信息
    @SerializedName("labelInfoList")
    val labelInfoList: List<LabelInfo>,
    // 付款方式
    @SerializedName("paymentTypeList")
    val paymentTypeList: List<PaymentType>,
    // 费用明细
    @SerializedName("feeValueVoList")
    val feeValueVoList: List<FeeValueVo>,
    // 可选租期
    @SerializedName("leaseTermList")
    val leaseTermList: List<LeaseTerm>,
)