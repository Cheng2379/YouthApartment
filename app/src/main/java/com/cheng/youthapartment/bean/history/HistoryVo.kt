package com.cheng.youthapartment.bean.history

import com.google.gson.annotations.SerializedName

/**
 *
 * @author Cheng
 * @since 2025/1/15
 */
data class HistoryVo(
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
    val records: List<HistoryRecord>,
    @SerializedName("total")
    val total: Int,
    @SerializedName("size")
    val size: Int,
    @SerializedName("current")
    val current: Int,
    @SerializedName("pages")
    val pages: Int,
)