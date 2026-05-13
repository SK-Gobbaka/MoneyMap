package com.example.moneymap.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymap.data.TransactionRecord
import com.example.moneymap.data.TransactionRepository
import com.example.moneymap.data.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class TransactionSort {
    LATEST,
    OLDEST,
    HIGHEST,
    LOWEST,
}

enum class TransactionTypeFilter {
    ALL,
    INCOME,
    EXPENSE,
}

data class TransactionsUiState(
    val items: List<TransactionRecord> = emptyList(),
    val sort: TransactionSort = TransactionSort.LATEST,
    val typeFilter: TransactionTypeFilter = TransactionTypeFilter.ALL,
    val categoryFilter: String? = null,
    val searchQuery: String = "",
)

class TransactionsViewModel(
    private val repository: TransactionRepository,
) : ViewModel() {
    private val sort = MutableStateFlow(TransactionSort.LATEST)
    private val typeFilter = MutableStateFlow(TransactionTypeFilter.ALL)
    private val categoryFilter = MutableStateFlow<String?>(null)
    private val searchQuery = MutableStateFlow("")

    val uiState: StateFlow<TransactionsUiState> = combine(
        repository.transactions,
        sort,
        typeFilter,
        categoryFilter,
        searchQuery,
    ) { transactions, s, tf, cat, q ->
        var list = transactions.asSequence()
        when (tf) {
            TransactionTypeFilter.ALL -> Unit
            TransactionTypeFilter.INCOME -> list = list.filter { it.type == TransactionType.INCOME }
            TransactionTypeFilter.EXPENSE -> list = list.filter { it.type == TransactionType.EXPENSE }
        }
        if (cat != null) {
            list = list.filter { it.category == cat }
        }
        val qTrim = q.trim().lowercase()
        if (qTrim.isNotEmpty()) {
            list = list.filter {
                it.notes.lowercase().contains(qTrim) ||
                    it.category.lowercase().contains(qTrim) ||
                    it.subcategory.lowercase().contains(qTrim)
            }
        }
        var sorted = list.toList()
        sorted = when (s) {
            TransactionSort.LATEST -> sorted.sortedWith(
                compareByDescending<TransactionRecord> { it.dateEpochDay }.thenByDescending { it.id },
            )
            TransactionSort.OLDEST -> sorted.sortedWith(
                compareBy<TransactionRecord> { it.dateEpochDay }.thenBy { it.id },
            )
            TransactionSort.HIGHEST -> sorted.sortedByDescending { it.amount }
            TransactionSort.LOWEST -> sorted.sortedBy { it.amount }
        }
        TransactionsUiState(
            items = sorted,
            sort = s,
            typeFilter = tf,
            categoryFilter = cat,
            searchQuery = q,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        TransactionsUiState(),
    )

    fun setSort(value: TransactionSort) {
        sort.value = value
    }

    fun setTypeFilter(value: TransactionTypeFilter) {
        typeFilter.value = value
    }

    fun setCategoryFilter(value: String?) {
        categoryFilter.value = value
    }

    fun setSearch(value: String) {
        searchQuery.value = value
    }

    fun delete(record: TransactionRecord) {
        viewModelScope.launch { repository.delete(record) }
    }
}
