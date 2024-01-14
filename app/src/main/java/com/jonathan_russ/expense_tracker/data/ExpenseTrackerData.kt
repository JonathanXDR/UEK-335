package com.jonathan_russ.expense_tracker.data

import androidx.annotation.StringRes
import com.jonathan_russ.expense_tracker.R

enum class Recurrence(
    @StringRes val stringRes: Int,
) {
    Daily(R.string.edit_expense_recurrence_day),
    Weekly(R.string.edit_expense_recurrence_week),
    Monthly(R.string.edit_expense_recurrence_month),
    Yearly(R.string.edit_expense_recurrence_year),
}

data class ExpenseTrackerData(
    val id: Int,
    val name: String,
    val description: String,
    val price: Float,
    val monthlyPrice: Float,
    val everyXRecurrence: Int,
    val recurrence: Recurrence,
)
