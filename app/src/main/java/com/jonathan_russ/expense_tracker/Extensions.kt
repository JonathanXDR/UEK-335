package com.jonathan_russ.expense_tracker

fun Float.toValueString(): String {
    return "%.2f".format(this)
}