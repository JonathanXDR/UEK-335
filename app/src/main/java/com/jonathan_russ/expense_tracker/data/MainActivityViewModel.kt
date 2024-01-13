package com.jonathan_russ.expense_tracker.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.jonathan_russ.expense_tracker.toValueString
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

class MainActivityViewModel : ViewModel() {
    private val _subscriptionsData = mutableStateListOf<ExpenseTrackerData>(
        ExpenseTrackerData(
            name = "Netflix",
            description = "My Netflix description",
            priceValue = 9.99f,
        ),
        ExpenseTrackerData(
            name = "Disney Plus",
            description = "My Disney Plus description",
            priceValue = 5f,
        ),
        ExpenseTrackerData(
            name = "Amazon Prime",
            description = "My Disney Plus description",
            priceValue = 7.95f,
        ),
    )
    val subscriptionData: ImmutableList<ExpenseTrackerData>
        get() = _subscriptionsData.toImmutableList()

    private var _montlyPrice by mutableStateOf("")
    val monthlyPrice: String
        get() = _montlyPrice

    init {
        var price = 0f
        _subscriptionsData.forEach {
            price += it.priceValue
        }
        _montlyPrice = "${price.toValueString()} €" // TODO: Make currency dynamic
    }
}