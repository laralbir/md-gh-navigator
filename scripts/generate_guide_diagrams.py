#!/usr/bin/env python3
"""
Generates high-resolution graphical diagrams and composite mockups for MD GitHub Navigator documentation:
1. diagram_repo_sync_workflow.png
2. diagram_reader_layout_tablet_vs_phone.png
3. diagram_annotations_flow.png
4. diagram_tts_engine.png
5. diagram_settings_themes.png

Uses real screenshots captured from the live Android app in GitHub Modern Paper theme,
with clean GitHub palette (#FFFFFF, #0969DA, #1F2328, #D0D7DE, #2DA44E) and 0% brown.
"""

import os
from PIL import Image, ImageDraw, ImageFont

OUTPUT_DIR = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", "docs", "assets", "images", "user-guide"))
os.makedirs(OUTPUT_DIR, exist_ok=True)

# -----------------------------------------------------------------------------
# Color Palette: GitHub Modern Clean (Zero Brown)
# -----------------------------------------------------------------------------
COLOR_CANVAS_BG = (255, 255, 255, 255)       # Crisp white
COLOR_PANEL_BG = (246, 248, 250, 255)        # Slate card background #F6F8FA
COLOR_HEADER_BG = (31, 35, 40, 255)          # Dark slate #1F2328
COLOR_HEADER_TITLE = (255, 255, 255, 255)    # White title
COLOR_HEADER_SUB = (208, 215, 222, 255)      # Light slate subtitle #D0D7DE
COLOR_PRIMARY_BLUE = (9, 105, 218, 255)      # Electric GitHub Blue #0969DA
COLOR_ACCENT_GREEN = (45, 164, 78, 255)      # Success Green #2DA44E
COLOR_ACCENT_PURPLE = (130, 80, 223, 255)    # Purple accent #8250DF
COLOR_BORDER = (208, 215, 222, 255)          # Border gray #D0D7DE
COLOR_TEXT_PRIMARY = (31, 35, 40, 255)       # Text slate #1F2328
COLOR_TEXT_SECONDARY = (87, 96, 106, 255)    # Secondary slate #57606A
COLOR_CHASSIS_BG = (240, 242, 245, 255)      # Neutral device chassis


def find_font():
    candidates = [
        "/Library/Fonts/Arial Unicode.ttf",
        "/System/Library/Fonts/Supplemental/Arial.ttf",
        "/System/Library/Fonts/Helvetica.ttc",
        "/System/Library/Fonts/SFNS.ttf",
    ]
    for c in candidates:
        if os.path.exists(c):
            return c
    return None


def find_bold_font():
    candidates = [
        "/System/Library/Fonts/Supplemental/Arial Bold.ttf",
        "/Library/Fonts/Arial Bold.ttf",
        "/Library/Fonts/Arial Unicode.ttf",
    ]
    for c in candidates:
        if os.path.exists(c):
            return c
    return find_font()


FONT_PATH = find_font()
FONT_BOLD_PATH = find_bold_font()


def get_font(size, bold=False):
    path = FONT_BOLD_PATH if bold else FONT_PATH
    if path and os.path.exists(path):
        try:
            return ImageFont.truetype(path, size)
        except Exception:
            pass
    return ImageFont.load_default()


def draw_header(draw, title, subtitle, width=1000):
    draw.rectangle([0, 0, width, 70], fill=COLOR_HEADER_BG)
    draw.text((24, 14), title, fill=COLOR_HEADER_TITLE, font=get_font(20, bold=True))
    draw.text((24, 42), subtitle, fill=COLOR_HEADER_SUB, font=get_font(13))
    draw.line([(0, 69), (width, 69)], fill=COLOR_PRIMARY_BLUE, width=3)


