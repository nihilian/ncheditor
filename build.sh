#!/usr/bin/env bash

PROJECT="ncheditor"

if [ -z "$ANDROID_HOME" ]; then
    echo "ERROR: environment variable ANDROID_HOME unset" >&2
    exit 1
fi
PLATFORM=${ANDROID_PLATFORM:-34}
ANDROID_JAR="$ANDROID_HOME/platforms/android-$PLATFORM/android.jar"
FRAMEWORK_AIDL="$ANDROID_HOME/platforms/android-$PLATFORM/framework.aidl"

LIB_DIR="$(realpath ${LIB_DIR:-lib})"
SOURCE_DIR="$(realpath ${SOURCE_DIR:-src})"
CLASSES_DIR="$(realpath ${CLASSES_DIR:-bin})"
BUILD_DIR="$(realpath ${BUILD_DIR:-out})"

mkdir -p "$LIB_DIR"
mkdir -p "$CLASSES_DIR"
mkdir -p "$BUILD_DIR"

if [ ! -e "$LIB_DIR/commons-cli-1.8.0.jar" ]; then
    echo "Missing library: \"commons-cli-1.8.0.jar\"" >&2
    echo "Download library and place in $LIB_DIR" >&2
    exit 1
fi

echo "Platform: android-$PLATFORM"
echo "Lib dir: $LIB_DIR"
echo "Source dir: $SOURCE_DIR"
echo "Classes dir: $CLASSES_DIR"
echo "Build dir: $BUILD_DIR"

echo "Cleaning project directory..."
rm -rf "$SOURCE_DIR/android/app/INotificationManager.java" \
       "$SOURCE_DIR/android/content/pm/IPackageManager.java" \
       "$CLASSES_DIR/*" \
       "$BUILD_DIR/*"

echo "Generating AIDL files..."
aidl -p"$FRAMEWORK_AIDL" -Isrc "$SOURCE_DIR/android/app/INotificationManager.aidl"
aidl -p"$FRAMEWORK_AIDL" -Isrc "$SOURCE_DIR/android/content/pm/IPackageManager.aidl"

cd "$SOURCE_DIR"
echo "Running Java Compiler..."
javac -source 1.8 -target 1.8 -d "../bin" \
      -cp ".:../lib/commons-cli-1.8.0.jar:$ANDROID_JAR" \
      "io/github/nihilian/ncheditor/Main.java"
cd ..

echo "Compiling to DEX..."
d8 --classpath "$ANDROID_JAR" --output "$BUILD_DIR/$PROJECT.jar" \
    "lib/commons-cli-1.8.0.jar" \
    "$CLASSES_DIR/io/github/nihilian/ncheditor/Main.class"

echo "Generating script executable..."
echo '#!/system/bin/sh' > "$BUILD_DIR/$PROJECT"
echo 'base=/data/local/tmp' >> "$BUILD_DIR/$PROJECT"
echo 'export CLASSPATH=$base/ncheditor.jar' >> "$BUILD_DIR/$PROJECT"
echo 'exec app_process $base io.github.nihilian.ncheditor.Main "$@"' >> "$BUILD_DIR/$PROJECT"
chmod +x "$BUILD_DIR/$PROJECT"
