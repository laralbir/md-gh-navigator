#!/usr/bin/env python3
"""
Generates high-resolution graphical diagrams for MD GitHub Navigator documentation:
1. diagram_repo_sync_workflow.png
2. diagram_reader_layout_tablet_vs_phone.png
3. diagram_annotations_flow.png
4. diagram_tts_engine.png
5. diagram_settings_themes.png
"""

import os
from PIL import Image, ImageDraw, ImageFont

OUTPUT_DIR = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", "docs", "assets", "images", "user-guide"))
os.makedirs(OUTPUT_DIR, exist_ok=True)


def draw_header(draw, title, subtitle, width=800):
    draw.rectangle([0, 0, width, 60], fill=(44, 36, 32, 255))
    draw.text((24, 12), title, fill=(250, 247, 242, 255), font_size=18)
    draw.text((24, 34), subtitle, fill=(215, 204, 200, 255), font_size=12)


def generate_repo_sync_diagram():
    size = (800, 420)
    img = Image.new("RGBA", size, (250, 247, 242, 255))
    draw = ImageDraw.Draw(img)
    draw_header(draw, "1. Flujo de Sincronización y Caché Selectivo",
                "MD GitHub Navigator • Arquitectura de Importación Offline", 800)

    # 4 columns/steps
    steps = [
        ("Fuentes de Datos", ["• GitHub API v3 (ETag)", "• OAuth Device Flow / PAT", "• Android SAF Local"], 30, 90, 160, 260, (239, 235, 233, 255), (93, 64, 55, 255)),
        ("Filtro Selectivo", ["• Descarga exclusiva .md", "• Imágenes & assets", "• Omite binarios pesados", "• Ahorro >90% espacio"], 230, 90, 160, 260, (245, 240, 235, 255), (121, 85, 72, 255)),
        ("Almacenamiento", ["• Room SQLite (v2)", "• DataStore Cifrado Tink", "• Disk Cache privada", "• Metadatos & Progreso"], 430, 90, 160, 260, (239, 235, 233, 255), (93, 64, 55, 255)),
        ("Lector eReader", ["• ComposeMarkdownReader", "• Assets locales aislados", "• Diagramas locales", "• Lectura 100% offline"], 630, 90, 140, 260, (220, 237, 200, 255), (51, 105, 30, 255)),
    ]

    for title, lines, x, y, w, h, bg_color, border_color in steps:
        draw.rounded_rectangle([x, y, x + w, y + h], radius=10, fill=bg_color, outline=border_color, width=2)
        # Header inside box
        draw.rectangle([x, y, x + w, y + 36], fill=border_color)
        draw.text((x + 10, y + 10), title, fill=(255, 255, 255, 255), font_size=13)
        # Bullet points
        for i, line in enumerate(lines):
            draw.text((x + 10, y + 50 + i * 28), line, fill=(44, 36, 32, 255), font_size=11)

    # Connecting arrows
    for arrow_x in [195, 395, 595]:
        draw.line([(arrow_x, 220), (arrow_x + 30, 220)], fill=(93, 64, 55, 255), width=3)
        draw.polygon([(arrow_x + 30, 215), (arrow_x + 30, 225), (arrow_x + 35, 220)], fill=(93, 64, 55, 255))

    # Bottom status note
    draw.rounded_rectangle([30, 365, 770, 400], radius=6, fill=(235, 230, 224, 255))
    draw.text((45, 375), "✔ Optimizado para tablets TCL NXTPAPER y smartphones Android con cifrado Keystore",
              fill=(93, 64, 55, 255), font_size=12)

    path = os.path.join(OUTPUT_DIR, "diagram_repo_sync_workflow.png")
    img.save(path, "PNG")
    print(f"Generated: {path}")


