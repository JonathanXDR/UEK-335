package com.jonathan_russ.expense_tracker

import android.app.Application
import com.jonathan_russ.expense_tracker.viewmodel.database.ExpenseRepository
import com.jonathan_russ.expense_tracker.viewmodel.database.Paymentbase

class ExpenseTrackerApplication : Application() {
    private val database by lazy { Paymentbase.getDatabase(this) }
    val repository by lazy { ExpenseRepository(database.paymentDao()) }
}
