from __future__ import annotations

from pathlib import Path
from PIL import Image

ROOT = Path(__file__).resolve().parents[1]
INPUT = ROOT / "assets" / "branding" / "Flange Helper.png"
OUTPUT_DIR = ROOT / "assets" / "branding" / "exports"
NAME = "FH"

ANDROID_SIZES = [48, 72, 96, 144, 192, 512, 1024]
IOS_SIZES = [20, 29, 40, 58, 60, 76, 80, 87, 120, 152, 167, 180, 1024]


def ensure_output_dir() -> None:
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)


def resize_and_save(img: Image.Image, size: int, platform: str) -> Path:
    target = img.resize((size, size), Image.LANCZOS)
    filename = f"{size}x{size}_{NAME}_{platform}.png"
    out_path = OUTPUT_DIR / filename
    target.save(out_path, format="PNG")
    return out_path


def main() -> None:
    if not INPUT.exists():
        raise SystemExit(f"Input not found: {INPUT}")

    img = Image.open(INPUT)
    if img.mode not in ("RGB", "RGBA"):
        img = img.convert("RGBA")

    ensure_output_dir()

    generated: list[Path] = []
    for size in ANDROID_SIZES:
        generated.append(resize_and_save(img, size, "Android"))

    for size in IOS_SIZES:
        generated.append(resize_and_save(img, size, "iOS"))

    print(f"Input: {INPUT} ({img.width}x{img.height})")
    print(f"Output dir: {OUTPUT_DIR}")
    print(f"Generated {len(generated)} files")


if __name__ == "__main__":
    main()