def generate_reader_layout_diagram():
    size = (880, 440)
    img = Image.new("RGBA", size, (250, 247, 242, 255))
    draw = ImageDraw.Draw(img)
    draw_header(draw, "2. Experiencia eReader: Smartphone vs Tablet NXTPAPER 11 Plus",
                "Diseño Adaptativo con Navigation Drawer y Dual-Pane Split Layout", 880)

    # Panel A: Smartphone (Portrait)
    sx, sy, sw, sh = 40, 85, 220, 330
    draw.rounded_rectangle([sx, sy, sx + sw, sy + sh], radius=16, fill=(20, 20, 22, 255), outline=(90, 80, 75, 255), width=3)
    # Screen
    ssx, ssy, ssw, ssh = sx + 8, sy + 8, sw - 16, sh - 16
    draw.rounded_rectangle([ssx, ssy, ssx + ssw, ssy + ssh], radius=10, fill=(250, 247, 242, 255))
    # Top Bar
    draw.rectangle([ssx, ssy, ssx + ssw, ssy + 32], fill=(93, 64, 55, 255))
    draw.text((ssx + 8, ssy + 8), "☰  README.md  🔍 🔊", fill=(255, 255, 255, 255), font_size=10)
    # Drawer overlay hint
    draw.rounded_rectangle([ssx, ssy + 32, ssx + 100, ssy + ssh - 26], radius=4, fill=(243, 237, 228, 255), outline=(188, 170, 164, 255), width=1)
    draw.text((ssx + 6, ssy + 40), "📑 TOC Drawer", fill=(93, 64, 55, 255), font_size=8)
    for i, t in enumerate(["• Intro", "• Install", "• Usage"]):
        draw.text((ssx + 8, ssy + 55 + i * 16), t, fill=(121, 85, 72, 255), font_size=8)
    # Content lines
    for i in range(10):
        draw.line([(ssx + 110, ssy + 45 + i * 16), (ssx + ssw - 10, ssy + 45 + i * 16)], fill=(188, 170, 164, 255), width=2)
    # Bottom bar
    draw.rectangle([ssx, ssy + ssh - 26, ssx + ssw, ssy + ssh], fill=(239, 235, 233, 255))
    draw.text((ssx + 10, ssy + ssh - 18), "📖 45%  •  Capítulo 2", fill=(93, 64, 55, 255), font_size=8)

    draw.text((sx + 35, sy + sh + 5), "Smartphone (Drawer Modal)", fill=(44, 36, 32, 255), font_size=12)

    # Panel B: Tablet TCL NXTPAPER 11 Plus (Split-Pane)
    tx, ty, tw, th = 320, 85, 520, 330
    draw.rounded_rectangle([tx, ty, tx + tw, ty + th], radius=20, fill=(20, 20, 22, 255), outline=(90, 80, 75, 255), width=3)
    # Screen
    tsx, tsy, tsw, tsh = tx + 10, ty + 10, tw - 20, th - 20
    draw.rounded_rectangle([tsx, tsy, tsx + tsw, tsy + tsh], radius=12, fill=(250, 247, 242, 255))

    # Top Bar across
    draw.rectangle([tsx, tsy, tsx + tsw, tsy + 36], fill=(93, 64, 55, 255))
    draw.text((tsx + 12, tsy + 10), "📚 MD GitHub Navigator  |  facebook/react (main)  —  README.md", fill=(255, 255, 255, 255), font_size=11)
    draw.text((tsx + tsw - 130, tsy + 10), "⚙️ Ajustes   📑 Notas", fill=(255, 255, 255, 255), font_size=11)

    # Permanent Left Sidebar: TOC & Index
    toc_w = 150
    draw.rectangle([tsx, tsy + 36, tsx + toc_w, tsy + tsh - 40], fill=(243, 237, 228, 255))
    draw.line([(tsx + toc_w, tsy + 36), (tsx + toc_w, tsy + tsh - 40)], fill=(211, 196, 184, 255), width=2)
    draw.text((tsx + 10, tsy + 46), "TABLA DE CONTENIDOS", fill=(93, 64, 55, 255), font_size=9)
    toc_items = ["1. Getting Started", "2. Core Architecture", "  2.1 Components", "  2.2 Hooks", "3. Testing Guidelines", "4. API Reference"]
    for i, it in enumerate(toc_items):
        draw.text((tsx + 10, tsy + 68 + i * 20), it, fill=(78, 52, 46, 255), font_size=9)

    # Center: Reading Canvas
    cx = tsx + toc_w + 14
    cw = tsw - toc_w - 28
    draw.text((cx, tsy + 46), "# Getting Started with React", fill=(44, 36, 32, 255), font_size=14)
    draw.text((cx, tsy + 70), "React is a JavaScript library for building user interfaces.", fill=(78, 52, 46, 255), font_size=10)

    # Diagram preview inside tablet
    diag_w = 180
    diag_h = 75
    draw.rounded_rectangle([cx, tsy + 95, cx + diag_w, tsy + 95 + diag_h], radius=6, fill=(255, 253, 249, 255), outline=(211, 196, 184, 255), width=1)
    draw.text((cx + 10, tsy + 100), "Mermaid Diagram Preview", fill=(141, 110, 99, 255), font_size=8)
    draw.line([(cx + 20, tsy + 130), (cx + 80, tsy + 130)], fill=(93, 64, 55, 255), width=2)
    draw.rounded_rectangle([cx + 80, tsy + 120, cx + 160, tsy + 140], radius=3, fill=(239, 235, 233, 255))
    draw.text((cx + 85, tsy + 125), "Zoomable (Pinch)", fill=(93, 64, 55, 255), font_size=7)

    # Highlighted snippet
    draw.rectangle([cx + diag_w + 12, tsy + 95, cx + cw - 10, tsy + 135], fill=(255, 224, 130, 200))
    draw.text((cx + diag_w + 16, tsy + 100), "⭐ Resaltado & Nota:", fill=(44, 36, 32, 255), font_size=8)
    draw.text((cx + diag_w + 16, tsy + 115), "Revisar arquitectura en la versión v1.0", fill=(78, 52, 46, 255), font_size=8)

    # Bottom TTS bar across tablet
    draw.rounded_rectangle([tsx + 6, tsy + tsh - 36, tsx + tsw - 6, tsy + tsh - 4], radius=6, fill=(239, 235, 233, 255), outline=(211, 196, 184, 255), width=1)
    draw.text((tsx + 16, tsy + tsh - 24), "🔊  Lectura por voz  ▶ Pausa   02:15 / 08:40   Velocidad: 1.0x   Voz: Android TTS Natural", fill=(93, 64, 55, 255), font_size=10)

    draw.text((tx + 120, ty + th + 5), "TCL NXTPAPER 11 Plus (Doble Panel Fijo)", fill=(44, 36, 32, 255), font_size=12)

    path = os.path.join(OUTPUT_DIR, "diagram_reader_layout_tablet_vs_phone.png")
    img.save(path, "PNG")
    print(f"Generated: {path}")


