package com.jonathan_russ.expense_tracker.ui.editpayment

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.provider.Settings
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
import com.jonathan_russ.expense_tracker.R
import com.jonathan_russ.expense_tracker.ReminderBroadcastReceiver
import com.jonathan_russ.expense_tracker.data.RecurrenceEnum
import java.util.Calendar

@Composable
fun ReminderOption(
    reminder: Boolean,
    onReminderToggled: (Boolean) -> Unit,
    firstPaymentDate: Long,
    selectedRecurrence: RecurrenceEnum
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.edit_payment_set_reminder),
            modifier = Modifier.weight(1f)
        )

        Switch(
            checked = reminder,
            onCheckedChange = { newValue ->
                onReminderToggled(newValue)
                if (newValue) {
                    if (context.getSystemService(AlarmManager::class.java)
                            .canScheduleExactAlarms()
                    ) {
                        scheduleReminder(firstPaymentDate, selectedRecurrence, context)
                    } else {

                        requestScheduleExactAlarmPermission(context)
                    }
                }
            },
        )


    }
}

fun Context.scheduleNotification(timeInMillis: Long, title: String, content: String) {
    val intent = Intent(this, ReminderBroadcastReceiver::class.java).apply {
        putExtra("title", title)
        putExtra("content", content)
    }

    val requestCode = 0

    val pendingIntentFlag =
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT

    val pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, pendingIntentFlag)
    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

    alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
}


fun calculateNextReminderTime(firstPaymentDate: Long, recurrence: RecurrenceEnum): Long {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = firstPaymentDate
    }

    when (recurrence) {
        RecurrenceEnum.Daily -> calendar.add(Calendar.DAY_OF_MONTH, 1)
        RecurrenceEnum.Weekly -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
        RecurrenceEnum.Monthly -> calendar.add(Calendar.MONTH, 1)
        RecurrenceEnum.Yearly -> calendar.add(Calendar.YEAR, 1)
    }

    return calendar.timeInMillis
}


fun requestScheduleExactAlarmPermission(context: Context) {
    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
    context.startActivity(intent)
}

fun scheduleReminder(firstPaymentDate: Long, recurrence: RecurrenceEnum, context: Context) {
    val reminderTime = calculateNextReminderTime(firstPaymentDate, recurrence)
    context.scheduleNotification(reminderTime, "Payment Reminder", "Time to make your payment!")
}
