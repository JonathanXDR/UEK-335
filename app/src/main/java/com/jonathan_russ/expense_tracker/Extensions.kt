package com.jonathan_russ.expense_tracker

import java.text.NumberFormat
import java.util.Calendar

fun Float.toCurrencyString(): String {
    return NumberFormat.getCurrencyInstance().format(this)
}

fun Float.toLocalString(): String {
    return NumberFormat.getInstance().let {
        it.minimumFractionDigits = 2
        it.format(this)
    }
}

fun String.toFloatIgnoreSeparator(): Float {
    val converted = replace(",", ".")
    return converted.toFloat()
}

fun Calendar.isSameDay(other: Calendar): Boolean {
    return this.get(Calendar.YEAR) == other.get(Calendar.YEAR) &&
            this.get(Calendar.MONTH) == other.get(Calendar.MONTH) &&
            this.get(Calendar.DAY_OF_MONTH) == other.get(Calendar.DAY_OF_MONTH)
}
