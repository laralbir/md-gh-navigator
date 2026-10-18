#!/usr/bin/env python3
"""
Seeds sample repositories, markdown documents, annotations, and TOC items
into a SQLite database for MD GitHub Navigator, matching Room's schema.
"""

import os
import sqlite3
import subprocess

DB_PATH = "/tmp/md_gh_navigator.db"
if os.path.exists(DB_PATH):
    os.remove(DB_PATH)

conn = sqlite3.connect(DB_PATH)
cursor = conn.cursor()

# 1. Create Room tables
cursor.execute("""
CREATE TABLE IF NOT EXISTS `repositories` (
    `id` TEXT NOT NULL,
    `name` TEXT NOT NULL,
    `owner` TEXT,
    `type` TEXT NOT NULL,
    `defaultBranch` TEXT NOT NULL,
    `startFilePath` TEXT NOT NULL,
    `localUri` TEXT,
    `lastSyncTimestamp` INTEGER NOT NULL,
    `eTag` TEXT,
    `isPinned` INTEGER NOT NULL,
    PRIMARY KEY(`id`)
)
""")

cursor.execute("""
CREATE TABLE IF NOT EXISTS `documents` (
    `id` TEXT NOT NULL,
    `repoId` TEXT NOT NULL,
    `filePath` TEXT NOT NULL,
    `title` TEXT NOT NULL,
    `content` TEXT NOT NULL,
    `scrollProgress` REAL NOT NULL,
    `scrollY` INTEGER NOT NULL,
    `lastReadTimestamp` INTEGER NOT NULL,
    `fileSha` TEXT,
    `isFavorite` INTEGER NOT NULL,
    PRIMARY KEY(`id`)
)
""")

cursor.execute("CREATE INDEX IF NOT EXISTS `index_documents_repoId` ON `documents` (`repoId`)")
cursor.execute("CREATE INDEX IF NOT EXISTS `index_documents_lastReadTimestamp` ON `documents` (`lastReadTimestamp`)")

cursor.execute("""
CREATE TABLE IF NOT EXISTS `annotations` (
    `id` TEXT NOT NULL,
    `documentId` TEXT NOT NULL,
    `repoId` TEXT NOT NULL,
    `type` TEXT NOT NULL,
    `title` TEXT,
    `selectedText` TEXT NOT NULL,
    `note` TEXT,
    `colorHex` TEXT NOT NULL,
    `startOffset` INTEGER NOT NULL,
    `endOffset` INTEGER NOT NULL,
    `scrollProgress` REAL NOT NULL,
    `scrollY` INTEGER NOT NULL,
    `createdAt` INTEGER NOT NULL,
    `updatedAt` INTEGER NOT NULL,
    PRIMARY KEY(`id`)
)
""")

cursor.execute("CREATE INDEX IF NOT EXISTS `index_annotations_documentId` ON `annotations` (`documentId`)")
cursor.execute("CREATE INDEX IF NOT EXISTS `index_annotations_repoId` ON `annotations` (`repoId`)")
cursor.execute("CREATE INDEX IF NOT EXISTS `index_annotations_type` ON `annotations` (`type`)")

cursor.execute("""
CREATE TABLE IF NOT EXISTS `toc_items` (
    `id` TEXT NOT NULL,
    `documentId` TEXT NOT NULL,
    `headingId` TEXT NOT NULL,
    `title` TEXT NOT NULL,
    `level` INTEGER NOT NULL,
    `orderIndex` INTEGER NOT NULL,
    PRIMARY KEY(`id`)
)
""")

cursor.execute("CREATE INDEX IF NOT EXISTS `index_toc_items_documentId_orderIndex` ON `toc_items` (`documentId`, `orderIndex`)")

