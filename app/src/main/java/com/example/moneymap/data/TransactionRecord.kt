package com.example.moneymap.data

import com.example.moneymap.data.db.TransactionEntity
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

data class TransactionRecord(
    val id: Long,
    val type: TransactionType,
    val amount: Double,
    val category: String,
    val subcategory: String,
    val notes: String,
    val dateEpochDay: Long,
) {
    val date: LocalDate get() = LocalDate.ofEpochDay(dateEpochDay)

    fun formattedDate(): String =
        date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))

    fun toEntity(): TransactionEntity = TransactionEntity(
        id = id,
        type = type.storageKey,
        amount = amount,
        category = category,
        subcategory = subcategory,
        notes = notes,
        dateEpochDay = dateEpochDay,
    )

    companion object {
        fun fromEntity(e: TransactionEntity): TransactionRecord = TransactionRecord(
            id = e.id,
            type = TransactionType.fromStorage(e.type),
            amount = e.amount,
            category = e.category,
            subcategory = e.subcategory,
            notes = e.notes,
            dateEpochDay = e.dateEpochDay,
        )
    }
}
