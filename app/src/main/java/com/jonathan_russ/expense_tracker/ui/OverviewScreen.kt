package com.jonathan_russ.expense_tracker.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun OverviewScreen() {
}

@Composable
private fun OverviewHeaderElement(
    @StringRes header: Int,
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(id = header),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier =
        modifier
            .padding(16.dp)
            .fillMaxWidth(),
        overflow = TextOverflow.Ellipsis,
    )
}


