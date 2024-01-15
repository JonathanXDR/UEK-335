package com.jonathan_russ.expense_tracker.viewmodel.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Payment::class], version = 4)
abstract class Paymentbase : RoomDatabase() {
    abstract fun paymentDao(): PaymentDao

    companion object {
        @Volatile
        private var instance: Paymentbase? = null

        fun getDatabase(context: Context): Paymentbase {
            return instance ?: synchronized(this) {
                val tmpInstance =
                    Room.databaseBuilder(
                        context.applicationContext,
                        Paymentbase::class.java,
                        "recurring-expenses",
                    )
                        .addMigrations(migration_1_2)
                        .addMigrations(migration_2_3)
                        .addMigrations(migration_3_4)
                        .build()
                instance = tmpInstance
                tmpInstance
            }
        }

        private val migration_1_2 =
            object : Migration(1, 2) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL(
                        "ALTER TABLE recurring_expenses ADD COLUMN everyXRecurrence INTEGER DEFAULT 1",
                    )
                    db.execSQL("ALTER TABLE recurring_expenses ADD COLUMN recurrence INTEGER DEFAULT 3")
                }
            }

        private val migration_2_3 =
            object : Migration(2, 3) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL("ALTER TABLE recurring_expenses ADD COLUMN firstPayment INTEGER DEFAULT 0")
                }
            }

        private val migration_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE payments ADD COLUMN monthlyPrice REAL NOT NULL DEFAULT 0.0")
                db.execSQL("ALTER TABLE payments ADD COLUMN nextPaymentRemainingDays INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE payments ADD COLUMN nextPaymentDate TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE payments ADD COLUMN location TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE payments ADD COLUMN category TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE payments ADD COLUMN reminder INTEGER NOT NULL DEFAULT 0")
            }
        }

    }
}
