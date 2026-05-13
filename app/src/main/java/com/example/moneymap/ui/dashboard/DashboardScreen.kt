package com.example.moneymap.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.NorthEast
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.SouthEast
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.moneymap.data.TransactionRecord
import com.example.moneymap.data.TransactionType
import com.example.moneymap.ui.theme.Emerald300
import com.example.moneymap.ui.theme.Emerald50
import com.example.moneymap.ui.theme.Emerald600
import com.example.moneymap.ui.theme.Gray100
import com.example.moneymap.ui.theme.Gray500
import com.example.moneymap.ui.theme.Gray900
import com.example.moneymap.ui.theme.Indigo200
import com.example.moneymap.ui.theme.Indigo600
import com.example.moneymap.ui.theme.Red300
import com.example.moneymap.ui.theme.Red50
import com.example.moneymap.ui.theme.Red500
import com.example.moneymap.ui.theme.White
import com.example.moneymap.util.MoneyFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private const val SAMPLE_AVATAR =
    "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200&h=200&fit=crop&crop=faces"

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onTransactionClick: (Long) -> Unit,
    onSeeAllActivity: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val today = LocalDate.now()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        item { DashboardHeader(Modifier.fillMaxWidth()) }
        item {
            Column(Modifier.padding(horizontal = 24.dp)) {
                Spacer(Modifier.height(16.dp))
                BalanceGradientCard(
                    balance = MoneyFormat.format(state.balance),
                    income = MoneyFormat.format(state.totalIncome),
                    expense = MoneyFormat.format(state.totalExpense),
                    cardholderName = "Alex Carter",
                )
                Spacer(Modifier.height(24.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom,
                ) {
                    Text(
                        "Recent Activity",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    TextButton(onClick = onSeeAllActivity) {
                        Text("See All", fontWeight = FontWeight.Medium, color = Indigo600)
                    }
                }
                Spacer(Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp,
                    shadowElevation = 1.dp,
                    border = BorderStroke(1.dp, Gray100),
                ) {
                    if (state.recent.isEmpty()) {
                        Text(
                            "No transactions yet.",
                            modifier = Modifier.padding(32.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Gray500,
                        )
                    } else {
                        Column(Modifier.padding(vertical = 4.dp)) {
                            state.recent.forEachIndexed { index, tx ->
                                RecentActivityRow(
                                    record = tx,
                                    today = today,
                                    onClick = { onTransactionClick(tx.id) },
                                    showDivider = index != state.recent.lastIndex,
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(96.dp))
            }
        }
    }
}

@Composable
private fun DashboardHeader(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 0.dp,
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                val ctx = LocalContext.current
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Gray100),
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(ctx)
                            .data(SAMPLE_AVATAR)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                }
                Column {
                    Text(
                        "Good Morning!",
                        style = MaterialTheme.typography.labelMedium,
                        color = Gray500,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                    )
                    Text(
                        "Alex Carter",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(1.dp, Gray100, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Outlined.Notifications,
                    contentDescription = null,
                    tint = Gray900.copy(alpha = 0.62f),
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 6.dp, end = 6.dp)
                        .size(9.dp)
                        .clip(CircleShape)
                        .background(Red500)
                        .border(2.dp, White, CircleShape),
                )
            }
        }
    }
}

@Composable
private fun BalanceGradientCard(
    balance: String,
    income: String,
    expense: String,
    cardholderName: String,
) {
    val gradient = Brush.linearGradient(
        colors = listOf(
            Indigo600,
            Color(0xFF1D4ED8),
            Color(0xFF312E81),
        ),
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.58f)
            .clip(RoundedCornerShape(24.dp))
            .background(gradient)
            .border(1.dp, White.copy(alpha = 0.1f), RoundedCornerShape(24.dp)),
    ) {
        Box(
            Modifier
                .size(240.dp)
                .align(Alignment.TopEnd)
                .background(
                    Brush.radialGradient(
                        colors = listOf(White.copy(alpha = 0.14f), White.copy(alpha = 0f)),
                    ),
                ),
        )
        Column(
            Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column {
                    Text(
                        "CARDHOLDER NAME",
                        color = Indigo200.copy(alpha = 0.98f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 2.4.sp,
                    )
                    Text(
                        cardholderName.uppercase(),
                        color = White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        letterSpacing = 1.5.sp,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.AccountBalanceWallet,
                        contentDescription = null,
                        tint = Indigo200.copy(alpha = 0.85f),
                    )
                    Spacer(Modifier.size(12.dp))
                    WalletChipGraphic()
                }
            }
            Column {
                Text(
                    "TOTAL BALANCE",
                    color = Indigo200.copy(alpha = 0.96f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 2.4.sp,
                )
                Text(
                    balance,
                    color = White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 38.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(top = 6.dp, bottom = 20.dp),
                )
                Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Outlined.SouthEast,
                                contentDescription = null,
                                tint = Emerald300,
                                modifier = Modifier.size(14.dp),
                            )
                            Spacer(Modifier.size(4.dp))
                            Text(
                                "INCOME",
                                color = Emerald300,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 2.sp,
                            )
                        }
                        Text(
                            income,
                            color = White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Outlined.NorthEast,
                                contentDescription = null,
                                tint = Red300,
                                modifier = Modifier.size(14.dp),
                            )
                            Spacer(Modifier.size(4.dp))
                            Text(
                                "EXPENSE",
                                color = Red300,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 2.sp,
                            )
                        }
                        Text(
                            expense,
                            color = White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WalletChipGraphic() {
    val borderGold = Color(0x66CA8A04)
    val lineGold = Color(0x33CA8A04)
    Box(
        modifier = Modifier
            .width(40.dp)
            .height(28.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFFFDE68A),
                        Color(0xFFF59E0B),
                    ),
                ),
            )
            .border(1.dp, borderGold, RoundedCornerShape(6.dp)),
    ) {
        HorizontalDivider(
            modifier = Modifier.align(Alignment.Center),
            thickness = 1.dp,
            color = lineGold,
        )
        Box(
            Modifier
                .fillMaxHeight()
                .width(1.dp)
                .align(Alignment.Center)
                .background(lineGold),
        )
    }
}

