package com.example.moneymap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import com.example.moneymap.data.TransactionRepository
import com.example.moneymap.data.db.MoneyMapDatabase
import com.example.moneymap.preferences.ThemeMode
import com.example.moneymap.preferences.ThemePreferences
import com.example.moneymap.ui.MoneyMapApp
import com.example.moneymap.ui.theme.MoneyMapTheme
import androidx.lifecycle.compose.collectAsStateWithLifecycle

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val database = MoneyMapDatabase.getDatabase(applicationContext)
        val repository = TransactionRepository(database.transactionDao())
        val themePreferences = ThemePreferences(applicationContext)
        setContent {
            val themeMode by themePreferences.themeMode.collectAsStateWithLifecycle(
                initialValue = ThemeMode.FOLLOW_SYSTEM,
            )
            val darkTheme = when (themeMode) {
                ThemeMode.FOLLOW_SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }
            MoneyMapTheme(darkTheme = darkTheme, dynamicColor = false) {
                MoneyMapApp(repository, themePreferences)
            }
        }
    }
}
