package com.laralbir.mdghnavigator.feature.annotations.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.automirrored.filled.StickyNote2
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.laralbir.mdghnavigator.core.database.entity.AnnotationEntity
import com.laralbir.mdghnavigator.core.database.entity.AnnotationType
import com.laralbir.mdghnavigator.core.designsystem.component.EmptyStateView
import com.laralbir.mdghnavigator.feature.annotations.AnnotationFilterTab
import com.laralbir.mdghnavigator.feature.annotations.AnnotationsIntent
import com.laralbir.mdghnavigator.feature.annotations.AnnotationsUiState
import com.laralbir.mdghnavigator.feature.annotations.R

/**
 * Unified side panel and drawer for fast navigation to bookmarks, highlights, and annotations.
 */
@Composable
fun UnifiedAnnotationsDrawer(
    uiState: AnnotationsUiState,
    onIntent: (AnnotationsIntent) -> Unit,
    onNavigateToAnnotation: (AnnotationEntity) -> Unit,
    onClose: () -> Unit,
    onExportClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxHeight()
            .width(340.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Bookmarks,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.annotations_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onExportClick) {
                        Icon(
                            imageVector = Icons.Default.FileUpload,
                            contentDescription = stringResource(R.string.annotations_export_button)
                        )
                    }
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.annotations_close)
                        )
                    }
                }
            }

            // Quick add bookmark button
            Button(
                onClick = { onIntent(AnnotationsIntent.SetAddBookmarkDialogOpen(true)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Icon(
                    imageVector = Icons.Default.BookmarkAdd,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(R.string.annotations_add_bookmark))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Filter Tabs
            ScrollableTabRow(
                selectedTabIndex = uiState.selectedFilter.ordinal,
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 12.dp
            ) {
                Tab(
                    selected = uiState.selectedFilter == AnnotationFilterTab.ALL,
                    onClick = { onIntent(AnnotationsIntent.SetFilter(AnnotationFilterTab.ALL)) },
                    text = { Text("${stringResource(R.string.annotations_tab_all)} (${uiState.annotations.size})") }
                )
                Tab(
                    selected = uiState.selectedFilter == AnnotationFilterTab.BOOKMARKS,
                    onClick = { onIntent(AnnotationsIntent.SetFilter(AnnotationFilterTab.BOOKMARKS)) },
                    text = { Text("${stringResource(R.string.annotations_tab_bookmarks)} (${uiState.bookmarksCount})") }
                )
                Tab(
                    selected = uiState.selectedFilter == AnnotationFilterTab.HIGHLIGHTS,
                    onClick = { onIntent(AnnotationsIntent.SetFilter(AnnotationFilterTab.HIGHLIGHTS)) },
                    text = { Text("${stringResource(R.string.annotations_tab_highlights)} (${uiState.highlightsCount})") }
                )
                Tab(
                    selected = uiState.selectedFilter == AnnotationFilterTab.NOTES,
                    onClick = { onIntent(AnnotationsIntent.SetFilter(AnnotationFilterTab.NOTES)) },
                    text = { Text("${stringResource(R.string.annotations_tab_notes)} (${uiState.notesCount})") }
                )
            }

            // List of items or empty state
            val filtered = uiState.filteredAnnotations
            if (filtered.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.Bookmark,
                    title = stringResource(R.string.annotations_empty_title),
                    message = stringResource(R.string.annotations_empty_message),
                    actionLabel = stringResource(R.string.annotations_add_bookmark),
                    onActionClick = { onIntent(AnnotationsIntent.SetAddBookmarkDialogOpen(true)) }
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filtered, key = { it.id }) { item ->
                        AnnotationNavigationCard(
                            item = item,
                            onClick = { onNavigateToAnnotation(item) },
                            onDelete = { onIntent(AnnotationsIntent.Delete(item.id)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnnotationNavigationCard(
    item: AnnotationEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val icon = when (item.type) {
                        AnnotationType.BOOKMARK -> Icons.Default.Bookmark
                        AnnotationType.HIGHLIGHT -> Icons.Default.FormatPaint
                        AnnotationType.NOTE -> Icons.AutoMirrored.Filled.StickyNote2
                    }
                    val iconColor = when (item.type) {
                        AnnotationType.BOOKMARK -> MaterialTheme.colorScheme.primary
                        AnnotationType.HIGHLIGHT -> MaterialTheme.colorScheme.tertiary
                        AnnotationType.NOTE -> MaterialTheme.colorScheme.secondary
                    }

                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))

                    val progressPercent = (item.scrollProgress * 100).toInt()
                    SuggestionChip(
                        onClick = onClick,
                        label = { Text(stringResource(R.string.annotations_progress_chip, progressPercent)) }
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = stringResource(R.string.annotations_delete),
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            when (item.type) {
                AnnotationType.BOOKMARK -> {
                    Text(
                        text = item.title ?: stringResource(R.string.annotations_default_bookmark_title),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    val noteText = item.note
                    if (!noteText.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = noteText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                AnnotationType.HIGHLIGHT -> {
                    Text(
                        text = "\"${item.selectedText}\"",
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                AnnotationType.NOTE -> {
                    if (item.selectedText.isNotBlank()) {
                        Text(
                            text = "\"${item.selectedText}\"",
                            style = MaterialTheme.typography.bodySmall,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                    Text(
                        text = item.note.orEmpty(),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
