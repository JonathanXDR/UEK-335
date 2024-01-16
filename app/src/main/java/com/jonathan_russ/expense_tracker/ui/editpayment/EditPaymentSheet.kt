package com.jonathan_russ.expense_tracker.ui.editpayment


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.jonathan_russ.expense_tracker.R
import com.jonathan_russ.expense_tracker.data.RecurrenceEnum
import com.jonathan_russ.expense_tracker.data.RecurringPaymentData
import com.jonathan_russ.expense_tracker.toFloatIgnoreSeparator
import com.jonathan_russ.expense_tracker.toLocalString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPaymentSheet(
    onUpdatePayment: (RecurringPaymentData) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    currentData: RecurringPaymentData? = null,
    onDeletePayment: ((RecurringPaymentData) -> Unit)? = null,
) {
    val sheetState: SheetState =
        rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
        )
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        windowInsets = WindowInsets.statusBars,
        modifier = modifier,
    ) {
        EditPaymentInternal(
            onUpdatePayment = onUpdatePayment,
            confirmButtonString =
            if (currentData == null) {
                stringResource(R.string.edit_payment_button_add)
            } else {
                stringResource(
                    R.string.edit_payment_button_update,
                )
            },
            currentData = currentData,
            onDeletePayment = onDeletePayment,
        )
    }
}

@Composable
private fun EditPaymentInternal(
    onUpdatePayment: (RecurringPaymentData) -> Unit,
    confirmButtonString: String,
    currentData: RecurringPaymentData? = null,
    onDeletePayment: ((RecurringPaymentData) -> Unit)? = null,
) {
    var nameState by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(currentData?.name ?: ""))
    }
    val nameInputError =
        rememberSaveable {
            mutableStateOf(false)
        }
    var descriptionState by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(currentData?.description ?: ""))
    }
    var priceState by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(currentData?.price?.toLocalString() ?: ""))
    }
    val priceInputError = rememberSaveable { mutableStateOf(false) }
    var everyXRecurrenceState by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(currentData?.everyXRecurrence?.toString() ?: ""))
    }
    val everyXRecurrenceInputError = rememberSaveable { mutableStateOf(false) }
    var selectedRecurrence by rememberSaveable {
        mutableStateOf(currentData?.recurrence ?: RecurrenceEnum.Monthly)
    }
    var firstPaymentDate by rememberSaveable {
        mutableLongStateOf(currentData?.firstPayment ?: 0L)
    }
    val nextPaymentRemainingDays = rememberSaveable {
        mutableIntStateOf(0)
    }
    val nextPaymentDate = rememberSaveable {
        mutableStateOf("")
    }
    var locationState by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(currentData?.location ?: ""))
    }

    var selectedCategory by rememberSaveable {
        mutableStateOf(currentData?.category)
    }

    var reminderState by rememberSaveable {
        mutableStateOf(currentData?.reminder ?: false)
    }


    val scrollState = rememberScrollState()
    val localFocusManager = LocalFocusManager.current
    val categories = listOf(
        "Sports",
        "News",
        "Entertainment",
        "Technology",
        "Health",
        "Food",
        "Travel",
        "Shopping",
        "Other",
    )
    val handleCategorySelected: (String?) -> Unit = { category ->
        println("Selected category: $category")
    }

    Column(
        modifier =
        Modifier
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState),
    ) {
        NameOption(
            name = nameState,
            onNameChanged = { nameState = it },
            nameInputError = nameInputError.value,
            onNext = { localFocusManager.moveFocus(FocusDirection.Next) },
        )
        DescriptionOption(
            description = descriptionState,
            onDescriptionChanged = { descriptionState = it },
            onNext = { localFocusManager.moveFocus(FocusDirection.Next) },
        )
        PriceOption(
            price = priceState,
            onPriceChanged = { priceState = it },
            priceInputError = priceInputError.value,
            onNext = { localFocusManager.moveFocus(FocusDirection.Next) },
        )
        RecurrenceOption(
            everyXRecurrence = everyXRecurrenceState,
            onEveryXRecurrenceChanged = { everyXRecurrenceState = it },
            everyXRecurrenceInputError = everyXRecurrenceInputError.value,
            selectedRecurrence = selectedRecurrence,
            onSelectRecurrence = { selectedRecurrence = it },
            onNext = { localFocusManager.clearFocus() },
        )
        FirstPaymentOption(
            date = firstPaymentDate,
            onDateSelected = { firstPaymentDate = it },
        )
        LocationOption(
            location = locationState.text,
            onLocationSelected = { location -> locationState = TextFieldValue(location) },
            onLocationChanged = { locationState = it },
            locationInputError = false,
            onNext = { localFocusManager.clearFocus() },
        )
        CategoryOption(
            categories = categories,
            selectedCategory = selectedCategory,
            onCategorySelected = { newCategory ->
                selectedCategory = newCategory
            }
        )
        ReminderOption(
            reminder = reminderState,
            onReminderToggled = { reminderState = it },
        )
        Row(
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            if (currentData != null) {
                OutlinedButton(
                    onClick = {
                        onDeletePayment?.invoke(currentData)
                    },
                    colors =
                    ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error,
                    ),
                    modifier =
                    Modifier
                        .weight(1f)
                ) {
                    Text(
                        text = stringResource(R.string.edit_payment_button_delete),
                    )
                }
            }
            Button(
                onClick = {
                    onConfirmClicked(
                        nameInputError,
                        priceInputError,
                        everyXRecurrenceInputError,
                        nameState,
                        descriptionState,
                        priceState,
                        everyXRecurrenceState,
                        selectedRecurrence,
                        firstPaymentDate,
                        nextPaymentRemainingDays,
                        nextPaymentDate,
                        locationState,
                        selectedCategory,
                        reminderState,
                        onUpdatePayment,
                        currentData,
                    )
                },
                modifier =
                Modifier
                    .weight(1f)
            ) {
                Text(
                    text = confirmButtonString,
                    modifier = Modifier.padding(vertical = 4.dp),
                )
            }
        }
    }
}

