# Murder Mansion Revival Report

Date: 2026-03-17

## Executive Summary

Update on 2026-03-18:

This report originally assumed a backend-backed replacement for online multiplayer. The current target is narrower:

- no web target
- no hosted backend
- native-only online play
- listen-server model

That changes the recommendation. The multiplayer path is still a rewrite, but it is now a native listen-server rewrite rather than a backend/service rewrite.

This repo is revivable, but not by "fixing a few broken files." The codebase is from the libGDX / Gradle / Android tooling stack of roughly 2014-2015, and it is blocked by three separate problems:

1. The build toolchain is obsolete and does not match current Android or Apple submission requirements.
2. The Android online services integration depends on Google Play Games APIs that were deprecated years ago, with the old Games v1 SDK now scheduled to start disappearing from the SDK in May 2026.
3. The game code itself has a handful of correctness bugs and partially stubbed flows that need cleanup before the game is reliably playable again.

The good news is that the core game code, art, maps, audio, and tutorial flow are all present. The most practical path is:

1. Restore a local playable build first, ideally desktop plus Android.
2. Get single-player/local mode stable before any multiplayer rewrite.
3. Remove or replace the obsolete Google Play multiplayer stack.
4. Rebuild online as a native listen-server flow for Android/iOS/desktop.
5. Ship Android first.
6. Treat iOS as a second migration, not part of the first rescue pass.

## Repo State

The active game project is `MurderMansion/`. The other top-level directories (`ZombBird/`, `BaseGameUtils/`, `google-play-services_lib/`) are historical or support material, not the main shipping app.

There are no uncommitted changes in the worktree.

## What Is Broken or Obsolete

### 1. Build and Tooling

The current build is far too old to ship:

