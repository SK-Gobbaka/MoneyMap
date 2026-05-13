package com.example.moneymap.ui.charts

import android.graphics.Color as AndroidColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate

private fun designPalette(): List<Int> = listOf(
    AndroidColor.parseColor("#4F46E5"),
    AndroidColor.parseColor("#10B981"),
    AndroidColor.parseColor("#F59E0B"),
    AndroidColor.parseColor("#EF4444"),
    AndroidColor.parseColor("#8B5CF6"),
    AndroidColor.parseColor("#06B6D4"),
    AndroidColor.parseColor("#EC4899"),
)

private val fallbackColors: List<Int> = buildList {
    addAll(ColorTemplate.MATERIAL_COLORS.asList())
    addAll(ColorTemplate.COLORFUL_COLORS.asList())
}

@Composable
fun ExpensePieChart(
    slices: List<Pair<String, Float>>,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            PieChart(ctx).apply {
                description.isEnabled = false
                isDrawHoleEnabled = true
                setHoleColor(AndroidColor.TRANSPARENT)
                legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                legend.isWordWrapEnabled = true
                setEntryLabelTextSize(10f)
                setUsePercentValues(false)
                setDrawEntryLabels(true)
            }
        },
        update = { chart ->
            if (slices.isEmpty()) {
                chart.data = null
                chart.invalidate()
                return@AndroidView
            }
            val entries = slices.map { (label, value) -> PieEntry(value, label) }
            val set = PieDataSet(entries, "").apply {
                colors = designPalette()
                valueTextSize = 10f
                sliceSpace = 2f
            }
            chart.data = PieData(set)
            chart.invalidate()
        },
    )
}

@Composable
fun ExpenseLineChart(
    points: List<Pair<String, Float>>,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            LineChart(ctx).apply {
                description.isEnabled = false
                axisRight.isEnabled = false
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.granularity = 1f
                xAxis.setDrawGridLines(false)
                axisLeft.setDrawGridLines(true)
                legend.isEnabled = false
                setTouchEnabled(true)
                setScaleEnabled(false)
            }
        },
        update = { chart ->
            if (points.isEmpty()) {
                chart.data = null
                chart.invalidate()
                return@AndroidView
            }
            val entries = points.mapIndexed { i, p -> Entry(i.toFloat(), p.second) }
            val labels = points.map { it.first.substringAfterLast('-') }
            val set = LineDataSet(entries, "Expenses").apply {
                color = AndroidColor.parseColor("#6750A4")
                setCircleColor(AndroidColor.parseColor("#6750A4"))
                lineWidth = 2f
                circleRadius = 3f
                setDrawValues(false)
            }
            chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            chart.data = LineData(set)
            chart.invalidate()
        },
    )
}

@Composable
fun ExpenseBarChart(
    bars: List<Pair<String, Float>>,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            BarChart(ctx).apply {
                description.isEnabled = false
                axisRight.isEnabled = false
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.granularity = 1f
                xAxis.setDrawGridLines(false)
                axisLeft.setDrawGridLines(true)
                legend.isEnabled = false
                setFitBars(true)
            }
        },
        update = { chart ->
            if (bars.isEmpty()) {
                chart.data = null
                chart.invalidate()
                return@AndroidView
            }
            val entries = bars.mapIndexed { i, b -> BarEntry(i.toFloat(), b.second) }
            val labels = bars.map { it.first }
            val set = BarDataSet(entries, "Expenses").apply {
                colors = fallbackColors
                valueTextSize = 9f
            }
            chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            chart.xAxis.labelRotationAngle = -35f
            chart.data = BarData(set).apply { barWidth = 0.5f }
            chart.invalidate()
        },
    )
}

fun chartHeightDefault() = 260.dp

@Composable
fun AnalyticsDonutChart(
    slices: List<Pair<String, Float>>,
    modifier: Modifier = Modifier,
    drawHole: Boolean = true,
    showEntryLabels: Boolean = false,
) {
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            PieChart(ctx).apply {
                description.isEnabled = false
                isDrawHoleEnabled = drawHole
                setHoleRadius(58f)
                setTransparentCircleRadius(61f)
                setHoleColor(AndroidColor.WHITE)
                setDrawEntryLabels(showEntryLabels)
                legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                legend.isEnabled = slices.isNotEmpty()
                legend.isWordWrapEnabled = true
                setUsePercentValues(false)
                setDrawCenterText(false)
            }
        },
        update = { chart ->
            if (slices.isEmpty()) {
                chart.data = null
                chart.invalidate()
                return@AndroidView
            }
            val entries = slices.map { (label, value) -> PieEntry(value, label) }
            val palette = designPalette()
            val set = PieDataSet(entries, "").apply {
                colors = slices.mapIndexed { idx, _ -> palette[idx % palette.size] }
                valueTextSize = 10f
                sliceSpace = 5f
                setDrawValues(false)
            }
            chart.centerText = ""
            chart.data = PieData(set)
            chart.invalidate()
        },
    )
}

@Composable
fun InsightsExpenseBarChart(
    bars: List<Pair<String, Float>>,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            BarChart(ctx).apply {
                description.isEnabled = false
                axisRight.isEnabled = false
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.granularity = 1f
                xAxis.setDrawGridLines(false)
                xAxis.textSize = 11f
                axisLeft.isEnabled = true
                axisLeft.setDrawGridLines(true)
                legend.isEnabled = false
                setFitBars(false)
                minOffset = 0f
                extraBottomOffset = 8f
            }
        },
        update = { chart ->
            if (bars.isEmpty()) {
                chart.data = null
                chart.invalidate()
                return@AndroidView
            }
            val entries = bars.mapIndexed { i, b -> BarEntry(i.toFloat(), b.second) }
            val labels = bars.map { shortenBarLabel(it.first) }
            val set = BarDataSet(entries, "Spent").apply {
                color = AndroidColor.parseColor("#4F46E5")
                valueTextSize = 10f
            }
            set.highLightAlpha = 40
            chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            chart.xAxis.labelRotationAngle = 0f
            chart.axisLeft.axisMinimum = 0f
            chart.data = BarData(set).apply { barWidth = 0.45f }
            chart.invalidate()
        },
    )
}

private fun shortenBarLabel(label: String): String =
    label.split(",").firstOrNull()?.trim()?.take(8) ?: label.take(10)