private fun onConfirmClicked(
    nameInputError: MutableState<Boolean>,
    priceInputError: MutableState<Boolean>,
    everyXRecurrenceInputError: MutableState<Boolean>,
    nameState: TextFieldValue,
    descriptionState: TextFieldValue,
    priceState: TextFieldValue,
    everyXRecurrenceState: TextFieldValue,
    selectedRecurrence: RecurrenceEnum,
    firstPayment: Long,
    nextPaymentRemainingDays: MutableState<Int>,
    nextPaymentDate: MutableState<String>,
    locationState: TextFieldValue,
    selectedCategory: String?,
    reminderState: Boolean,
    onUpdatePayment: (RecurringPaymentData) -> Unit,
    currentData: RecurringPaymentData?
) {
    nameInputError.value = false
    priceInputError.value = false
    everyXRecurrenceInputError.value = false

    val name = nameState.text
    val description = descriptionState.text
    val price =
        priceState.text.toFloatIgnoreSeparator()
    val everyXRecurrence = everyXRecurrenceState.text.toIntOrNull()
        ?: 1
    val nextPaymentRemainingDaysValue =
        nextPaymentRemainingDays.value
    val nextPaymentDateValue = nextPaymentDate.value
    val location = locationState.text
    val category = selectedCategory ?: ""


    if (verifyUserInput(
            name = name,
            onNameInputError = { nameInputError.value = true },
            price = price.toString(),
            onPriceInputError = { priceInputError.value = true },
            everyXRecurrence = everyXRecurrence.toString(),
            onEveryXRecurrenceError = { everyXRecurrenceInputError.value = true },
        )
    ) {
        onUpdatePayment(
            RecurringPaymentData(
                id = currentData?.id ?: 0,
                name = name,
                description = description,
                price = price,
                monthlyPrice = price,
                everyXRecurrence = everyXRecurrence,
                recurrence = selectedRecurrence,
                firstPayment = firstPayment,
                location = location,
                category = category,
                reminder = reminderState
            ),
        )
    }

}

private fun verifyUserInput(
    name: String,
    onNameInputError: () -> Unit,
    price: String,
    onPriceInputError: () -> Unit,
    everyXRecurrence: String,
    onEveryXRecurrenceError: () -> Unit,
): Boolean {
    var everythingCorrect = true
    if (!isNameValid(name)) {
        onNameInputError()
        everythingCorrect = false
    }
    if (!isPriceValid(price)) {
        onPriceInputError()
        everythingCorrect = false
    }
    if (!isEveryXRecurrenceValid(everyXRecurrence)) {
        onEveryXRecurrenceError()
        everythingCorrect = false
    }
    return everythingCorrect
}

private fun isNameValid(name: String): Boolean {
    return name.isNotBlank()
}

private fun isPriceValid(price: String): Boolean {
    val priceConverted = price.replace(",", ".")
    return priceConverted.toFloatOrNull() != null
}

private fun isEveryXRecurrenceValid(everyXRecurrence: String): Boolean {
    return everyXRecurrence.isBlank() || everyXRecurrence.toIntOrNull() != null
}
