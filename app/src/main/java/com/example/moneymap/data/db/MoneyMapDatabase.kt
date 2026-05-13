package com.example.moneymap.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TransactionEntity::class], version = 1, exportSchema = false)
abstract class MoneyMapDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var instance: MoneyMapDatabase? = null

        fun getDatabase(context: Context): MoneyMapDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    MoneyMapDatabase::class.java,
                    "moneymap.db",
                ).build().also { instance = it }
            }
    }
}
