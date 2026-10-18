package com.laralbir.mdghnavigator.feature.reader.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.laralbir.mdghnavigator.core.designsystem.component.ErrorStateView
import com.laralbir.mdghnavigator.core.designsystem.component.LoadingView
import com.laralbir.mdghnavigator.core.designsystem.component.MdTopAppBar
import com.laralbir.mdghnavigator.core.designsystem.component.ReadingProgressBar
import com.laralbir.mdghnavigator.core.designsystem.component.rememberReaderWindowLayout
import com.laralbir.mdghnavigator.core.designsystem.theme.AppThemeMode
import com.laralbir.mdghnavigator.core.renderer.ComposeMarkdownReader
import com.laralbir.mdghnavigator.core.renderer.LinkTarget
import com.laralbir.mdghnavigator.feature.navigator.ui.NavigatorIntent
import com.laralbir.mdghnavigator.feature.navigator.ui.NavigatorViewModel
import com.laralbir.mdghnavigator.feature.navigator.ui.TocSidebar
import kotlinx.coroutines.launch

import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    documentId: String,
    repoId: String,
    onBackClick: () -> Unit,
    onNavigateToDocument: (String) -> Unit,
    modifier: Modifier = Modifier,
    readerViewModel: ReaderViewModel = hiltViewModel(),
    navigatorViewModel: NavigatorViewModel = hiltViewModel()
) {
    val readerUiState by readerViewModel.uiState.collectAsState()
    val navUiState by navigatorViewModel.uiState.collectAsState()
    val windowLayout = rememberReaderWindowLayout()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    var showThemeMenu by remember { mutableStateOf(false) }

    LaunchedEffect(documentId) {
        readerViewModel.onIntent(ReaderIntent.LoadDocument(documentId))
        navigatorViewModel.loadRepository(repoId = repoId, initialDocId = documentId)
    }

    val appThemeMode = when (readerUiState.themeMode.lowercase()) {
        "paper" -> AppThemeMode.PAPER
        "neon" -> AppThemeMode.NEON_DEV
        else -> AppThemeMode.SCHOLAR
    }

    val contentComposable: @Composable () -> Unit = {
        Scaffold(
            topBar = {
                Column {
                    MdTopAppBar(
                        title = readerUiState.document?.title?.ifBlank { "Lector Markdown" } ?: "Lector Markdown",
                        subtitle = readerUiState.document?.filePath,
                        onNavigationClick = onBackClick,
                        actions = {
                            IconButton(onClick = {
                                if (windowLayout.isTwoPaneSplitEnabled) {
                                    readerViewModel.onIntent(ReaderIntent.ToggleToc)
                                } else {
                                    coroutineScope.launch {
                                        if (drawerState.isClosed) drawerState.open() else drawerState.close()
                                    }
                                }
                            }) {
                                Icon(
                                    imageVector = if (readerUiState.isTocOpen || drawerState.isOpen) {
                                        Icons.AutoMirrored.Filled.MenuOpen
                                    } else {
                                        Icons.Default.Menu
                                    },
                                    contentDescription = "Índice de contenidos"
                                )
                            }

                            Box {
                                IconButton(onClick = { showThemeMenu = true }) {
                                    Icon(
                                        imageVector = Icons.Default.Palette,
                                        contentDescription = "Cambiar tema"
                                    )
                                }

                                DropdownMenu(
                                    expanded = showThemeMenu,
                                    onDismissRequest = { showThemeMenu = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Tema Scholar (NXTPAPER)") },
                                        onClick = {
                                            readerViewModel.onIntent(ReaderIntent.ChangeTheme("scholar"))
                                            showThemeMenu = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Tema Paper (Minimalista)") },
                                        onClick = {
                                            readerViewModel.onIntent(ReaderIntent.ChangeTheme("paper"))
                                            showThemeMenu = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Tema Neon Dev (Alto Contraste)") },
                                        onClick = {
                                            readerViewModel.onIntent(ReaderIntent.ChangeTheme("neon"))
                                            showThemeMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    )
                    ReadingProgressBar(
                        progress = readerUiState.scrollProgress,
                        showLabel = false
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when {
                    readerUiState.isLoading -> {
                        LoadingView(message = "Cargando documento...")
                    }
                    readerUiState.errorMessage != null -> {
                        ErrorStateView(
                            message = readerUiState.errorMessage ?: "Error desconocido",
                            onRetry = { readerViewModel.onIntent(ReaderIntent.LoadDocument(documentId)) }
                        )
                    }
                    readerUiState.document != null -> {
                        val doc = readerUiState.document!!

                        if (windowLayout.isTwoPaneSplitEnabled) {
                            // Two-pane split layout for Tablets (e.g. TCL NXTPAPER 11 Plus)
                            Row(modifier = Modifier.fillMaxSize()) {
                                if (readerUiState.isTocOpen) {
                                    TocSidebar(
                                        uiState = navUiState,
                                        onIntent = navigatorViewModel::onIntent,
                                        onDocumentSelected = { selectedDoc ->
                                            onNavigateToDocument(selectedDoc.id)
                                        },
                                        onTocItemSelected = { /* Handled via anchor */ }
                                    )
                                    VerticalDivider(
                                        thickness = 1.dp,
                                        color = MaterialTheme.colorScheme.outlineVariant
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .padding(horizontal = windowLayout.contentHorizontalPadding)
                                ) {
                                    ComposeMarkdownReader(
                                        markdown = doc.content,
                                        themeMode = appThemeMode,
                                        onScrollProgress = { progress, scrollY ->
                                            readerViewModel.onIntent(
                                                ReaderIntent.UpdateScroll(progress, scrollY)
                                            )
                                        },
                                        onLinkClicked = { target ->
                                            handleLinkTarget(
                                                context = context,
                                                target = target,
                                                navUiState = navUiState,
                                                onNavigateToDocument = onNavigateToDocument
                                            )
                                        }
                                    )
                                }
                            }
                        } else {
                            // Single-pane phone layout
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = windowLayout.contentHorizontalPadding)
                            ) {
                                ComposeMarkdownReader(
                                    markdown = doc.content,
                                    themeMode = appThemeMode,
                                    onScrollProgress = { progress, scrollY ->
                                        readerViewModel.onIntent(
                                            ReaderIntent.UpdateScroll(progress, scrollY)
                                        )
                                    },
                                    onLinkClicked = { target ->
                                        handleLinkTarget(
                                            context = context,
                                            target = target,
                                            navUiState = navUiState,
                                            onNavigateToDocument = onNavigateToDocument
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (windowLayout.isTwoPaneSplitEnabled) {
        contentComposable()
    } else {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    TocSidebar(
                        uiState = navUiState,
                        onIntent = navigatorViewModel::onIntent,
                        onDocumentSelected = { selectedDoc ->
                            coroutineScope.launch { drawerState.close() }
                            onNavigateToDocument(selectedDoc.id)
                        },
                        onTocItemSelected = {
                            coroutineScope.launch { drawerState.close() }
                        }
                    )
                }
            },
            modifier = modifier
        ) {
            contentComposable()
        }
    }
}

private fun handleLinkTarget(
    context: android.content.Context,
    target: LinkTarget,
    navUiState: com.laralbir.mdghnavigator.feature.navigator.ui.NavigatorUiState,
    onNavigateToDocument: (String) -> Unit
) {
    when (target) {
        is LinkTarget.ExternalWeb -> {
            runCatching {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(target.url)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            }
        }
        is LinkTarget.RelativeDocument -> {
            val matchingDoc = navUiState.documents.find { doc ->
                doc.filePath.endsWith(target.relativePath) ||
                        doc.filePath.equals(target.relativePath, ignoreCase = true)
            }
            if (matchingDoc != null) {
                onNavigateToDocument(matchingDoc.id)
            }
        }
        is LinkTarget.Anchor -> {
            // Anchor scroll handled directly inside WebView via JavaScript
        }
        else -> Unit
    }
}
