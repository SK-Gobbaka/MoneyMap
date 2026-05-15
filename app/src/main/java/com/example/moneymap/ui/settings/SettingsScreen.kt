package com.example.moneymap.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.moneymap.preferences.ThemeMode
import com.example.moneymap.ui.theme.Gray100
import com.example.moneymap.ui.theme.Gray50
import com.example.moneymap.ui.theme.Gray400
import com.example.moneymap.ui.theme.Gray500
import com.example.moneymap.ui.theme.Gray900
import com.example.moneymap.ui.theme.Indigo600
import com.example.moneymap.ui.theme.Red50
import com.example.moneymap.ui.theme.Red500

private const val SAMPLE_AVATAR =
    "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=240&h=240&fit=crop&crop=faces"

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val mode by viewModel.themeMode.collectAsStateWithLifecycle()
    val systemDark = isSystemInDarkTheme()
    val effectiveDark = when (mode) {
        ThemeMode.FOLLOW_SYSTEM -> systemDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    var securityDialogVisible by remember { mutableStateOf(false) }
    var privacyDialogVisible by remember { mutableStateOf(false) }

    if (securityDialogVisible) {
        AlertDialog(
            onDismissRequest = { securityDialogVisible = false },
            title = { Text("Security Settings") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("• Biometric Lock: Enhanced security using your fingerprint or face ID.")
                    Text("• Two-Factor Authentication: Add an extra layer of security to your account.")
                    Text("• Device Management: View and manage all devices currently logged into your account.")
                    Text("• Pin Code: Set a secondary PIN for accessing sensitive financial data.")
                }
            },
            confirmButton = {
                TextButton(onClick = { securityDialogVisible = false }) { Text("Close") }
            }
        )
    }

    if (privacyDialogVisible) {
        AlertDialog(
            onDismissRequest = { privacyDialogVisible = false },
            title = { Text("Data & Privacy") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("• Data Encryption: Your financial records are encrypted with AES-256 standard.")
                    Text("• Local-First: Most of your data stays on your device and is never shared with third parties.")
                    Text("• Data Export: Download a copy of your transaction history at any time in CSV format.")
                    Text("• Account Deletion: You can permanently delete your account and all associated data.")
                }
            },
            confirmButton = {
                TextButton(onClick = { privacyDialogVisible = false }) { Text("Close") }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                "Settings",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    .padding(horizontal = 24.dp)
                    .statusBarsPadding()
                    .padding(top = 8.dp, bottom = 14.dp),
            )
        }

        Column(Modifier.padding(horizontal = 24.dp, vertical = 20.dp)) {
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                shadowElevation = 1.dp,
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val ctx = LocalContext.current
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
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
                    Column(Modifier.weight(1f).padding(horizontal = 14.dp)) {
                        Text("Alex Carter", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text("alex.carter@example.com", fontSize = 14.sp, color = Gray500, fontWeight = FontWeight.Medium)
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Outlined.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }

            Spacer(Modifier.height(28.dp))
            SettingsSectionTitle("PREFERENCES")
            SettingsCard {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        Modifier
                            .weight(1f)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(10.dp),
                        ) {
                            Icon(Icons.Outlined.DarkMode, contentDescription = null, tint = Gray500)
                        }
                        Spacer(Modifier.size(12.dp))
                        Text("Dark Mode", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp)
                    }
                    Switch(
                        checked = effectiveDark,
                        onCheckedChange = { checked ->
                            viewModel.setThemeMode(if (checked) ThemeMode.DARK else ThemeMode.LIGHT)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.surface,
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                        ),
                    )
                }
            }

            Spacer(Modifier.height(22.dp))
            SettingsSectionTitle("SECURITY")
            SettingsCard {
                SettingsRow(
                    icon = { Icon(Icons.Outlined.Security, contentDescription = null, tint = Gray500) },
                    label = "Security Settings",
                    onClick = { securityDialogVisible = true }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
                SettingsRow(
                    icon = { Icon(Icons.Outlined.Person, contentDescription = null, tint = Gray500) },
                    label = "Data & Privacy",
                    onClick = { privacyDialogVisible = true }
                )
            }

            Spacer(Modifier.height(22.dp))
            SettingsSectionTitle("SUPPORT")
            SettingsCard {
                SettingsRow(
                    icon = { Icon(Icons.Outlined.HelpOutline, contentDescription = null, tint = Gray500) },
                    label = "Help Center",
                )
            }

            Spacer(Modifier.height(28.dp))
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {},
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Outlined.Logout, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.size(10.dp))
                    Text("Log Out", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Spacer(Modifier.height(100.dp))
        }
    }
}

@Composable
private fun SettingsSectionTitle(text: String) {
    Text(
        text,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        fontSize = 11.sp,
        letterSpacing = 1.8.sp,
        modifier = Modifier.padding(start = 8.dp, bottom = 10.dp),
    )
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shadowElevation = 1.dp,
    ) {
        Column { content() }
    }
}

@Composable
private fun SettingsRow(
    icon: @Composable () -> Unit,
    label: String,
    onClick: () -> Unit = {},
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(10.dp),
            ) {
                icon()
            }
            Text(
                label,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 14.dp),
            )
            Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = Gray400)
        }
    }
}
