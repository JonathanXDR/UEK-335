package com.jonathan_russ.expense_tracker.ui.view

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jonathan_russ.expense_tracker.R
import com.jonathan_russ.expense_tracker.data.RecurrenceEnum
import com.jonathan_russ.expense_tracker.data.RecurringPaymentData
import com.jonathan_russ.expense_tracker.toCurrencyString
import kotlinx.collections.immutable.ImmutableList

@Composable
fun PaymentsView(
    weeklyPayment: String,
    monthlyPayment: String,
    yearlyPayment: String,
    recurringPaymentData: ImmutableList<RecurringPaymentData>,
    onItemClicked: (RecurringPaymentData) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = contentPadding,
        modifier = modifier.fillMaxWidth(),
    ) {
        item {
            PaymentSummary(
                weeklyPayment = weeklyPayment,
                monthlyPayment = monthlyPayment,
                yearlyPayment = yearlyPayment,
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }
        items(items = recurringPaymentData) { recurringPaymentData ->
            RecurringPayment(
                recurringPaymentData = recurringPaymentData,
                onItemClicked = {
                    onItemClicked(recurringPaymentData)
                },
            )
        }
    }
}

@Composable
private fun PaymentSummary(
    weeklyPayment: String,
    monthlyPayment: String,
    yearlyPayment: String,
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
            text = monthlyPayment,
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
                    text = weeklyPayment,
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
                    text = yearlyPayment,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}

@Composable
private fun RecurringPayment(
    recurringPaymentData: RecurringPaymentData,
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
                    text = recurringPaymentData.name,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (recurringPaymentData.description.isNotBlank()) {
                    Text(
                        text = recurringPaymentData.description,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = recurringPaymentData.monthlyPrice.toCurrencyString(),
                    style = MaterialTheme.typography.headlineSmall,
                )
                if (recurringPaymentData.recurrence != RecurrenceEnum.Monthly ||
                    recurringPaymentData.everyXRecurrence != 1
                ) {
                    Text(
                        text =
                        "${recurringPaymentData.price.toCurrencyString()} / " +
                                "${recurringPaymentData.everyXRecurrence} " +
                                stringResource(id = recurringPaymentData.recurrence.shortStringRes),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.End,
                    )
                }
            }
        }
    }
}
