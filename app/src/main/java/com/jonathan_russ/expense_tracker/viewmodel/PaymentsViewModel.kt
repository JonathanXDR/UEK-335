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
import com.jonathan_russ.expense_tracker.data.PaymentData
import com.jonathan_russ.expense_tracker.data.RecurrenceEnum
import com.jonathan_russ.expense_tracker.toCurrencyString
import com.jonathan_russ.expense_tracker.viewmodel.database.Payment
import com.jonathan_russ.expense_tracker.viewmodel.database.PaymentRepository
import com.jonathan_russ.expense_tracker.viewmodel.database.RecurrenceDatabase
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

class PaymentsViewModel(
    private val paymentRepository: PaymentRepository,
) : ViewModel() {
    private val _paymentData = mutableStateListOf<PaymentData>()
    val paymentData: ImmutableList<PaymentData>
        get() = _paymentData.toImmutableList()

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
            paymentRepository.allPaymentsByPrice.collect { payments ->
                onDatabaseUpdated(payments)
            }
        }
    }

    fun addPayment(payment: PaymentData) {
        viewModelScope.launch {
            paymentRepository.insert(
                Payment(
                    id = 0,
                    name = payment.name,
                    description = payment.description,
                    price = payment.price,
                    monthlyPrice = payment.monthlyPrice,
                    everyXRecurrence = payment.everyXRecurrence,
                    recurrence = getRecurrenceIntFromUIRecurrence(payment.recurrence),
                    firstPayment = payment.firstPayment,
                    nextPaymentRemainingDays = payment.nextPaymentRemainingDays,
                    nextPaymentDate = payment.nextPaymentDate,
                    location = payment.location,
                    category = payment.category,
                    reminder = payment.reminder,
                ),
            )
        }
    }

    fun editPayment(payment: PaymentData) {
        viewModelScope.launch {
            paymentRepository.update(
                Payment(
                    id = payment.id,
                    name = payment.name,
                    description = payment.description,
                    price = payment.price,
                    monthlyPrice = payment.monthlyPrice,
                    everyXRecurrence = payment.everyXRecurrence,
                    recurrence = getRecurrenceIntFromUIRecurrence(payment.recurrence),
                    firstPayment = payment.firstPayment,
                    nextPaymentRemainingDays = payment.nextPaymentRemainingDays,
                    nextPaymentDate = payment.nextPaymentDate,
                    location = payment.location,
                    category = payment.category,
                    reminder = payment.reminder,
                ),
            )
        }
    }

    fun deletePayment(payment: PaymentData) {
        viewModelScope.launch {
            paymentRepository.delete(
                Payment(
                    id = payment.id,
                    name = payment.name,
                    description = payment.description,
                    price = payment.price,
                    monthlyPrice = payment.monthlyPrice,
                    everyXRecurrence = payment.everyXRecurrence,
                    recurrence = getRecurrenceIntFromUIRecurrence(payment.recurrence),
                    firstPayment = payment.firstPayment,
                    nextPaymentRemainingDays = payment.nextPaymentRemainingDays,
                    nextPaymentDate = payment.nextPaymentDate,
                    location = payment.location,
                    category = payment.category,
                    reminder = payment.reminder,
                ),
            )
        }
    }

    private fun onDatabaseUpdated(payments: List<Payment>) {
        _paymentData.clear()
        payments.forEach {
            _paymentData.add(
                PaymentData(
                    id = it.id,
                    name = it.name,
                    description = it.description,
                    price = it.price,
                    monthlyPrice = it.monthlyPrice,
                    everyXRecurrence = it.everyXRecurrence,
                    recurrence = getRecurrenceFromDatabaseInt(
                        it.recurrence
                    ),
                    firstPayment = it.firstPayment,
                    nextPaymentRemainingDays = it.nextPaymentRemainingDays,
                    nextPaymentDate = it.nextPaymentDate,
                    location = it.location,
                    category = it.category,
                    reminder = it.reminder
                ),
            )
        }
        _paymentData.sortByDescending { it.monthlyPrice }
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
        _paymentData.forEach {
            price += it.monthlyPrice
        }
        _weeklyExpense = (price / (52 / 12f)).toCurrencyString()
        _monthlyExpense = price.toCurrencyString()
        _yearlyExpense = (price * 12).toCurrencyString()
    }

    companion object {
        fun create(paymentRepository: PaymentRepository): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    PaymentsViewModel(paymentRepository)
                }
            }
        }
    }
}
