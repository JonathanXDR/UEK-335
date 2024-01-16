package com.jonathan_russ.expense_tracker.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jonathan_russ.expense_tracker.data.ExpenseTrackerData
import com.jonathan_russ.expense_tracker.ui.theme.ExpenseTrackerTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun ExpenseTrackerOverview(
    recurringExpenseData: ImmutableList<ExpenseTrackerData>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = contentPadding,
        modifier = modifier.fillMaxWidth(),
    ) {
        items(items = recurringExpenseData) { recurringExpenseData ->
            RecurringExpense(
                recurringExpenseData = recurringExpenseData,
            )
        }
    }
}

@Composable
fun RecurringExpense(
    recurringExpenseData: ExpenseTrackerData,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = recurringExpenseData.name,
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = recurringExpenseData.description,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                text = recurringExpenseData.priceString,
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}

@Preview()
@Composable
private fun ExpenseTrackerOverviewPreview() {
    ExpenseTrackerTheme {
        Surface(modifier = Modifier.fillMaxWidth()) {
            ExpenseTrackerOverview(
                persistentListOf(
                    ExpenseTrackerData(
                        name = "Netflix",
                        description = "My Netflix description",
                        priceValue = 9.99f,
                    ),
                    ExpenseTrackerData(
                        name = "Disney Plus",
                        description = "My Disney Plus very very very very very very very very very long description",
                        priceValue = 5f,
                    ),
                    ExpenseTrackerData(
                        name = "Amazon Prime with a long name",
                        description = "My Disney Plus description",
                        priceValue = 7.95f,
                    ),
                )
            )
        }
    }
}