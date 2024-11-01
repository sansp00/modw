@REM ----------------------------------------------------------------------------
@REM Moderne CLI Wrapper startup batch script, version @project.version@
@REM
@REM Required ENV vars:
@REM JAVA_HOME - location of a JDK home dir
@REM ----------------------------------------------------------------------------

@echo off

setlocal enabledelayedexpansion

@REM ==== JAVA_HOME VALIDATION ====
if not "%JAVA_HOME%" == "" goto HasJavaHome

echo. >&2
echo Error: JAVA_HOME not found in your environment. >&2
echo Please set the JAVA_HOME variable in your environment to match the >&2
echo location of your Java installation. >&2
echo. >&2
goto error

:HasJavaHome
if exist "%JAVA_HOME%\bin\java.exe" goto init

echo. >&2
echo Error: JAVA_HOME is set to an invalid directory. >&2
echo JAVA_HOME = "%JAVA_HOME%" >&2
echo Please set the JAVA_HOME variable in your environment to match the >&2
echo location of your Java installation. >&2
echo. >&2
goto error
@REM ==== END JAVA_HOME VALIDATION ====


:init

set "MODW_HOME=%~dp0"
set "MODW_USER_HOME=%MODW_HOME%\.modw"

:value
set "key=%1"
for /f "tokens=1,* delims==" %%a in ('findstr /r "^!key!=.*" !"%MODW_USER_HOME%/modw.properties"!') do (
    set "value=%%b"
)
echo %value%
goto :EOF

set WRAPPER_JAR_PATH=%MODW_HOME%/modw-0.0.1-SNAPSHOT-pg.jar
#set WRAPPER_JAR_PATH=%MODW_HOME%/modw-@project.version@-pg.jar

if exist %MODW_USER_HOME%\modw.properties" {
	echo "Parsing 'modw.properties'"
	set "GROUPID=call value "wrapper.groupId""
	set "ARTIFACTID=call value "wrapper.artifactId""
	set "VERSION=call value "wrapper.version""
	set "QUALIFIER=call value "wrapper.qualifier""

	set "ARTIFACT_REPO_PATH=%GROUPID:.=\%"
	set "WRAPPER_REPO_PATH="%MODW_USER_HOME%\repo\%ARTIFACT_REPO_PATH%\%ARTIFACTID%-%VERSION%-%QUALIFIER%.jar""
	echo "Looking for '%WRAPPER_REPO_PATH%'"  	

	if exist %WRAPPER_REPO_PATH% {
		set "WRAPPER_JAR_PATH=%WRAPPER_REPO_PATH%"
	}
}
echo "Running with wrapper jar '%WRAPPER_JAR_PATH%'"
"%JAVA_HOME%\bin\java.exe" -jar %WRAPPER_JAR_PATH% %*