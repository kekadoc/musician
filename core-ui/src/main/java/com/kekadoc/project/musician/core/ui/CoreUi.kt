package com.kekadoc.project.musician.core.ui

import android.content.Context
import android.util.TypedValue
import androidx.core.os.ConfigurationCompat
import java.util.*

fun Context.dpToPx(dp: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
}

fun Context.getLocale(): Locale {
    return ConfigurationCompat.getLocales(resources.configuration).get(0)
}

