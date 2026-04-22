#!/bin/bash
# Workshop pre-bash guard.
# Blocks destructive commands that could wipe exercise solutions held in git tags/branches.
# Exit 1 -> command blocked, message shown to agent.

# Read tool input from stdin JSON (Claude Code hook contract).
INPUT=$(cat)
COMMAND=$(printf '%s' "$INPUT" | python3 -c 'import sys,json; d=json.load(sys.stdin); print(d.get("tool_input",{}).get("command",""))' 2>/dev/null)

[ -z "$COMMAND" ] && exit 0

block() {
  echo "BLOCKED by workshop pre-bash hook: $1" >&2
  echo "If you truly need this, ask the user to run the command themselves." >&2
  exit 1
}

# Protect git history + exercise solution tags.
echo "$COMMAND" | grep -qE '(^|[;&|[:space:]])git[[:space:]]+reset[[:space:]]+--hard'            && block "git reset --hard"
echo "$COMMAND" | grep -qE '(^|[;&|[:space:]])git[[:space:]]+clean[[:space:]]+-[fdxX]'           && block "git clean -f*"
echo "$COMMAND" | grep -qE '(^|[;&|[:space:]])git[[:space:]]+push[[:space:]]+(-f|--force([^-]|$))' && block "git push --force"
echo "$COMMAND" | grep -qE '(^|[;&|[:space:]])git[[:space:]]+branch[[:space:]]+-D'              && block "git branch -D"
echo "$COMMAND" | grep -qE '(^|[;&|[:space:]])git[[:space:]]+tag[[:space:]]+-d'                 && block "git tag -d (deletes exercise solution tag)"
echo "$COMMAND" | grep -qE '(^|[;&|[:space:]])git[[:space:]]+push[[:space:]]+.*--delete'        && block "git push --delete (remote tag/branch deletion)"

# Protect filesystem.
echo "$COMMAND" | grep -qE '(^|[;&|[:space:]])rm[[:space:]]+(-[a-zA-Z]*r[a-zA-Z]*f|-[a-zA-Z]*f[a-zA-Z]*r)[[:space:]]' && block "rm -rf (use Bash to remove specific files, not directory trees)"

# Protect Docker volumes (H2 data, Postgres etc.)
echo "$COMMAND" | grep -qE 'docker[[:space:]]+(compose[[:space:]]+)?down[[:space:]]+.*-v'       && block "docker compose down -v (wipes volumes)"
echo "$COMMAND" | grep -qE 'docker[[:space:]]+volume[[:space:]]+rm'                             && block "docker volume rm"

exit 0
