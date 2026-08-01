#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"

ANDROID_JAR=/usr/lib/android-sdk/platforms/android-23/android.jar
if [ ! -f "$ANDROID_JAR" ]; then
    echo "Missing $ANDROID_JAR - install with:" >&2
    echo "  apt-get install -y android-sdk-build-tools android-sdk-platform-23 libandroid-23-java libsmali-java" >&2
    exit 1
fi

rm -rf out
mkdir out

smali assemble -o out/classes.dex smali/

aapt package -f -M AndroidManifest.xml -I "$ANDROID_JAR" -F out/app-unsigned.apk

cp out/app-unsigned.apk out/app-with-dex.apk
(cd out && zip -j app-with-dex.apk classes.dex)

zipalign -f -p 4 out/app-with-dex.apk out/app-aligned.apk

if [ ! -f debug.keystore ]; then
    keytool -genkeypair -v -keystore debug.keystore -storepass android -alias androiddebugkey \
        -keypass android -keyalg RSA -keysize 2048 -validity 10000 \
        -dname "CN=Android Debug,O=Android,C=US"
fi

apksigner sign --ks debug.keystore --ks-pass pass:android --key-pass pass:android \
    --out HelloSmali-signed.apk out/app-aligned.apk

apksigner verify --print-certs HelloSmali-signed.apk
echo "Built HelloSmali-signed.apk"
