package com.jonathan_russ.expense_tracker.ui.editexpense

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryOption(
    categories: List<String>,
    onCategorySelected: (String?) -> Unit
) {
    val (selectedCategory, setSelectedCategory) = remember { mutableStateOf<String?>(null) }

    Text(text = "Category")
    LazyRow {
        items(categories) { category ->
            FilterChip(
                selected = category == selectedCategory,
                onClick = {
                    if (selectedCategory == category) {
                        setSelectedCategory(null)
                        onCategorySelected(null)
                    } else {
                        setSelectedCategory(category)
                        onCategorySelected(category)
                    }
                },
                label = { Text(category) },
                leadingIcon = if (category == selectedCategory) {
                    { Icon(imageVector = Icons.Filled.Check, contentDescription = "Selected") }
                } else null,
                colors = FilterChipDefaults.filterChipColors(),
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}