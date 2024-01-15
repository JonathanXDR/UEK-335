package com.jonathan_russ.expense_tracker.data

data class Payment(
    val id: Int,
    val name: String,
    val description: String = "",
    val price: Float,
    val monthlyPrice: Float,
    val everyXRecurrence: Int,
    val recurrence: Recurrence,
    val firstPayment: Long = 0L,
    val nextPaymentRemainingDays: Int = 0,
    val nextPaymentDate: String = "",
    val location: String = "",
    val category: String = "",
    val reminder: Boolean = false,
)

