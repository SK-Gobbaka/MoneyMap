package com.example.moneymap.ui.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.NorthEast
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SouthEast
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.moneymap.data.TransactionRecord
import com.example.moneymap.data.TransactionType
import com.example.moneymap.ui.theme.Emerald50
import com.example.moneymap.ui.theme.Emerald600
import com.example.moneymap.ui.theme.Gray100
import com.example.moneymap.ui.theme.Gray400
import com.example.moneymap.ui.theme.Gray500
import com.example.moneymap.ui.theme.Gray900
import com.example.moneymap.ui.theme.Indigo600
import com.example.moneymap.ui.theme.Red50
import com.example.moneymap.ui.theme.Red500
import com.example.moneymap.ui.theme.White
import com.example.moneymap.util.MoneyFormat
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.BorderStroke
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    viewModel: TransactionsViewModel,
    onEditTransaction: (Long) -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var pendingDelete by remember { mutableStateOf<TransactionRecord?>(null) }
    var sortMenuExpanded by remember { mutableStateOf(false) }

    val dateHeaderFormat = remember { DateTimeFormatter.ofPattern("EEE, MMM d") }
    val grouped = remember(state.items, dateHeaderFormat) {
        state.items
            .groupBy { it.dateEpochDay }
            .entries
            .sortedByDescending { it.key }
            .map { (epoch, list) ->
                val label = java.time.LocalDate.ofEpochDay(epoch).format(dateHeaderFormat)
                label to list
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 0.dp, modifier = Modifier.fillMaxWidth()) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp),
            ) {
                Spacer(Modifier.height(8.dp))
                Text(
                    "Activity",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Gray900,
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = viewModel::setSearch,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("Search transactions...", color = Gray400, fontSize = 14.sp)
                    },
                    trailingIcon = {
                        Box {
                            IconButton(onClick = { sortMenuExpanded = true }) {
                                Icon(
                                    Icons.Outlined.FilterList,
                                    contentDescription = "Sort",
                                    tint = if (state.sort != TransactionSort.LATEST) Indigo600 else Gray400,
                                )
                            }
                            DropdownMenu(
                                expanded = sortMenuExpanded,
                                onDismissRequest = { sortMenuExpanded = false },
                            ) {
                                TransactionSort.entries.forEach { sortOption ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                when (sortOption) {
                                                    TransactionSort.LATEST -> "Latest First"
                                                    TransactionSort.OLDEST -> "Oldest First"
                                                    TransactionSort.HIGHEST -> "Highest Amount"
                                                    TransactionSort.LOWEST -> "Lowest Amount"
                                                },
                                            )
                                        },
                                        onClick = {
                                            viewModel.setSort(sortOption)
                                            sortMenuExpanded = false
                                        },
                                        trailingIcon = {
                                            if (state.sort == sortOption) {
                                                Icon(
                                                    Icons.Default.Check,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp),
                                                )
                                            }
                                        },
                                    )
                                }
                            }
                        }
                    },
                    leadingIcon = {
                        Icon(Icons.Outlined.Search, contentDescription = null, tint = Gray400)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Indigo600.copy(alpha = 0.35f),
                        unfocusedBorderColor = Gray100,
                        cursorColor = Indigo600,
                        focusedContainerColor = Gray100,
                        unfocusedContainerColor = Gray100,
                    ),
                )
                Spacer(Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    FilterChip(
                        selected = state.typeFilter == TransactionTypeFilter.ALL,
                        onClick = { viewModel.setTypeFilter(TransactionTypeFilter.ALL) },
                        label = { Text("All") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Gray900,
                            selectedLabelColor = White,
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = Gray100,
                            selectedBorderColor = Gray900,
                            enabled = true,
                            selected = state.typeFilter == TransactionTypeFilter.ALL
                        )
                    )
                    FilterChip(
                        selected = state.typeFilter == TransactionTypeFilter.INCOME,
                        onClick = { viewModel.setTypeFilter(TransactionTypeFilter.INCOME) },
                        label = { Text("Income") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Gray900,
                            selectedLabelColor = White,
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = Gray100,
                            selectedBorderColor = Gray900,
                            enabled = true,
                            selected = state.typeFilter == TransactionTypeFilter.INCOME
                        )
                    )
                    FilterChip(
                        selected = state.typeFilter == TransactionTypeFilter.EXPENSE,
                        onClick = { viewModel.setTypeFilter(TransactionTypeFilter.EXPENSE) },
                        label = { Text("Expense") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Gray900,
                            selectedLabelColor = White,
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = Gray100,
                            selectedBorderColor = Gray900,
                            enabled = true,
                            selected = state.typeFilter == TransactionTypeFilter.EXPENSE
                        )
                    )
                }
                Spacer(Modifier.height(16.dp))
            }
        }

        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp),
        ) {
            if (grouped.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Gray100),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Outlined.Search, contentDescription = null, tint = Gray400)
                        }
                        Spacer(Modifier.height(14.dp))
                        Text("No transactions found", fontWeight = FontWeight.SemiBold, color = Gray900)
                        Spacer(Modifier.height(4.dp))
                        Text("Try changing your filters or search term.", fontSize = 14.sp, color = Gray500)
                    }
                }
            } else {
                items(grouped, key = { it.first }) { (dateLabel, txs) ->
                    Column(Modifier.fillMaxWidth()) {
                        Text(
                            dateLabel.uppercase(),
                            fontWeight = FontWeight.Bold,
                            color = Gray400,
                            letterSpacing = 1.8.sp,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(start = 6.dp),
                        )
                        Spacer(Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(26.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, Gray100),
                            shadowElevation = 1.dp,
                        ) {
                            Column(Modifier.padding(vertical = 4.dp)) {
                                txs.forEachIndexed { idx, tx ->
                                    ActivityTxRow(
                                        record = tx,
                                        onClick = { onEditTransaction(tx.id) },
                                        onDelete = { pendingDelete = tx },
                                        showDivider = idx != txs.lastIndex,
                                    )
                                }
                            }
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(96.dp)) }
        }
    }

    pendingDelete?.let { record ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("Delete transaction?") },
            text = { Text("This cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.delete(record)
                        pendingDelete = null
                    },
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) { Text("Cancel") }
            },
        )
    }
}

@Composable
private fun ActivityTxRow(
    record: TransactionRecord,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    showDivider: Boolean,
) {
    val isIncome = record.type == TransactionType.INCOME
    val title = record.category

    Column(Modifier.padding(horizontal = 6.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable(onClick = onClick)
                .padding(horizontal = 10.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (isIncome) Emerald50 else Red50),
                contentAlignment = Alignment.Center,
            ) {
                if (isIncome) {
                    Icon(Icons.Outlined.SouthEast, contentDescription = null, tint = Emerald600)
                } else {
                    Icon(Icons.Outlined.NorthEast, contentDescription = null, tint = Red500)
                }
            }
            Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                Text(title, fontWeight = FontWeight.SemiBold, color = Gray900, maxLines = 1)
                if (record.notes.isNotBlank()) {
                    Text(
                        record.notes,
                        fontSize = 12.sp,
                        color = Gray500,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 2.dp),
                        maxLines = 1,
                    )
                }
            }
            val amt = MoneyFormat.format(record.amount)
            Text(
                text = if (isIncome) "+$amt" else "−$amt",
                fontWeight = FontWeight.Bold,
                color = if (isIncome) Emerald600 else Gray900,
            )
            TextButton(onClick = onDelete, modifier = Modifier.padding(start = 4.dp)) {
                Text("Delete", color = Red500, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            }
        }
        if (showDivider) {
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.background),
            )
        }
    }
}
