# todo-swift

A tiny to-do list app: SwiftUI, iOS only, no third-party dependency. Items persist on the device with `@AppStorage`, and each item can carry a photo picked from the library with `PhotosPicker` (no camera or microphone).

**This project is hand-written. Xcode did not generate it, nobody opened it in Xcode, and nobody built it.** The machine that produced it has no full Xcode, and `xcodebuild` cannot run there.

## Build it

With a full Xcode installed:

```sh
open Todo.xcodeproj
```

Choose the `Todo` scheme and a simulator, then Run. To build from the command line: `xcodebuild -project Todo.xcodeproj -scheme Todo -sdk iphonesimulator build`. If Xcode rewrites the project file on first open, keep its version. A real device or archive needs a team: set `DEVELOPMENT_TEAM` (empty here) on the `Todo` target.

## What it is for

This is the fixture for the `publish-readiness` and `publish-ios` skills in the sibling repo `app-creators-skills`. It has the name "Todo" (`INFOPLIST_KEY_CFBundleDisplayName`), the id `io.github.interskh.todo`, `MARKETING_VERSION = 1.0.0`, `CURRENT_PROJECT_VERSION = 1`, a single-size 1024 `AppIcon` with no alpha channel, and one privacy-relevant permission: `INFOPLIST_KEY_NSPhotoLibraryUsageDescription`, with `GENERATE_INFOPLIST_FILE = YES`. The code calls `PHPhotoLibrary.requestAuthorization(for:)` before it shows the picker, so the key has an API to match; `PhotosPicker` alone would not ask for access. It has no signing team, no `.app-creators/` folder, and no privacy policy.

## What it is based on

- The project file follows the shape of the `Runner.xcodeproj` that Flutter 3.44.0's `flutter create` writes (`objectVersion = 54`, a classic file-reference layout), cut down to one SwiftUI app target with a Debug and a Release configuration. The Xcode template itself was not available, so this is not Xcode's own output.
- The build settings follow Apple's build settings reference (`GENERATE_INFOPLIST_FILE`, `INFOPLIST_KEY_*`, `ASSETCATALOG_COMPILER_APPICON_NAME`), as read for `publish-readiness`'s `checks.md`.
- The `AppIcon` set follows the single-size form that `checks.md` gives as the current Xcode default. The `Contents.json` files were written by hand in that shape.

## Verified

- `plutil -lint` passes on `project.pbxproj`. `plutil` does not read the scheme, which is not a property list, so it was checked with `xmllint --noout`. Every id in the project file points at a defined object.
- The `Contents.json` files parse as JSON. The icon is 1024x1024 with `hasAlpha: no`.
- `swiftc -typecheck` passes on the two Swift files against the macOS SDK, with a small shim in `/tmp` for `UIImage` and `Image(uiImage:)`, and `@State` swapped for an equivalent wrapper because the SwiftUI macro plugin ships only with Xcode. This is a type check on macOS, not an iOS build.
- `check-readiness.py --platform ios` gives `UNKNOWN` for the checks that need resolved Xcode settings, and no `FAIL`.

## Not verified

- No build, no run, no archive, no simulator, and no real `xcodebuild -showBuildSettings` output. Whether Xcode accepts the project file as written is unknown.
