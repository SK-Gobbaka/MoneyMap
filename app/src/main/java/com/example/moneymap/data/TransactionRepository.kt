package com.example.moneymap.data

import com.example.moneymap.data.db.TransactionDao
import com.example.moneymap.data.db.TransactionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionRepository(private val dao: TransactionDao) {
    val transactions: Flow<List<TransactionRecord>> =
        dao.observeAll().map { list -> list.map(TransactionRecord::fromEntity) }

    val totalIncome: Flow<Double> = dao.observeTotalIncome()
    val totalExpense: Flow<Double> = dao.observeTotalExpense()

    suspend fun getById(id: Long): TransactionRecord? =
        dao.getById(id)?.let(TransactionRecord::fromEntity)

    suspend fun save(record: TransactionRecord): Long {
        val entity = record.toEntity()
        return if (record.id == 0L) dao.insert(entity) else {
            dao.update(entity)
            record.id
        }
    }

    suspend fun delete(record: TransactionRecord) {
        dao.delete(record.toEntity())
    }

    suspend fun deleteById(id: Long) {
        val e = dao.getById(id) ?: return
        dao.delete(e)
    }
}
