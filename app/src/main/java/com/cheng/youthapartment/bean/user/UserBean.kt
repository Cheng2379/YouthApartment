package com.cheng.youthapartment.bean.user

/**
 *
 * @author Cheng
 * @since 2024/12/19
 */
data class UserBean(
    val nickname: String? = null,
    val avatarUrl: String? = null
) {
    override fun toString(): String {
        return "UserInfoBean{" +
                "\n          nickname = " + nickname +
                ",\n          avatarUrl = " + avatarUrl +
                "\n}"
    }
}