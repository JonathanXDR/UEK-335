package com.jonathan_russ.expense_tracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jonathan_russ.expense_tracker.data.BottomNavItem
import com.jonathan_russ.expense_tracker.data.ExpenseTrackerData
import com.jonathan_russ.expense_tracker.data.MainActivityViewModel
import com.jonathan_russ.expense_tracker.data.NavigationRoute
import com.jonathan_russ.expense_tracker.ui.ExpenseTrackerOverview
import com.jonathan_russ.expense_tracker.ui.theme.ExpenseTrackerTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainActivityViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainActivityContent(
                subscriptionsData = viewModel.subscriptionData,
                monthlyPrize = viewModel.monthlyPrice,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainActivityContent(
    subscriptionsData: ImmutableList<ExpenseTrackerData>,
    monthlyPrize: String,
) {
    val navController = rememberNavController()
    val backStackEntry = navController.currentBackStackEntryAsState()

    var addSubscriptionVisible by rememberSaveable { mutableStateOf(false) }

    val bottomNavItems = listOf(
        BottomNavItem(
            name = "Home",
            route = NavigationRoute.Home,
            icon = Icons.Rounded.Home,
        ),
        BottomNavItem(
            name = "Settings",
            route = NavigationRoute.Settings,
            icon = Icons.Rounded.Settings,
        ),
    )

    ExpenseTrackerTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Subscriptions", modifier = Modifier.weight(1f))
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(end = 16.dp),
                                ) {
                                    Text(
                                        text = "Monthly",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = monthlyPrize,
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                }
                            }
                        },
                    )
                },
                bottomBar = {
                    NavigationBar {
                        bottomNavItems.forEach { item ->
                            val selected =
                                item.route.value == backStackEntry.value?.destination?.route

                            NavigationBarItem(
                                selected = selected,
                                onClick = { navController.navigate(item.route.value) },
                                icon = {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = "${item.name} Icon"
                                    )
                                },
                                label = {
                                    Text(text = item.name)
                                }
                            )
                        }
                    }
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            addSubscriptionVisible = true
                        }
                    ) {
                        Icon(imageVector = Icons.Rounded.Add, contentDescription = "Add")
                    }
                },
                content = { paddingValues ->
                    NavHost(
                        navController = navController,
                        startDestination = NavigationRoute.Home.value,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                    ) {
                        composable(NavigationRoute.Home.value) {
                            ExpenseTrackerOverview(
                                subscriptionsData = subscriptionsData,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                            )
                        }
                        composable(NavigationRoute.Settings.value) {

                        }
                    }
                    if (addSubscriptionVisible) {
                        AddSubscription(
                            onDismissRequest = { addSubscriptionVisible = false },
                        )
                    }
                }
            )

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSubscription(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
    ) {
        Text(text = "Test")
    }
}

@Preview(showBackground = true)
@Composable
fun MainActivityContentPreview() {
    MainActivityContent(
        persistentListOf(
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
        ),
        "15,19 €"
    )
}