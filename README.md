<img src="https://raw.githubusercontent.com/pe3ep/macos-chat-fixes/refs/heads/main/src/main/resources/assets/macoschatfixes/icon.png" alt="favicon" width="96" style="image-rendering: pixelated">

# macOS Chat Fixes

Fixes Minecraft's text fields (chat, sign editing, world names, search boxes, etc.) to behave like a native macOS text field — proper ⌘ and ⌥ arrow key navigation, word-aware selection, and macOS-style deletion instead of Windows/Linux key bindings pretending to be Mac shortcuts.

### The problem

Vanilla Minecraft only really understands one set of non-Mac text shortcuts: Ctrl+Arrow to jump by word, Ctrl+A/C/V/X for select/copy/paste/cut. On macOS, it maps ⌘ onto those same Ctrl-shaped shortcuts which gets arrow navigation *wrong*. On macOS:

- **⌘ + Left/Right** should jump to the start/end of the line.
- **⌥ + Left/Right** should jump by word, vanilla doesn't bind this at all on Mac.

This mod fixes all of that, without touching anything on Windows or Linux.

### Contributing / Issues

Found a case where word-jumping or line navigation doesn't match real macOS behavior? Open an issue with the text and cursor position that produced the wrong result.