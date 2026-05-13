package com.example.moneymap.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    val amount: Double,
    val category: String,
    val subcategory: String,
    val notes: String,
    /** [java.time.LocalDate.toEpochDay] */
    val dateEpochDay: Long,
)
