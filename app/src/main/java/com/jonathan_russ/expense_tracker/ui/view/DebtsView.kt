package com.jonathan_russ.expense_tracker.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.List
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import com.jonathan_russ.expense_tracker.data.RecurringPaymentData
import com.jonathan_russ.expense_tracker.data.UpcomingPaymentData
import com.jonathan_russ.expense_tracker.toCurrencyString
import com.jonathan_russ.expense_tracker.viewmodel.UpcomingViewModel
import kotlinx.collections.immutable.ImmutableList

@Composable
fun UpcomingView(
    upcomingPaymentsViewModel: UpcomingViewModel,
    onItemClicked: (RecurringPaymentData) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    if (upcomingPaymentsViewModel.upcomingPaymentsData.size > 0) {
        UpcomingSummary(
            upcomingPaymentsData = upcomingPaymentsViewModel.upcomingPaymentsData,
            onItemClicked = {
                upcomingPaymentsViewModel.onPaymentWithIdClicked(it, onItemClicked)
            },
            modifier = modifier,
            contentPadding = contentPadding,
        )
    } else {
        UpcomingSummaryPlaceholder(
            modifier =
            modifier
                .fillMaxSize()
                .padding(vertical = 16.dp),
        )
    }
}

@Composable
private fun UpcomingSummary(
    upcomingPaymentsData: ImmutableList<UpcomingPaymentData>,
    onItemClicked: (Int) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = contentPadding,
        modifier = modifier.fillMaxWidth(),
    ) {
        items(items = upcomingPaymentsData) { upcomingPaymentData ->
            Payment(
                upcomingPaymentData = upcomingPaymentData,
                onItemClicked = {
                    onItemClicked(upcomingPaymentData.id)
                },
            )
        }
    }
}

@Composable
private fun Payment(
    upcomingPaymentData: UpcomingPaymentData,
    onItemClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val inDaysString =
        when (upcomingPaymentData.nextPaymentRemainingDays) {
            0 -> stringResource(id = R.string.upcoming_time_remaining_today)
            1 -> stringResource(id = R.string.upcoming_time_remaining_tomorrow)
            else ->
                stringResource(
                    id = R.string.upcoming_time_remaining_days,
                    upcomingPaymentData.nextPaymentRemainingDays,
                )
        }

    val progress =
        (upcomingPaymentData.nextPaymentRemainingDays.toFloat() / 30) // Assuming a 30-day month

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onItemClicked() }
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Column {
                    Text(
                        text = upcomingPaymentData.name,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (upcomingPaymentData.description.isNotBlank()) {
                        Text(
                            text = upcomingPaymentData.description,
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                Text(
                    text = upcomingPaymentData.price.toCurrencyString(),
                    style = MaterialTheme.typography.headlineSmall,
                    // fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(start = 16.dp)

                )
            }
            LinearProgressIndicator(
                progress = 1 - progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )
            Text(
                text = inDaysString,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )
        }
    }
}


@Composable
fun UpcomingSummaryPlaceholder(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Rounded.List,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
        )
        Text(
            text = stringResource(id = R.string.upcoming_placeholder_title),
            textAlign = TextAlign.Center,
        )
    }
}
