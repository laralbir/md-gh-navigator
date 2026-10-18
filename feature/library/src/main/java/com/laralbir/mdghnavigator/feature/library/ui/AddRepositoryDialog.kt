package com.laralbir.mdghnavigator.feature.library.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddRepositoryDialog(
    isLoading: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onAddGitHubRepo: (owner: String, repo: String, branch: String?, startFilePath: String) -> Unit,
    onSelectLocalFolder: (name: String, startFilePath: String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    // GitHub fields
    var owner by remember { mutableStateOf("") }
    var repoName by remember { mutableStateOf("") }
    var branch by remember { mutableStateOf("") }
    var startFile by remember { mutableStateOf("README.md") }

    // Local folder fields
    var localCollectionName by remember { mutableStateOf("") }
    var localStartFile by remember { mutableStateOf("README.md") }

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        shape = RoundedCornerShape(16.dp),
        title = {
            Text(
                text = "Añadir a la Biblioteca",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("GitHub") },
                        icon = { Icon(Icons.Default.Hub, contentDescription = null) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Carpeta Local") },
                        icon = { Icon(Icons.Default.Folder, contentDescription = null) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (selectedTab == 0) {
                    // Formulario GitHub
                    OutlinedTextField(
                        value = owner,
                        onValueChange = { owner = it },
                        label = { Text("Propietario / Organización *") },
                        placeholder = { Text("ej. laralbir") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = !isLoading
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = repoName,
                        onValueChange = { repoName = it },
                        label = { Text("Nombre del Repositorio *") },
                        placeholder = { Text("ej. md-gh-navigator") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = !isLoading
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = branch,
                        onValueChange = { branch = it },
                        label = { Text("Rama (opcional)") },
                        placeholder = { Text("por defecto: main") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = !isLoading
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = startFile,
                        onValueChange = { startFile = it },
                        label = { Text("Fichero principal de inicio") },
                        placeholder = { Text("README.md") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = !isLoading
                    )
                } else {
                    // Formulario Local
                    OutlinedTextField(
                        value = localCollectionName,
                        onValueChange = { localCollectionName = it },
                        label = { Text("Nombre de la Colección *") },
                        placeholder = { Text("ej. Mis Apuntes Markdown") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = !isLoading
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = localStartFile,
                        onValueChange = { localStartFile = it },
                        label = { Text("Fichero principal de inicio") },
                        placeholder = { Text("README.md") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = !isLoading
                    )
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedTab == 0) {
                        if (owner.isNotBlank() && repoName.isNotBlank()) {
                            onAddGitHubRepo(
                                owner.trim(),
                                repoName.trim(),
                                branch.trim().ifBlank { null },
                                startFile.trim().ifBlank { "README.md" }
                            )
                        }
                    } else {
                        if (localCollectionName.isNotBlank()) {
                            onSelectLocalFolder(
                                localCollectionName.trim(),
                                localStartFile.trim().ifBlank { "README.md" }
                            )
                        }
                    }
                },
                enabled = !isLoading && (
                    (selectedTab == 0 && owner.isNotBlank() && repoName.isNotBlank()) ||
                    (selectedTab == 1 && localCollectionName.isNotBlank())
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .height(18.dp)
                            .width(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sincronizando...")
                } else {
                    Text(if (selectedTab == 0) "Sincronizar Repo" else "Seleccionar Carpeta")
                }
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text("Cancelar")
            }
        }
    )
}
