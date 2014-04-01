@echo off
set slimevoid=%CD%\..\SlimevoidLibrary

if exist "%slimevoid%\build\libs" goto :SETUP
goto :FAIL

:SETUP
call %CD%\gradlew.bat clean
call %CD%\gradlew.bat setupDecompWorkspace
call %CD%\gradlew.bat eclipse
goto :COMPLETE

:FAIL
echo Please build the Slimevoid library first
set errorlevel=999
pause

:COMPLETE