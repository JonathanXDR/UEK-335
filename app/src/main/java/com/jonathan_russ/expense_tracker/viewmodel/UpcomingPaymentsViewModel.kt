package com.jonathan_russ.expense_tracker.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.jonathan_russ.expense_tracker.data.Payment
import com.jonathan_russ.expense_tracker.data.Recurrence
import com.jonathan_russ.expense_tracker.isSameDay
import com.jonathan_russ.expense_tracker.viewmodel.database.ExpenseRepository
import com.jonathan_russ.expense_tracker.viewmodel.database.RecurrenceDatabase
import com.jonathan_russ.expense_tracker.viewmodel.database.RecurringExpense
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

class DebtsViewModel(
    private val expenseRepository: ExpenseRepository?,
) : ViewModel() {
    private val _upcomingPaymentsData = mutableStateListOf<Payment>()
    val upcomingPaymentsData: ImmutableList<Payment>
        get() = _upcomingPaymentsData.toImmutableList()

    init {
        viewModelScope.launch {
            expenseRepository?.allRecurringExpensesByPrice?.collect { recurringExpenses ->
                onDatabaseUpdated(recurringExpenses)
            }
        }
    }

    fun onExpenseWithIdClicked(
        expenceId: Int,
        onItemClicked: (Payment) -> Unit,
    ) {
        viewModelScope.launch {
            expenseRepository?.getRecurringExpenseById(expenceId)?.let {
                val recurringExpenseData =
                    Payment(
                        id = it.id,
                        name = it.name!!,
                        description = it.description!!,
                        price = it.price!!,
                        monthlyPrice = it.getMonthlyPrice(),
                        everyXRecurrence = it.everyXRecurrence!!,
                        recurrence = getRecurrenceFromDatabaseInt(it.recurrence!!),
                        firstPayment = it.firstPayment!!,
                    )
                onItemClicked(recurringExpenseData)
            }
        }
    }

    private fun onDatabaseUpdated(recurringExpenses: List<RecurringExpense>) {
        _upcomingPaymentsData.clear()
        recurringExpenses.forEach {
            val firstPayment = it.firstPayment!!
            val nextPaymentInMilliseconds =
                getNextPaymentInMilliseconds(firstPayment, it.everyXRecurrence!!, it.recurrence!!)
            val nextPaymentRemainingDays = getNextPaymentDays(nextPaymentInMilliseconds)
            val nextPaymentDate =
                DateFormat.getDateInstance().format(Date(nextPaymentInMilliseconds))
            if (firstPayment > 0L) {
                _upcomingPaymentsData.add(
                    Payment(
                        id = it.id,
                        name = it.name!!,
                        description = it.description!!,
                        price = it.price!!,
                        monthlyPrice = it.getMonthlyPrice(),
                        everyXRecurrence = it.everyXRecurrence,
                        recurrence = getRecurrenceFromDatabaseInt(it.recurrence),
                        firstPayment = it.firstPayment,
                        nextPaymentRemainingDays = nextPaymentRemainingDays,
                        nextPaymentDate = nextPaymentDate,
                    ),
                )
            }
        }
        _upcomingPaymentsData.sortBy { it.nextPaymentRemainingDays }
    }

    private fun getNextPaymentInMilliseconds(
        firstPayment: Long,
        everyXRecurrence: Int,
        recurrence: Int,
    ): Long {
        val today = Calendar.getInstance()
        val nextPayment = Calendar.getInstance()
        nextPayment.timeInMillis = firstPayment

        while (today > nextPayment && !today.isSameDay(nextPayment)) {
            val field =
                when (recurrence) {
                    RecurrenceDatabase.Daily.value -> Calendar.DAY_OF_MONTH
                    RecurrenceDatabase.Weekly.value -> Calendar.WEEK_OF_YEAR
                    RecurrenceDatabase.Monthly.value -> Calendar.MONTH
                    RecurrenceDatabase.Yearly.value -> Calendar.YEAR
                    else -> Calendar.DAY_OF_MONTH
                }
            nextPayment.add(field, everyXRecurrence)
        }
        return nextPayment.timeInMillis
    }

    private fun getNextPaymentDays(nextPaymentInMilliseconds: Long): Int {
        val today =
            Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }
        val difference = nextPaymentInMilliseconds - today.timeInMillis
        return TimeUnit.MILLISECONDS.toDays(difference).toInt()
    }

    private fun getRecurrenceFromDatabaseInt(recurrenceInt: Int): Recurrence {
        return when (recurrenceInt) {
            RecurrenceDatabase.Daily.value -> Recurrence.Daily
            RecurrenceDatabase.Weekly.value -> Recurrence.Weekly
            RecurrenceDatabase.Monthly.value -> Recurrence.Monthly
            RecurrenceDatabase.Yearly.value -> Recurrence.Yearly
            else -> Recurrence.Monthly
        }
    }

    companion object {
        fun create(expenseRepository: ExpenseRepository): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    DebtsViewModel(expenseRepository)
                }
            }
        }
    }
}
