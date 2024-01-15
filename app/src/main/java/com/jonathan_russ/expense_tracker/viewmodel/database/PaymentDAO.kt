package com.jonathan_russ.expense_tracker.viewmodel.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments")
    fun getAll(): Flow<List<Payment>>

    @Query("SELECT * FROM payments ORDER BY price DESC")
    fun getAllByPrice(): Flow<List<Payment>>

    @Query("SELECT * FROM payments WHERE id = :id")
    fun getById(id: Int): Payment?

    @Insert
    fun insert(payment: Payment)

    @Update
    fun update(payment: Payment)

    @Delete
    fun delete(payment: Payment)
}
