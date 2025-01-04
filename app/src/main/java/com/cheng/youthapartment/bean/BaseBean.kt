package com.cheng.youthapartment.bean

import com.google.gson.annotations.SerializedName

/**
 *
 * @author Cheng
 * @since 2024/12/19
 */
data class BaseBean<T>(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String?,
    @SerializedName("data")
    val data: T
) {
    override fun toString(): String {
        return "BaseBean: { " +
                "\n        code=$code," +
                "\n         message='$message'," +
                "\n         data=$data " +
                "\n}"
    }
}