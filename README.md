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

# Pomo Android

Currently, only a Japanese version is available, but an English version is planned for future release.

## Preview

Here is what the app looks like:

<p align="center">
<img src="images/pomo-android/Screenshot_2026-11-01-15-55-30-675_com.example.myapplication-edit.jpg" width="220" alt="App Preview 1">
<img src="images/pomo-android/Screenshot_2026-11-01-15-55-52-841_com.miui.gallery-edit.jpg" width="220" alt="App Preview 2">
<img src="images/pomo-android/Screenshot_2026-11-01-15-56-13-433_com.miui.gallery-edit.jpg" width="220" alt="App Preview 3">
<img src="images/pomo-android/Screenshot_2026-11-01-15-56-33-027_com.miui.gallery-edit.jpg" width="220" alt="App Preview 4">
</p>

## Installation (APK / USB)

1. Go to the **Actions** tab on GitHub, open "Build APK," download the `pomo-android-debug-apk` from the latest run (or trigger a build manually via **Run workflow**), and extract the file. You will find `app-debug.apk` inside.
2. On your smartphone, enable **USB debugging** under "Settings > Developer options," then connect the device to your PC via USB.
3. Run the following command on your PC (requires [adb](https://developer.android.com/tools/adb)): 

```
adb install app-debug.apk
```

If you prefer not to use `adb`, you can copy the APK file to your smartphone, then open it via your file manager app to install it (you will need to enable the installation of apps from unknown sources).

## How to Build (Locally)

Open the `pomo-android` directory in Android Studio, or run the following commands:

```
cd pomo-android
./gradlew assembleDebug
```

The APK will be generated at `app/build/outputs/apk/debug/app-debug.apk`.

This is based on my "pomo" project.

License: GNU