def add_device_frame(img, corner_radius=14, border_color=COLOR_BORDER, border_width=3):
    """Wraps an image in a modern device chassis frame."""
    w, h = img.size
    framed = Image.new("RGBA", (w + border_width * 2, h + border_width * 2), (0, 0, 0, 0))
    draw = ImageDraw.Draw(framed)
    draw.rounded_rectangle(
        [0, 0, w + border_width * 2 - 1, h + border_width * 2 - 1],
        radius=corner_radius,
        fill=COLOR_CHASSIS_BG,
        outline=border_color,
        width=border_width
    )
    framed.paste(img, (border_width, border_width))
    return framed


# -----------------------------------------------------------------------------
# 1. Repo Sync & Cache Workflow Diagram
# -----------------------------------------------------------------------------
def generate_repo_sync_diagram():
    size = (1000, 520)
    canvas = Image.new("RGBA", size, COLOR_CANVAS_BG)
    draw = ImageDraw.Draw(canvas)
    draw_header(draw, "1. Flujo de Sincronización y Caché Selectivo",
                "MD GitHub Navigator • Arquitectura de Importación Offline y Captura Real del Diálogo", 1000)

    # Left: 2 Pipeline Blocks
    steps = [
        ("Fuentes de Entrada", [
            "• GitHub API v3 (ETag incremental)",
            "• Repositorios públicos y privados",
            "• Almacenamiento local (Android SAF)",
            "• Cifrado Keystore con Google Tink"
        ], 30, 95, 280, 180, COLOR_PANEL_BG, COLOR_PRIMARY_BLUE),
        ("Filtro Selectivo de Contenido", [
            "• Descarga exclusiva de ficheros .md",
            "• Descarga de imágenes y diagramas vinculados",
            "• Omisión de binarios, dependencias y builds",
            "• Ahorro >90% de almacenamiento y batería"
        ], 30, 300, 280, 180, COLOR_PANEL_BG, COLOR_ACCENT_GREEN),
    ]

    for title, lines, x, y, w, h, bg_color, border_color in steps:
        draw.rounded_rectangle([x, y, x + w, y + h], radius=10, fill=bg_color, outline=border_color, width=2)
        draw.rectangle([x, y, x + w, y + 36], fill=border_color)
        draw.text((x + 12, y + 8), title, fill=(255, 255, 255, 255), font=get_font(13, bold=True))
        for i, line in enumerate(lines):
            draw.text((x + 12, y + 48 + i * 28), line, fill=COLOR_TEXT_PRIMARY, font=get_font(11))

    # Connecting Arrow downwards between steps
    draw.line([(170, 276), (170, 298)], fill=COLOR_PRIMARY_BLUE, width=3)
    draw.polygon([(165, 293), (175, 293), (170, 300)], fill=COLOR_PRIMARY_BLUE)

    # Center-Right: Real Screenshot of "Añadir a la Biblioteca" modal
    modal_path = os.path.join(OUTPUT_DIR, "screenshot_add_repository_modal.png")
    if os.path.exists(modal_path):
        modal_img = Image.open(modal_path).convert("RGBA")
        crop_box = (150, 700, 1194, 2380)
        modal_crop = modal_img.crop(crop_box)
        target_h = 385
        target_w = int(modal_crop.width * (target_h / modal_crop.height))
        modal_resized = modal_crop.resize((target_w, target_h), Image.Resampling.LANCZOS)
        framed_modal = add_device_frame(modal_resized, corner_radius=12, border_color=COLOR_PRIMARY_BLUE, border_width=2)
        canvas.paste(framed_modal, (340, 95), framed_modal)

    # Right: Result Card
    rx, ry, rw, rh = 620, 95, 350, 385
    draw.rounded_rectangle([rx, ry, rx + rw, ry + rh], radius=10, fill=COLOR_PANEL_BG, outline=COLOR_PRIMARY_BLUE, width=2)
    draw.rectangle([rx, ry, rx + rw, ry + 36], fill=COLOR_PRIMARY_BLUE)
    draw.text((rx + 12, ry + 8), "Almacenamiento Local Room & Lectura", fill=(255, 255, 255, 255), font=get_font(13, bold=True))

    result_points = [
        "1. Validación de ETag y ramas en segundo plano.",
        "2. Persistencia en base de datos SQLite (Room v2):",
        "   - Repositorios anclados / orden alfabético.",
        "   - Documentos con progreso y último scroll.",
        "   - Árbol de contenidos TOC generado localmente.",
        "3. Lector ComposeMarkdownReader:",
        "   - Aislamiento de seguridad total en WebView.",
        "   - Sin peticiones externas durante la lectura.",
        "   - Experiencia de lectura 100% fuera de línea."
    ]
    for i, pt in enumerate(result_points):
        draw.text((rx + 14, ry + 50 + i * 32), pt, fill=COLOR_TEXT_PRIMARY, font=get_font(12))

    # Outer border
    draw.rectangle([0, 0, size[0] - 1, size[1] - 1], outline=COLOR_BORDER, width=1)

    path = os.path.join(OUTPUT_DIR, "diagram_repo_sync_workflow.png")
    canvas.save(path, "PNG")
    print(f"Generated: {path}")


