package com.laralbir.mdghnavigator.feature.settings.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.laralbir.mdghnavigator.feature.settings.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    LaunchedEffect(uiState.message) {
        val msg = uiState.message
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            viewModel.onIntent(SettingsIntent.ClearMessage)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings_title),
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.settings_navigate_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Sección: Apariencia
            SettingsSectionHeader(
                icon = Icons.Default.Palette,
                title = stringResource(R.string.settings_section_appearance)
            )

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Selector de Tema
                    Text(
                        text = stringResource(R.string.settings_theme),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ThemeOptionChip(
                            label = stringResource(R.string.settings_theme_paper),
                            colorDot = Color(0xFF0969DA),
                            isSelected = uiState.themeMode.equals("paper", ignoreCase = true),
                            onClick = { viewModel.onIntent(SettingsIntent.SetTheme("paper")) }
                        )

                        ThemeOptionChip(
                            label = stringResource(R.string.settings_theme_scholar),
                            colorDot = Color(0xFFCBD5E0),
                            isSelected = uiState.themeMode.equals("scholar", ignoreCase = true),
                            onClick = { viewModel.onIntent(SettingsIntent.SetTheme("scholar")) }
                        )

                        ThemeOptionChip(
                            label = stringResource(R.string.settings_theme_neon),
                            colorDot = Color(0xFF00E5FF),
                            isSelected = uiState.themeMode.equals("neon", ignoreCase = true),
                            onClick = { viewModel.onIntent(SettingsIntent.SetTheme("neon")) }
                        )
                    }

                    // Selector de Tipografía
                    Text(
                        text = stringResource(R.string.settings_typography),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = uiState.fontFamily.equals("literata", ignoreCase = true),
                            onClick = { viewModel.onIntent(SettingsIntent.SetFontFamily("literata")) },
                            label = { Text(stringResource(R.string.settings_font_literata)) }
                        )

                        FilterChip(
                            selected = uiState.fontFamily.equals("inter", ignoreCase = true),
                            onClick = { viewModel.onIntent(SettingsIntent.SetFontFamily("inter")) },
                            label = { Text(stringResource(R.string.settings_font_inter)) }
                        )

                        FilterChip(
                            selected = uiState.fontFamily.equals("system", ignoreCase = true),
                            onClick = { viewModel.onIntent(SettingsIntent.SetFontFamily("system")) },
                            label = { Text(stringResource(R.string.settings_font_system)) }
                        )
                    }

                    // Selector de Escala de Fuente
                    Text(
                        text = stringResource(R.string.settings_font_scale),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(0.85f to "85%", 1.0f to "100%", 1.15f to "115%", 1.35f to "135%").forEach { (scale, label) ->
                            FilterChip(
                                selected = kotlin.math.abs(uiState.fontScale - scale) < 0.05f,
                                onClick = { viewModel.onIntent(SettingsIntent.SetFontScale(scale)) },
                                label = { Text(label) }
                            )
                        }
                    }

                    // Caja de Vista Previa en Vivo
                    Text(
                        text = stringResource(R.string.settings_preview_title),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    val (previewBg, previewFg) = when (uiState.themeMode.lowercase()) {
                        "paper" -> Color(0xFFFAFAFA) to Color(0xFF1E1E1E)
                        "neon" -> Color(0xFF0F141C) to Color(0xFFE6EDF3)
                        else -> Color(0xFFFBF0D9) to Color(0xFF2C2214) // Scholar
                    }

                    val previewFamily = when (uiState.fontFamily.lowercase()) {
                        "literata" -> FontFamily.Serif
                        "inter" -> FontFamily.SansSerif
                        else -> FontFamily.Default
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(previewBg)
                            .border(1.dp, Color(0x22000000), RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.settings_preview_sample),
                            color = previewFg,
                            fontSize = (16 * uiState.fontScale).sp,
                            lineHeight = (24 * uiState.fontScale).sp,
                            fontFamily = previewFamily
                        )
                    }
                }
            }

            // Sección: Idioma
            SettingsSectionHeader(
                icon = Icons.Default.Language,
                title = stringResource(R.string.settings_section_language)
            )

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = uiState.languageCode.equals("system", ignoreCase = true),
                            onClick = { viewModel.onIntent(SettingsIntent.SetLanguage("system")) },
                            label = { Text(stringResource(R.string.settings_lang_system)) }
                        )

                        FilterChip(
                            selected = uiState.languageCode.equals("es", ignoreCase = true),
                            onClick = { viewModel.onIntent(SettingsIntent.SetLanguage("es")) },
                            label = { Text(stringResource(R.string.settings_lang_es)) }
                        )

                        FilterChip(
                            selected = uiState.languageCode.equals("en", ignoreCase = true),
                            onClick = { viewModel.onIntent(SettingsIntent.SetLanguage("en")) },
                            label = { Text(stringResource(R.string.settings_lang_en)) }
                        )
                    }
                }
            }

            // Sección: Almacenamiento y Caché
            SettingsSectionHeader(
                icon = Icons.Default.Storage,
                title = stringResource(R.string.settings_section_storage)
            )

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.settings_cache_size),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = uiState.cacheSizeFormatted,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Button(
                            onClick = { viewModel.onIntent(SettingsIntent.ClearCache) },
                            enabled = !uiState.isClearingCache,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            if (uiState.isClearingCache) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.CleaningServices,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(stringResource(R.string.settings_clear_cache))
                            }
                        }
                    }
                }
            }

            // Sección: Acerca de
            SettingsSectionHeader(
                icon = Icons.Default.Info,
                title = stringResource(R.string.settings_section_about)
            )

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.settings_app_name),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.settings_app_version),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stringResource(R.string.settings_license),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    OutlinedButton(
                        onClick = {
                            val browserIntent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://github.com/laralbir/md-gh-navigator")
                            )
                            context.startActivity(browserIntent)
                        },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(stringResource(R.string.settings_github_repo))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SettingsSectionHeader(
    icon: ImageVector,
    title: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun ThemeOptionChip(
    label: String,
    colorDot: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        leadingIcon = if (isSelected) {
            {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        } else {
            {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(colorDot)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), CircleShape)
                )
            }
        },
        label = { Text(label) }
    )
}
