package com.jonathan_russ.expense_tracker


import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Card
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jonathan_russ.expense_tracker.data.BottomNavigation
import com.jonathan_russ.expense_tracker.data.PaymentData
import com.jonathan_russ.expense_tracker.ui.editexpense.EditPayment
import com.jonathan_russ.expense_tracker.ui.theme.ExpenseTrackerTheme
import com.jonathan_russ.expense_tracker.view.DebtsView
import com.jonathan_russ.expense_tracker.view.PaymentsView
import com.jonathan_russ.expense_tracker.viewmodel.DebtsViewModel
import com.jonathan_russ.expense_tracker.viewmodel.PaymentsViewModel
import kotlinx.collections.immutable.ImmutableList

class MainActivity : ComponentActivity() {
    private val paymentViewModel: PaymentsViewModel by viewModels {
        PaymentsViewModel.create((application as ExpenseTrackerApplication).repository)
    }
    private val upcomingPaymentsViewModel: DebtsViewModel by viewModels {
        DebtsViewModel.create((application as ExpenseTrackerApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()

        enableEdgeToEdge()

        setContent {
            MainActivityContent(
                weeklyExpense = paymentViewModel.weeklyExpense,
                monthlyExpense = paymentViewModel.monthlyExpense,
                yearlyExpense = paymentViewModel.yearlyExpense,
                paymentData = paymentViewModel.paymentData,
                onPaymentAdded = {
                    paymentViewModel.addPayment(it)
                },
                onPaymentEdited = {
                    paymentViewModel.editPayment(it)
                },
                onPaymentDeleted = {
                    paymentViewModel.deletePayment(it)
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
    paymentData: ImmutableList<PaymentData>,
    onPaymentAdded: (PaymentData) -> Unit,
    onPaymentEdited: (PaymentData) -> Unit,
    onPaymentDeleted: (PaymentData) -> Unit,
    upcomingPaymentsViewModel: DebtsViewModel,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val backStackEntry = navController.currentBackStackEntryAsState()

    val titleRes by remember {
        derivedStateOf {
            when (backStackEntry.value?.destination?.route) {
                BottomNavigation.Home.route -> R.string.home_title
                BottomNavigation.Debts.route -> R.string.upcoming_title
                else -> R.string.home_title
            }
        }
    }

    var addPaymentVisible by rememberSaveable { mutableStateOf(false) }

    var selectedPayment by rememberSaveable { mutableStateOf<PaymentData?>(null) }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val bottomNavigationItems =
        listOf(
            BottomNavigation.Home,
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
                        BottomNavigation.Debts.route == backStackEntry.value?.destination?.route
                    ) {
                        FloatingActionButton(onClick = {
                            addPaymentVisible = true
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
                        startDestination = BottomNavigation.Home.route,
                        modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                    ) {
                        composable(BottomNavigation.Home.route) {
                            PaymentsView(
                                weeklyExpense = weeklyExpense,
                                monthlyExpense = monthlyExpense,
                                yearlyExpense = yearlyExpense,
                                paymentData = paymentData,
                                onItemClicked = {
                                    selectedPayment = it
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
                                    selectedPayment = it
                                },
                                modifier =
                                Modifier
                                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            )
                        }
                    }
                    if (addPaymentVisible) {
                        EditPayment(
                            onUpdateExpense = {
                                onPaymentAdded(it)
                                addPaymentVisible = false
                            },
                            onDismissRequest = { addPaymentVisible = false },
                        )
                    }
                    if (selectedPayment != null) {
                        EditPayment(
                            onUpdateExpense = {
                                onPaymentEdited(it)
                                selectedPayment = null
                            },
                            onDismissRequest = { selectedPayment = null },
                            currentData = selectedPayment,
                            onDeleteExpense = {
                                onPaymentDeleted(it)
                                selectedPayment = null
                            },
                        )
                    }
                },
            )
        }
    }

    @Composable
    fun BalanceOverview(
        totalBalance: String,
        income: String,
        expense: String,
        modifier: Modifier = Modifier
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    text = "Total Balance",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = totalBalance,
                    style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Income",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = income,
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Medium)
                        )
                    }
                    Column {
                        Text(
                            text = "Expense",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = expense,
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Medium)
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun MonthlySummary(income: String, expense: String, modifier: Modifier = Modifier) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // SummaryCard("Income", income)
            // SummaryCard("Expense", expense)
        }
    }

    @Composable
    fun SummaryCard(label: String, amount: String) {
        Card {
            Column(
                modifier =

                Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = amount,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

private fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Test"
        val descriptionText = "Test"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel =
            NotificationChannel(BroadcastReceiver.CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
        // val notificationManager: NotificationManager =  getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // notificationManager.createNotificationChannel(channel)
    }
}



