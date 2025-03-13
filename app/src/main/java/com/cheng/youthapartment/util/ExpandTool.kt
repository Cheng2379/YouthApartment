package com.cheng.youthapartment.util

import android.content.Intent
import android.os.Build
import android.os.Parcelable
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
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

@Suppress("DEPRECATION")
inline fun <reified T : Parcelable> Intent.getYAParcelableExtra(
    key: String
): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelableExtra(key, T::class.java)
    } else {
        @Suppress("DEPRECATION")
        getParcelableExtra(key) as? T
    }
}

fun View.findTextViewById(id: Int): TextView = findViewById(id)

fun View.findEditTextById(id: Int): EditText = findViewById(id)

fun View.findImageViewById(id: Int): ImageView = findViewById(id)


