package com.jonathan_russ.expense_tracker.data

data class UpcomingPaymentData(
    val id: Int,
    val name: String,
    val description: String,
    val price: Float,
    val monthlyPrice: Float,
    val everyXRecurrence: Int,
    val recurrence: Recurrence,
    val firstPayment: Long,
    val nextPaymentRemainingDays: Int,
    val nextPaymentDate: String,
)
