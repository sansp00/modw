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
MODW_USER_HOME=$HOME/.modw

value() {
    local key=$1
    local value=$(grep "^${key}=" "$MODW_USER_HOME/modw.properties" | cut -d'=' -f2)
    echo $value
}

WRAPPER_JAR_PATH=$MODW_HOME/modw-0.0.1-SNAPSHOT-pg.jar
#WRAPPER_JAR_PATH=$MODW_HOME/modw-@project.version@-pg.jar
if [ -f "$MODW_USER_HOME/modw.properties" ]; then
	echo "Parsing 'modw.properties'" >&2 	
	GROUPID=$(value "wrapper.groupId")
	ARTIFACTID=$(value "wrapper.artifactId")
	VERSION=$(value "wrapper.version")
	QUALIFIER=$(value "wrapper.qualifier")

	ARTIFACT_REPO_PATH=$(echo $GROUPID | tr '.' '/')
	WRAPPER_REPO_PATH=$MODW_USER_HOME/repo/$ARTIFACT_REPO_PATH/$ARTIFACTID-$VERSION-$QUALIFIER.jar
	echo "Looking for '$WRAPPER_REPO_PATH'" >&2 	
	if [ -f $WRAPPER_REPO_PATH ]; then
		WRAPPER_JAR_PATH=$WRAPPER_REPO_PATH
	fi
fi

echo "Running with wrapper jar $WRAPPER_JAR_PATH" >&2
exec "$JAVACMD" -jar "$WRAPPER_JAR_PATH" "$@"

