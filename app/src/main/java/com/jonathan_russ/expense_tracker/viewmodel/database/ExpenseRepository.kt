package com.jonathan_russ.expense_tracker.viewmodel.database

import androidx.annotation.WorkerThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ExpenseRepository(
    private val paymentDao: PaymentDao,
) {
    val allPayments: Flow<List<Payment>> = paymentDao.getAll()
    val allPaymentsByPrice: Flow<List<Payment>> =
        paymentDao.getAllByPrice()

    @WorkerThread
    suspend fun getPaymentById(id: Int): Payment? =
        withContext(Dispatchers.IO) {
            return@withContext paymentDao.getById(id)
        }

    @WorkerThread
    suspend fun insert(payment: Payment) =
        withContext(Dispatchers.IO) {
            paymentDao.insert(payment)
        }

    @WorkerThread
    suspend fun update(payment: Payment) =
        withContext(Dispatchers.IO) {
            paymentDao.update(payment)
        }

    @WorkerThread
    suspend fun delete(payment: Payment) =
        withContext(Dispatchers.IO) {
            paymentDao.delete(payment)
        }
}
