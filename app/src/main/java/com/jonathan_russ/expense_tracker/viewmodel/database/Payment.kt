package com.jonathan_russ.expense_tracker.viewmodel.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payments")
data class Payment(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String = "",
    @ColumnInfo(name = "price") val price: Float,
    @ColumnInfo(name = "monthlyPrice") val monthlyPrice: Float,
    @ColumnInfo(name = "everyXRecurrence") val everyXRecurrence: Int,
    @ColumnInfo(name = "recurrence") val recurrence: Int,
    @ColumnInfo(name = "firstPayment") val firstPayment: Long = 0L,
    @ColumnInfo(name = "nextPaymentRemainingDays") val nextPaymentRemainingDays: Int = 0,
    @ColumnInfo(name = "nextPaymentDate") val nextPaymentDate: String = "",
    @ColumnInfo(name = "location") val location: String = "",
    @ColumnInfo(name = "category") val category: String = "",
    @ColumnInfo(name = "reminder") val reminder: Boolean = false,
) {
    fun calculateMonthlyPrice(): Float {
        return when (recurrence) {
            RecurrenceDatabase.Daily.value -> {
                (365 / 12f) / everyXRecurrence * price
            }

            RecurrenceDatabase.Weekly.value -> {
                (52 / 12f) / everyXRecurrence * price
            }

            RecurrenceDatabase.Monthly.value -> {
                1f / everyXRecurrence * price
            }

            RecurrenceDatabase.Yearly.value -> {
                price / (everyXRecurrence * 12f)
            }

            else -> 0f
        }
    }
}

enum class RecurrenceDatabase(
    val value: Int,
) {
    Daily(1),
    Weekly(2),
    Monthly(3),
    Yearly(4),
}
