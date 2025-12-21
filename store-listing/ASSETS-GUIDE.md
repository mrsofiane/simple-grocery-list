# Store Assets Generation Guide

## App Icon

The adaptive icon (for Android 8+) is already configured with vector drawables:
- Background: Green (#4CAF50)
- Foreground: White shopping cart with checkmark
- Monochrome: Black version for themed icons (Android 13+)

### Generate Legacy Icons (for older devices)

The webp files in `mipmap-*` folders are fallbacks for pre-Android 8 devices. To regenerate them:

1. **Using Android Studio:**
   - Right-click on `res` folder
   - Select "New > Image Asset"
   - Choose "Launcher Icons (Adaptive and Legacy)"
   - Use the existing foreground/background drawables
   - Click "Next" and "Finish"

2. **Or use online tools:**
   - Export the vector as SVG
   - Use https://romannurik.github.io/AndroidAssetStudio/icons-launcher.html

### Icon Sizes Reference
| Density  | Size (px) | Folder          |
|----------|-----------|-----------------|
| mdpi     | 48x48     | mipmap-mdpi     |
| hdpi     | 72x72     | mipmap-hdpi     |
| xhdpi    | 96x96     | mipmap-xhdpi    |
| xxhdpi   | 144x144   | mipmap-xxhdpi   |
| xxxhdpi  | 192x192   | mipmap-xxxhdpi  |

---

## Screenshots

### Required Screenshots
- **Minimum:** 2 screenshots
- **Recommended:** 4-8 screenshots
- **Sizes:**
  - Phone: 1080x1920 (9:16) or 1440x2560
  - Tablet 7": 1200x1920
  - Tablet 10": 1800x2560

### How to Capture Screenshots

1. **Using Android Studio:**
   ```bash
   # Run the app on emulator/device
   # In Logcat window, click camera icon
   # Or: View > Tool Windows > Device File Explorer
   ```

2. **Using ADB:**
   ```bash
   adb shell screencap -p /sdcard/screenshot.png
   adb pull /sdcard/screenshot.png ./store-listing/screenshots/
   ```

3. **Using Emulator:**
   - Click camera icon in emulator toolbar
   - Screenshots saved to desktop

### Recommended Screenshot Sequence
1. Main list with 4-5 items (different categories)
2. Adding a new item (input form expanded)
3. Progress bar visible with some items checked
4. Swipe-to-delete action in progress
5. Category filter dropdown open
6. Empty state (optional)

---

## Feature Graphic

**Size:** 1024 x 500 pixels

### Design Suggestions
- Use green gradient background (#4CAF50 to #388E3C)
- Place app icon on the left
- Add app name "Grocery List" in white
- Tagline: "Simple. Fast. Beautiful."

### Create with Figma/Canva
1. Create 1024x500 canvas
2. Add gradient background
3. Place app icon (use the vector)
4. Add text with shadow for readability

---

## Hi-res Icon

**Size:** 512 x 512 pixels
**Format:** PNG (32-bit with alpha)

This is displayed in Google Play Store. Generate from the adaptive icon foreground on the background.

---

## Quick Checklist

- [ ] Hi-res icon (512x512)
- [ ] Feature graphic (1024x500)
- [ ] Phone screenshots (min 2, max 8)
- [ ] Short description (80 chars)
- [ ] Full description (4000 chars)
- [ ] Release notes
- [ ] Privacy policy URL (if applicable)
- [ ] App category selected
- [ ] Content rating questionnaire completed
