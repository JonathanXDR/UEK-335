package com.jonathan_russ.expense_tracker.data

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Payment
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.ui.graphics.vector.ImageVector
import com.jonathan_russ.expense_tracker.R

sealed class BottomNavigation(
    val route: String,
    @StringRes val name: Int,
    val icon: ImageVector,
) {
    data object Payments :
        BottomNavigation("payments", R.string.bottom_nav_home, Icons.Rounded.Payments)

    data object Debts :
        BottomNavigation("debts", R.string.bottom_nav_upcoming, Icons.Rounded.Payment)
}
