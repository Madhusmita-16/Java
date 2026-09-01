@echo off
@setlocal

set MAVEN_PROJECTBASEDIR=%~dp0
if "%MAVEN_PROJECTBASEDIR:~-1%"=="\" set MAVEN_PROJECTBASEDIR=%MAVEN_PROJECTBASEDIR:~0,-1%

if exist "%MAVEN_PROJECTBASEDIR%\.mvn\apache-maven-3.9.6\bin\mvn.cmd" (
    call "%MAVEN_PROJECTBASEDIR%\.mvn\apache-maven-3.9.6\bin\mvn.cmd" %*
    exit /b %ERRORLEVEL%
)

mvn %*
