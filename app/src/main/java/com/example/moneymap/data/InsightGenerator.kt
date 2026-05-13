package com.example.moneymap.data

import java.time.LocalDate
import java.time.temporal.ChronoUnit

object InsightGenerator {
    fun generate(transactions: List<TransactionRecord>, today: LocalDate = LocalDate.now()): List<String> {
        if (transactions.isEmpty()) {
            return listOf("Add a few transactions to unlock spending insights.")
        }
        val insights = mutableListOf<String>()
        val expenses = transactions.filter { it.type == TransactionType.EXPENSE }
        val income = transactions.filter { it.type == TransactionType.INCOME }

        val thisMonthStart = today.withDayOfMonth(1)
        val lastMonthStart = thisMonthStart.minusMonths(1)
        val lastMonthEnd = thisMonthStart.minusDays(1)

        val thisMonthExpense = sumInRange(expenses, thisMonthStart, today)
        val lastMonthExpense = sumInRange(expenses, lastMonthStart, lastMonthEnd)

        if (lastMonthExpense > 0 && thisMonthExpense > 0) {
            val delta = (thisMonthExpense - lastMonthExpense) / lastMonthExpense * 100
            when {
                delta > 5 -> insights += "Spending is up about ${delta.toInt()}% vs last month so far."
                delta < -5 -> insights += "You spent less than last month — nice discipline."
            }
        }

        val byCategory = expenses
            .filter { !it.date.isBefore(today.minusDays(30)) }
            .groupBy { it.category }
            .mapValues { (_, v) -> v.sumOf { it.amount } }
        val top = byCategory.maxByOrNull { it.value }
        if (top != null && top.value > 0) {
            insights += "${top.key} is your top spending category in the last 30 days."
        }

        val weekStart = today.minusDays(6)
        val prevWeekStart = today.minusDays(13)
        val prevWeekEnd = today.minusDays(7)
        val thisWeek = sumInRange(expenses, weekStart, today)
        val prevWeek = sumInRange(expenses, prevWeekStart, prevWeekEnd)
        if (prevWeek > 0 && thisWeek > 0) {
            val pct = (thisWeek - prevWeek) / prevWeek * 100
            if (pct > 15) {
                insights += "This week's expenses are notably higher than the week before."
            } else if (pct < -15) {
                insights += "You spent less this week than the previous week."
            }
        }

        val avgDaily = if (expenses.isNotEmpty()) {
            val oldest = expenses.minOf { it.dateEpochDay }
            val days = ChronoUnit.DAYS.between(LocalDate.ofEpochDay(oldest), today).coerceAtLeast(1)
            expenses.sumOf { it.amount } / days
        } else 0.0

        val todayExpense = expenses.filter { it.dateEpochDay == today.toEpochDay() }.sumOf { it.amount }
        if (avgDaily > 0 && todayExpense > avgDaily * 1.5) {
            insights += "Today's spending is above your typical daily average."
        }

        val totalIn = income.sumOf { it.amount }
        val totalOut = expenses.sumOf { it.amount }
        if (totalIn > 0 && totalOut > totalIn) {
            insights += "All-time expenses currently exceed recorded income — review your budget."
        }

        if (insights.isEmpty()) {
            insights += "Keep logging transactions for richer trend insights."
        }
        return insights.distinct()
    }

    private fun sumInRange(
        expenses: List<TransactionRecord>,
        start: LocalDate,
        end: LocalDate,
    ): Double {
        val s = start.toEpochDay()
        val e = end.toEpochDay()
        return expenses.filter { it.dateEpochDay in s..e }.sumOf { it.amount }
    }
}
