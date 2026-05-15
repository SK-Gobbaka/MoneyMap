package com.example.moneymap.ui.reports

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.moneymap.ui.charts.AnalyticsDonutChart
import com.example.moneymap.ui.charts.InsightsExpenseBarChart
import com.example.moneymap.ui.theme.ChartAmber
import com.example.moneymap.ui.theme.ChartCyan
import com.example.moneymap.ui.theme.ChartEmerald
import com.example.moneymap.ui.theme.ChartIndigo
import com.example.moneymap.ui.theme.ChartPink
import com.example.moneymap.ui.theme.ChartRed
import com.example.moneymap.ui.theme.ChartViolet
import com.example.moneymap.ui.theme.Gray100
import com.example.moneymap.ui.theme.Gray400
import com.example.moneymap.ui.theme.Gray500
import com.example.moneymap.ui.theme.Indigo600
import com.example.moneymap.ui.theme.Red50
import com.example.moneymap.ui.theme.Red500
import com.example.moneymap.ui.theme.White
import com.example.moneymap.util.MoneyFormat

private val insightPalette = listOf(
    ChartIndigo, ChartEmerald, ChartAmber, ChartRed, ChartViolet, ChartCyan, ChartPink,
)

@Composable
fun ReportsScreen(viewModel: ReportsViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 0.dp,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
            ) {
                Text(
                    "Insights",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Your financial behavior",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Gray500,
                    fontWeight = FontWeight.Medium,
                )
            }
        }

        Column(
            Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            state.topCategory?.let { (name, value) ->
                SmartInsightCard(name = name, amount = value.toDouble())
            }

            Surface(
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, Gray100),
                shadowElevation = 1.dp,
            ) {
                Column(Modifier.padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PieChart, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
                        Spacer(Modifier.size(10.dp))
                        Text(
                            "Expense Breakdown",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 16.sp,
                        )
                    }
                    Spacer(Modifier.height(20.dp))
                    if (state.expenseByCategory.isEmpty()) {
                        Text(
                            "No expenses recorded yet.",
                            color = Gray500,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    } else {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                        ) {
                            AnalyticsDonutChart(
                                slices = state.expenseByCategory,
                                modifier = Modifier.fillMaxSize(),
                            )
                            Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "TOTAL",
                                    color = Gray400,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.2.sp,
                                )
                                Text(
                                    MoneyFormat.format(state.totalExpense),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                        Spacer(Modifier.height(20.dp))
                        state.expenseByCategory.take(4).forEachIndexed { idx, cat ->
                            val pct =
                                if (state.totalExpense > 0) {
                                    ((cat.second.toDouble() / state.totalExpense) * 100).toInt().coerceIn(0, 100)
                                } else {
                                    0
                                }
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                val dotColor = insightPalette[idx % insightPalette.size]
                                Box(
                                    Modifier
                                        .size(10.dp)
                                        .clip(RoundedCornerShape(99.dp))
                                        .background(dotColor),
                                )
                                Spacer(Modifier.size(10.dp))
                                Text(cat.first, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                                Text(MoneyFormat.format(cat.second.toDouble()), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Spacer(Modifier.size(8.dp))
                                Text(
                                    "${pct}%",
                                    color = Gray400,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp,
                                )
                            }
                        }
                    }
                }
            }

            Surface(
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, Gray100),
                shadowElevation = 1.dp,
            ) {
                Column(Modifier.padding(24.dp)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            "Spending by Day",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 16.sp,
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Red50)
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                        ) {
                            Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Red500, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.size(4.dp))
                            Text("High", color = Red500, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    if (state.expenseByDay.isEmpty()) {
                        Text("Not enough data for chart.", color = Gray500)
                    } else {
                        InsightsExpenseBarChart(
                            bars = state.expenseByDay,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                        )
                    }
                }
            }
            Spacer(Modifier.height(80.dp))
        }
    }
}

@Composable
private fun SmartInsightCard(name: String, amount: Double) {
    val indigoHint = androidx.compose.ui.graphics.Color(0xFFE0E7FF)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Indigo600),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(White.copy(alpha = 0.14f), White.copy(alpha = 0f)),
                    ),
                ),
        )
        Row(
            Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.Lightbulb,
                    contentDescription = null,
                    tint = androidx.compose.ui.graphics.Color(0xFFFDE047),
                    modifier = Modifier.size(26.dp),
                )
            }
            Column {
                Text(
                    "Smart Insight",
                    color = White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "You spent the most on $name this period, totaling ${MoneyFormat.format(amount)}. Consider setting a budget for this category.",
                    color = indigoHint,
                    lineHeight = 20.sp,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}
