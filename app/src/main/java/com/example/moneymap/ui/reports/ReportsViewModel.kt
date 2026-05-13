package com.example.moneymap.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymap.data.TransactionRepository
import com.example.moneymap.data.TransactionType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.format.DateTimeFormatter
import java.util.Locale

data class ReportsUiState(
    val totalExpense: Double = 0.0,
    val totalIncome: Double = 0.0,
    val expenseByCategory: List<Pair<String, Float>> = emptyList(),
    /** Label (e.g. Jan 5) to amount for expense bar chart */
    val expenseByDay: List<Pair<String, Float>> = emptyList(),
    val topCategory: Pair<String, Float>? = null,
)

class ReportsViewModel(
    repository: TransactionRepository,
) : ViewModel() {
    private val dayLabelFormat = DateTimeFormatter.ofPattern("MMM d", Locale.getDefault())

    val uiState: StateFlow<ReportsUiState> =
        repository.transactions.map { transactions ->
            val income = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
            val expenses = transactions.filter { it.type == TransactionType.EXPENSE }
            val totalExpense = expenses.sumOf { it.amount }
            val byCategory = expenses
                .groupBy { it.category }
                .mapValues { (_, v) -> v.sumOf { it.amount }.toFloat() }
                .entries
                .sortedByDescending { it.value }
                .map { it.key to it.value }
            val top = byCategory.firstOrNull()
            val byDayMap = expenses.groupBy { it.dateEpochDay }.mapValues { (_, v) ->
                v.sumOf { it.amount }.toFloat()
            }
            val expenseByDay = byDayMap.entries
                .sortedBy { it.key }
                .map { (epoch, amount) ->
                    val d = java.time.LocalDate.ofEpochDay(epoch)
                    d.format(dayLabelFormat) to amount
                }
            ReportsUiState(
                totalExpense = totalExpense,
                totalIncome = income,
                expenseByCategory = byCategory,
                expenseByDay = expenseByDay,
                topCategory = top,
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            ReportsUiState(),
        )
}