# -----------------------------------------------------------------------------
# 2. Reader Layout: Smartphone vs Tablet NXTPAPER 11 Plus
# -----------------------------------------------------------------------------
def generate_reader_layout_diagram():
    size = (1080, 560)
    canvas = Image.new("RGBA", size, COLOR_CANVAS_BG)
    draw = ImageDraw.Draw(canvas)
    draw_header(draw, "2. Experiencia eReader: Smartphone vs Tablet TCL NXTPAPER 11 Plus",
                "Diseño Adaptativo con Navigation Drawer en Móvil y Doble Panel Split en Tablet (Capturas Reales)", 1080)

    # Left: Smartphone real screenshot
    phone_path = os.path.join(OUTPUT_DIR, "screenshot_reader_reading.png")
    if os.path.exists(phone_path):
        phone_img = Image.open(phone_path).convert("RGBA")
        target_h = 420
        target_w = int(phone_img.width * (target_h / phone_img.height))
        phone_resized = phone_img.resize((target_w, target_h), Image.Resampling.LANCZOS)
        framed_phone = add_device_frame(phone_resized, corner_radius=14, border_color=COLOR_BORDER, border_width=3)
        canvas.paste(framed_phone, (30, 90), framed_phone)

    # Smartphone label
    draw.text((30, 520), "Smartphone: Lienzo a pantalla completa y menús en Drawer lateral",
              fill=COLOR_TEXT_PRIMARY, font=get_font(11, bold=True))

    # Right: Tablet real dual-pane screenshot
    tablet_path = os.path.join(OUTPUT_DIR, "screenshot_tablet_dual_pane_reader.png")
    if os.path.exists(tablet_path):
        tablet_img = Image.open(tablet_path).convert("RGBA")
        target_w = 780
        target_h = int(tablet_img.height * (target_w / tablet_img.width))
        tablet_resized = tablet_img.resize((target_w, target_h), Image.Resampling.LANCZOS)
        framed_tablet = add_device_frame(tablet_resized, corner_radius=14, border_color=COLOR_BORDER, border_width=3)
        canvas.paste(framed_tablet, (270, 90), framed_tablet)

    # Tablet label
    draw.text((270, 520), "TCL NXTPAPER 11 Plus: Doble panel simultáneo fijo (Índice / Archivos a la izquierda y lectura Markdown a la derecha)",
              fill=COLOR_TEXT_PRIMARY, font=get_font(11, bold=True))

    # Outer border
    draw.rectangle([0, 0, size[0] - 1, size[1] - 1], outline=COLOR_BORDER, width=1)

    path = os.path.join(OUTPUT_DIR, "diagram_reader_layout_tablet_vs_phone.png")
    canvas.save(path, "PNG")
    print(f"Generated: {path}")


