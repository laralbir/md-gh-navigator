package com.laralbir.mdghnavigator.feature.tts.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.laralbir.mdghnavigator.feature.tts.R
import com.laralbir.mdghnavigator.feature.tts.TtsState
import java.util.Locale

/**
 * Compact, floating playback bar for Text-to-Speech reading in the Markdown Reader.
 */
@Composable
fun TtsPlayerBar(
    state: TtsState,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onStop: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onRateChange: (Float) -> Unit,
    onLanguageSelected: (Locale) -> Unit = {},
    onVoiceSelected: (String) -> Unit = {},
    onOpenVoiceSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = state.isVisible,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            tonalElevation = 6.dp,
            shadowElevation = 8.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Top Progress indicator
                LinearProgressIndicator(
                    progress = { state.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    // Header: Reading status + sentence count + close
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(R.string.tts_reading_aloud),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (state.totalSentences > 0) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "(${state.currentSentenceIndex + 1}/${state.totalSentences})",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        IconButton(
                            onClick = onStop,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(R.string.tts_stop),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Current sentence text preview
                    state.currentSentence?.let { sentenceText ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = sentenceText,
                            style = MaterialTheme.typography.bodySmall,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    var showVoiceDialog by remember { mutableStateOf(false) }

                    if (showVoiceDialog) {
                        TtsVoiceSettingsDialog(
                            state = state,
                            onDismiss = { showVoiceDialog = false },
                            onLanguageSelected = onLanguageSelected,
                            onVoiceSelected = onVoiceSelected,
                            onRateChange = onRateChange
                        )
                    }

                    // Playback controls row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left controls: Speed and Language/Voice
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Speed picker
                            var showSpeedMenu by remember { mutableStateOf(false) }
                            Box {
                                OutlinedButton(
                                    onClick = { showSpeedMenu = true },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "${state.speechRate}x",
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }

                                DropdownMenu(
                                    expanded = showSpeedMenu,
                                    onDismissRequest = { showSpeedMenu = false }
                                ) {
                                    listOf(0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { rate ->
                                        DropdownMenuItem(
                                            text = { Text("${rate}x") },
                                            onClick = {
                                                onRateChange(rate)
                                                showSpeedMenu = false
                                            }
                                        )
                                    }
                                }
                            }

                            // Voice & Language button
                            OutlinedButton(
                                onClick = {
                                    onOpenVoiceSettings()
                                    showVoiceDialog = true
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = stringResource(R.string.tts_select_voice_and_language),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = (state.selectedLanguage?.language ?: "ES").uppercase(Locale.ROOT),
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }

                        // Playback buttons
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = onPrevious) {
                                Icon(
                                    imageVector = Icons.Default.SkipPrevious,
                                    contentDescription = stringResource(R.string.tts_previous)
                                )
                            }

                            FilledTonalIconButton(
                                onClick = {
                                    if (state.isPlaying) onPause() else onPlay()
                                },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (state.isPlaying) stringResource(R.string.tts_pause) else stringResource(R.string.tts_play),
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            IconButton(onClick = onNext) {
                                Icon(
                                    imageVector = Icons.Default.SkipNext,
                                    contentDescription = stringResource(R.string.tts_next)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
