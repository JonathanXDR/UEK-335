package com.jonathan_russ.expense_tracker.viewmodel.database

import androidx.annotation.WorkerThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ExpenseRepository(
    private val recurringExpenseDao: RecurringExpenseDAO,
) {
    val allRecurringExpenses: Flow<List<RecurringExpense>> = recurringExpenseDao.getAll()
    val allRecurringExpensesByPrice: Flow<List<RecurringExpense>> =
        recurringExpenseDao.getAllByPrice()

    @WorkerThread
    suspend fun insert(recurringExpense: RecurringExpense) = withContext(Dispatchers.IO) {
        recurringExpenseDao.insert(recurringExpense)
    }

    @WorkerThread
    suspend fun delete(recurringExpense: RecurringExpense) = withContext(Dispatchers.IO) {
        recurringExpenseDao.delete(recurringExpense)
    }
}