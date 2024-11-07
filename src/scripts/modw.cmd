@REM ----------------------------------------------------------------------------
@REM Moderne CLI Wrapper startup batch script, version @project.version@
@REM
@REM Required ENV vars:
@REM JAVA_HOME - location of a JDK home dir
@REM ----------------------------------------------------------------------------

@ECHO off

@REM ==== JAVA_HOME VALIDATION ====
IF NOT "%JAVA_HOME%" == "" GOTO HasJavaHome

ECHO. >&2
ECHO Error: JAVA_HOME not found in your environment. >&2
ECHO Please SET the JAVA_HOME variable in your environment to match the >&2
ECHO location of your Java installation. >&2
ECHO. >&2
GOTO error

:HasJavaHome
IF EXIST "%JAVA_HOME%\bin\java.exe" (
    SET "JAVACMD="%JAVA_HOME%/bin/java""
    GOTO ReadProperties
)

ECHO. >&2
ECHO Error: JAVA_HOME is SET to an invalid directory. >&2
ECHO JAVA_HOME = "%JAVA_HOME%" >&2
ECHO Please SET the JAVA_HOME variable in your environment to match the >&2
ECHO location of your Java installation. >&2
ECHO. >&2
GOTO error
@REM ==== END JAVA_HOME VALIDATION ====

:ReadProperties
SET "MODW_HOME=%~dp0"
SET "MODW_USER_HOME=%HOMEDRIVE%%HOMEPATH%\.modw"
SET "WRAPPER_REPO_PATH="

IF NOT EXIST "%MODW_USER_HOME%\modw.properties" (
	GOTO ExecuteWrapper
)

CALL :getValueOf "wrapper.groupId"
SET "GROUPID=%value%"

CALL :getValueOf "wrapper.artifactId"
SET "ARTIFACTID=%value%"

CALL :getValueOf "wrapper.version"
SET "VERSION=%value%"

CALL :getValueOf "wrapper.qualifier"
SET "QUALIFIER=%value%"

SET "ARTIFACT_REPO_PATH=%GROUPID:.=/%"
SET "WRAPPER_REPO_PATH=%MODW_USER_HOME%\repo\%ARTIFACT_REPO_PATH%\%ARTIFACTID%-%VERSION%-%QUALIFIER%.jar"

:ExecuteWrapper
IF EXIST "%WRAPPER_REPO_PATH%" (
  SET "WRAPPER_JAR_PATH=%WRAPPER_REPO_PATH%"
) ELSE (
  SET "WRAPPER_JAR_PATH=%MODW_HOME%modw-@project.version@-pg.jar"
)

ECHO "Running with wrapper %WRAPPER_JAR_PATH%" >&2
%JAVACMD% -jar "%WRAPPER_JAR_PATH%" %*

:getValueOf
SETLOCAL enabledelayedexpansion
SET "value="
SET "key=%~1"
SET "modwProperties=%MODW_USER_HOME%\modw.properties"
FOR /f "tokens=1,2 delims==" %%a IN (!modwProperties!) DO (
    IF "%%a"=="!key!" (
        SET "value=%%b"
		ENDLOCAL
		EXIT /b
    )
)
endlocal
EXIT /b