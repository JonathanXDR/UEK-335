package com.jonathan_russ.expense_tracker.data

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.jonathan_russ.expense_tracker.R

sealed class BottomNavigation(val route: String, @StringRes val name: Int, val icon: ImageVector) {
    data object Home : BottomNavigation("home", R.string.bottom_nav_home, Icons.Rounded.Home)
    data object Settings :
        BottomNavigation("settings", R.string.bottom_nav_settings, Icons.Rounded.Settings)
}