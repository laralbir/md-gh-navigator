# Frequently Asked Questions (FAQ)

Answers to common questions regarding **MD GitHub Navigator** functionality, privacy, and device support.

---

### Does the app work completely offline?
**Yes.** Thanks to the selective offline cache architecture, once you synchronize a remote GitHub repository or link a local folder via Storage Access Framework, all Markdown documents, diagrams, and linked images are stored locally in SQLite and private storage for instant offline reading.

### Does the app download heavy compiled binaries or source code files?
**No.** To protect your device's internal storage, the app strictly filters out binaries, executables, node_modules, and media files unlinked from markdown. If you click on an external binary download link, it will be handed over to your system's web browser or download manager.

### Can I use the app with private company or personal repositories?
**Yes.** You can authenticate seamlessly using **OAuth Device Flow** or supply a Personal Access Token (PAT) with read-only repository permissions (`repo:read`). All credentials are encrypted using Google Tink and authenticated Android Keystore hardware keys.

### What visual reading themes are available?
The application defaults to **GitHub Modern (Paper)**, featuring a crisp white background, electric blue accents, and high contrast for sharp technical readability. In addition, you can choose the **Scholar** theme (softened contrast and balanced tones ideal for prolonged sessions and matte screens like the **TCL NXTPAPER 11 Plus**) or the **Neon Dev** theme (cyber dark mode). You can switch themes with a single tap from the top action bar or Settings.

### Can I export the annotations and notes I make while reading?
**Yes.** All highlighted passages, notes, and reading bookmarks can be compiled into a single structured Markdown file (`.md`) and shared to Obsidian, Logseq, Notion, Google Drive, or email via Android's native share sheet.