def generate_annotations_diagram():
    size = (800, 400)
    img = Image.new("RGBA", size, (250, 247, 242, 255))
    draw = ImageDraw.Draw(img)
    draw_header(draw, "3. Marcadores, Resaltados y Exportación de Notas",
                "MD GitHub Navigator • Flujo de Anotaciones y Sincronización en Room", 800)

    steps = [
        ("1. Selección o Marcador", ["• Selección de texto", "• Gesto dog-ear", "• Posición exacta de scroll", "• Progreso porcentual %"], 30, 90, 160, 250, (239, 235, 233, 255), (93, 64, 55, 255)),
        ("2. Modal Flotante", ["• 4 Colores resaltado", "• Nota personal opcional", "• Guardado instantáneo", "• Persistencia Room DB"], 230, 90, 160, 250, (255, 248, 225, 255), (245, 127, 23, 255)),
        ("3. Cajón Unificado", ["• Pestaña Todos", "• Pestaña Marcadores", "• Pestaña Resaltados", "• Salto táctil instantáneo"], 430, 90, 160, 250, (239, 235, 233, 255), (93, 64, 55, 255)),
        ("4. Compartir & Exportar", ["• Exportación Markdown", "• Formato .md estructurado", "• Android Share Sheet", "• Compatible con Obsidian"], 630, 90, 140, 250, (220, 237, 200, 255), (51, 105, 30, 255)),
    ]

    for title, lines, x, y, w, h, bg_color, border_color in steps:
        draw.rounded_rectangle([x, y, x + w, y + h], radius=10, fill=bg_color, outline=border_color, width=2)
        draw.rectangle([x, y, x + w, y + 36], fill=border_color)
        draw.text((x + 10, y + 10), title, fill=(255, 255, 255, 255), font_size=12)
        for i, line in enumerate(lines):
            draw.text((x + 10, y + 50 + i * 26), line, fill=(44, 36, 32, 255), font_size=11)

    for arrow_x in [195, 395, 595]:
        draw.line([(arrow_x, 215), (arrow_x + 30, 215)], fill=(93, 64, 55, 255), width=3)
        draw.polygon([(arrow_x + 30, 210), (arrow_x + 30, 220), (arrow_x + 35, 215)], fill=(93, 64, 55, 255))

    draw.rounded_rectangle([30, 355, 770, 385], radius=6, fill=(235, 230, 224, 255))
    draw.text((45, 363), "✔ Salto con un toque mediante evaluación JavaScript de window.ReaderApi.scrollToProgress()",
              fill=(93, 64, 55, 255), font_size=11)

    path = os.path.join(OUTPUT_DIR, "diagram_annotations_flow.png")
    img.save(path, "PNG")
    print(f"Generated: {path}")


