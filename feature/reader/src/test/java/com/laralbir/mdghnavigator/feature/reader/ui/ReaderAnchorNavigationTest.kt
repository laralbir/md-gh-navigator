package com.laralbir.mdghnavigator.feature.reader.ui

import com.laralbir.mdghnavigator.core.database.entity.DocumentEntity
import com.laralbir.mdghnavigator.core.renderer.LinkTarget
import com.laralbir.mdghnavigator.feature.navigator.ui.NavigatorUiState
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ReaderAnchorNavigationTest {

    private fun simulateHandleLinkTarget(
        target: LinkTarget,
        documents: List<DocumentEntity>,
        activeDocId: String,
        onNavigateToDoc: (String) -> Unit,
        onNavigateToAnchor: (String) -> Unit
    ) {
        when (target) {
            is LinkTarget.Anchor -> {
                onNavigateToAnchor(target.anchorId)
            }
            is LinkTarget.RelativeDocument -> {
                val cleanPath = target.relativePath.trimStart('.', '/')
                val matchingDoc = documents.find { doc ->
                    doc.filePath.equals(cleanPath, ignoreCase = true) ||
                            doc.filePath.endsWith("/$cleanPath", ignoreCase = true) ||
                            doc.filePath.endsWith(cleanPath, ignoreCase = true) ||
                            doc.filePath.substringAfterLast('/').equals(cleanPath.substringAfterLast('/'), ignoreCase = true)
                }
                if (matchingDoc != null) {
                    val anchor = target.targetAnchor
                    if (matchingDoc.id == activeDocId) {
                        if (anchor != null) {
                            onNavigateToAnchor(anchor)
                        }
                    } else {
                        if (anchor != null) {
                            onNavigateToAnchor(anchor)
                        }
                        onNavigateToDoc(matchingDoc.id)
                    }
                }
            }
            else -> Unit
        }
    }

    @Test
    @DisplayName("In-page anchor link triggers onNavigateToAnchor with target anchorId")
    fun testInPageAnchorClick() {
        var targetAnchor: String? = null
        var navigatedDocId: String? = null

        val target = LinkTarget.Anchor("sección-de-instalación")
        simulateHandleLinkTarget(
            target = target,
            documents = emptyList(),
            activeDocId = "doc-1",
            onNavigateToDoc = { navigatedDocId = it },
            onNavigateToAnchor = { targetAnchor = it }
        )

        assertEquals("sección-de-instalación", targetAnchor)
        assertNull(navigatedDocId)
    }

    @Test
    @DisplayName("Relative link to current document with anchor triggers anchor navigation without reloading doc")
    fun testRelativeLinkToCurrentDocWithAnchor() {
        val docs = listOf(
            DocumentEntity(id = "doc-1", repoId = "repo-1", title = "Readme", filePath = "README.md", content = "# Readme")
        )
        var targetAnchor: String? = null
        var navigatedDocId: String? = null

        val target = LinkTarget.RelativeDocument(relativePath = "./README.md", targetAnchor = "requisitos")
        simulateHandleLinkTarget(
            target = target,
            documents = docs,
            activeDocId = "doc-1",
            onNavigateToDoc = { navigatedDocId = it },
            onNavigateToAnchor = { targetAnchor = it }
        )

        assertEquals("requisitos", targetAnchor)
        assertNull(navigatedDocId, "Must not navigate to a new document if already active")
    }

    @Test
    @DisplayName("Relative link to another document with anchor triggers both doc navigation and target anchor")
    fun testRelativeLinkToOtherDocWithAnchor() {
        val docs = listOf(
            DocumentEntity(id = "doc-1", repoId = "repo-1", title = "Readme", filePath = "README.md", content = "# Readme"),
            DocumentEntity(id = "doc-2", repoId = "repo-1", title = "Guide", filePath = "docs/guide.md", content = "# Guide")
        )
        var targetAnchor: String? = null
        var navigatedDocId: String? = null

        val target = LinkTarget.RelativeDocument(relativePath = "docs/guide.md", targetAnchor = "paso-2")
        simulateHandleLinkTarget(
            target = target,
            documents = docs,
            activeDocId = "doc-1",
            onNavigateToDoc = { navigatedDocId = it },
            onNavigateToAnchor = { targetAnchor = it }
        )

        assertEquals("doc-2", navigatedDocId)
        assertEquals("paso-2", targetAnchor)
    }
}
