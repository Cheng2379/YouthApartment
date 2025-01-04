package com.cheng.youthapartment.util

import android.content.Context
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

/**
 *
 * @author Cheng
 * @since 2025/1/4
 */

fun String.showToast(context: Context, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, this, duration).show()
}

fun Int.showToast(context: Context, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, this, duration).show()
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


