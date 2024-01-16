package com.jonathan_russ.expense_tracker.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.jonathan_russ.expense_tracker.data.RecurrenceEnum
import com.jonathan_russ.expense_tracker.data.RecurringPaymentData
import com.jonathan_russ.expense_tracker.data.UpcomingPaymentData
import com.jonathan_russ.expense_tracker.isSameDay
import com.jonathan_russ.expense_tracker.viewmodel.database.PaymentRepository
import com.jonathan_russ.expense_tracker.viewmodel.database.RecurrenceDatabase
import com.jonathan_russ.expense_tracker.viewmodel.database.RecurringPayment
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

class UpcomingViewModel(
    private val paymentRepository: PaymentRepository?,
) : ViewModel() {
    private val _upcomingPaymentsData = mutableStateListOf<UpcomingPaymentData>()
    val upcomingPaymentsData: ImmutableList<UpcomingPaymentData>
        get() = _upcomingPaymentsData.toImmutableList()

    init {
        viewModelScope.launch {
            paymentRepository?.allRecurringPaymentsByPrice?.collect { recurringPayments ->
                onDatabaseUpdated(recurringPayments)
            }
        }
    }

    fun onPaymentWithIdClicked(
        expenceId: Int,
        onItemClicked: (RecurringPaymentData) -> Unit,
    ) {
        viewModelScope.launch {
            paymentRepository?.getRecurringPaymentById(expenceId)?.let {
                val recurringPaymentData =
                    RecurringPaymentData(
                        id = it.id,
                        name = it.name!!,
                        description = it.description!!,
                        price = it.price!!,
                        monthlyPrice = it.getMonthlyPrice(),
                        everyXRecurrence = it.everyXRecurrence!!,
                        recurrence = getRecurrenceFromDatabaseInt(it.recurrence!!),
                        firstPayment = it.firstPayment!!,
                    )
                onItemClicked(recurringPaymentData)
            }
        }
    }

    private fun onDatabaseUpdated(recurringPayments: List<RecurringPayment>) {
        _upcomingPaymentsData.clear()
        recurringPayments.forEach {
            val firstPayment = it.firstPayment!!
            val nextPaymentInMilliseconds =
                getNextPaymentInMilliseconds(firstPayment, it.everyXRecurrence!!, it.recurrence!!)
            val nextPaymentRemainingDays = getNextPaymentDays(nextPaymentInMilliseconds)
            val nextPaymentDate =
                DateFormat.getDateInstance().format(Date(nextPaymentInMilliseconds))
            if (firstPayment > 0L) {
                _upcomingPaymentsData.add(
                    UpcomingPaymentData(
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

    private fun getRecurrenceFromDatabaseInt(recurrenceInt: Int): RecurrenceEnum {
        return when (recurrenceInt) {
            RecurrenceDatabase.Daily.value -> RecurrenceEnum.Daily
            RecurrenceDatabase.Weekly.value -> RecurrenceEnum.Weekly
            RecurrenceDatabase.Monthly.value -> RecurrenceEnum.Monthly
            RecurrenceDatabase.Yearly.value -> RecurrenceEnum.Yearly
            else -> RecurrenceEnum.Monthly
        }
    }

    companion object {
        fun create(paymentRepository: PaymentRepository): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    UpcomingViewModel(paymentRepository)
                }
            }
        }
    }
}
