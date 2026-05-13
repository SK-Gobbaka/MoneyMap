package com.example.moneymap.data

enum class TransactionType(val storageKey: String) {
    INCOME("INCOME"),
    EXPENSE("EXPENSE");

    companion object {
        fun fromStorage(key: String): TransactionType =
            entries.firstOrNull { it.storageKey == key } ?: EXPENSE
    }
}
