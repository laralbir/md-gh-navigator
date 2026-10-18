#!/usr/bin/env python3
"""
Generates high-resolution Google Play Store graphical assets:
1. icon_play_512x512.png (512x512 PNG, 32-bit with alpha)
2. feature_graphic_1024x500.png (1024x500 PNG banner)
"""

import math
import os
from PIL import Image, ImageDraw, ImageFont

ASSETS_DIR = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", "docs", "assets", "store"))
os.makedirs(ASSETS_DIR, exist_ok=True)


def draw_rounded_rect(draw, bounds, radius, fill, outline=None, width=1):
    draw.rounded_rectangle(bounds, radius=radius, fill=fill, outline=outline, width=width)


def generate_play_icon():
    size = (512, 512)
    img = Image.new("RGBA", size, (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)

    # 1. Background squircle / rounded rectangle with subtle warm gradient
    margin = 24
    bg_bounds = [margin, margin, size[0] - margin, size[1] - margin]
    radius = 110

    # Draw gradient in squircle
    mask = Image.new("L", size, 0)
    mask_draw = ImageDraw.Draw(mask)
    mask_draw.rounded_rectangle(bg_bounds, radius=radius, fill=255)

    gradient = Image.new("RGBA", size)
    g_draw = ImageDraw.Draw(gradient)
    for y in range(size[1]):
        t = y / size[1]
        # Interpolate from #FAF7F2 (250, 247, 242) to #EDE3D8 (237, 227, 216)
        r = int(250 * (1 - t) + 237 * t)
        g = int(247 * (1 - t) + 227 * t)
        b = int(242 * (1 - t) + 216 * t)
        g_draw.line([(0, y), (size[0], y)], fill=(r, g, b, 255))

    img.paste(gradient, (0, 0), mask)

    # Border
    draw.rounded_rectangle(bg_bounds, radius=radius, outline=(211, 196, 184, 255), width=3)

    # 2. Open Book Silhouette
    # Cover / Leather shadow (#3E2723)
    cover_poly = [
        (256, 150),
        (216, 138), (176, 138), (124, 148), (116, 160),
        (116, 350), (128, 362), (180, 350), (220, 352),
        (256, 366),
        (292, 352), (332, 350), (384, 362), (396, 350),
        (396, 160), (388, 148), (336, 138), (296, 138)
    ]
    draw.polygon(cover_poly, fill=(62, 39, 35, 255))

    # Pages (Ivory #FFFDF9)
    pages_poly = [
        (256, 160),
        (220, 148), (180, 148), (134, 158),
        (134, 338),
        (180, 330), (220, 332),
        (256, 348),
        (292, 332), (332, 330), (378, 338),
        (378, 158),
        (332, 148), (292, 148)
    ]
    draw.polygon(pages_poly, fill=(255, 253, 249, 255))
    draw.line([(256, 160), (256, 348)], fill=(215, 204, 200, 255), width=3)

    # 3. Left Page: Markdown Elements
    # "M" mark
    m_color = (93, 64, 55, 255)
    draw.line([(155, 220), (155, 185), (175, 208), (195, 185), (195, 220)], fill=m_color, width=5)
    # Down arrow
    arrow_color = (121, 85, 72, 255)
    draw.line([(212, 185), (212, 218)], fill=arrow_color, width=4)
    draw.line([(203, 209), (212, 218), (221, 209)], fill=arrow_color, width=4)

    # Text lines
    line_color = (141, 110, 99, 255)
    draw.line([(155, 245), (225, 245)], fill=line_color, width=5)
    draw.line([(155, 270), (215, 270)], fill=line_color, width=5)
    draw.line([(155, 295), (195, 295)], fill=(161, 136, 127, 255), width=4)

    # 4. Right Page: Git Branch Graph
    git_color = (93, 64, 55, 255)
    branch_color = (121, 85, 72, 255)
    # Main trunk
    draw.line([(288, 190), (288, 315)], fill=git_color, width=5)
    # Branch curve
    draw.arc([288, 215, 340, 275], start=-90, end=90, fill=branch_color, width=5)

    # Nodes
    def draw_node(cx, cy, r, fill):
        draw.ellipse([cx - r, cy - r, cx + r, cy + r], fill=fill)

    draw_node(288, 195, 8, git_color)
    draw_node(288, 230, 8, branch_color)
    draw_node(338, 245, 8, (161, 136, 127, 255))
    draw_node(288, 280, 8, git_color)
    draw_node(288, 315, 6, (62, 39, 35, 255))

    icon_path = os.path.join(ASSETS_DIR, "icon_play_512x512.png")
    img.save(icon_path, "PNG")
    print(f"Generated: {icon_path}")


def generate_feature_graphic():
    size = (1024, 500)
    img = Image.new("RGBA", size)
    draw = ImageDraw.Draw(img)

    # 1. Background dark Scholar / NXTPAPER leather gradient (#1E1917 -> #2C2420 -> #3E2723)
    for x in range(size[0]):
        t = x / size[0]
        r = int(30 * (1 - t) + 62 * t)
        g = int(25 * (1 - t) + 39 * t)
        b = int(23 * (1 - t) + 35 * t)
        draw.line([(x, 0), (x, size[1])], fill=(r, g, b, 255))

    # Decorative warm circular ambient glow behind tablet
    glow = Image.new("RGBA", size, (0, 0, 0, 0))
    glow_draw = ImageDraw.Draw(glow)
    for r in range(240, 0, -10):
        alpha = int(45 * (1 - r / 240))
        glow_draw.ellipse([700 - r, 250 - r, 700 + r, 250 + r], fill=(215, 204, 200, alpha))
    img = Image.alpha_composite(img, glow)
    draw = ImageDraw.Draw(img)

    # 2. Left side: Branding text & badges
    # Title
    draw.text((64, 90), "MD GitHub Navigator", fill=(250, 247, 242, 255), font_size=46)
    # Tagline
    draw.text((64, 150), "The Modern Markdown & Git eReader", fill=(215, 204, 200, 255), font_size=24)
    draw.text((64, 185), "Engineered for Android & TCL NXTPAPER 11 Plus", fill=(188, 170, 164, 255), font_size=18)

    # Feature bullets / Pill badges
    badges = [
        ("✦  Selective Offline Cache (.md & assets)", 235),
        ("✦  Integrated Mermaid, D2 & SVG Diagrams", 280),
        ("✦  Unified Annotations, Highlights & TTS Voice", 325),
        ("✦  Scholar, Paper & Neon Dev High-Contrast Themes", 370),
    ]
    for text, y in badges:
        # Badge background
        bbox = draw.textbbox((76, y), text, font_size=16)
        pad = 8
        draw.rounded_rectangle([bbox[0] - pad, bbox[1] - pad, bbox[2] + pad, bbox[3] + pad],
                               radius=6, fill=(44, 36, 32, 200), outline=(93, 64, 55, 255), width=1)
        draw.text((76, y), text, fill=(237, 227, 216, 255), font_size=16)

    # Store release tag badge
    ver_text = "Release v0.4.0 • Google Play & GitHub Releases"
    draw.text((64, 430), ver_text, fill=(161, 136, 127, 255), font_size=14)

    # 3. Right side: Stylized NXTPAPER Tablet Mockup
    # Tablet outer chassis (TCL NXTPAPER matte dark slate)
    tab_x, tab_y, tab_w, tab_h = 570, 70, 400, 360
    draw.rounded_rectangle([tab_x, tab_y, tab_x + tab_w, tab_y + tab_h], radius=20, fill=(20, 20, 22, 255),
                           outline=(75, 75, 80, 255), width=3)
    # Tablet screen (Scholar Paper #FAF7F2)
    screen_pad = 14
    sx, sy, sw, sh = tab_x + screen_pad, tab_y + screen_pad, tab_w - screen_pad * 2, tab_h - screen_pad * 2
    draw.rounded_rectangle([sx, sy, sx + sw, sy + sh], radius=10, fill=(250, 247, 242, 255))

    # Inside screen: Split view (TOC Drawer + Markdown Reader)
    # TOC Drawer (left column)
    toc_w = 110
    draw.rectangle([sx, sy, sx + toc_w, sy + sh], fill=(243, 237, 228, 255))
    draw.line([(sx + toc_w, sy), (sx + toc_w, sy + sh)], fill=(211, 196, 184, 255), width=2)
    draw.text((sx + 10, sy + 15), "Table of Contents", fill=(93, 64, 55, 255), font_size=9)
    for i, item in enumerate(["1. Introduction", "2. Architecture", "  2.1 Clean Arch", "  2.2 Render Engine", "3. Offline Sync"]):
        draw.text((sx + 10, sy + 38 + i * 18), item, fill=(121, 85, 72, 255), font_size=8)

    # Reader Content Area (right column)
    rx = sx + toc_w + 14
    draw.text((rx, sy + 14), "## MD GitHub Navigator", fill=(44, 36, 32, 255), font_size=13)
    draw.text((rx, sy + 35), "Offline-first Markdown reader with interactive", fill=(78, 52, 46, 255), font_size=9)
    draw.text((rx, sy + 49), "diagram rendering, notes and annotations.", fill=(78, 52, 46, 255), font_size=9)

    # Highlighted text snippet
    hl_box = [rx, sy + 70, rx + 190, sy + 84]
    draw.rectangle(hl_box, fill=(255, 224, 130, 200))
    draw.text((rx + 4, sy + 71), "TCL NXTPAPER 11 Plus Display Optimization", fill=(44, 36, 32, 255), font_size=8)

    # Diagram box inside reader
    diag_box = [rx, sy + 95, rx + 220, sy + 210]
    draw.rounded_rectangle(diag_box, radius=6, fill=(255, 253, 249, 255), outline=(211, 196, 184, 255), width=1)
    draw.text((rx + 10, sy + 102), "graph TD: Architecture Pipeline", fill=(141, 110, 99, 255), font_size=8)
    # Simple diagram boxes inside
    draw.rounded_rectangle([rx + 15, sy + 120, rx + 75, sy + 145], radius=4, fill=(239, 235, 233, 255), outline=(93, 64, 55, 255), width=1)
    draw.text((rx + 22, sy + 128), "Git Sync", fill=(93, 64, 55, 255), font_size=8)
    draw.line([(rx + 75, sy + 132), (rx + 95, sy + 132)], fill=(93, 64, 55, 255), width=2)
    draw.rounded_rectangle([rx + 95, sy + 120, rx + 155, sy + 145], radius=4, fill=(215, 204, 200, 255), outline=(93, 64, 55, 255), width=1)
    draw.text((rx + 102, sy + 128), "Room DB", fill=(62, 39, 35, 255), font_size=8)
    draw.line([(rx + 155, sy + 132), (rx + 175, sy + 132)], fill=(93, 64, 55, 255), width=2)
    draw.rounded_rectangle([rx + 175, sy + 120, rx + 215, sy + 145], radius=4, fill=(239, 235, 233, 255), outline=(93, 64, 55, 255), width=1)
    draw.text((rx + 180, sy + 128), "Reader", fill=(93, 64, 55, 255), font_size=8)

    # Bottom TTS player bar in reader
    tts_y = sy + sh - 45
    draw.rounded_rectangle([sx + toc_w + 4, tts_y, sx + sw - 4, sy + sh - 4], radius=6, fill=(239, 235, 233, 255),
                           outline=(211, 196, 184, 255), width=1)
    draw.text((sx + toc_w + 12, tts_y + 12), "▶  01:24 / 04:15  •  1.0x  •  Elena (es-ES)", fill=(93, 64, 55, 255), font_size=9)

    graphic_path = os.path.join(ASSETS_DIR, "feature_graphic_1024x500.png")
    img.save(graphic_path, "PNG")
    print(f"Generated: {graphic_path}")


if __name__ == "__main__":
    generate_play_icon()
    generate_feature_graphic()

