#!/bin/sh
# ----------------------------------------------------------------------------
# Moderne CLI Wrapper startup batch script, version @project.version@
#
# Required ENV vars:
# ------------------
#   JAVA_HOME - location of a JDK home dir
# ----------------------------------------------------------------------------

if [ -z "$JAVA_HOME" ]; then
  echo "Warning: JAVA_HOME environment variable is not set." >&2
fi

JAVACMD="$JAVA_HOME/bin/java"

if [ ! -x "$JAVACMD" ]; then
  echo "Error: JAVA_HOME is not defined correctly." >&2
  echo "  We cannot execute $JAVACMD" >&2
  exit 1
fi

MODW_USER_HOME=$HOME/.modw

modw_version() {
  local version=$(cat "$MODW_USER_HOME/modw.version")
  echo $version
}

MODW_VERSION=$(modw_version)
MODW_JAR_PATH=$MODW_USER_HOME/wrapper/modw-$MODW_VERSION-pg.jar

echo "Running with wrapper jar $WRAPPER_JAR_PATH" >&2
exec "$JAVACMD" -jar "$WRAPPER_JAR_PATH" "$@"