cursor.execute("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
cursor.execute("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '22cb5f83689961e78734ac8eb11a0825')")

# 2. Insert sample repositories
repos = [
    (
        "github:laralbir/md-gh-navigator",
        "md-gh-navigator",
        "laralbir",
        "GITHUB",
        "main",
        "README.md",
        None,
        1729280000000,
        '"etag_v100"',
        1
    ),
    (
        "local:clean-architecture-guide",
        "Guía Arquitectura Limpia",
        "Local",
        "LOCAL_STORAGE",
        "main",
        "arquitectura.md",
        "content://com.android.externalstorage.documents/tree/primary%3ADocuments",
        1729270000000,
        None,
        0
    )
]

cursor.executemany("""
INSERT INTO repositories VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
""", repos)

# 3. Sample Markdown content with authentic Spanish formatting
sample_md = """# MD GitHub Navigator

Lector eReader especializado en documentación técnica en **Markdown** almacenada en repositorios de **GitHub** y carpetas locales.

---

## 📖 Características Principales

- **Renderizado Nativo y Seguro:** Visualización híbrida mediante WebView local aislado con soporte para tipografía legible y temas e-ink.
- **Soporte Offline Completo:** Caché selectiva que descarga únicamente ficheros `.md` y recursos vinculados.
- **Optimización para Pantalla Mate:** Paleta de colores cálida *Scholar* diseñada específicamente para tablets **TCL NXTPAPER 11 Plus**.
- **Motor Text-to-Speech (TTS):** Locución fluida del texto descartando sintaxis markdown y enlaces crudos.
- **Anotaciones y Marcadores:** Resaltado cromático, notas al margen y exportación unificada.

```kotlin
// Inyección de dependencias con Hilt en Clean Architecture
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var preferencesRepository: UserPreferencesRepository
}
```

---

## 🎨 Temas Visuales Disponibles

1. **Scholar (Por defecto):** Fondo pergamino cálido `#FAF7F2` con texto sepia `#2C2420`.
2. **Paper (Minimalista):** Alto contraste blanco y negro para entornos iluminados.
3. **Neon Dev (IDE):** Tema oscuro para desarrolladores con acentos cian y magenta.
"""

doc_id = "github:laralbir/md-gh-navigator:README.md"
docs = [
    (
        doc_id,
        "github:laralbir/md-gh-navigator",
        "README.md",
        "MD GitHub Navigator — Documentación Técnica",
        sample_md,
        0.35,
        420,
        1729281000000,
        "sha_abc123",
        1
    ),
    (
        "local:clean-architecture-guide:arquitectura.md",
        "local:clean-architecture-guide",
        "arquitectura.md",
        "Principios de Clean Architecture en Android",
        "# Clean Architecture\n\nGuía de separación por capas: Core, Features y Domain.",
        0.10,
        120,
        1729272000000,
        "sha_def456",
        0
    )
]

cursor.executemany("""
INSERT INTO documents VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
""", docs)

# 4. Insert TOC items
toc_items = [
    ("toc_01", doc_id, "heading-1", "1. MD GitHub Navigator", 1, 0),
    ("toc_02", doc_id, "heading-2", "2. Características Principales", 2, 1),
    ("toc_03", doc_id, "heading-3", "3. Renderizado Nativo y Seguro", 3, 2),
    ("toc_04", doc_id, "heading-4", "4. Soporte Offline Completo", 3, 3),
    ("toc_05", doc_id, "heading-5", "5. Optimización NXTPAPER", 3, 4),
    ("toc_06", doc_id, "heading-6", "6. Temas Visuales Disponibles", 2, 5),
]

cursor.executemany("""
INSERT INTO toc_items VALUES (?, ?, ?, ?, ?, ?)
""", toc_items)

# 5. Insert sample annotations
annotations = [
    (
        "ann_01",
        doc_id,
        "github:laralbir/md-gh-navigator",
        "BOOKMARK",
        "Capítulo Temas Visuales",
        "",
        "Revisar colores para pantalla mate",
        "#FFD54F",
        0,
        0,
        0.55,
        1200,
        1729282000000,
        1729282000000
    ),
    (
        "ann_02",
        doc_id,
        "github:laralbir/md-gh-navigator",
        "HIGHLIGHT",
        None,
        "Paleta de colores cálida Scholar diseñada específicamente para tablets TCL NXTPAPER",
        None,
        "#81C784",
        120,
        210,
        0.35,
        780,
        1729282100000,
        1729282100000
    ),
    (
        "ann_03",
        doc_id,
        "github:laralbir/md-gh-navigator",
        "NOTE",
        "Nota Arquitectónica",
        "Inyección de dependencias con Hilt en Clean Architecture",
        "Asegurar que los ViewModels se instancien con HiltViewModel",
        "#FF8A80",
        300,
        360,
        0.45,
        990,
        1729282200000,
        1729282200000
    )
]

cursor.executemany("""
INSERT INTO annotations VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
""", annotations)

conn.commit()
conn.close()

print(f"Successfully generated populated database at: {DB_PATH}")
