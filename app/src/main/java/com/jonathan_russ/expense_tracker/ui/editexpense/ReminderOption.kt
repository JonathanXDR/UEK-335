package com.jonathan_russ.expense_tracker.ui.editexpense

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jonathan_russ.expense_tracker.MainActivity
import com.jonathan_russ.expense_tracker.R

@Composable
fun ReminderOption(
    reminder: Boolean,
    onReminderToggled: (Boolean) -> Unit,
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.edit_expense_set_reminder),
            modifier = Modifier.weight(1f)
        )

        Switch(
            checked = reminder,
            onCheckedChange = { newValue ->
                onReminderToggled(newValue)
                if (newValue) {
                    val timeForNotification = System.currentTimeMillis() + 5000L
                    (context as? MainActivity)?.scheduleNotification(
                        timeForNotification
                    )
                }
            },
        )
    }
}



