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
import com.jonathan_russ.expense_tracker.data.RecurrenceEnum
import com.jonathan_russ.expense_tracker.data.RecurringPaymentData
import com.jonathan_russ.expense_tracker.toCurrencyString
import com.jonathan_russ.expense_tracker.viewmodel.database.PaymentRepository
import com.jonathan_russ.expense_tracker.viewmodel.database.RecurrenceDatabase
import com.jonathan_russ.expense_tracker.viewmodel.database.RecurringPayment
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PaymentsViewModel(
    private val expenseRepository: PaymentRepository,
) : ViewModel() {
    private val _recurringPaymentData = mutableStateListOf<RecurringPaymentData>()
    val recurringPaymentData: ImmutableList<RecurringPaymentData>
        get() = _recurringPaymentData.toImmutableList()

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

    fun addRecurringExpense(recurringExpense: RecurringPaymentData) {
        viewModelScope.launch {
            expenseRepository.insert(
                RecurringPayment(
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

    fun editRecurringExpense(recurringExpense: RecurringPaymentData) {
        viewModelScope.launch {
            expenseRepository.update(
                RecurringPayment(
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

    fun deleteRecurringExpense(recurringExpense: RecurringPaymentData) {
        viewModelScope.launch {
            expenseRepository.delete(
                RecurringPayment(
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

    private fun onDatabaseUpdated(recurringExpenses: List<RecurringPayment>) {
        _recurringPaymentData.clear()
        recurringExpenses.forEach {
            _recurringPaymentData.add(
                RecurringPaymentData(
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
        _recurringPaymentData.sortByDescending { it.monthlyPrice }
        updateExpenseSummary()
    }

    private fun getRecurrenceFromDatabaseInt(recurrenceInt: Int): RecurrenceEnum {
        return when (recurrenceInt) {
            RecurrenceDatabase.Daily.value -> RecurrenceEnum.Daily
            RecurrenceDatabase.Weekly.value -> RecurrenceEnum.Weekly
            RecurrenceDatabase.Monthly.value -> RecurrenceEnum.Monthly
            RecurrenceDatabase.Yearly.value -> RecurrenceEnum.Yearly
            else -> RecurrenceEnum.Monthly
        }
    }

    private fun getRecurrenceIntFromUIRecurrence(recurrence: RecurrenceEnum): Int {
        return when (recurrence) {
            RecurrenceEnum.Daily -> RecurrenceDatabase.Daily.value
            RecurrenceEnum.Weekly -> RecurrenceDatabase.Weekly.value
            RecurrenceEnum.Monthly -> RecurrenceDatabase.Monthly.value
            RecurrenceEnum.Yearly -> RecurrenceDatabase.Yearly.value
        }
    }

    private fun updateExpenseSummary() {
        var price = 0f
        _recurringPaymentData.forEach {
            price += it.monthlyPrice
        }
        _weeklyExpense = (price / (52 / 12f)).toCurrencyString()
        _monthlyExpense = price.toCurrencyString()
        _yearlyExpense = (price * 12).toCurrencyString()
    }

    companion object {
        fun create(expenseRepository: PaymentRepository): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    PaymentsViewModel(expenseRepository)
                }
            }
        }
    }
}
