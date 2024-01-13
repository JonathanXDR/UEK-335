package com.jonathan_russ.expense_tracker.viewmodel.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [RecurringExpense::class], version = 1)
abstract class RecurringExpenseDatabase : RoomDatabase() {
    abstract fun recurringExpenseDao(): RecurringExpenseDAO

    companion object {
        @Volatile
        private var INSTANCE: RecurringExpenseDatabase? = null

        fun getDatabase(context: Context): RecurringExpenseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RecurringExpenseDatabase::class.java,
                    "recurring-expenses"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}