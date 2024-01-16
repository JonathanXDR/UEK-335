package com.jonathan_russ.expense_tracker.ui.editpayment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.jonathan_russ.expense_tracker.R

@Composable
fun NameOption(
    name: TextFieldValue,
    onNameChanged: (TextFieldValue) -> Unit,
    nameInputError: Boolean,
    onNext: KeyboardActionScope.() -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.edit_payment_name),
            style = MaterialTheme.typography.bodyLarge,
        )
        PaymentTextField(
            value = name,
            onValueChange = onNameChanged,
            placeholder = stringResource(R.string.edit_payment_name_placeholder),
            keyboardActions =
            KeyboardActions(onNext = onNext),
            singleLine = true,
            isError = nameInputError,
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
        )
    }
}
