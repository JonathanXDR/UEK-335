package com.jonathan_russ.expense_tracker.viewmodel.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RecurringPaymentDao {
    @Query("SELECT * FROM recurring_payments")
    fun getAll(): Flow<List<RecurringPayment>>

    @Query("SELECT * FROM recurring_payments ORDER BY price DESC")
    fun getAllByPrice(): Flow<List<RecurringPayment>>

    @Query("SELECT * FROM recurring_payments WHERE id = :id")
    fun getById(id: Int): RecurringPayment?

    @Insert
    fun insert(recurringPayment: RecurringPayment)

    @Update
    fun update(recurringPayment: RecurringPayment)

    @Delete
    fun delete(recurringPayment: RecurringPayment)
}
