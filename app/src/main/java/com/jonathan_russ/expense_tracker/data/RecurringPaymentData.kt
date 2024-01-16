package com.jonathan_russ.expense_tracker.data


data class RecurringPaymentData(
    val id: Int,
    val name: String,
    val description: String,
    val price: Float,
    val monthlyPrice: Float,
    val everyXRecurrence: Int,
    val recurrence: RecurrenceEnum,
    val firstPayment: Long,
    val location: String,
    val category: String,
    val reminder: Boolean,
)
