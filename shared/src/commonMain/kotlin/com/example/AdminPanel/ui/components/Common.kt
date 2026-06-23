package com.example.AdminPanel.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.material.icons.filled.DateRange

@Composable
fun DetailSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(bottom = 12.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            content()
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Text(value, fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDropdown(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        // The read-only text field acting as the dropdown box
        OutlinedTextField(
            value = selectedOption.ifEmpty { label },
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            ),
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                .fillMaxWidth()
        )

        // The popup menu containing the selectable list items
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeRangeDropdown(
    selectedLabel: String,
    onPresetSelected: (String) -> Unit,
    onCustomRangeSelected: (Long?, Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var showDatePickerModal by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedLabel.ifEmpty { "Select Time Range" },
            onValueChange = {},
            readOnly = true,
            leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            ),
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            val presets = listOf("All Time")

            presets.forEach { preset ->
                DropdownMenuItem(
                    text = { Text(preset) },
                    onClick = {
                        onPresetSelected(preset)
                        expanded = false
                    }
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            // Custom selection option that triggers the date picker dialog
            DropdownMenuItem(
                text = { Text("Custom Range...", color = MaterialTheme.colorScheme.primary) },
                onClick = {
                    expanded = false
                    showDatePickerModal = true
                }
            )
        }
    }

    // Modern Material 3 Date Range Picker Dialog
    if (showDatePickerModal) {
        val dateRangePickerState = rememberDateRangePickerState()

        DatePickerDialog(
            onDismissRequest = { showDatePickerModal = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        onCustomRangeSelected(
                            dateRangePickerState.selectedStartDateMillis,
                            dateRangePickerState.selectedEndDateMillis
                        )
                        showDatePickerModal = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerModal = false }) { Text("Cancel") }
            }
        ) {
            DateRangePicker(
                state = dateRangePickerState,
                modifier = Modifier.weight(1f).padding(16.dp)
            )
        }
    }
}