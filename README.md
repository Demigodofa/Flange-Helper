# Flange Helper (Android)

Flange Helper is a phone/tablet app that collects flange‑assembly data, performs a few calculations, captures an optional signature, and exports a PDF report. This repo is the Android implementation, designed to be visually consistent with the MaterialGuardian app so the suite feels familiar across products.

## Current State (What’s Done)

- Git repo initialized and synced to GitHub on `main`.
- Starter UI flow implemented:
  - Splash sequence with fade transitions:
    - “Brought to you by:”
    - Welders Helper logo + “Welders Helper”
    - Fade into start screen
  - Start screen styled to match MaterialGuardian (logo, primary button, divider, empty state).
  - Placeholder first input screen with matching header/back button styling.
- Color system aligned with MaterialGuardian.
- Flange Helper logo imported for in‑app use.

## Goal (What We’re Building)

- A minimal‑data, form‑driven app with:
  - fields, dropdowns, checkboxes, auto‑populate, formulas
  - bolt‑pattern diagrams
  - optional signature capture
  - PDF report export
- UI look and navigation that matches the MaterialGuardian suite (headers, buttons, spacing, colors).
- Future iOS version using the same brand assets.

## Key Files and Why They Exist

### App Flow
- `app/src/main/java/com/kevin/flangejointassembly/MainActivity.kt`
  - App entry. Controls splash → start screen → form screen.

### Splash Screen
- `app/src/main/java/com/kevin/flangejointassembly/ui/SplashScreen.kt`
  - In‑app splash animation with the two‑phase fade.

### Start Screen (MaterialGuardian‑style)
- `app/src/main/java/com/kevin/flangejointassembly/ui/StartScreen.kt`
  - Logo, primary CTA button, divider, empty state.

### First Input Screen Placeholder
- `app/src/main/java/com/kevin/flangejointassembly/ui/FormScreen.kt`
  - Placeholder for the first report input page.

### Header / Back Button Style
- `app/src/main/java/com/kevin/flangejointassembly/ui/components/FlangeHeader.kt`
  - Circular back button + right‑aligned logo, matching MaterialGuardian.

### Design Tokens
- `app/src/main/java/com/kevin/flangejointassembly/ui/DesignTokens.kt`
  - Shared colors and UI tokens for consistent styling.

### Theme / Colors
- `app/src/main/java/com/kevin/flangejointassembly/ui/theme/Color.kt`
- `app/src/main/java/com/kevin/flangejointassembly/ui/theme/Theme.kt`
  - Overrides the default Compose palette to match the suite colors.

## Branding Assets

### Flange Helper Logo (in‑app use)
- `assets/branding/Flange Helper.png` — master PNG.
- `assets/branding/exports/` — generated sizes for Android and iOS.
- `app/src/main/res/drawable/flange_helper_512.png` — in‑app logo used on start screen.

### Welders Helper Splash Logo (splash only)
- `assets/branding/Welders Helper MAIN Program.png` — master PNG.
- `app/src/main/res/drawable/welders_helper_main_program.png` — drawable used by splash sequence.

## Asset Generation

- Script: `scripts/generate_icons.py`
  - Generates icon sizes from `assets/branding/Flange Helper.png`.
  - Output: `assets/branding/exports/`.

## Naming Conventions

- Generated images use: `WIDTHxHEIGHT_FH_Android.png` and `WIDTHxHEIGHT_FH_iOS.png`.
- Drawable names are lowercase with underscores to match Android conventions.

## Next Steps

- Wire the real report form fields, dropdowns, and formulas.
- Add bolt‑pattern diagram components.
- Implement PDF export and optional signature capture.
- Replace launcher icons with Flange Helper assets.
- Build report history/templates if required.
