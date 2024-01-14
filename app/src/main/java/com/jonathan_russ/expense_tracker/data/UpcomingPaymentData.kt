package com.jonathan_russ.expense_tracker.data

data class UpcomingPaymentData(
    val name: String,
    val price: Float,
    val nextPaymentRemainingDays: Int,
    val nextPaymentDate: String,
)
