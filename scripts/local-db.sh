#!/bin/bash
set -euo pipefail
# Avoid macOS locale initialization spawning threads during PostgreSQL startup.
export LC_ALL=C
export LANG=C
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
LOCAL="$ROOT/.local"
DATA="$LOCAL/postgres"
PG_BIN="${BERRIEL_PG_BIN:-/usr/local/opt/postgresql@15/bin}"
if [ ! -x "$PG_BIN/pg_ctl" ]; then
  echo "Set BERRIEL_PG_BIN to your PostgreSQL 15 bin directory." >&2
  exit 1
fi
case "${1:-start}" in
  start)
    mkdir -p "$LOCAL"
    if [ ! -f "$DATA/PG_VERSION" ]; then
      "$PG_BIN/initdb" -D "$DATA" -U berriel --encoding=UTF8 --locale=C --auth=trust > "$LOCAL/postgres-init.log"
    fi
    if ! "$PG_BIN/pg_ctl" -D "$DATA" status >/dev/null 2>&1; then
      "$PG_BIN/pg_ctl" -D "$DATA" -l "$LOCAL/postgres.log" -o "-h 127.0.0.1 -p 55432 -k ''" -w start
    fi
    if ! "$PG_BIN/psql" -h 127.0.0.1 -p 55432 -U berriel -d postgres -Atc "SELECT 1 FROM pg_database WHERE datname='berriel_local'" | grep -q 1; then
      "$PG_BIN/createdb" -h 127.0.0.1 -p 55432 -U berriel berriel_local
    fi
    echo "Local database ready at 127.0.0.1:55432/berriel_local"
    ;;
  stop) "$PG_BIN/pg_ctl" -D "$DATA" -m fast -w stop ;;
  status) "$PG_BIN/pg_ctl" -D "$DATA" status ;;
  *) echo "Usage: $0 {start|stop|status}" >&2; exit 1 ;;
esac
