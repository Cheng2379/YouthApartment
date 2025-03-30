package com.cheng.youthapartment.bean.lease


import com.cheng.youthapartment.bean.properties.GraphBean
import com.google.gson.annotations.SerializedName

data class LeaseDetailBean(
    @SerializedName("additionalInfo")
    val additionalInfo: String,
    @SerializedName("apartmentGraphVoList")
    val apartmentGraphVoList: List<GraphBean>,
    @SerializedName("apartmentId")
    val apartmentId: Int,
    @SerializedName("apartmentName")
    val apartmentName: String,
    @SerializedName("deposit")
    val deposit: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("identificationNumber")
    val identificationNumber: String,
    @SerializedName("leaseEndDate")
    var leaseEndDate: String,
    @SerializedName("leaseStartDate")
    var leaseStartDate: String,
    @SerializedName("leaseTermId")
    var leaseTermId: Int,
    @SerializedName("leaseTermMonthCount")
    var leaseTermMonthCount: Int,
    @SerializedName("leaseTermUnit")
    val leaseTermUnit: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("paymentTypeId")
    var paymentTypeId: Int,
    @SerializedName("paymentTypeName")
    var paymentTypeName: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("rent")
    val rent: Int,
    @SerializedName("roomGraphVoList")
    val roomGraphVoList: List<GraphBean>,
    @SerializedName("roomId")
    val roomId: Int,
    @SerializedName("roomNumber")
    val roomNumber: String,
    @SerializedName("sourceType")
    val sourceType: Int,
    @SerializedName("status")
    var status: Int
)