package com.example.moneymap.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.moneymap.data.TransactionRepository
import com.example.moneymap.preferences.ThemePreferences
import com.example.moneymap.ui.add.AddTransactionViewModel
import com.example.moneymap.ui.dashboard.DashboardViewModel
import com.example.moneymap.ui.reports.ReportsViewModel
import com.example.moneymap.ui.settings.SettingsViewModel
import com.example.moneymap.ui.transactions.TransactionsViewModel

@Suppress("UNCHECKED_CAST")
class MoneyMapViewModelFactory(
    private val repository: TransactionRepository,
    private val themePreferences: ThemePreferences,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(DashboardViewModel::class.java) ->
                DashboardViewModel(repository) as T
            modelClass.isAssignableFrom(TransactionsViewModel::class.java) ->
                TransactionsViewModel(repository) as T
            modelClass.isAssignableFrom(ReportsViewModel::class.java) ->
                ReportsViewModel(repository) as T
            modelClass.isAssignableFrom(SettingsViewModel::class.java) ->
                SettingsViewModel(themePreferences) as T
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}

fun addTransactionViewModelFactory(
    repository: TransactionRepository,
    editId: Long?,
): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddTransactionViewModel::class.java)) {
            return AddTransactionViewModel(repository, editId) as T
        }
        throw IllegalArgumentException()
    }
}
