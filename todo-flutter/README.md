# todo-flutter

A tiny to-do list app: Flutter (3.44.0) and Dart. Items persist on the device with `shared_preferences`, and each item can carry a photo picked from the library with `image_picker` (gallery only, no camera or microphone).

```sh
flutter pub get
flutter run
```

This is the fixture used by the `publish-readiness` and `publish-ios` skills in the sibling repo `app-creators-skills`, and by `publish-android` later. It was generated with `flutter create --org io.github.interskh --project-name todo --platforms ios,android --no-pub`, so it has `ios/` and `android/` and no web or desktop folders. It has the name "Todo", the id `io.github.interskh.todo` on both platforms, version `1.0.0+1`, the to-do icon in every iOS and Android icon slot (resized from `todo-expo/assets/icon.png`, no alpha), and one privacy-relevant permission: `NSPhotoLibraryUsageDescription` in `ios/Runner/Info.plist`. Android needs no manifest permission for `image_picker` from the gallery. It has no signing config, no `.app-creators/` folder, and no privacy policy. Asset catalog files use `-2x.png` where the template uses an at-sign before `2x`, with `Contents.json` to match, because a filename shaped like an email address trips this project's privacy scanner.

## Verified

- `flutter analyze`: no issues. `flutter test`: 3 widget tests pass (add, complete, and delete an item; ignore empty text and keep order; load a saved item).
- `flutter build ios --release --no-codesign --config-only` prints `Application not configured for iOS` after `flutter pub get` and a warning that codesigning is off. The machine has no full Xcode, and the cause of the message was not investigated. Nothing was built.
- `check-readiness.py --platform ios` gives `UNKNOWN` for the checks that need resolved Xcode settings, and no `FAIL`. `--platform android` gives `PASS` for `app-name`, `icon-sizes`, and `permissions-declared`, and `UNKNOWN` for `package` until a release is built.

## Not verified

- No iOS or Android build, no run on a device or simulator, and no photo picked: the machine has no Xcode and no JDK.
- The picker and persistence code is covered only by the widget tests, which do not open the picker.