# -----------------------------------------------------------------------------
# 3. Annotations, Bookmarks & Markdown Export
# -----------------------------------------------------------------------------
def generate_annotations_diagram():
    size = (1040, 540)
    canvas = Image.new("RGBA", size, COLOR_CANVAS_BG)
    draw = ImageDraw.Draw(canvas)
    draw_header(draw, "3. Marcadores, Resaltados y Exportación de Notas",
                "MD GitHub Navigator • Cajón Unificado de Anotaciones y Diálogo de Marcadores (Capturas Reales)", 1040)

    # Screenshot 1: Drawer
    drawer_path = os.path.join(OUTPUT_DIR, "screenshot_reader_annotations_drawer.png")
    if os.path.exists(drawer_path):
        drawer_img = Image.open(drawer_path).convert("RGBA")
        target_h = 410
        target_w = int(drawer_img.width * (target_h / drawer_img.height))
        drawer_resized = drawer_img.resize((target_w, target_h), Image.Resampling.LANCZOS)
        framed_drawer = add_device_frame(drawer_resized, corner_radius=14, border_color=COLOR_BORDER, border_width=3)
        canvas.paste(framed_drawer, (30, 90), framed_drawer)

    draw.text((30, 510), "Panel Unificado: Pestañas Todos, Marcadores y Resaltados",
              fill=COLOR_TEXT_PRIMARY, font=get_font(11, bold=True))

    # Screenshot 2: Add Bookmark Dialog
    dialog_path = os.path.join(OUTPUT_DIR, "screenshot_add_bookmark_dialog.png")
    if os.path.exists(dialog_path):
        dialog_img = Image.open(dialog_path).convert("RGBA")
        crop_box = (150, 1000, 1194, 2100)
        dialog_crop = dialog_img.crop(crop_box)
        target_h = 240
        target_w = int(dialog_crop.width * (target_h / dialog_crop.height))
        dialog_resized = dialog_crop.resize((target_w, target_h), Image.Resampling.LANCZOS)
        framed_dialog = add_device_frame(dialog_resized, corner_radius=12, border_color=COLOR_PRIMARY_BLUE, border_width=2)
        canvas.paste(framed_dialog, (260, 90), framed_dialog)

    draw.text((260, 340), "Diálogo Modal: Añadir Marcador con título y nota",
              fill=COLOR_TEXT_PRIMARY, font=get_font(11, bold=True))

    # Right Card: Export and Features
    ex, ey, ew, eh = 550, 90, 460, 410
    draw.rounded_rectangle([ex, ey, ex + ew, ey + eh], radius=10, fill=COLOR_PANEL_BG, outline=COLOR_PRIMARY_BLUE, width=2)
    draw.rectangle([ex, ey, ex + ew, ey + 36], fill=COLOR_PRIMARY_BLUE)
    draw.text((ex + 12, ey + 8), "Exportador Markdown y Navegación Rápida", fill=(255, 255, 255, 255), font=get_font(13, bold=True))

    features = [
        "• Salto Instantáneo al Progreso Exacto:",
        "  Un toque sobre cualquier tarjeta salta al porcentaje",
        "  de scroll guardado en Room SQLite.",
        "• Marcadores Rápidos (Dog-Ear):",
        "  Permite registrar la posición de lectura con título",
        "  descriptivo y notas personales asociadas.",
        "• Exportación Completa a Markdown (.md):",
        "  Genera un fichero estructurado con marcadores,",
        "  citas textuales y notas, listo para compartir vía",
        "  Android Share Sheet o integrar en Obsidian / Logseq."
    ]
    for i, line in enumerate(features):
        draw.text((ex + 14, ey + 50 + i * 29), line, fill=COLOR_TEXT_PRIMARY, font=get_font(12))

    # Outer border
    draw.rectangle([0, 0, size[0] - 1, size[1] - 1], outline=COLOR_BORDER, width=1)

    path = os.path.join(OUTPUT_DIR, "diagram_annotations_flow.png")
    canvas.save(path, "PNG")
    print(f"Generated: {path}")


