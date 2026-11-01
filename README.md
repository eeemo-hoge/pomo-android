# Pomo Android

現在は日本語版のみですが、今後、英語版もリリース予定です。

## プレビュー

こんな感じの画面です。

<p align="center">
  <img src="images/pomo-android/Screenshot_2026-11-01-15-55-30-675_com.example.myapplication-edit.jpg" width="220" alt="アプリのプレビュー 1">
  <img src="images/pomo-android/Screenshot_2026-11-01-15-55-52-841_com.miui.gallery-edit.jpg" width="220" alt="アプリのプレビュー 2">
  <img src="images/pomo-android/Screenshot_2026-11-01-15-56-13-433_com.miui.gallery-edit.jpg" width="220" alt="アプリのプレビュー 3">
  <img src="images/pomo-android/Screenshot_2026-11-01-15-56-33-027_com.miui.gallery-edit.jpg" width="220" alt="アプリのプレビュー 4">
</p>

## インストール方法（APK / USB）

1. GitHub の **Actions** タブで「Build APK」を開き、最新の実行（または **Run workflow** で手動実行）から `pomo-android-debug-apk` をダウンロードして解凍します。中に `app-debug.apk` があります。
2. スマホの「設定 > 開発者向けオプション」で **USB デバッグ** を有効にし、USB で PC に接続します。
3. PC で次のコマンドを実行します（要 [adb](https://developer.android.com/tools/adb)）。

   ```
   adb install app-debug.apk
   ```

   adb を使わない場合は、APK をスマホにコピーし、ファイルアプリから開いてインストールします（提供元不明のアプリの許可が必要です）。

## ビルド方法（ローカル）

Android Studio で `pomo-android` ディレクトリを開くか、次のコマンドを実行します。

```
cd pomo-android
./gradlew assembleDebug
```

APK は `app/build/outputs/apk/debug/app-debug.apk` に出力されます。

私の pomo を流用しています。

ライセンス: GNU
---

# Pomo Android (English)

Currently only the Japanese UI is available; an English version is planned.

## Preview

See the screenshots in the Japanese section above.

## Install (APK via USB)

1. In the GitHub **Actions** tab, open "Build APK", pick the latest run (or trigger one with **Run workflow**), then download and unzip the `pomo-android-debug-apk` artifact. It contains `app-debug.apk`.
2. On your phone, enable **USB debugging** under "Settings > Developer options", and connect it to your PC via USB.
3. Run the following on your PC (requires [adb](https://developer.android.com/tools/adb)):

   ```
   adb install app-debug.apk
   ```

   Without adb, copy the APK to your phone and open it from a file manager (you must allow installing apps from unknown sources).

## Build locally

Open the `pomo-android` directory in Android Studio, or run:

```
cd pomo-android
./gradlew assembleDebug
```

The APK is generated at `app/build/outputs/apk/debug/app-debug.apk`.

This is based on my own pomo.

License: GNU
