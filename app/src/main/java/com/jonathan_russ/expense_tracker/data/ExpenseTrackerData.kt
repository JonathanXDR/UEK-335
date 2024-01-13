package com.jonathan_russ.expense_tracker.data

import com.jonathan_russ.expense_tracker.toValueString

data class ExpenseTrackerData(
    val id: Int,
    val name: String,
    val description: String,
    val priceValue: Float,
) {
    val priceString = "${priceValue.toValueString()} €" // TODO: Make currency dynamic
}