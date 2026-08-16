#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"

GRADLE_VERSION="9.2.1"
DIST_ROOT="$PWD/.gradle-dist"
DIST_DIR="$DIST_ROOT/gradle-$GRADLE_VERSION"
DIST_ZIP="$DIST_ROOT/gradle-$GRADLE_VERSION-bin.zip"

if ! command -v java >/dev/null 2>&1; then
    echo "ERROR: Java no está instalado o no está en PATH. Se necesita Java 21." >&2
    exit 1
fi

if [ ! -x "$DIST_DIR/bin/gradle" ]; then
    mkdir -p "$DIST_ROOT"
    echo "Descargando Gradle $GRADLE_VERSION..."
    curl -L "https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip" -o "$DIST_ZIP"
    unzip -q -o "$DIST_ZIP" -d "$DIST_ROOT"
fi

echo "Compilando Easy Farmer's Delight Compat 0.1.0-dev..."
"$DIST_DIR/bin/gradle" --no-daemon clean build --stacktrace
echo "LISTO: revisa build/libs/"
