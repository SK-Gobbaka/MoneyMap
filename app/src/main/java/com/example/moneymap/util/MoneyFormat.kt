package com.example.moneymap.util

import java.text.NumberFormat
import java.util.Locale

object MoneyFormat {
    private val currency: NumberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())

    fun format(amount: Double): String = currency.format(amount)
}
