# todo-expo

A tiny to-do list app: Expo (SDK 58), React Native, and TypeScript. Items persist on the device with AsyncStorage, and each item can carry a photo picked with `expo-image-picker` (photo library only, no camera or microphone).

```sh
npm ci
npx expo start
```

This is the fixture used by the `publish-readiness` skill in the sibling repo `app-creators-skills`. It has a name, a slug, bundle ids, icons, and one privacy-relevant permission for the skill to check. It has no `ios/` or `android/` directory and no EAS config.
