package com.cheng.youthapartment.bean.room


import com.google.gson.annotations.SerializedName

data class RoomBean(
    /**
     * {
     *  "records": [...]
     *  "total": 13,
     *  "size": 10,
     *  "current": 1,
     *  "orders": [],
     *  "optimizeCountSql": true,
     *  "searchCount": true,
     *  "maxLimit": null,
     *  "countId": null,
     *  "pages": 2
     * }
     */
    @SerializedName("records")
    val roomRecords: List<RoomRecord>,
    @SerializedName("total")
    val total: Int,
    @SerializedName("size")
    val size: Int,
    @SerializedName("countId")
    val countId: Any,
    @SerializedName("current")
    val current: Int,
    @SerializedName("maxLimit")
    val maxLimit: Any,
    @SerializedName("optimizeCountSql")
    val optimizeCountSql: Boolean,
    @SerializedName("orders")
    val orders: List<Any>,
    @SerializedName("pages")
    val pages: Int,
    @SerializedName("searchCount")
    val searchCount: Boolean
)