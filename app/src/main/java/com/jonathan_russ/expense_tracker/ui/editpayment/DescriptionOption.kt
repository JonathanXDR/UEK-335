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
fun DescriptionOption(
    description: TextFieldValue,
    onDescriptionChanged: (TextFieldValue) -> Unit,
    onNext: KeyboardActionScope.() -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.edit_payment_description),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 8.dp),
        )
        PaymentTextField(
            value = description,
            onValueChange = onDescriptionChanged,
            placeholder = stringResource(R.string.edit_payment_description_placeholder),
            keyboardActions =
            KeyboardActions(onNext = onNext),
            maxLines = 2,
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
        )
    }
}
