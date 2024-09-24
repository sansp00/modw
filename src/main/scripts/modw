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

MODW_HOME=$( dirname "$0" )

exec "$JAVACMD" -jar "$MODW_HOME/modw-@project.version@-pg.jar" "$@"