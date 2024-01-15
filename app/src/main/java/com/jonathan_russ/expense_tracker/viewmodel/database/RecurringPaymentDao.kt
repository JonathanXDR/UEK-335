package com.jonathan_russ.expense_tracker.viewmodel.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RecurringPaymentDao {
    @Query("SELECT * FROM recurring_expenses")
    fun getAll(): Flow<List<RecurringPayment>>

    @Query("SELECT * FROM recurring_expenses ORDER BY price DESC")
    fun getAllByPrice(): Flow<List<RecurringPayment>>

    @Query("SELECT * FROM recurring_expenses WHERE id = :id")
    fun getById(id: Int): RecurringPayment?

    @Insert
    fun insert(recurringExpense: RecurringPayment)

    @Update
    fun update(recurringExpense: RecurringPayment)

    @Delete
    fun delete(recurringExpense: RecurringPayment)
}
