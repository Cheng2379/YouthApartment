package com.cheng.youthapartment.entity.user

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 *
 * @author Cheng
 * @since 2024/12/19
 */
@Parcelize
data class UserBean(
    val nickname: String? = null,
    val avatarUrl: String? = null
) : Parcelable{
    override fun toString(): String {
        return "UserInfoBean{" +
                "\n          nickname = " + nickname +
                ",\n          avatarUrl = " + avatarUrl +
                "\n}"
    }
}