package com.laralbir.mdghnavigator.feature.library.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.laralbir.mdghnavigator.core.database.entity.RepositoryEntity
import com.laralbir.mdghnavigator.core.database.entity.RepositoryType
import com.laralbir.mdghnavigator.feature.library.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RepositoryCard(
    repository: RepositoryEntity,
    onClick: () -> Unit,
    onSyncClick: () -> Unit,
    onPinClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
    onEditAliasClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = if (repository.type == RepositoryType.GITHUB) Icons.Default.Hub else Icons.Default.Folder,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = repository.alias ?: repository.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onPinClick) {
                        Icon(
                            imageVector = if (repository.isPinned) Icons.Default.PushPin else Icons.Outlined.PushPin,
                            contentDescription = if (repository.isPinned) stringResource(R.string.library_unpin) else stringResource(R.string.library_pin),
                            tint = if (repository.isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onEditAliasClick) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(R.string.library_edit_alias_button),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onSyncClick) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = stringResource(R.string.library_sync),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = stringResource(R.string.library_delete),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            val subtitleText = when {
                repository.alias != null && repository.owner != null -> "${repository.owner}/${repository.name}"
                repository.alias != null -> repository.name
                repository.owner != null -> repository.owner
                else -> null
            }

            if (subtitleText != null) {
                Text(
                    text = subtitleText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (repository.type == RepositoryType.GITHUB) {
                    SuggestionChip(
                        onClick = {},
                        label = { Text(stringResource(R.string.library_chip_branch, repository.defaultBranch)) }
                    )
                }
                SuggestionChip(
                    onClick = {},
                    label = { Text(stringResource(R.string.library_chip_start, repository.startFilePath)) }
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            val dateText = if (repository.lastSyncTimestamp > 0) {
                val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                stringResource(R.string.library_synced_at, formatter.format(Date(repository.lastSyncTimestamp)))
            } else {
                stringResource(R.string.library_not_synced)
            }

            Text(
                text = dateText,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
    }
}

