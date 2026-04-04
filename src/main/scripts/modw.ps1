# Moderne CLI Wrapper startup script, version @project.version@
# Required ENV vars:
# ------------------
#   JAVA_HOME - location of a JDK home dir

if (-not $env:JAVA_HOME) {
    Write-Error "Warning: JAVA_HOME environment variable is not set."
}

$JAVACMD = "$env:JAVA_HOME\bin\java.exe"

if (-not (Test-Path $JAVACMD -PathType Leaf)) {
    Write-Error "Error: JAVA_HOME is not defined correctly."
    Write-Error "  We cannot execute $JAVACMD"
    exit 1
}

$MODW_USER_HOME = Join-Path $env:USERPROFILE ".modw"

function Get-ModwVersion {
    $version = Get-Content "$MODW_USER_HOME/modw.version"
    return $version
}

$MODW_VERSION = Get-ModwVersion
$MODW_JAR_PATH = Join-Path $MODW_USER_HOME "wrapper/modw-$MODW_VERSION-pg.jar"

Write-Error "Running with wrapper jar $MODW_JAR_PATH"
& $JAVACMD -jar $MODW_JAR_PATH @args
