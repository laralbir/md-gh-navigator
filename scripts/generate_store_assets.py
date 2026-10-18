#!/usr/bin/env python3
"""
Generates high-resolution Google Play Store graphical assets:
1. icon_play_512x512.png (512x512 PNG, 32-bit with alpha)
2. feature_graphic_1024x500.png (1024x500 PNG banner with real app tablet screenshot)
"""

import math
import os
from PIL import Image, ImageDraw, ImageFont

ASSETS_DIR = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", "docs", "assets", "store"))
GUIDE_DIR = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", "docs", "assets", "images", "user-guide"))
os.makedirs(ASSETS_DIR, exist_ok=True)


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


def generate_play_icon():
    size = (512, 512)
    img = Image.new("RGBA", size, (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)

    margin = 24
    bg_bounds = [margin, margin, size[0] - margin, size[1] - margin]
    radius = 110

    mask = Image.new("L", size, 0)
    mask_draw = ImageDraw.Draw(mask)
    mask_draw.rounded_rectangle(bg_bounds, radius=radius, fill=255)

    gradient = Image.new("RGBA", size)
    g_draw = ImageDraw.Draw(gradient)
    for y in range(size[1]):
        t = y / size[1]
        r = int(250 * (1 - t) + 237 * t)
        g = int(247 * (1 - t) + 227 * t)
        b = int(242 * (1 - t) + 216 * t)
        g_draw.line([(0, y), (size[0], y)], fill=(r, g, b, 255))

    img.paste(gradient, (0, 0), mask)
    draw.rounded_rectangle(bg_bounds, radius=radius, outline=(211, 196, 184, 255), width=3)

    cover_poly = [
        (256, 150),
        (216, 138), (176, 138), (124, 148), (116, 160),
        (116, 350), (128, 362), (180, 350), (220, 352),
        (256, 366),
        (292, 352), (332, 350), (384, 362), (396, 350),
        (396, 160), (388, 148), (336, 138), (296, 138)
    ]
    draw.polygon(cover_poly, fill=(62, 39, 35, 255))

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
    draw.line([(256, 160), (256, 348)], fill=(211, 196, 184, 255), width=3)

    for i in range(5):
        y_off = 185 + i * 26
        draw.line([(156, y_off), (236, y_off - 4)], fill=(211, 196, 184, 255), width=3)

    badge_x, badge_y, badge_w, badge_h = 140, 240, 80, 60
    draw.rounded_rectangle([badge_x, badge_y, badge_x + badge_w, badge_y + badge_h],
                           radius=8, fill=(93, 64, 55, 255))
    draw.text((badge_x + 12, badge_y + 12), "M ↓", fill=(250, 247, 242, 255), font=get_font(28, bold=True))

    git_color = (93, 64, 55, 255)
    branch_color = (141, 110, 99, 255)
    draw.line([(288, 195), (288, 315)], fill=git_color, width=4)
    draw.line([(288, 230), (338, 245)], fill=branch_color, width=4)
    draw.line([(338, 245), (338, 310)], fill=branch_color, width=4)

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

    for x in range(size[0]):
        t = x / size[0]
        r = int(30 * (1 - t) + 62 * t)
        g = int(25 * (1 - t) + 39 * t)
        b = int(23 * (1 - t) + 35 * t)
        draw.line([(x, 0), (x, size[1])], fill=(r, g, b, 255))

    glow = Image.new("RGBA", size, (0, 0, 0, 0))
    glow_draw = ImageDraw.Draw(glow)
    for r in range(240, 0, -10):
        alpha = int(45 * (1 - r / 240))
        glow_draw.ellipse([720 - r, 250 - r, 720 + r, 250 + r], fill=(215, 204, 200, alpha))
    img = Image.alpha_composite(img, glow)
    draw = ImageDraw.Draw(img)

    # 2. Left side: Branding text & badges
    draw.text((54, 75), "MD GitHub Navigator", fill=(250, 247, 242, 255), font=get_font(38, bold=True))
    draw.text((54, 130), "The Modern Markdown & Git eReader", fill=(215, 204, 200, 255), font=get_font(20, bold=True))
    draw.text((54, 160), "Optimizado para Android y TCL NXTPAPER 11 Plus", fill=(188, 170, 164, 255), font=get_font(15))

    badges = [
        ("✦  Caché Offline Selectiva (.md y recursos vinculados)", 210),
        ("✦  Diagramas Locales Mermaid, D2 y Renderizado SVG", 255),
        ("✦  Anotaciones Unificadas, Resaltados y Voz TTS", 300),
        ("✦  Temas Scholar, Paper y Neon Dev de Alto Contraste", 345),
    ]
    for text, y in badges:
        pad_x, pad_y = 12, 6
        draw.rounded_rectangle([54, y - pad_y, 500, y + 26], radius=6, fill=(44, 36, 32, 210), outline=(93, 64, 55, 255), width=1)
        draw.text((64, y), text, fill=(237, 227, 216, 255), font=get_font(13))

    ver_text = "Release v1.0.0 • Google Play & GitHub Releases"
    draw.text((54, 420), ver_text, fill=(188, 170, 164, 255), font=get_font(14, bold=True))

    # 3. Right side: REAL Tablet Screenshot inside sleek tablet chassis
    tablet_path = os.path.join(GUIDE_DIR, "screenshot_tablet_dual_pane_reader.png")
    if os.path.exists(tablet_path):
        tab_img = Image.open(tablet_path).convert("RGBA")
        target_w = 460
        target_h = int(tab_img.height * (target_w / tab_img.width))
        tab_resized = tab_img.resize((target_w, target_h), Image.Resampling.LANCZOS)

        border_w = 6
        frame_w, frame_h = target_w + border_w * 2, target_h + border_w * 2
        tablet_frame = Image.new("RGBA", (frame_w, frame_h), (0, 0, 0, 0))
        f_draw = ImageDraw.Draw(tablet_frame)
        f_draw.rounded_rectangle([0, 0, frame_w - 1, frame_h - 1], radius=16,
                                 fill=(20, 20, 22, 255), outline=(90, 80, 75, 255), width=border_w)
        tablet_frame.paste(tab_resized, (border_w, border_w))

        img.paste(tablet_frame, (530, 85), tablet_frame)

    graphic_path = os.path.join(ASSETS_DIR, "feature_graphic_1024x500.png")
    img.save(graphic_path, "PNG")
    print(f"Generated: {graphic_path}")


if __name__ == "__main__":
    generate_play_icon()
    generate_feature_graphic()
