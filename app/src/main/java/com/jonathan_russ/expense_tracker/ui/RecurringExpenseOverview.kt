package com.jonathan_russ.expense_tracker.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jonathan_russ.expense_tracker.R
import com.jonathan_russ.expense_tracker.data.Recurrence
import com.jonathan_russ.expense_tracker.data.RecurringExpenseData
import com.jonathan_russ.expense_tracker.toCurrencyString
import com.jonathan_russ.expense_tracker.ui.theme.ExpenseTrackerTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun RecurringExpenseOverview(
    weeklyExpense: String,
    monthlyExpense: String,
    yearlyExpense: String,
    recurringExpenseData: ImmutableList<RecurringExpenseData>,
    onItemClicked: (RecurringExpenseData) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = contentPadding,
        modifier = modifier.fillMaxWidth(),
    ) {
        item {
            RecurringExpenseSummary(
                weeklyExpense = weeklyExpense,
                monthlyExpense = monthlyExpense,
                yearlyExpense = yearlyExpense,
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }
        items(items = recurringExpenseData) { recurringExpenseData ->
            RecurringExpense(
                recurringExpenseData = recurringExpenseData,
                onItemClicked = {
                    onItemClicked(recurringExpenseData)
                },
            )
        }
    }
}

@Composable
private fun RecurringExpenseSummary(
    weeklyExpense: String,
    monthlyExpense: String,
    yearlyExpense: String,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
        modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Text(
            text = stringResource(R.string.home_summary_monthly),
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = monthlyExpense,
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.size(8.dp))
        Row {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = stringResource(R.string.home_summary_weekly),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = weeklyExpense,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = stringResource(R.string.home_summary_yearly),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = yearlyExpense,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}

@Composable
private fun RecurringExpense(
    recurringExpenseData: RecurringExpenseData,
    onItemClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.clickable { onItemClicked() },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp),
        ) {
            Column(
                modifier =
                Modifier
                    .padding(end = 16.dp)
                    .weight(1f),
            ) {
                Text(
                    text = recurringExpenseData.name,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (recurringExpenseData.description.isNotBlank()) {
                    Text(
                        text = recurringExpenseData.description,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = recurringExpenseData.monthlyPrice.toCurrencyString(),
                    style = MaterialTheme.typography.headlineSmall,
                )
                if (recurringExpenseData.recurrence != Recurrence.Monthly ||
                    recurringExpenseData.everyXRecurrence != 1
                ) {
                    Text(
                        text =
                        "${recurringExpenseData.price.toCurrencyString()} / " +
                                "${recurringExpenseData.everyXRecurrence} " +
                                stringResource(id = recurringExpenseData.recurrence.shortStringRes),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.End,
                    )
                }
            }
        }
    }
}

@Preview()
@Composable
private fun RecurringExpenseOverviewPreview() {
    ExpenseTrackerTheme {
        Surface(modifier = Modifier.fillMaxWidth()) {
            RecurringExpenseOverview(
                weeklyExpense = "4,00 €",
                monthlyExpense = "16,00 €",
                yearlyExpense = "192,00 €",
                recurringExpenseData =
                persistentListOf(
                    RecurringExpenseData(
                        id = 0,
                        name = "Netflix",
                        description = "My Netflix description",
                        price = 9.99f,
                        monthlyPrice = 9.99f,
                        everyXRecurrence = 1,
                        recurrence = Recurrence.Monthly,
                        0L,
                    ),
                    RecurringExpenseData(
                        id = 1,
                        name = "Disney Plus",
                        description =
                        "My Disney Plus very very very very very " +
                                "very very very very long description",
                        price = 5f,
                        monthlyPrice = 5f,
                        everyXRecurrence = 1,
                        recurrence = Recurrence.Monthly,
                        1L,
                    ),
                    RecurringExpenseData(
                        id = 2,
                        name = "Amazon Prime with a long name",
                        description = "",
                        price = 7.95f,
                        monthlyPrice = 7.95f,
                        everyXRecurrence = 1,
                        recurrence = Recurrence.Monthly,
                        2L,
                    ),
                    RecurringExpenseData(
                        id = 3,
                        name = "Yearly Test Subscription",
                        description = "Test Description with another very long name",
                        price = 72f,
                        monthlyPrice = 6f,
                        everyXRecurrence = 1,
                        recurrence = Recurrence.Yearly,
                        3L,
                    ),
                ),
                onItemClicked = {},
            )
        }
    }
}
