package com.jonathan_russ.expense_tracker.data

import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val name: String,
    val route: NavigationRoute,
    val icon: ImageVector,
)