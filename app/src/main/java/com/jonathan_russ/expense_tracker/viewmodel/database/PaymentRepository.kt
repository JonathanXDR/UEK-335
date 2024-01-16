package com.jonathan_russ.expense_tracker.viewmodel.database

import androidx.annotation.WorkerThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class PaymentRepository(
    private val recurringPaymentDao: RecurringPaymentDao,
) {
    val allRecurringPayments: Flow<List<RecurringPayment>> = recurringPaymentDao.getAll()
    val allRecurringPaymentsByPrice: Flow<List<RecurringPayment>> =
        recurringPaymentDao.getAllByPrice()

    @WorkerThread
    suspend fun getRecurringPaymentById(id: Int): RecurringPayment? =
        withContext(Dispatchers.IO) {
            return@withContext recurringPaymentDao.getById(id)
        }

    @WorkerThread
    suspend fun insert(recurringPayment: RecurringPayment) =
        withContext(Dispatchers.IO) {
            recurringPaymentDao.insert(recurringPayment)
        }

    @WorkerThread
    suspend fun update(recurringPayment: RecurringPayment) =
        withContext(Dispatchers.IO) {
            recurringPaymentDao.update(recurringPayment)
        }

    @WorkerThread
    suspend fun delete(recurringPayment: RecurringPayment) =
        withContext(Dispatchers.IO) {
            recurringPaymentDao.delete(recurringPayment)
        }
}