- The Gradle wrapper points at Gradle 2.2 over plain HTTP: [MurderMansion/gradle/wrapper/gradle-wrapper.properties](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/gradle/wrapper/gradle-wrapper.properties#L1), [MurderMansion/gradle/wrapper/gradle-wrapper.properties](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/gradle/wrapper/gradle-wrapper.properties#L6)
- The Android Gradle plugin is `1.0.0`: [MurderMansion/build.gradle](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/build.gradle#L9)
- The project still uses `jcenter()`: [MurderMansion/build.gradle](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/build.gradle#L3), [MurderMansion/build.gradle](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/build.gradle#L5)
- libGDX is pinned to `1.5.3`: [MurderMansion/build.gradle](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/build.gradle#L21)
- RoboVM is pinned to `1.0.0-beta-03`: [MurderMansion/build.gradle](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/build.gradle#L10), [MurderMansion/build.gradle](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/build.gradle#L22)
- The project is still set up for Eclipse/Ant-era Android project generation: [MurderMansion/android/build.gradle](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/android/build.gradle#L69), [MurderMansion/android/build.gradle](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/android/build.gradle#L100)
- Java source targets are 1.6/1.7: [MurderMansion/core/build.gradle](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/core/build.gradle#L3), [MurderMansion/desktop/build.gradle](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/desktop/build.gradle#L3), [MurderMansion/ios/build.gradle](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/ios/build.gradle#L3), [MurderMansion/android/build.gradle](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/android/build.gradle#L79)

I also could not run the wrapper as-is in this workspace:

- `gradlew` is not executable.
- Running `sh gradlew tasks --offline` fails before any project evaluation because the wrapper is trying to use an ancient local cache path for Gradle 2.2 that is not present.

Conclusion: this should be treated as a migration to a modern libGDX project layout, not a "repair the existing Gradle files" job.

### 2. Android Shipping Blockers

The Android app is nowhere near current Play requirements:

- `compileSdkVersion 21`: [MurderMansion/android/build.gradle](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/android/build.gradle#L2), [MurderMansion/android/build.gradle](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/android/build.gradle#L3)
- `targetSdkVersion 22`: [MurderMansion/android/AndroidManifest.xml](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/android/AndroidManifest.xml#L7), [MurderMansion/android/AndroidManifest.xml](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/android/AndroidManifest.xml#L9)
- `minSdkVersion 9`: [MurderMansion/android/AndroidManifest.xml](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/android/AndroidManifest.xml#L7), [MurderMansion/android/AndroidManifest.xml](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/android/AndroidManifest.xml#L8)
- Native packaging only covers `armeabi`, `armeabi-v7a`, and `x86`, not `arm64-v8a` or `x86_64`: [MurderMansion/build.gradle](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/build.gradle#L55), [MurderMansion/build.gradle](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/build.gradle#L57), [MurderMansion/build.gradle](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/build.gradle#L59), [MurderMansion/build.gradle](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/build.gradle#L61), [MurderMansion/android/build.gradle](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/android/build.gradle#L27), [MurderMansion/android/build.gradle](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/android/build.gradle#L36)

The Google Play Games integration is also legacy:

- It uses `GoogleApiClient`, `Games`, `RoomConfig`, `Games.RealTimeMultiplayer`, and `BaseGameUtils` helper classes: [MurderMansion/android/src/com/jkjk/MurderMansion/android/AndroidLauncher.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/android/src/com/jkjk/MurderMansion/android/AndroidLauncher.java#L13), [MurderMansion/android/src/com/jkjk/MurderMansion/android/AndroidLauncher.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/android/src/com/jkjk/MurderMansion/android/AndroidLauncher.java#L20), [MurderMansion/android/src/com/jkjk/MurderMansion/android/AndroidLauncher.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/android/src/com/jkjk/MurderMansion/android/AndroidLauncher.java#L211), [MurderMansion/android/src/com/jkjk/MurderMansion/android/AndroidLauncher.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/android/src/com/jkjk/MurderMansion/android/AndroidLauncher.java#L217), [MurderMansion/android/src/com/jkjk/MurderMansion/android/AndroidLauncher.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/android/src/com/jkjk/MurderMansion/android/AndroidLauncher.java#L246), [MurderMansion/android/src/com/jkjk/MurderMansion/android/AndroidLauncher.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/android/src/com/jkjk/MurderMansion/android/AndroidLauncher.java#L305), [MurderMansion/android/src/com/jkjk/MurderMansion/android/AndroidLauncher.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/android/src/com/jkjk/MurderMansion/android/AndroidLauncher.java#L334)
- `BaseGameUtils` and `google-play-services_lib` still exist as old library projects at the repo root, but they are not even included in the current Gradle settings: [MurderMansion/settings.gradle](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/settings.gradle#L1)

Conclusion: Android online features must be removed or rewritten against current services. They are not salvageable as-is.

### 3. Multiplayer Architecture Risk

Even independent of API deprecation, the multiplayer design is fragile for a 2026 mobile release:

- The host device opens a raw `ServerSocket`, chooses a random port, and shares its local IPv4 address to peers: [MurderMansion/core/src/com/jkjk/Host/MMServer.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/core/src/com/jkjk/Host/MMServer.java#L354), [MurderMansion/core/src/com/jkjk/Host/MMServer.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/core/src/com/jkjk/Host/MMServer.java#L362), [MurderMansion/core/src/com/jkjk/Host/MMServer.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/core/src/com/jkjk/Host/MMServer.java#L404)
- Clients then connect directly to that address via Java sockets: [MurderMansion/core/src/com/jkjk/GameWorld/MMClient.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/core/src/com/jkjk/GameWorld/MMClient.java#L139), [MurderMansion/core/src/com/jkjk/GameWorld/MMClient.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/core/src/com/jkjk/GameWorld/MMClient.java#L273), [MurderMansion/core/src/com/jkjk/GameWorld/MMClient.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/core/src/com/jkjk/GameWorld/MMClient.java#L282)
- Google Play room messages are only being used to hand off address/port: [MurderMansion/android/src/com/jkjk/MurderMansion/android/RealTimeCommunication.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/android/src/com/jkjk/MurderMansion/android/RealTimeCommunication.java#L37), [MurderMansion/android/src/com/jkjk/MurderMansion/android/RealTimeCommunication.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/android/src/com/jkjk/MurderMansion/android/RealTimeCommunication.java#L67), [MurderMansion/android/src/com/jkjk/MurderMansion/android/RealTimeCommunication.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/android/src/com/jkjk/MurderMansion/android/RealTimeCommunication.java#L94)

That is a LAN-style architecture, and the repo's current version of it is too fragile to ship unchanged.

Practical implication:

- if you want native online multiplayer back without running hosted infrastructure, plan for a listen-server redesign, not a patch
- if you want browser play later, that becomes a separate architecture problem

### 4. iOS Shipping Blockers

The iOS target is further behind than Android:

- The backend is RoboVM beta, not a current stack: [MurderMansion/build.gradle](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/build.gradle#L68), [MurderMansion/build.gradle](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/build.gradle#L74)
- The config still targets `thumbv7`: [MurderMansion/ios/robovm.xml](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/ios/robovm.xml#L4), [MurderMansion/ios/robovm.xml](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/ios/robovm.xml#L5)
- The plist requires `armv7` and `opengles-2`: [MurderMansion/ios/Info.plist.xml](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/ios/Info.plist.xml#L36), [MurderMansion/ios/Info.plist.xml](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/ios/Info.plist.xml#L39)
- The launcher appears to have a direct compile error: it instantiates `new murdermansion()` instead of `new MurderMansion(...)`: [MurderMansion/ios/src/com/jkjk/MurderMansion/IOSLauncher.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/ios/src/com/jkjk/MurderMansion/IOSLauncher.java#L13)
- The bundle ID is really a launcher-class-based placeholder, not a production app identifier: [MurderMansion/ios/robovm.properties](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/ios/robovm.properties#L2)
- The iOS assets are still in the old pre-asset-catalog style.

Conclusion: iOS should be treated as a second migration after Android or desktop recovery.

### 5. Gameplay / Java Code Issues

There are several likely logic bugs unrelated to store requirements:

- Many string comparisons use `==` / `!=` instead of `.equals(...)`, which is unreliable in Java and can cause role/type checks to fail at runtime:
  - [MurderMansion/core/src/com/jkjk/GameObjects/Characters/GameCharacter.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/core/src/com/jkjk/GameObjects/Characters/GameCharacter.java#L77)
  - [MurderMansion/core/src/com/jkjk/GameObjects/Characters/GameCharacter.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/core/src/com/jkjk/GameObjects/Characters/GameCharacter.java#L95)
  - [MurderMansion/core/src/com/jkjk/GameWorld/GameWorld.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/core/src/com/jkjk/GameWorld/GameWorld.java#L213)
  - [MurderMansion/core/src/com/jkjk/Screens/LoadingScreen.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/core/src/com/jkjk/Screens/LoadingScreen.java#L62)
  - [MurderMansion/core/src/com/jkjk/GameWorld/HudRenderer.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/core/src/com/jkjk/GameWorld/HudRenderer.java#L161)

- The desktop action resolver fakes room state changes but does not set server address or port: [MurderMansion/desktop/src/com/jkjk/MurderMansion/desktop/ActionResolverDesktop.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/desktop/src/com/jkjk/MurderMansion/desktop/ActionResolverDesktop.java#L45), [MurderMansion/desktop/src/com/jkjk/MurderMansion/desktop/ActionResolverDesktop.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/desktop/src/com/jkjk/MurderMansion/desktop/ActionResolverDesktop.java#L58), [MurderMansion/desktop/src/com/jkjk/MurderMansion/desktop/ActionResolverDesktop.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/desktop/src/com/jkjk/MurderMansion/desktop/ActionResolverDesktop.java#L65)
- The wait screen refuses to continue unless room state is `ROOM_PLAY` and a real server address and port exist: [MurderMansion/core/src/com/jkjk/Screens/WaitScreen.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/core/src/com/jkjk/Screens/WaitScreen.java#L63), [MurderMansion/core/src/com/jkjk/Screens/WaitScreen.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/core/src/com/jkjk/Screens/WaitScreen.java#L65)

This means the desktop quick/invite/join buttons are effectively dead. The tutorial path is the only clearly self-contained playable path I found because it explicitly creates a local `MMServer` and `MMClient`: [MurderMansion/core/src/com/jkjk/Screens/TutorialScreen.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/core/src/com/jkjk/Screens/TutorialScreen.java#L71), [MurderMansion/core/src/com/jkjk/Screens/TutorialScreen.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/core/src/com/jkjk/Screens/TutorialScreen.java#L76), [MurderMansion/core/src/com/jkjk/Screens/TutorialScreen.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/core/src/com/jkjk/Screens/TutorialScreen.java#L77), [MurderMansion/core/src/com/jkjk/Screens/TutorialScreen.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/core/src/com/jkjk/Screens/TutorialScreen.java#L96), [MurderMansion/core/src/com/jkjk/Screens/TutorialScreen.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/core/src/com/jkjk/Screens/TutorialScreen.java#L101), [MurderMansion/core/src/com/jkjk/Screens/TutorialScreen.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/core/src/com/jkjk/Screens/TutorialScreen.java#L102)

- `LoadingScreen` uses a fixed render-loop count of `500` instead of time-based readiness or an asset manager: [MurderMansion/core/src/com/jkjk/Screens/LoadingScreen.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/core/src/com/jkjk/Screens/LoadingScreen.java#L79)
- There is a large amount of debug logging and unfinished cleanup across game, host, and screen classes.

Conclusion: the game logic is not lost, but it needs a stabilization pass before it should be considered playable.

## Current Publishing Requirements That Matter

### Google Play

As of the current official policy snapshot:

- Since August 31, 2025, new apps and app updates must target Android 15 / API 35.
- Existing apps must target Android 14 / API 34 or higher to remain available to new users on newer Android versions.
- New apps on Google Play must be published as Android App Bundles.
- Apps published on Google Play that ship native code need 64-bit support.

Compared to this repo:

- current target SDK is 22
- current compile SDK is 21
- current native packaging is 32-bit only
- current output path is APK-era, not AAB-era

### Apple App Store

Apple has already announced the next minimum SDK cutoff:

- Starting April 28, 2026, iOS and iPadOS apps uploaded to App Store Connect must be built with the iOS 26 / iPadOS 26 SDK or later.

Also relevant:

- App privacy details are required for new app submissions and updates.
- A privacy policy URL is required.
- If you use listed third-party SDKs, Apple requires privacy manifests and signatures for them in affected cases.

Compared to this repo:

- the iOS target is built around RoboVM beta and old asset/plist conventions
- the launcher currently looks uncompilable
- the app metadata is not ready for modern submission

## Recommended Revival Plan

### Phase 1: Recover a Playable Core Build

Goal: get the game running locally before trying to ship.

Recommended steps:

1. Generate a fresh modern libGDX project.
2. Keep only `core`, `desktop`, and `android` at first.
3. Port the existing `core/src` game code and `android/assets` into the new project.
4. Move desktop from LWJGL to LWJGL3.
5. Fix the Java string-comparison bugs.
6. Make the tutorial or a local single-player / hotseat-style mode the default playable path.
7. Temporarily disable the menu items for Google Play login, invites, quick match, and achievements until replacements exist.

This is the fastest way to answer the most important question: does the game still feel good once it runs?

### Phase 1.5: Single-Player / Local Recovery

Goal: stabilize one non-networked gameplay path before touching multiplayer.

Recommended steps:

1. Make one clean path from menu to gameplay to score screen work reliably.
2. Use tutorial/local mode as the first stable gameplay target.
3. Stub or hide obsolete network/login UI until the replacement exists.
4. Validate controls, collisions, pickups, win conditions, and screen transitions without network code in the way.

This phase creates the baseline that the later host runtime will be extracted from.

### Phase 2: Native Multiplayer Rebuild

Goal: revive online play for native clients without hosted infrastructure.

Recommended steps:

1. Remove Google Play realtime room dependencies from the session model.
2. Keep host-authoritative game logic, but extract it from the current socket glue.
3. Introduce a platform-neutral transport/session interface.
4. Replace the underscore-delimited string protocol with typed messages and stable entity ids.
5. Build LAN and direct host/join first.
6. Treat desktop-hosted internet matches as the first serious online target.
7. Treat mobile-hosted internet matches as best-effort, not guaranteed on all networks.

This is the lowest-maintenance way to bring back online play.

### Phase 3: Android Shipping Build

Goal: publish an Android version again.

Recommended steps:

1. Update to a current libGDX release.
2. Move to a current Android Gradle Plugin and Gradle version.
3. Target API 35, compile with API 35.
4. Build AABs instead of APK-only release artifacts.
5. Ensure arm64-v8a support.
6. Migrate to AndroidX-compatible dependencies.
7. Replace or remove Google Play Games v1 integrations.

For Android, the right target under the current plan is:

- modern Android packaging and store compliance
- native listen-server support
- no dependency on retired Google Play realtime rooms

### Phase 4: iOS Port

Goal: publish on the App Store after Android is stable.

Recommended steps:

1. Recreate the iOS target on the current libGDX iOS backend.
2. Treat the existing RoboVM target as reference only.
3. Replace old plist/icon/launch-image conventions with a current Xcode-compatible setup.
4. Fix bundle ID, signing, privacy, and App Store metadata properly.

I would not spend time trying to patch the current `ios/` module in place.

## Priority Order

If you want the fastest route back to something real:

1. Modernize the project skeleton.
2. Make desktop tutorial/local mode playable.
3. Stabilize a single-player/local baseline.
4. Rebuild native listen-server multiplayer on desktop first.
5. Bring that multiplayer stack to Android.
6. Ship Android.
7. Port to iOS.
8. Revisit achievements / platform integrations.

## Estimated Effort

Roughly:

- Desktop/local playable recovery: a few focused days if the port is clean.
- Single-player/local stabilization should happen before multiplayer and is part of the core rescue effort.
- Native listen-server multiplayer rewrite: likely the dominant piece of work.
- Android shipping build with native listen-server support: likely 2-5 weeks depending on how much of the old multiplayer logic ports cleanly.
- iOS shipping build after Android: likely another 1-2+ weeks.

## Bottom Line

Yes, this repo is revivable.

No, it is not close to store-ready.

The right framing is:

- salvage the game content and core mechanics
- port them into a modern libGDX project
- get a stable single-player/local version working first
- rebuild online as a native listen-server flow
- ship a smaller, stable Android comeback release first
- then bring the same native multiplayer stack to iOS

## Native Multiplayer Decision

As of 2026-03-18, the recommended online target is:

- Android, iOS, and desktop only
- no web/browser target
- no hosted backend
- listen-server model

That means the realistic product shape is:

- LAN play should be first-class
- private direct-connect matches are feasible
- desktop-hosted internet matches are much more realistic than phone-hosted internet matches
- browser play is out of scope

This is a viable direction, but it still requires rewriting:

- the Google Play room/session layer
- the current socket glue
- the wire protocol
- disconnect/reconnect handling

It does not require rewriting the game into a backend service.

## External Sources

- Google Play target API requirements: https://support.google.com/googleplay/android-developer/answer/11926878?hl=en
- Google Play target API policy summary: https://support.google.com/googleplay/android-developer/answer/16561298?hl=en
- Android App Bundles: https://developer.android.com/guide/app-bundle
- Google Play 64-bit requirement: https://developer.android.com/google/play/requirements/64-bit
- Google Play Games real-time multiplayer deprecation: https://developer.android.com/games/services/cpp/api/deprecated/deprecated
- Android games release notes for Games v1 SDK/API deprecation timing: https://developer.android.com/games/docs/release-notes
- Apple submitting apps: https://developer.apple.com/ios/submit/
- Apple upcoming SDK minimum requirements: https://developer.apple.com/news/?id=ueeok6yw
- Apple app privacy details: https://developer.apple.com/app-store/app-privacy-details/
- Apple manage app privacy: https://developer.apple.com/help/app-store-connect/manage-app-information/manage-app-privacy
- Apple third-party SDK requirements: https://developer.apple.com/support/third-party-SDK-requirements/
- libGDX latest release: https://libgdx.com/news/2025/10/gdx-1-14-0
- libGDX 1.13.5 Android minimum SDK note: https://libgdx.com/news/2025/05/gdx-1-13-5
- libGDX project generation docs: https://libgdx.com/wiki/start/project-generation
