package com.example.moneymap.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymap.data.CategoryCatalog
import com.example.moneymap.data.TransactionRecord
import com.example.moneymap.data.TransactionRepository
import com.example.moneymap.data.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class AddTransactionUiState(
    val amountText: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val category: String = CategoryCatalog.expenseCategories.first().name,
    val subcategory: String = CategoryCatalog.expenseCategories.first().subcategories.first(),
    val notes: String = "",
    val date: LocalDate = LocalDate.now(),
    val isSaving: Boolean = false,
    val saveDone: Boolean = false,
    val error: String? = null,
    val isLoaded: Boolean = false,
)

class AddTransactionViewModel(
    private val repository: TransactionRepository,
    private val editId: Long?,
) : ViewModel() {
    private val _state = MutableStateFlow(AddTransactionUiState())
    val state: StateFlow<AddTransactionUiState> = _state.asStateFlow()

    init {
        val id = editId
        if (id != null) {
            viewModelScope.launch {
                val existing = repository.getById(id)
                if (existing != null) {
                    _state.update {
                        it.copy(
                            amountText = formatAmount(existing.amount),
                            type = existing.type,
                            category = existing.category,
                            subcategory = existing.subcategory,
                            notes = existing.notes,
                            date = existing.date,
                            isLoaded = true,
                        )
                    }
                    syncSubcategoryForType()
                } else {
                    _state.update { it.copy(isLoaded = true, error = "Transaction not found") }
                }
            }
        } else {
            _state.update { it.copy(isLoaded = true) }
        }
    }

    fun setAmount(text: String) {
        val cleaned = text.filter { it.isDigit() || it == '.' || it == ',' }
        _state.update { it.copy(amountText = cleaned) }
    }

    fun setType(type: TransactionType) {
        _state.update { it.copy(type = type) }
        syncSubcategoryForType()
    }

    fun setCategory(name: String) {
        _state.update { it.copy(category = name) }
        syncSubcategoryForType()
    }

    fun setSubcategory(name: String) {
        _state.update { it.copy(subcategory = name) }
    }

    fun setNotes(notes: String) {
        _state.update { it.copy(notes = notes) }
    }

    fun setDate(date: LocalDate) {
        _state.update { it.copy(date = date) }
    }

    private fun syncSubcategoryForType() {
        _state.update { s ->
            when (s.type) {
                TransactionType.INCOME -> {
                    val subs = CategoryCatalog.incomeSubcategories
                    val sub = if (s.subcategory in subs) s.subcategory else subs.first()
                    s.copy(
                        category = CategoryCatalog.incomeCategoryName,
                        subcategory = sub,
                    )
                }
                TransactionType.EXPENSE -> {
                    val cat = CategoryCatalog.expenseCategories.firstOrNull { it.name == s.category }
                        ?: CategoryCatalog.expenseCategories.first()
                    val sub = if (s.subcategory in cat.subcategories) {
                        s.subcategory
                    } else {
                        cat.subcategories.first()
                    }
                    s.copy(category = cat.name, subcategory = sub)
                }
            }
        }
    }

    fun save(onSuccess: () -> Unit) {
        val s = _state.value
        val amount = parseAmount(s.amountText)
        if (amount == null || amount <= 0) {
            _state.update { it.copy(error = "Enter a valid amount") }
            return
        }
        _state.update { it.copy(isSaving = true, error = null) }
        viewModelScope.launch {
            try {
                val record = TransactionRecord(
                    id = editId ?: 0L,
                    type = s.type,
                    amount = amount,
                    category = if (s.type == TransactionType.INCOME) {
                        CategoryCatalog.incomeCategoryName
                    } else {
                        s.category
                    },
                    subcategory = s.subcategory,
                    notes = s.notes.trim(),
                    dateEpochDay = s.date.toEpochDay(),
                )
                repository.save(record)
                _state.update { it.copy(isSaving = false, saveDone = true) }
                onSuccess()
            } catch (e: Exception) {
                _state.update {
                    it.copy(isSaving = false, error = e.message ?: "Could not save")
                }
            }
        }
    }

    fun delete(onSuccess: () -> Unit) {
        val id = editId ?: return
        viewModelScope.launch {
            repository.deleteById(id)
            onSuccess()
        }
    }

    fun consumeError() {
        _state.update { it.copy(error = null) }
    }

    private fun parseAmount(text: String): Double? {
        val normalized = text.replace(',', '.').trim()
        if (normalized.isEmpty()) return null
        return normalized.toDoubleOrNull()
    }

    private fun formatAmount(value: Double): String {
        if (value % 1.0 == 0.0) return value.toInt().toString()
        return value.toString()
    }
}
