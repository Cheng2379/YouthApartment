package com.cheng.youthapartment.entity.room

import com.cheng.youthapartment.entity.properties.AttrValueBean
import com.cheng.youthapartment.entity.properties.FacilityInfoBean
import com.cheng.youthapartment.entity.properties.FeeValueBean
import com.cheng.youthapartment.entity.properties.GraphBean
import com.cheng.youthapartment.entity.properties.LabelInfoBean
import com.cheng.youthapartment.entity.properties.LeaseTermBean
import com.cheng.youthapartment.entity.properties.PaymentTypeBean
import com.cheng.youthapartment.entity.apartment.ApartmentDetailBean
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
    val apartmentDetailBean: ApartmentDetailBean,
    // 房间图片列表
    @SerializedName("graphVoList")
    val graphVoList: List<GraphBean>,
    // 基本信息
    @SerializedName("attrValueVoList")
    val attrValueList: List<AttrValueBean>,
    // 配套信息
    @SerializedName("facilityInfoList")
    val facilityInfoList: List<FacilityInfoBean>,
    // 标签信息
    @SerializedName("labelInfoList")
    val labelInfoList: List<LabelInfoBean>,
    // 付款方式
    @SerializedName("paymentTypeList")
    val paymentTypeList: List<PaymentTypeBean>,
    // 费用明细
    @SerializedName("feeValueVoList")
    val feeValueList: List<FeeValueBean>,
    // 可选租期
    @SerializedName("leaseTermList")
    val leaseTermList: List<LeaseTermBean>,
)