@Composable
private fun RecentActivityRow(
    record: TransactionRecord,
    today: LocalDate,
    onClick: () -> Unit,
    showDivider: Boolean,
) {
    val isIncome = record.type == TransactionType.INCOME
    val isToday = record.dateEpochDay == today.toEpochDay()
    val dateFmt = DateTimeFormatter.ofPattern("MMM d")
    val subtitle = buildString {
        append(if (isToday) "Today" else record.date.format(dateFmt))
        if (record.notes.isNotBlank()) {
            append(" • ")
            append(record.notes.take(48))
        }
    }
    val title = if (isIncome) record.subcategory else record.category

    Column(
        Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isIncome) Emerald50 else Red50),
                contentAlignment = Alignment.Center,
            ) {
                if (isIncome) {
                    Icon(Icons.Outlined.SouthEast, contentDescription = null, tint = Emerald600)
                } else {
                    Icon(Icons.Outlined.NorthEast, contentDescription = null, tint = Red500)
                }
            }
            Column(Modifier.weight(1f).padding(start = 12.dp)) {
                Text(
                    title,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    subtitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = Gray500,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 2.dp),
                    fontSize = 12.sp,
                )
            }
            val amt = MoneyFormat.format(record.amount)
            Text(
                text = if (isIncome) "+$amt" else "−$amt",
                fontWeight = FontWeight.Bold,
                color = if (isIncome) Emerald600 else MaterialTheme.colorScheme.onSurface,
            )
        }
        if (showDivider) {
            HorizontalDivider(color = Color(0xFFF9FAFB))
        }
    }
}
