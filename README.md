# Blocker Android

Blocker Android is an accessibility-based Android application designed to help users block or restrict access to certain apps and monitor device usage. It leverages Android's Accessibility Service to analyze the UI, detect usage of blocked apps, and enforce restrictions based on user-defined rules and scripts.

## Features
- Block access to selected apps using accessibility overlays
- Analyze the UI tree for advanced blocking and monitoring
- Script-based detection for custom blocking logic
- Update management with remote version checking and in-app update downloads
- Local data storage for user settings and blocked app lists
- **Screenshot Overlay:** Floating overlay powered by Jetpack Compose, allowing users to trigger a UI tree screenshots and drag the overlay anywhere on the screen. The overlay can be closed or used to initiate a screenshot after a delay, leveraging the accessibility service for screen capture.

