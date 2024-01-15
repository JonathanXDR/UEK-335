package com.jonathan_russ.expense_tracker.data

import androidx.annotation.StringRes
import com.jonathan_russ.expense_tracker.R

enum class Recurrence(
    @StringRes val fullStringRes: Int,
    @StringRes val shortStringRes: Int,
) {
    Daily(R.string.edit_expense_recurrence_day, R.string.edit_expense_recurrence_day_short),
    Weekly(R.string.edit_expense_recurrence_week, R.string.edit_expense_recurrence_week_short),
    Monthly(R.string.edit_expense_recurrence_month, R.string.edit_expense_recurrence_month_short),
    Yearly(R.string.edit_expense_recurrence_year, R.string.edit_expense_recurrence_year_short),
}
