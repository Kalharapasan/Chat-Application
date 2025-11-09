Screenshots for the Chat Application

This folder is intended to hold screenshot images used in the project `README.md` or documentation.

Recommended filenames:
- `screen1.png` — Main chat list
- `screen2.png` — Conversation view
- `screen3.png` — New message / compose UI

Recommended sizes:
- 1080x1920 (portrait) or 1920x1080 (landscape) for full-screen images
- Use PNG for lossless quality; JPEG is acceptable for smaller file sizes

How to add screenshots:
1. Place the image files in this folder with the recommended filenames.
2. Commit them to the repository.
3. They will automatically be displayed in `README.md` if referenced using the relative path `docs/screenshots/screen1.png`.

Taking screenshots via ADB (example):
- Capture to device and pull to repo (Windows PowerShell):

```powershell
adb exec-out screencap -p > .\docs\screenshots\screen1.png
```

Or capture to device and then pull:

```powershell
adb shell screencap -p /sdcard/screen1.png; adb pull /sdcard/screen1.png .\docs\screenshots\
```

Notes:
- Keep images under ~2MB each for GitHub display performance.
- Prefer portrait screenshots for phone UIs.
