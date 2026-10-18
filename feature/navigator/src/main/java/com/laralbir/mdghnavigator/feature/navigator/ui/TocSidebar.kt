package com.laralbir.mdghnavigator.feature.navigator.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.laralbir.mdghnavigator.core.database.entity.DocumentEntity
import com.laralbir.mdghnavigator.core.database.entity.TocItemEntity

@Composable
fun TocSidebar(
    uiState: NavigatorUiState,
    onIntent: (NavigatorIntent) -> Unit,
    modifier: Modifier = Modifier,
    onDocumentSelected: (DocumentEntity) -> Unit = {},
    onTocItemSelected: (TocItemEntity) -> Unit = {}
) {
    Surface(
        modifier = modifier
            .fillMaxHeight()
            .width(320.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TabRow(
                selectedTabIndex = if (uiState.selectedTab == NavigatorTab.TOC) 0 else 1,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = uiState.selectedTab == NavigatorTab.TOC,
                    onClick = { onIntent(NavigatorIntent.SelectTab(NavigatorTab.TOC)) },
                    text = { Text("Índice") },
                    icon = { Icon(Icons.Default.FormatListNumbered, contentDescription = "Índice") }
                )
                Tab(
                    selected = uiState.selectedTab == NavigatorTab.FILES,
                    onClick = { onIntent(NavigatorIntent.SelectTab(NavigatorTab.FILES)) },
                    text = { Text("Archivos") },
                    icon = { Icon(Icons.Default.Folder, contentDescription = "Archivos") }
                )
            }

            when (uiState.selectedTab) {
                NavigatorTab.TOC -> {
                    TocList(
                        tocItems = uiState.tocItems,
                        onTocItemSelected = { item ->
                            onIntent(NavigatorIntent.SelectTocItem(item))
                            onTocItemSelected(item)
                        }
                    )
                }
                NavigatorTab.FILES -> {
                    FilesList(
                        searchQuery = uiState.searchQuery,
                        documents = uiState.filteredDocuments,
                        currentDocumentId = uiState.currentDocumentId,
                        onSearchQueryChanged = { onIntent(NavigatorIntent.UpdateSearchQuery(it)) },
                        onDocumentSelected = { doc ->
                            onIntent(NavigatorIntent.SelectDocument(doc))
                            onDocumentSelected(doc)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TocList(
    tocItems: List<TocItemEntity>,
    onTocItemSelected: (TocItemEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    if (tocItems.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Sin encabezados en este documento",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(vertical = 8.dp)
        ) {
            items(tocItems, key = { it.id }) { item ->
                TocItemRow(
                    item = item,
                    onClick = { onTocItemSelected(item) }
                )
            }
        }
    }
}

@Composable
private fun TocItemRow(
    item: TocItemEntity,
    onClick: () -> Unit
) {
    val indentDp = ((item.level - 1).coerceAtLeast(0) * 16).dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .padding(start = indentDp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val iconSize = when (item.level) {
            1 -> 18.dp
            2 -> 14.dp
            else -> 10.dp
        }

        Box(
            modifier = Modifier
                .size(iconSize)
                .clip(RoundedCornerShape(2.dp))
                .background(
                    if (item.level == 1) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outline
                )
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = item.title,
            style = if (item.level <= 2) {
                MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            } else {
                MaterialTheme.typography.bodySmall
            },
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun FilesList(
    searchQuery: String,
    documents: List<DocumentEntity>,
    currentDocumentId: String?,
    onSearchQueryChanged: (String) -> Unit,
    onDocumentSelected: (DocumentEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text("Buscar archivos...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChanged("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Limpiar búsqueda")
                    }
                }
            },
            singleLine = true
        )

        if (documents.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No se encontraron archivos markdown",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(documents, key = { it.id }) { doc ->
                    val isSelected = doc.id == currentDocumentId
                    FileItemRow(
                        doc = doc,
                        isSelected = isSelected,
                        onClick = { onDocumentSelected(doc) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FileItemRow(
    doc: DocumentEntity,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }

    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(backgroundColor)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isSelected) Icons.AutoMirrored.Filled.MenuBook else Icons.Default.Description,
            contentDescription = null,
            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = doc.title.ifBlank { doc.filePath },
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = doc.filePath,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (doc.scrollProgress > 0f) {
            val progressPercent = (doc.scrollProgress * 100).toInt()
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "$progressPercent%",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