# -----------------------------------------------------------------------------
# 4. Text-to-Speech (TTS) Engine Architecture
# -----------------------------------------------------------------------------
def generate_tts_diagram():
    size = (1040, 520)
    canvas = Image.new("RGBA", size, COLOR_CANVAS_BG)
    draw = ImageDraw.Draw(canvas)
    draw_header(draw, "4. Arquitectura del Motor Text-to-Speech (TTS)",
                "MD GitHub Navigator • Limpieza Sintáctica, Locución Android y Barra de Reproducción en Vivo", 1040)

    # Pipeline Steps
    blocks = [
        ("1. Documento Markdown", [
            "• Párrafos y listas",
            "• Títulos (# y ##)",
            "• Enlaces [texto](url)",
            "• Bloques ```código```"
        ], 30, 90, 220, 180, COLOR_PANEL_BG, COLOR_PRIMARY_BLUE),
        ("2. MarkdownSpeechCleaner", [
            "• Filtra URLs crudas",
            "• Omite sintaxis y código",
            "• Limpia símbolos markdown",
            "• Salida: Prosa fluida"
        ], 280, 90, 220, 180, (235, 245, 255, 255), COLOR_PRIMARY_BLUE),
        ("3. AndroidTtsSpeechEngine", [
            "• API android.speech.tts",
            "• Detección de idioma",
            "• Voces naturales de Android",
            "• Velocidades: 0.8x a 2.0x"
        ], 530, 90, 220, 180, COLOR_PANEL_BG, COLOR_PRIMARY_BLUE),
        ("4. TtsPlayerBar (UI)", [
            "• Botón Play / Pausa",
            "• Frase ant / siguiente",
            "• Selector de velocidad",
            "• Indicador de progreso"
        ], 780, 90, 220, 180, (230, 246, 236, 255), COLOR_ACCENT_GREEN),
    ]

    for title, lines, x, y, w, h, bg_color, border_color in blocks:
        draw.rounded_rectangle([x, y, x + w, y + h], radius=10, fill=bg_color, outline=border_color, width=2)
        draw.rectangle([x, y, x + w, y + 36], fill=border_color)
        draw.text((x + 10, y + 8), title, fill=(255, 255, 255, 255), font=get_font(12, bold=True))
        for i, line in enumerate(lines):
            draw.text((x + 10, y + 48 + i * 26), line, fill=COLOR_TEXT_PRIMARY, font=get_font(11))

    # Connecting arrows
    for arrow_x in [253, 503, 753]:
        draw.line([(arrow_x, 180), (arrow_x + 22, 180)], fill=COLOR_PRIMARY_BLUE, width=3)
        draw.polygon([(arrow_x + 22, 175), (arrow_x + 22, 185), (arrow_x + 27, 180)], fill=COLOR_PRIMARY_BLUE)

    # Bottom: Real Screenshot Crop of the TtsPlayerBar
    tts_screen_path = os.path.join(OUTPUT_DIR, "screenshot_reader_tts.png")
    if os.path.exists(tts_screen_path):
        tts_img = Image.open(tts_screen_path).convert("RGBA")
        crop_box = (20, 2600, 1324, 2970)
        player_crop = tts_img.crop(crop_box)
        target_w = 970
        target_h = int(player_crop.height * (target_w / player_crop.width))
        player_resized = player_crop.resize((target_w, target_h), Image.Resampling.LANCZOS)
        framed_player = add_device_frame(player_resized, corner_radius=10, border_color=COLOR_PRIMARY_BLUE, border_width=2)
        canvas.paste(framed_player, (30, 310), framed_player)

    draw.text((30, 485), "Barra de Reproducción Real: Control de locución continua con seguimiento de frase y velocidad",
              fill=COLOR_TEXT_PRIMARY, font=get_font(12, bold=True))

    # Outer border
    draw.rectangle([0, 0, size[0] - 1, size[1] - 1], outline=COLOR_BORDER, width=1)

    path = os.path.join(OUTPUT_DIR, "diagram_tts_engine.png")
    canvas.save(path, "PNG")
    print(f"Generated: {path}")


