package com.jonathan_russ.expense_tracker.viewmodel.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [RecurringExpense::class], version = 1, exportSchema = false)
abstract class RecurringExpenseDatabase : RoomDatabase() {

    abstract fun recurringExpenseDao(): RecurringExpenseDAO

    companion object {

        @Volatile
        private var instance: RecurringExpenseDatabase? = null

        fun getDatabase(context: Context): RecurringExpenseDatabase {
            return instance ?: synchronized(this) {
                val tmpInstance =
                    Room.databaseBuilder(
                        context.applicationContext,
                        RecurringExpenseDatabase::class.java,
                        "recurring-expenses",
                    ).build()
                instance = tmpInstance
                tmpInstance
            }
        }
    }
}
