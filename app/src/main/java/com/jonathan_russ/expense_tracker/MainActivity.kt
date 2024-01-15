package com.jonathan_russ.expense_tracker

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
import com.jonathan_russ.expense_tracker.ui.editexpense.EditPaymentSheet
import com.jonathan_russ.expense_tracker.ui.theme.ExpenseTrackerTheme
import com.jonathan_russ.expense_tracker.ui.view.DebtsView
import com.jonathan_russ.expense_tracker.ui.view.PaymentsView
import com.jonathan_russ.expense_tracker.viewmodel.DebtsViewModel
import com.jonathan_russ.expense_tracker.viewmodel.PaymentsViewModel
import kotlinx.collections.immutable.ImmutableList

class MainActivity : ComponentActivity() {
    private val paymentsViewModel: PaymentsViewModel by viewModels {
        PaymentsViewModel.create((application as ExpenseTrackerApplication).repository)
    }
    private val upcomingPaymentsViewModel: DebtsViewModel by viewModels {
        DebtsViewModel.create((application as ExpenseTrackerApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            MainActivityContent(
                weeklyExpense = paymentsViewModel.weeklyExpense,
                monthlyExpense = paymentsViewModel.monthlyExpense,
                yearlyExpense = paymentsViewModel.yearlyExpense,
                recurringPaymentData = paymentsViewModel.recurringPaymentData,
                onRecurringExpenseAdded = {
                    paymentsViewModel.addRecurringExpense(it)
                },
                onRecurringExpenseEdited = {
                    paymentsViewModel.editRecurringExpense(it)
                },
                onRecurringExpenseDeleted = {
                    paymentsViewModel.deleteRecurringExpense(it)
                },
                upcomingPaymentsViewModel = upcomingPaymentsViewModel,
            )
        }
    }
}

@Suppress("ktlint:compose:vm-forwarding-check")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainActivityContent(
    weeklyExpense: String,
    monthlyExpense: String,
    yearlyExpense: String,
    recurringPaymentData: ImmutableList<RecurringPaymentData>,
    onRecurringExpenseAdded: (RecurringPaymentData) -> Unit,
    onRecurringExpenseEdited: (RecurringPaymentData) -> Unit,
    onRecurringExpenseDeleted: (RecurringPaymentData) -> Unit,
    upcomingPaymentsViewModel: DebtsViewModel,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val backStackEntry = navController.currentBackStackEntryAsState()

    val titleRes by remember {
        derivedStateOf {
            when (backStackEntry.value?.destination?.route) {
                BottomNavigation.Payments.route -> R.string.home_title
                BottomNavigation.Debts.route -> R.string.upcoming_title
                else -> R.string.home_title
            }
        }
    }

    var addRecurringExpenseVisible by rememberSaveable { mutableStateOf(false) }

    var selectedRecurringExpense by rememberSaveable { mutableStateOf<RecurringPaymentData?>(null) }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val bottomNavigationItems =
        listOf(
            BottomNavigation.Payments,
            BottomNavigation.Debts,
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
                                        // Pop up to the start destination of the graph to
                                        // avoid building up a large stack of destinations
                                        // on the back stack as users select items
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        // Avoid multiple copies of the same destination when
                                        // reselecting the same item
                                        launchSingleTop = true
                                        // Restore state when reselecting a previously selected item
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
                    if (BottomNavigation.Payments.route == backStackEntry.value?.destination?.route ||
                        BottomNavigation.Debts.route == backStackEntry.value?.destination?.route
                    ) {
                        FloatingActionButton(onClick = {
                            addRecurringExpenseVisible = true
                        }) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription =
                                stringResource(R.string.home_add_expense_fab_content_description),
                            )
                        }
                    }
                },
                content = { paddingValues ->
                    NavHost(
                        navController = navController,
                        startDestination = BottomNavigation.Payments.route,
                        modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                    ) {
                        composable(BottomNavigation.Payments.route) {
                            PaymentsView(
                                weeklyExpense = weeklyExpense,
                                monthlyExpense = monthlyExpense,
                                yearlyExpense = yearlyExpense,
                                recurringPaymentData = recurringPaymentData,
                                onItemClicked = {
                                    selectedRecurringExpense = it
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
                        composable(BottomNavigation.Debts.route) {
                            DebtsView(
                                upcomingPaymentsViewModel = upcomingPaymentsViewModel,
                                onItemClicked = {
                                    selectedRecurringExpense = it
                                },
                                modifier =
                                Modifier
                                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            )
                        }
                    }
                    if (addRecurringExpenseVisible) {
                        EditPaymentSheet(
                            onUpdateExpense = {
                                onRecurringExpenseAdded(it)
                                addRecurringExpenseVisible = false
                            },
                            onDismissRequest = { addRecurringExpenseVisible = false },
                        )
                    }
                    if (selectedRecurringExpense != null) {
                        EditPaymentSheet(
                            onUpdateExpense = {
                                onRecurringExpenseEdited(it)
                                selectedRecurringExpense = null
                            },
                            onDismissRequest = { selectedRecurringExpense = null },
                            currentData = selectedRecurringExpense,
                            onDeleteExpense = {
                                onRecurringExpenseDeleted(it)
                                selectedRecurringExpense = null
                            },
                        )
                    }
                },
            )
        }
    }
}