# -----------------------------------------------------------------------------
# 5. Settings, Themes & Storage Manager
# -----------------------------------------------------------------------------
def generate_settings_diagram():
    size = (1040, 560)
    canvas = Image.new("RGBA", size, COLOR_CANVAS_BG)
    draw = ImageDraw.Draw(canvas)
    draw_header(draw, "5. Ajustes, Paletas de Color y Gestor de Almacenamiento",
                "MD GitHub Navigator • Temas Visuales (GitHub Modern / Slate / Neon Dev) y Control de Caché (Capturas Reales)", 1040)

    # Screenshot 1: GitHub Modern Settings (screenshot_settings_scholar.png contains the light theme settings)
    scholar_path = os.path.join(OUTPUT_DIR, "screenshot_settings_scholar.png")
    if os.path.exists(scholar_path):
        sc_img = Image.open(scholar_path).convert("RGBA")
        target_h = 420
        target_w = int(sc_img.width * (target_h / sc_img.height))
        sc_resized = sc_img.resize((target_w, target_h), Image.Resampling.LANCZOS)
        framed_sc = add_device_frame(sc_resized, corner_radius=12, border_color=COLOR_PRIMARY_BLUE, border_width=2)
        canvas.paste(framed_sc, (30, 90), framed_sc)

    draw.text((30, 525), "Tema GitHub Modern (Blanco Puro)", fill=COLOR_TEXT_PRIMARY, font=get_font(11, bold=True))

    # Screenshot 2: Neon Dev Settings
    neon_path = os.path.join(OUTPUT_DIR, "screenshot_settings_neon.png")
    if os.path.exists(neon_path):
        ne_img = Image.open(neon_path).convert("RGBA")
        target_h = 420
        target_w = int(ne_img.width * (target_h / ne_img.height))
        ne_resized = ne_img.resize((target_w, target_h), Image.Resampling.LANCZOS)
        framed_ne = add_device_frame(ne_resized, corner_radius=12, border_color=(0, 229, 255, 255), border_width=2)
        canvas.paste(framed_ne, (260, 90), framed_ne)

    draw.text((260, 525), "Tema Neon Dev (Alto Contraste)", fill=COLOR_TEXT_PRIMARY, font=get_font(11, bold=True))

    # Screenshot 3: Storage Manager section
    storage_path = os.path.join(OUTPUT_DIR, "screenshot_settings_storage.png")
    if os.path.exists(storage_path):
        st_img = Image.open(storage_path).convert("RGBA")
        crop_box = (40, 1650, 1300, 2750)
        st_crop = st_img.crop(crop_box)
        target_w = 510
        target_h = int(st_crop.height * (target_w / st_crop.width))
        st_resized = st_crop.resize((target_w, target_h), Image.Resampling.LANCZOS)
        framed_st = add_device_frame(st_resized, corner_radius=12, border_color=COLOR_PRIMARY_BLUE, border_width=2)
        canvas.paste(framed_st, (495, 90), framed_st)

    draw.text((495, 525), "Gestor de Almacenamiento y Caché: Limpieza en un toque sin perder progreso",
              fill=COLOR_TEXT_PRIMARY, font=get_font(11, bold=True))

    # Outer border
    draw.rectangle([0, 0, size[0] - 1, size[1] - 1], outline=COLOR_BORDER, width=1)

    path = os.path.join(OUTPUT_DIR, "diagram_settings_themes.png")
    canvas.save(path, "PNG")
    print(f"Generated: {path}")


if __name__ == "__main__":
    generate_repo_sync_diagram()
    generate_reader_layout_diagram()
    generate_annotations_diagram()
    generate_tts_diagram()
    generate_settings_diagram()
