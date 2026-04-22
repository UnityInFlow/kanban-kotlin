#!/bin/bash
# Workshop post-tool-call: light compile check after Kotlin file edits.
# Silent on success. Exit 2 re-engages the agent on compile failure.
#
# Scope: only when a .kt file under kanban-kotlin/ or kanban-spring/ was edited.
# Build tool: auto-detects Maven (pom.xml) or Gradle (build.gradle / build.gradle.kts).

set -euo pipefail

INPUT=$(cat)
FILE_PATH=$(printf '%s' "$INPUT" | python3 -c '
import sys, json
try:
    d = json.load(sys.stdin)
    ti = d.get("tool_input", {}) or {}
    print(ti.get("file_path") or ti.get("path") or "")
except Exception:
    pass
' 2>/dev/null)

# No file path or not a Kotlin source file -> nothing to check.
[ -z "$FILE_PATH" ] && exit 0
case "$FILE_PATH" in
  *.kt|*.kts) ;;
  *) exit 0 ;;
esac

# Resolve which subproject owns this file.
PROJECT_ROOT=""
for PROJ in kanban-kotlin kanban-spring; do
  case "$FILE_PATH" in
    *"/$PROJ/"*) PROJECT_ROOT="${CLAUDE_PROJECT_DIR:-$(pwd)}/$PROJ"; break ;;
  esac
done
[ -z "$PROJECT_ROOT" ] && exit 0
[ -d "$PROJECT_ROOT" ] || exit 0

cd "$PROJECT_ROOT"

# Detect build tool. Prefer wrapper (./mvnw, ./gradlew) — workshop projects ship a wrapper.
if [ -f "pom.xml" ]; then
  if [ -x "./mvnw" ]; then CMD=(./mvnw -q compile -DskipTests)
  elif command -v mvn >/dev/null 2>&1; then CMD=(mvn -q compile -DskipTests)
  else exit 0; fi
elif [ -f "build.gradle.kts" ] || [ -f "build.gradle" ]; then
  if [ -x "./gradlew" ]; then CMD=(./gradlew -q compileKotlin)
  elif command -v gradle >/dev/null 2>&1; then CMD=(gradle -q compileKotlin)
  else exit 0; fi
else
  exit 0
fi

OUTPUT=$("${CMD[@]}" 2>&1) || {
  {
    echo "Compile failed after edit to $FILE_PATH"
    echo "---"
    echo "$OUTPUT" | tail -40
  } >&2
  exit 2
}

# Success: silent.
exit 0
