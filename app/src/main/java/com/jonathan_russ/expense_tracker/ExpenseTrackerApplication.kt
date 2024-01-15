package com.jonathan_russ.expense_tracker

import android.app.Application
import com.jonathan_russ.expense_tracker.viewmodel.database.ExpenseRepository
import com.jonathan_russ.expense_tracker.viewmodel.database.RecurringExpenseDatabase

class ExpenseTrackerApplication : Application() {
    private val database by lazy { RecurringExpenseDatabase.getDatabase(this) }
    val repository by lazy { ExpenseRepository(database.recurringExpenseDao()) }
}
