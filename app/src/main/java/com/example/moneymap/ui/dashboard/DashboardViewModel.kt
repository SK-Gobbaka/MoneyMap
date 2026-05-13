package com.example.moneymap.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymap.data.InsightGenerator
import com.example.moneymap.data.TransactionRecord
import com.example.moneymap.data.TransactionRepository
import com.example.moneymap.data.TransactionType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class DashboardUiState(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balance: Double = 0.0,
    val recent: List<TransactionRecord> = emptyList(),
    val insights: List<String> = emptyList(),
    val expenseByCategory: List<Pair<String, Float>> = emptyList(),
)

class DashboardViewModel(
    repository: TransactionRepository,
) : ViewModel() {
    val uiState: StateFlow<DashboardUiState> = combine(
        repository.transactions,
        repository.totalIncome,
        repository.totalExpense,
    ) { transactions, income, expense ->
        val balance = income - expense
        val recent = transactions.take(4)
        val insights = InsightGenerator.generate(transactions)
        val expenseByCategory = transactions
            .filter { it.type == TransactionType.EXPENSE }
            .groupBy { it.category }
            .mapValues { (_, v) -> v.sumOf { it.amount }.toFloat() }
            .entries
            .sortedByDescending { it.value }
            .take(8)
            .map { it.key to it.value }
        DashboardUiState(
            totalIncome = income,
            totalExpense = expense,
            balance = balance,
            recent = recent,
            insights = insights,
            expenseByCategory = expenseByCategory,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        DashboardUiState(),
    )
}
