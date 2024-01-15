package com.jonathan_russ.expense_tracker.viewmodel.database

import androidx.annotation.WorkerThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class PaymentRepository(
    private val recurringExpenseDao: RecurringPaymentDao,
) {
    val allRecurringExpenses: Flow<List<RecurringPayment>> = recurringExpenseDao.getAll()
    val allRecurringExpensesByPrice: Flow<List<RecurringPayment>> =
        recurringExpenseDao.getAllByPrice()

    @WorkerThread
    suspend fun getRecurringExpenseById(id: Int): RecurringPayment? =
        withContext(Dispatchers.IO) {
            return@withContext recurringExpenseDao.getById(id)
        }

    @WorkerThread
    suspend fun insert(recurringExpense: RecurringPayment) =
        withContext(Dispatchers.IO) {
            recurringExpenseDao.insert(recurringExpense)
        }

    @WorkerThread
    suspend fun update(recurringExpense: RecurringPayment) =
        withContext(Dispatchers.IO) {
            recurringExpenseDao.update(recurringExpense)
        }

    @WorkerThread
    suspend fun delete(recurringExpense: RecurringPayment) =
        withContext(Dispatchers.IO) {
            recurringExpenseDao.delete(recurringExpense)
        }
}
