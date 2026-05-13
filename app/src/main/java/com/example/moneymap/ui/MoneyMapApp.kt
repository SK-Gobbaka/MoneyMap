package com.example.moneymap.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ripple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.moneymap.data.TransactionRepository
import com.example.moneymap.preferences.ThemePreferences
import com.example.moneymap.ui.add.AddTransactionScreen
import com.example.moneymap.ui.add.AddTransactionViewModel
import com.example.moneymap.ui.dashboard.DashboardScreen
import com.example.moneymap.ui.dashboard.DashboardViewModel
import com.example.moneymap.ui.reports.ReportsScreen
import com.example.moneymap.ui.reports.ReportsViewModel
import com.example.moneymap.ui.settings.SettingsScreen
import com.example.moneymap.ui.settings.SettingsViewModel
import com.example.moneymap.ui.transactions.TransactionsScreen
import com.example.moneymap.ui.transactions.TransactionsViewModel
import com.example.moneymap.ui.theme.Gray400
import com.example.moneymap.ui.theme.Indigo600
import com.example.moneymap.ui.theme.Indigo700
import com.example.moneymap.ui.theme.White

object Routes {
    const val Dashboard = "dashboard"
    const val Transactions = "transactions"
    const val Add = "add"
    const val Insights = "insights"
    const val Settings = "settings"
    const val Edit = "edit/{id}"

    fun edit(id: Long): String = "edit/$id"
}

private data class BottomItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
)

/** Bottom inset reserved for navigation bar row (FAB sits above). */
private val BottomNavInset = 72.dp
private val FabCenterSpacing = 48.dp

@Composable
fun MoneyMapApp(
    repository: TransactionRepository,
    themePreferences: ThemePreferences,
) {
    val navController = rememberNavController()
    val factory = remember(repository, themePreferences) {
        MoneyMapViewModelFactory(repository, themePreferences)
    }
    val items = remember {
        listOf(
            BottomItem(Routes.Dashboard, "Home", Icons.Default.Home),
            BottomItem(Routes.Transactions, "Activity", Icons.AutoMirrored.Filled.CompareArrows),
            BottomItem(Routes.Insights, "Insights", Icons.Default.PieChart),
            BottomItem(Routes.Settings, "Settings", Icons.Default.Settings),
        )
    }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val route = currentDestination?.route
    val showShell = route != Routes.Add && route?.startsWith("edit") != true

    Box(Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Routes.Dashboard,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (showShell) BottomNavInset else 0.dp),
        ) {
            composable(Routes.Dashboard) {
                val vm: DashboardViewModel = viewModel(factory = factory)
                DashboardScreen(
                    viewModel = vm,
                    onTransactionClick = { id -> navController.navigate(Routes.edit(id)) },
                    onSeeAllActivity = {
                        navController.navigate(Routes.Transactions) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
            composable(Routes.Transactions) {
                val vm: TransactionsViewModel = viewModel(factory = factory)
                TransactionsScreen(
                    viewModel = vm,
                    onEditTransaction = { id -> navController.navigate(Routes.edit(id)) },
                )
            }
            composable(Routes.Add) {
                val vm: AddTransactionViewModel = viewModel(
                    factory = addTransactionViewModelFactory(repository, editId = null),
                )
                AddTransactionScreen(
                    viewModel = vm,
                    isEdit = false,
                    onSaved = { navController.popBackStack() },
                )
            }
            composable(Routes.Insights) {
                val vm: ReportsViewModel = viewModel(factory = factory)
                ReportsScreen(viewModel = vm)
            }
            composable(Routes.Settings) {
                val vm: SettingsViewModel = viewModel(factory = factory)
                SettingsScreen(viewModel = vm)
            }
            composable(
                route = Routes.Edit,
                arguments = listOf(navArgument("id") { type = NavType.LongType }),
            ) {
                val id = it.arguments!!.getLong("id")
                val vm: AddTransactionViewModel = viewModel(
                    factory = addTransactionViewModelFactory(repository, editId = id),
                    key = "edit_$id",
                )
                AddTransactionScreen(
                    viewModel = vm,
                    isEdit = true,
                    onSaved = { navController.popBackStack() },
                )
            }
        }

        if (showShell) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                tonalElevation = 0.dp,
                shadowElevation = 6.dp,
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val home = items[0]
                    val activity = items[1]
                    val insights = items[2]
                    val settings = items[3]
                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        BottomNavDestination(
                            label = home.label,
                            icon = home.icon,
                            selected = currentDestination?.hierarchy?.any { it.route == home.route } == true,
                            onClick = {
                                navController.navigate(home.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                        )
                    }
                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        BottomNavDestination(
                            label = activity.label,
                            icon = activity.icon,
                            selected = currentDestination?.hierarchy?.any { it.route == activity.route } == true,
                            onClick = {
                                navController.navigate(activity.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                        )
                    }
                    Spacer(Modifier.width(FabCenterSpacing))
                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        BottomNavDestination(
                            label = insights.label,
                            icon = insights.icon,
                            selected = currentDestination?.hierarchy?.any { it.route == insights.route } == true,
                            onClick = {
                                navController.navigate(insights.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                        )
                    }
                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        BottomNavDestination(
                            label = settings.label,
                            icon = settings.icon,
                            selected = currentDestination?.hierarchy?.any { it.route == settings.route } == true,
                            onClick = {
                                navController.navigate(settings.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                        )
                    }
                }
            }

            MoneyMapAddFab(
                onClick = { navController.navigate(Routes.Add) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = BottomNavInset - 36.dp),
            )
        }
    }
}

/**
 * Centered primary action matching the design: indigo circle, soft indigo-tinted shadow,
 * thin outlined plus, slight press scale (see AddTransactionFAB in the web export).
 */
@Composable
private fun MoneyMapAddFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.94f else 1f,
        animationSpec = tween(durationMillis = 120),
        label = "fab_scale",
    )
    val fabColor = if (pressed) Indigo700 else Indigo600
    Box(
        modifier = modifier
            .size(60.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(
                elevation = 14.dp,
                shape = CircleShape,
                ambientColor = Color(0xFFC7D2FE).copy(alpha = 0.65f),
                spotColor = Indigo600.copy(alpha = 0.28f),
            )
            .clip(CircleShape)
            .background(fabColor)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = false, radius = 32.dp),
                role = Role.Button,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Outlined.Add,
            contentDescription = "New transaction",
            tint = White,
            modifier = Modifier.size(30.dp),
        )
    }
}

@Composable
private fun BottomNavDestination(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val active = Indigo600
    val idle = Gray400
    val color = if (selected) active else idle
    Column(
        Modifier
            .selectable(selected, onClick = onClick, role = Role.Tab),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(24.dp))
        Spacer(Modifier.height(4.dp))
        Text(
            label,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}
