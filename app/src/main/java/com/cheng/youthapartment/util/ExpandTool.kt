package com.cheng.youthapartment.util

import android.view.View
import android.widget.Toast
import com.cheng.youthapartment.App
import com.google.android.material.snackbar.Snackbar

/**
 *
 * @author Cheng
 * @since 2025/1/4
 */

fun String.showToast(duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(App.mContext, this, duration).show()
}

fun Int.showToast(duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(App.mContext, this, duration).show()
}

fun View.showSnackbar(
    text: String,
    actionString: String? = null,
    duration: Int = Snackbar.LENGTH_SHORT,
    block: (() -> Unit)? = null
) {
    val snackbar = Snackbar.make(this, text, duration)
    if (actionString != null && block != null) {
        snackbar.setAction(actionString) {
            block()
        }
    }
    snackbar.show()
}

fun View.showSnackbar(
    text: String,
    resourceId: Int? = null,
    duration: Int = Snackbar.LENGTH_SHORT,
    block: (() -> Unit)? = null
) {

    val snackbar = Snackbar.make(this, text, duration)
    if (resourceId != null && block != null) {
        snackbar.setAction(resourceId) {
            block()
        }
    }
    snackbar.show()
}


