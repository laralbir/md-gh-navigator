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
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberDrawerState
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
import androidx.compose.ui.res.stringResource
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
import com.laralbir.mdghnavigator.feature.annotations.AnnotationsIntent
import com.laralbir.mdghnavigator.feature.annotations.AnnotationsViewModel
import com.laralbir.mdghnavigator.feature.annotations.ui.AddBookmarkDialog
import com.laralbir.mdghnavigator.feature.annotations.ui.UnifiedAnnotationsDrawer
import com.laralbir.mdghnavigator.feature.navigator.ui.NavigatorIntent
import com.laralbir.mdghnavigator.feature.navigator.ui.NavigatorViewModel
import com.laralbir.mdghnavigator.feature.navigator.ui.TocSidebar
import com.laralbir.mdghnavigator.feature.reader.R
import com.laralbir.mdghnavigator.feature.reader.ui.component.ZoomableImageViewer
import com.laralbir.mdghnavigator.feature.tts.TtsState
import com.laralbir.mdghnavigator.feature.tts.ui.TtsPlayerBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

private enum class ReaderDrawerContent {
    TOC,
    ANNOTATIONS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    documentId: String,
    repoId: String,
    onBackClick: () -> Unit,
    onNavigateToDocument: (String) -> Unit,
    modifier: Modifier = Modifier,
    readerViewModel: ReaderViewModel = hiltViewModel(),
    navigatorViewModel: NavigatorViewModel = hiltViewModel(),
    annotationsViewModel: AnnotationsViewModel = hiltViewModel()
) {
    val readerUiState by readerViewModel.uiState.collectAsState()
    val navUiState by navigatorViewModel.uiState.collectAsState()
    val annotationsUiState by annotationsViewModel.uiState.collectAsState()
    val ttsState by (readerViewModel.ttsController?.uiState ?: remember { MutableStateFlow(TtsState()) }).collectAsState()
    val windowLayout = rememberReaderWindowLayout()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    var showThemeMenu by remember { mutableStateOf(false) }
    var isAnnotationsOpen by remember { mutableStateOf(false) }
    var activeDrawerContent by remember { mutableStateOf(ReaderDrawerContent.TOC) }
    var targetScrollProgress by remember { mutableStateOf<Float?>(null) }
    var targetHeadingId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(documentId, repoId) {
        readerViewModel.onIntent(ReaderIntent.LoadDocument(documentId))
        navigatorViewModel.loadRepository(repoId = repoId, initialDocId = documentId)
        annotationsViewModel.onIntent(AnnotationsIntent.Load(documentId = documentId, repoId = repoId))
    }

    LaunchedEffect(targetScrollProgress) {
        if (targetScrollProgress != null) {
            delay(350)
            targetScrollProgress = null
        }
    }

    LaunchedEffect(targetHeadingId) {
        if (targetHeadingId != null) {
            delay(350)
            targetHeadingId = null
        }
    }

    LaunchedEffect(annotationsUiState.exportedMarkdown) {
        val markdown = annotationsUiState.exportedMarkdown ?: return@LaunchedEffect
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, markdown)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        runCatching { context.startActivity(shareIntent) }
        annotationsViewModel.onIntent(AnnotationsIntent.ClearExported)
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
                    val defaultTitle = stringResource(R.string.reader_title_default)
                    MdTopAppBar(
                        title = readerUiState.document?.title?.ifBlank { defaultTitle } ?: defaultTitle,
                        subtitle = readerUiState.document?.filePath,
                        onNavigationClick = onBackClick,
                        actions = {
                            // TTS Playback toggle
                            IconButton(onClick = {
                                val controller = readerViewModel.ttsController ?: return@IconButton
                                if (ttsState.isPlaying) controller.pause() else controller.play()
                            }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = stringResource(R.string.reader_tts_button),
                                    tint = if (ttsState.isPlaying) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }

                            // Quick Add Bookmark
                            IconButton(onClick = {
                                annotationsViewModel.onIntent(AnnotationsIntent.SetAddBookmarkDialogOpen(true))
                            }) {
                                Icon(
                                    imageVector = Icons.Default.BookmarkAdd,
                                    contentDescription = stringResource(R.string.reader_add_bookmark_quick)
                                )
                            }

                            // Bookmarks & Annotations Drawer/Panel Toggle
                            IconButton(onClick = {
                                if (windowLayout.isTwoPaneSplitEnabled) {
                                    isAnnotationsOpen = !isAnnotationsOpen
                                } else {
                                    activeDrawerContent = ReaderDrawerContent.ANNOTATIONS
                                    coroutineScope.launch {
                                        if (drawerState.isOpen && activeDrawerContent == ReaderDrawerContent.ANNOTATIONS) {
                                            drawerState.close()
                                        } else {
                                            drawerState.open()
                                        }
                                    }
                                }
                            }) {
                                val isBookmarkedPanelActive = isAnnotationsOpen ||
                                        (drawerState.isOpen && activeDrawerContent == ReaderDrawerContent.ANNOTATIONS)
                                Icon(
                                    imageVector = Icons.Default.Bookmarks,
                                    contentDescription = stringResource(R.string.reader_bookmarks_button),
                                    tint = if (isBookmarkedPanelActive) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }

                            // Table of Contents Toggle
                            IconButton(onClick = {
                                if (windowLayout.isTwoPaneSplitEnabled) {
                                    readerViewModel.onIntent(ReaderIntent.ToggleToc)
                                } else {
                                    activeDrawerContent = ReaderDrawerContent.TOC
                                    coroutineScope.launch {
                                        if (drawerState.isOpen && activeDrawerContent == ReaderDrawerContent.TOC) {
                                            drawerState.close()
                                        } else {
                                            drawerState.open()
                                        }
                                    }
                                }
                            }) {
                                val isTocActive = readerUiState.isTocOpen ||
                                        (drawerState.isOpen && activeDrawerContent == ReaderDrawerContent.TOC)
                                Icon(
                                    imageVector = if (isTocActive) {
                                        Icons.AutoMirrored.Filled.MenuOpen
                                    } else {
                                        Icons.Default.Menu
                                    },
                                    contentDescription = stringResource(R.string.reader_toc_button)
                                )
                            }

                            // Theme Selector
                            Box {
                                IconButton(onClick = { showThemeMenu = true }) {
                                    Icon(
                                        imageVector = Icons.Default.Palette,
                                        contentDescription = stringResource(R.string.reader_change_theme)
                                    )
                                }

                                DropdownMenu(
                                    expanded = showThemeMenu,
                                    onDismissRequest = { showThemeMenu = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text(stringResource(R.string.reader_theme_scholar)) },
                                        onClick = {
                                            readerViewModel.onIntent(ReaderIntent.ChangeTheme("scholar"))
                                            showThemeMenu = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text(stringResource(R.string.reader_theme_paper)) },
                                        onClick = {
                                            readerViewModel.onIntent(ReaderIntent.ChangeTheme("paper"))
                                            showThemeMenu = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text(stringResource(R.string.reader_theme_neon)) },
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
            },
            bottomBar = {
                readerViewModel.ttsController?.let { controller ->
                    TtsPlayerBar(
                        state = ttsState,
                        onPlay = controller::play,
                        onPause = controller::pause,
                        onStop = controller::stop,
                        onNext = controller::nextSentence,
                        onPrevious = controller::previousSentence,
                        onRateChange = controller::setSpeechRate
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
                        LoadingView(message = stringResource(R.string.reader_loading_doc))
                    }
                    readerUiState.errorMessage != null -> {
                        ErrorStateView(
                            message = readerUiState.errorMessage ?: stringResource(R.string.reader_unknown_error),
                            onRetry = { readerViewModel.onIntent(ReaderIntent.LoadDocument(documentId)) }
                        )
                    }
                    readerUiState.document != null -> {
                        val doc = readerUiState.document!!

                        if (windowLayout.isTwoPaneSplitEnabled) {
                            // Two/Three-pane split layout for Tablets (e.g. TCL NXTPAPER 11 Plus)
                            Row(modifier = Modifier.fillMaxSize()) {
                                if (readerUiState.isTocOpen) {
                                    TocSidebar(
                                        uiState = navUiState,
                                        onIntent = navigatorViewModel::onIntent,
                                        onDocumentSelected = { selectedDoc ->
                                            onNavigateToDocument(selectedDoc.id)
                                        },
                                        onTocItemSelected = { heading ->
                                            targetHeadingId = heading.id
                                        }
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
                                        targetScrollProgress = targetScrollProgress,
                                        targetHeadingId = targetHeadingId,
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
                                        },
                                        onImageClicked = { imageUrl, title ->
                                            readerViewModel.onIntent(
                                                ReaderIntent.OpenImageViewer(imageUrl, title)
                                            )
                                        }
                                    )
                                }

                                if (isAnnotationsOpen) {
                                    VerticalDivider(
                                        thickness = 1.dp,
                                        color = MaterialTheme.colorScheme.outlineVariant
                                    )
                                    UnifiedAnnotationsDrawer(
                                        uiState = annotationsUiState,
                                        onIntent = annotationsViewModel::onIntent,
                                        onNavigateToAnnotation = { annotation ->
                                            targetScrollProgress = annotation.scrollProgress
                                        },
                                        onClose = { isAnnotationsOpen = false },
                                        onExportClick = {
                                            annotationsViewModel.onIntent(
                                                AnnotationsIntent.Export(
                                                    documentTitle = readerUiState.document?.title ?: "Document"
                                                )
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
                                    targetScrollProgress = targetScrollProgress,
                                    targetHeadingId = targetHeadingId,
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
                                    },
                                    onImageClicked = { imageUrl, title ->
                                        readerViewModel.onIntent(
                                            ReaderIntent.OpenImageViewer(imageUrl, title)
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
                    when (activeDrawerContent) {
                        ReaderDrawerContent.TOC -> {
                            TocSidebar(
                                uiState = navUiState,
                                onIntent = navigatorViewModel::onIntent,
                                onDocumentSelected = { selectedDoc ->
                                    coroutineScope.launch { drawerState.close() }
                                    onNavigateToDocument(selectedDoc.id)
                                },
                                onTocItemSelected = { heading ->
                                    targetHeadingId = heading.id
                                    coroutineScope.launch { drawerState.close() }
                                }
                            )
                        }
                        ReaderDrawerContent.ANNOTATIONS -> {
                            UnifiedAnnotationsDrawer(
                                uiState = annotationsUiState,
                                onIntent = annotationsViewModel::onIntent,
                                onNavigateToAnnotation = { annotation ->
                                    targetScrollProgress = annotation.scrollProgress
                                    coroutineScope.launch { drawerState.close() }
                                },
                                onClose = {
                                    coroutineScope.launch { drawerState.close() }
                                },
                                onExportClick = {
                                    annotationsViewModel.onIntent(
                                        AnnotationsIntent.Export(
                                            documentTitle = readerUiState.document?.title ?: "Document"
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
            },
            modifier = modifier
        ) {
            contentComposable()
        }
    }

    if (annotationsUiState.isAddBookmarkDialogOpen) {
        AddBookmarkDialog(
            scrollProgress = readerUiState.scrollProgress,
            scrollY = readerUiState.scrollY,
            onDismiss = {
                annotationsViewModel.onIntent(AnnotationsIntent.SetAddBookmarkDialogOpen(false))
            },
            onConfirm = { title, note ->
                annotationsViewModel.onIntent(
                    AnnotationsIntent.AddBookmark(
                        title = title,
                        scrollProgress = readerUiState.scrollProgress,
                        scrollY = readerUiState.scrollY,
                        note = note
                    )
                )
            }
        )
    }

    if (readerUiState.selectedImageUrl != null) {
        ZoomableImageViewer(
            imageUrl = readerUiState.selectedImageUrl!!,
            title = readerUiState.selectedImageTitle,
            onDismiss = {
                readerViewModel.onIntent(ReaderIntent.CloseImageViewer)
            }
        )
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
