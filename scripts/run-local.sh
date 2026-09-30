#!/bin/bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"
export JAVA_HOME="${BERRIEL_JAVA_HOME:-$(/usr/libexec/java_home -v 17)}"
export PATH="$JAVA_HOME/bin:$PATH"
bash scripts/local-db.sh start
# Run the test suite before starting the application.
mvn -B package
exec "$JAVA_HOME/bin/java" -jar casaberriel-backend/target/casaberriel-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=local
