package com.example.moneymap.ui.add

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.moneymap.data.CategoryCatalog
import com.example.moneymap.data.TransactionType
import com.example.moneymap.ui.theme.Gray100
import com.example.moneymap.ui.theme.Gray900
import com.example.moneymap.ui.theme.Indigo600
import com.example.moneymap.ui.theme.White
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    viewModel: AddTransactionViewModel,
    isEdit: Boolean,
    onSaved: () -> Unit,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showDatePicker by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var subExpanded by remember { mutableStateOf(false) }

    if (!state.isLoaded) {
        Text("Loading…", modifier = Modifier.padding(24.dp))
        return
    }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Indigo600.copy(alpha = 0.55f),
        unfocusedBorderColor = Gray100,
        cursorColor = Indigo600,
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(horizontal = 22.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack, modifier = Modifier.padding(end = 8.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
            Text(
                if (isEdit) "Edit Transaction" else "New Transaction",
                fontSize = if (isEdit) 22.sp else 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = state.type == TransactionType.EXPENSE,
                onClick = { viewModel.setType(TransactionType.EXPENSE) },
                label = { Text("Expense") },
            )
            FilterChip(
                selected = state.type == TransactionType.INCOME,
                onClick = { viewModel.setType(TransactionType.INCOME) },
                label = { Text("Income") },
            )
        }
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = state.amountText,
            onValueChange = viewModel::setAmount,
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(18.dp),
            colors = fieldColors,
        )
        Spacer(Modifier.height(8.dp))
        if (state.type == TransactionType.EXPENSE) {
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = it },
                modifier = Modifier.fillMaxWidth(),
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = state.category,
                    onValueChange = {},
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = fieldColors,
                )
                DropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                    CategoryCatalog.expenseCategories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text("${cat.emoji} ${cat.name}") },
                            onClick = {
                                viewModel.setCategory(cat.name)
                                categoryExpanded = false
                            },
                        )
                    }
                }
            }
        } else {
            OutlinedTextField(
                readOnly = true,
                value = CategoryCatalog.incomeCategoryName,
                onValueChange = {},
                label = { Text("Category") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = fieldColors,
            )
        }
        Spacer(Modifier.height(8.dp))
        val subs = if (state.type == TransactionType.INCOME) {
            CategoryCatalog.incomeSubcategories
        } else {
            CategoryCatalog.subcategoriesFor(state.category)
        }
        ExposedDropdownMenuBox(
            expanded = subExpanded,
            onExpandedChange = { subExpanded = it },
            modifier = Modifier.fillMaxWidth(),
        ) {
            OutlinedTextField(
                readOnly = true,
                value = state.subcategory,
                onValueChange = {},
                label = { Text("Subcategory") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = subExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = fieldColors,
            )
            DropdownMenu(expanded = subExpanded, onDismissRequest = { subExpanded = false }) {
                subs.forEach { sub ->
                    DropdownMenuItem(
                        text = { Text(sub) },
                        onClick = {
                            viewModel.setSubcategory(sub)
                            subExpanded = false
                        },
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth()) {
            Text("Date: ${state.date}")
        }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = state.notes,
            onValueChange = viewModel::setNotes,
            label = { Text("Note (optional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            shape = RoundedCornerShape(18.dp),
            colors = fieldColors,
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { viewModel.save(onSaved) },
            enabled = !state.isSaving,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Indigo600,
                contentColor = White,
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 6.dp),
        ) {
            Text(
                if (state.isSaving) "Saving…" else "Save Transaction",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            )
        }
        if (isEdit) {
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = { viewModel.delete(onSaved) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Delete")
            }
        }
        Spacer(Modifier.height(32.dp))
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = state.date.atStartOfDay(ZoneId.systemDefault())
                .toInstant().toEpochMilli(),
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = datePickerState.selectedDateMillis
                        if (millis != null) {
                            val picked = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                            viewModel.setDate(picked)
                        }
                        showDatePicker = false
                    },
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    state.error?.let { msg ->
        AlertDialog(
            onDismissRequest = viewModel::consumeError,
            title = { Text("Could not save") },
            text = { Text(msg) },
            confirmButton = {
                TextButton(onClick = viewModel::consumeError) { Text("OK") }
            },
        )
    }
}
