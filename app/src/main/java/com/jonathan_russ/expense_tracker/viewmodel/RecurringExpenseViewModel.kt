package com.jonathan_russ.expense_tracker.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.jonathan_russ.expense_tracker.data.Payment
import com.jonathan_russ.expense_tracker.data.Recurrence
import com.jonathan_russ.expense_tracker.toCurrencyString
import com.jonathan_russ.expense_tracker.viewmodel.database.ExpenseRepository
import com.jonathan_russ.expense_tracker.viewmodel.database.RecurrenceDatabase
import com.jonathan_russ.expense_tracker.viewmodel.database.RecurringExpense
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class RecurringExpenseViewModel(
    private val expenseRepository: ExpenseRepository,
) : ViewModel() {
    private val _recurringExpenseData = mutableStateListOf<Payment>()
    val recurringExpenseData: ImmutableList<Payment>
        get() = _recurringExpenseData.toImmutableList()

    private var _weeklyExpense by mutableStateOf("")
    private var _monthlyExpense by mutableStateOf("")
    private var _yearlyExpense by mutableStateOf("")
    val weeklyExpense: String
        get() = _weeklyExpense
    val monthlyExpense: String
        get() = _monthlyExpense
    val yearlyExpense: String
        get() = _yearlyExpense

    init {
        viewModelScope.launch {
            expenseRepository.allRecurringExpensesByPrice.collect { recurringExpenses ->
                onDatabaseUpdated(recurringExpenses)
            }
        }
    }

    fun addRecurringExpense(recurringExpense: Payment) {
        viewModelScope.launch {
            expenseRepository.insert(
                RecurringExpense(
                    id = 0,
                    name = recurringExpense.name,
                    description = recurringExpense.description,
                    price = recurringExpense.price,
                    everyXRecurrence = recurringExpense.everyXRecurrence,
                    recurrence = getRecurrenceIntFromUIRecurrence(recurringExpense.recurrence),
                    firstPayment = recurringExpense.firstPayment,
                ),
            )
        }
    }

    fun editRecurringExpense(recurringExpense: Payment) {
        viewModelScope.launch {
            expenseRepository.update(
                RecurringExpense(
                    id = recurringExpense.id,
                    name = recurringExpense.name,
                    description = recurringExpense.description,
                    price = recurringExpense.price,
                    everyXRecurrence = recurringExpense.everyXRecurrence,
                    recurrence = getRecurrenceIntFromUIRecurrence(recurringExpense.recurrence),
                    firstPayment = recurringExpense.firstPayment,
                ),
            )
        }
    }

    fun deleteRecurringExpense(recurringExpense: Payment) {
        viewModelScope.launch {
            expenseRepository.delete(
                RecurringExpense(
                    id = recurringExpense.id,
                    name = recurringExpense.name,
                    description = recurringExpense.description,
                    price = recurringExpense.price,
                    everyXRecurrence = recurringExpense.everyXRecurrence,
                    recurrence = getRecurrenceIntFromUIRecurrence(recurringExpense.recurrence),
                    firstPayment = recurringExpense.firstPayment,
                ),
            )
        }
    }

    fun onDatabaseRestored() {
        viewModelScope.launch {
            val recurringExpenses = expenseRepository.allRecurringExpensesByPrice.first()
            onDatabaseUpdated(recurringExpenses)
        }
    }

    private fun onDatabaseUpdated(recurringExpenses: List<RecurringExpense>) {
        _recurringExpenseData.clear()
        recurringExpenses.forEach {
            _recurringExpenseData.add(
                Payment(
                    id = it.id,
                    name = it.name!!,
                    description = it.description!!,
                    price = it.price!!,
                    monthlyPrice = it.getMonthlyPrice(),
                    everyXRecurrence = it.everyXRecurrence!!,
                    recurrence = getRecurrenceFromDatabaseInt(it.recurrence!!),
                    firstPayment = it.firstPayment!!,
                ),
            )
        }
        _recurringExpenseData.sortByDescending { it.monthlyPrice }
        updateExpenseSummary()
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

    private fun getRecurrenceIntFromUIRecurrence(recurrence: Recurrence): Int {
        return when (recurrence) {
            Recurrence.Daily -> RecurrenceDatabase.Daily.value
            Recurrence.Weekly -> RecurrenceDatabase.Weekly.value
            Recurrence.Monthly -> RecurrenceDatabase.Monthly.value
            Recurrence.Yearly -> RecurrenceDatabase.Yearly.value
        }
    }

    private fun updateExpenseSummary() {
        var price = 0f
        _recurringExpenseData.forEach {
            price += it.monthlyPrice
        }
        _weeklyExpense = (price / (52 / 12f)).toCurrencyString()
        _monthlyExpense = price.toCurrencyString()
        _yearlyExpense = (price * 12).toCurrencyString()
    }

    companion object {
        fun create(expenseRepository: ExpenseRepository): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    RecurringExpenseViewModel(expenseRepository)
                }
            }
        }
    }
}