def generate_tts_diagram():
    size = (800, 380)
    img = Image.new("RGBA", size, (250, 247, 242, 255))
    draw = ImageDraw.Draw(img)
    draw_header(draw, "4. Arquitectura del Motor Text-to-Speech (TTS)",
                "MD GitHub Navigator • Limpieza Sintáctica y Locución Continua", 800)

    blocks = [
        ("Documento Markdown", ["• Párrafos de texto", "• Títulos (#, ##)", "• Enlaces [texto](url)", "• Bloques de código ```"], 30, 85, 160, 230, (239, 235, 233, 255), (93, 64, 55, 255)),
        ("MarkdownSpeechCleaner", ["• Filtra enlaces crudos", "• Omite bloques código", "• Preserva puntuación", "• Salida: Texto natural"], 230, 85, 170, 230, (255, 243, 224, 255), (230, 81, 0, 255)),
        ("AndroidTtsSpeechEngine", ["• android.speech.tts", "• Selección de voces", "• Velocidad 0.5x - 2.0x", "• Pausa / Reanudación"], 440, 85, 160, 230, (239, 235, 233, 255), (93, 64, 55, 255)),
        ("TtsPlayerBar (UI)", ["• Botón Play / Pausa", "• Salto 15s / Párrafos", "• Selector de velocidad", "• Scroll sincronizado"], 640, 85, 130, 230, (220, 237, 200, 255), (51, 105, 30, 255)),
    ]

    for title, lines, x, y, w, h, bg_color, border_color in blocks:
        draw.rounded_rectangle([x, y, x + w, y + h], radius=10, fill=bg_color, outline=border_color, width=2)
        draw.rectangle([x, y, x + w, y + 36], fill=border_color)
        draw.text((x + 8, y + 10), title, fill=(255, 255, 255, 255), font_size=11)
        for i, line in enumerate(lines):
            draw.text((x + 8, y + 50 + i * 26), line, fill=(44, 36, 32, 255), font_size=10)

    for arrow_x in [195, 405, 605]:
        draw.line([(arrow_x, 200), (arrow_x + 30, 200)], fill=(93, 64, 55, 255), width=3)
        draw.polygon([(arrow_x + 30, 195), (arrow_x + 30, 205), (arrow_x + 35, 200)], fill=(93, 64, 55, 255))

    draw.rounded_rectangle([30, 330, 770, 360], radius=6, fill=(235, 230, 224, 255))
    draw.text((45, 340), "✔ Reproducción en segundo plano con control de ciclo de vida desacoplado de la UI",
              fill=(93, 64, 55, 255), font_size=11)

    path = os.path.join(OUTPUT_DIR, "diagram_tts_engine.png")
    img.save(path, "PNG")
    print(f"Generated: {path}")


def generate_settings_diagram():
    size = (800, 420)
    img = Image.new("RGBA", size, (250, 247, 242, 255))
    draw = ImageDraw.Draw(img)
    draw_header(draw, "5. Ajustes, Paletas de Color y Gestor de Almacenamiento",
                "MD GitHub Navigator • Temas Visuales y Control de Caché en Disco", 800)

    # 3 Theme Cards
    themes = [
        ("Tema Scholar (Default)", "• Optimizado para TCL NXTPAPER\n• Fondo cálido pergamino (#FAF7F2)\n• Texto marrón café (#2C2420)\n• Tipografía Serif (Literata)",
         30, 85, 230, 240, (250, 247, 242, 255), (93, 64, 55, 255), (93, 64, 55, 255)),
        ("Tema Paper (Minimalista)", "• Alto contraste neutro\n• Fondo blanco puro o negro\n• Inspirado en Notion / Medium\n• Tipografía Sans (Inter)",
         285, 85, 230, 240, (255, 255, 255, 255), (31, 35, 40, 255), (31, 35, 40, 255)),
        ("Tema Neon Dev (IDE)", "• Fondo oscuro cyber (#0A0E14)\n• Acentos Cian (#00E5FF)\n• Magenta terminal (#FF007F)\n• Tipografía Monospace",
         540, 85, 230, 240, (10, 14, 20, 255), (0, 229, 255, 255), (0, 229, 255, 255)),
    ]

    for title, desc, x, y, w, h, bg_color, border_color, text_color in themes:
        draw.rounded_rectangle([x, y, x + w, y + h], radius=10, fill=bg_color, outline=border_color, width=2)
        draw.rectangle([x, y, x + w, y + 36], fill=border_color)
        header_text_color = (255, 255, 255, 255) if border_color != (0, 229, 255, 255) else (10, 14, 20, 255)
        draw.text((x + 10, y + 10), title, fill=header_text_color, font_size=11)
        for i, line in enumerate(desc.split("\n")):
            draw.text((x + 10, y + 50 + i * 24), line, fill=text_color, font_size=10)

    # Bottom Storage Manager Card
    draw.rounded_rectangle([30, 345, 770, 405], radius=10, fill=(239, 235, 233, 255), outline=(93, 64, 55, 255), width=2)
    draw.text((45, 355), "🗄️  Gestor de Almacenamiento & Caché Selectiva (StorageManager)", fill=(93, 64, 55, 255), font_size=12)
    draw.text((45, 375), "• Cálculo dinámico de espacio ocupado en MB / GB  • Limpieza de caché con 1 toque sin perder marcadores ni progreso",
              fill=(78, 52, 46, 255), font_size=10)

    path = os.path.join(OUTPUT_DIR, "diagram_settings_themes.png")
    img.save(path, "PNG")
    print(f"Generated: {path}")


if __name__ == "__main__":
    generate_repo_sync_diagram()
    generate_reader_layout_diagram()
    generate_annotations_diagram()
    generate_tts_diagram()
    generate_settings_diagram()
