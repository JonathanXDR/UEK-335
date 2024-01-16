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
import kotlinx.coroutines.launch

class HomeViewModel(
    private val paymentRepository: PaymentRepository,
) : ViewModel() {
    private val _recurringPaymentData = mutableStateListOf<RecurringPaymentData>()
    val recurringPaymentData: ImmutableList<RecurringPaymentData>
        get() = _recurringPaymentData.toImmutableList()

    private var _weeklyPayment by mutableStateOf("")
    private var _monthlyPayment by mutableStateOf("")
    private var _yearlyPayment by mutableStateOf("")
    val weeklyPayment: String
        get() = _weeklyPayment
    val monthlyPayment: String
        get() = _monthlyPayment
    val yearlyPayment: String
        get() = _yearlyPayment

    init {
        viewModelScope.launch {
            paymentRepository.allRecurringPaymentsByPrice.collect { recurringPayments ->
                onDatabaseUpdated(recurringPayments)
            }
        }
    }

    fun addRecurringPayment(recurringPayment: RecurringPaymentData) {
        viewModelScope.launch {
            paymentRepository.insert(
                RecurringPayment(
                    id = 0,
                    name = recurringPayment.name,
                    description = recurringPayment.description,
                    price = recurringPayment.price,
                    everyXRecurrence = recurringPayment.everyXRecurrence,
                    recurrence = getRecurrenceIntFromUIRecurrence(recurringPayment.recurrence),
                    firstPayment = recurringPayment.firstPayment,
                    location = recurringPayment.location,
                    category = recurringPayment.category,
                    reminder = recurringPayment.reminder,
                ),
            )
        }
    }

    fun editRecurringPayment(recurringPayment: RecurringPaymentData) {
        viewModelScope.launch {
            paymentRepository.update(
                RecurringPayment(
                    id = recurringPayment.id,
                    name = recurringPayment.name,
                    description = recurringPayment.description,
                    price = recurringPayment.price,
                    everyXRecurrence = recurringPayment.everyXRecurrence,
                    recurrence = getRecurrenceIntFromUIRecurrence(recurringPayment.recurrence),
                    firstPayment = recurringPayment.firstPayment,
                    location = recurringPayment.location,
                    category = recurringPayment.category,
                    reminder = recurringPayment.reminder,
                ),
            )
        }
    }

    fun deleteRecurringPayment(recurringPayment: RecurringPaymentData) {
        viewModelScope.launch {
            paymentRepository.delete(
                RecurringPayment(
                    id = recurringPayment.id,
                    name = recurringPayment.name,
                    description = recurringPayment.description,
                    price = recurringPayment.price,
                    everyXRecurrence = recurringPayment.everyXRecurrence,
                    recurrence = getRecurrenceIntFromUIRecurrence(recurringPayment.recurrence),
                    firstPayment = recurringPayment.firstPayment,
                    location = recurringPayment.location,
                    category = recurringPayment.category,
                    reminder = recurringPayment.reminder,
                ),
            )
        }
    }

    private fun onDatabaseUpdated(recurringPayments: List<RecurringPayment>) {
        _recurringPaymentData.clear()
        recurringPayments.forEach {
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
                    location = it.location!!,
                    category = it.category!!,
                    reminder = it.reminder!!,
                ),
            )
        }
        _recurringPaymentData.sortByDescending { it.monthlyPrice }
        updatePaymentSummary()
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

    private fun updatePaymentSummary() {
        var price = 0f
        _recurringPaymentData.forEach {
            price += it.monthlyPrice
        }
        _weeklyPayment = (price / (52 / 12f)).toCurrencyString()
        _monthlyPayment = price.toCurrencyString()
        _yearlyPayment = (price * 12).toCurrencyString()
    }

    companion object {
        fun create(paymentRepository: PaymentRepository): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    HomeViewModel(paymentRepository)
                }
            }
        }
    }
}
