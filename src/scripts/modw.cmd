@REM ----------------------------------------------------------------------------
@REM Moderne CLI Wrapper startup batch script, version @project.version@
@REM
@REM Required ENV vars:
@REM JAVA_HOME - location of a JDK home dir
@REM ----------------------------------------------------------------------------

@echo off


@REM ==== JAVA_HOME VALIDATION ====
IF NOT "%JAVA_HOME%" == "" GOTO HasJavaHome

echo. >&2
echo Error: JAVA_HOME not found in your environment. >&2
echo Please set the JAVA_HOME variable in your environment to match the >&2
echo location of your Java installation. >&2
echo. >&2
GOTO error

:HasJavaHome
IF exist "%JAVA_HOME%\bin\java.exe" GOTO ReadProperties

echo. >&2
echo Error: JAVA_HOME is set to an invalid directory. >&2
echo JAVA_HOME = "%JAVA_HOME%" >&2
echo Please set the JAVA_HOME variable in your environment to match the >&2
echo location of your Java installation. >&2
echo. >&2
GOTO error
@REM ==== END JAVA_HOME VALIDATION ====

:ReadProperties
set "MODW_HOME=%~dp0"
set "MODW_USER_HOME=%HOMEDRIVE%%HOMEPATH%\.modw"
set "WRAPPER_REPO_PATH="

IF not exist "%MODW_USER_HOME%\modw.properties" ( 
	GOTO ExecuteWrapper
)

CALL :getValueOf "wrapper.groupId"
set "GROUPID=%value%"

CALL :getValueOf "wrapper.artifactId"
set "ARTIFACTID=%value%"

CALL :getValueOf "wrapper.version"
set "VERSION=%value%"

CALL :getValueOf "wrapper.qualifier"
set "QUALIFIER=%value%"

set "ARTIFACT_REPO_PATH=%GROUPID:.=/%"
set "WRAPPER_REPO_PATH=%MODW_USER_HOME%\repo\%ARTIFACT_REPO_PATH%\%ARTIFACTID%-%VERSION%-%QUALIFIER%.jar"

:ExecuteWrapper
if exist "%WRAPPER_REPO_PATH%" (
  set "WRAPPER_JAR_PATH=%WRAPPER_REPO_PATH%"
) else (
  set "WRAPPER_JAR_PATH=%MODW_HOME%modw-0.0.1-SNAPSHOT-pg.jar"
)

echo "Running with wrapper %WRAPPER_JAR_PATH%" >&2
%JAVA_HOME%\bin\java.exe -jar "%WRAPPER_JAR_PATH%" %*

:getValueOf
setlocal enabledelayedexpansion
set "key=%~1"
set "modwProperties=%MODW_USER_HOME%\modw.properties"
FOR /f "tokens=1,2 delims==" %%a IN (!modwProperties!) DO (
    IF "%%a"=="!key!" (
        set "value=%%b"
		endlocal
		exit /b
    )
)
set "value="
endlocal
exit /b 