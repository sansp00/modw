@REM ----------------------------------------------------------------------------
@REM Moderne CLI Wrapper startup batch script, version @project.version@
@REM
@REM Required ENV vars:
@REM JAVA_HOME - location of a JDK home dir
@REM ----------------------------------------------------------------------------

@echo off

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

"%JAVA_HOME%\bin\java.exe" -jar %~dp0modw-@project.version@-pg.jar %*