package com.jonathan_russ.expense_tracker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jonathan_russ.expense_tracker.data.BottomNavigation
import com.jonathan_russ.expense_tracker.data.RecurringPaymentData
import com.jonathan_russ.expense_tracker.ui.editpayment.EditPaymentSheet
import com.jonathan_russ.expense_tracker.ui.theme.ExpenseTrackerTheme
import com.jonathan_russ.expense_tracker.ui.view.HomeView
import com.jonathan_russ.expense_tracker.ui.view.UpcomingView
import com.jonathan_russ.expense_tracker.viewmodel.HomeViewModel
import com.jonathan_russ.expense_tracker.viewmodel.UpcomingViewModel
import kotlinx.collections.immutable.ImmutableList


class MainActivity : ComponentActivity() {
    private val homeViewModel: HomeViewModel by viewModels {
        HomeViewModel.create((application as ExpenseTrackerApplication).repository)
    }
    private val upcomingHomeViewModel: UpcomingViewModel by viewModels {
        UpcomingViewModel.create((application as ExpenseTrackerApplication).repository)
    }

    companion object {
        private const val CHANNEL_ID = "com.jonathan_russ.expense_tracker.payment_reminders"
    }

    private fun createNotificationChannel() {
        val name = getString(R.string.notification_channel_name)
        val descriptionText = getString(R.string.notification_channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        enableEdgeToEdge()

        setContent {
            MainActivityContent(
                weeklyPayment = homeViewModel.weeklyPayment,
                monthlyPayment = homeViewModel.monthlyPayment,
                yearlyPayment = homeViewModel.yearlyPayment,
                recurringPaymentData = homeViewModel.recurringPaymentData,
                onRecurringPaymentAdded = {
                    homeViewModel.addRecurringPayment(it)
                },
                onRecurringPaymentEdited = {
                    homeViewModel.editRecurringPayment(it)
                },
                onRecurringPaymentDeleted = {
                    homeViewModel.deleteRecurringPayment(it)
                },
                upcomingHomeViewModel = upcomingHomeViewModel,
            )
        }
    }
}

@Suppress("ktlint:compose:vm-forwarding-check")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainActivityContent(
    weeklyPayment: String,
    monthlyPayment: String,
    yearlyPayment: String,
    recurringPaymentData: ImmutableList<RecurringPaymentData>,
    onRecurringPaymentAdded: (RecurringPaymentData) -> Unit,
    onRecurringPaymentEdited: (RecurringPaymentData) -> Unit,
    onRecurringPaymentDeleted: (RecurringPaymentData) -> Unit,
    upcomingHomeViewModel: UpcomingViewModel,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val backStackEntry = navController.currentBackStackEntryAsState()

    val titleRes by remember {
        derivedStateOf {
            when (backStackEntry.value?.destination?.route) {
                BottomNavigation.Home.route -> R.string.home_title
                BottomNavigation.Upcoming.route -> R.string.upcoming_title
                else -> R.string.home_title
            }
        }
    }

    var addRecurringPaymentVisible by rememberSaveable { mutableStateOf(false) }

    var selectedRecurringPayment by rememberSaveable { mutableStateOf<RecurringPaymentData?>(null) }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val bottomNavigationItems =
        listOf(
            BottomNavigation.Home,
            BottomNavigation.Upcoming,
        )

    ExpenseTrackerTheme {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = stringResource(id = titleRes),
                            )
                        },
                        scrollBehavior = scrollBehavior,
                    )
                },
                bottomBar = {
                    NavigationBar {
                        bottomNavigationItems.forEach { item ->
                            val selected =
                                item.route == backStackEntry.value?.destination?.route

                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = null,
                                    )
                                },
                                label = {
                                    Text(text = stringResource(id = item.name))
                                },
                            )
                        }
                    }
                },
                floatingActionButton = {
                    if (BottomNavigation.Home.route == backStackEntry.value?.destination?.route ||
                        BottomNavigation.Upcoming.route == backStackEntry.value?.destination?.route
                    ) {
                        FloatingActionButton(onClick = {
                            addRecurringPaymentVisible = true
                        }) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription =
                                stringResource(R.string.home_add_payment_fab_content_description),
                            )
                        }
                    }
                },
                content = { paddingValues ->
                    NavHost(
                        navController = navController,
                        startDestination = BottomNavigation.Home.route,
                        modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                    ) {
                        composable(BottomNavigation.Home.route) {
                            HomeView(
                                weeklyPayment = weeklyPayment,
                                monthlyPayment = monthlyPayment,
                                yearlyPayment = yearlyPayment,
                                recurringPaymentData = recurringPaymentData,
                                onItemClicked = {
                                    selectedRecurringPayment = it
                                },
                                contentPadding =
                                PaddingValues(
                                    top = 8.dp,
                                    bottom = 88.dp,
                                    start = 16.dp,
                                    end = 16.dp,
                                ),
                                modifier =
                                Modifier
                                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                            )
                        }
                        composable(BottomNavigation.Upcoming.route) {
                            UpcomingView(
                                upcomingHomeViewModel = upcomingHomeViewModel,
                                onItemClicked = {
                                    selectedRecurringPayment = it
                                },
                                modifier =
                                Modifier
                                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            )
                        }
                    }
                    if (addRecurringPaymentVisible) {
                        EditPaymentSheet(
                            onUpdatePayment = {
                                onRecurringPaymentAdded(it)
                                addRecurringPaymentVisible = false
                            },
                            onDismissRequest = { addRecurringPaymentVisible = false },
                            currentData = null
                        )
                    }
                    if (selectedRecurringPayment != null) {
                        EditPaymentSheet(
                            onUpdatePayment = {
                                onRecurringPaymentEdited(it)
                                selectedRecurringPayment = null
                            },
                            onDismissRequest = { selectedRecurringPayment = null },
                            currentData = selectedRecurringPayment,
                            onDeletePayment = {
                                onRecurringPaymentDeleted(it)
                                selectedRecurringPayment = null
                            },
                        )
                    }
                },
            )
        }
    }

}


