# Flange Helper (Android)

Flange Helper is a phone/tablet app that collects flange‑assembly data, performs calculations, captures optional signatures and photos, and exports a PDF report. This repo is the Android implementation, designed to be visually consistent with the MaterialGuardian app so the suite feels familiar across products.

## Current State (What’s Done)

- Git repo initialized and synced to GitHub on `main`.
- Starter UI flow implemented:
  - Splash sequence with fade transitions:
    - “Brought to you by:”
    - Welders Helper logo + “Welders Helper”
    - Fade into start screen
  - Start screen styled to match MaterialGuardian (logo, primary button, divider, empty state).
  - Job creation/edit flow with persistent storage.
  - Flange form flow with extensive fields and torque pass checklist.
- Storage usage meter on the start screen (750 MB budget).
- Reference data file added for torque calculation.
- Temperature‑based allowable stress support added for supported grades.
- Bolt tightening sequences added to reference data (clockwise numbering; populated for all even counts 4–88).
- Tightening sequence display wired into the flange form (numbering rule + sequence list).

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
  - App entry. Controls splash → start screen → job detail → flange form.

### Splash Screen
- `app/src/main/java/com/kevin/flangejointassembly/ui/SplashScreen.kt`
  - In‑app splash animation with the two‑phase fade.

### Start Screen (MaterialGuardian‑style)
- `app/src/main/java/com/kevin/flangejointassembly/ui/StartScreen.kt`
  - Logo, primary CTA button, divider, empty state, storage meter, and job list.

### Job Detail Screen
- `app/src/main/java/com/kevin/flangejointassembly/ui/JobDetailScreen.kt`
  - Shows job info and launches new flange forms.

### Flange Form
- `app/src/main/java/com/kevin/flangejointassembly/ui/FlangeFormScreen.kt`
  - Main report form with torque calculation inputs, QA checks, and pass checklist.

### Data Models & Storage
- `app/src/main/java/com/kevin/flangejointassembly/ui/JobModels.kt`
  - Job + flange form data structures.
- `app/src/main/java/com/kevin/flangejointassembly/ui/JobStorage.kt`
  - JSON persistence for jobs + forms in app‑private storage.

### Reference Data (Torque Calculation)
- `app/src/main/assets/flange_reference.json`
  - Diameter/TPI lookup, tensile stress area, and allowable stress vs temperature.
- `app/src/main/java/com/kevin/flangejointassembly/ui/ReferenceData.kt`
  - Loads reference data and exposes helpers for torque calculation.

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

## Torque Calculation Notes

- Target torque uses:
  - `F = As * S_ksi * 1000 * pctYield`
  - `T = (K * D * F) / 12`
- If **Specified Target Torque** is entered, it overrides calculated torque.
- If **Wet Torque** is selected and **Specified Target Torque** is used, the specified value is adjusted by the selected lube percent.
- Allowable stress at temperature is used when data is available for a grade; otherwise room‑temp Sy is used.

## Next Steps

- Add remaining allowable stress data (B8, A453 660 Class C/D, B8M additional classes).
- Implement photo capture, signatures, and PDF export.
- Add bolt‑pattern diagram rendering.
