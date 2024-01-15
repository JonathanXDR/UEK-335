package com.jonathan_russ.expense_tracker

import android.app.Application
import com.jonathan_russ.expense_tracker.viewmodel.database.PaymentDatabase
import com.jonathan_russ.expense_tracker.viewmodel.database.PaymentRepository

class ExpenseTrackerApplication : Application() {
    private val database by lazy { PaymentDatabase.getDatabase(this) }
    val repository by lazy { PaymentRepository(database.recurringExpenseDao()) }
}
