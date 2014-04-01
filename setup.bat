@echo off
set slimevoid=2.0.4.5

rem if exist "%slimevoid%\build\libs" goto :SETUP
rem goto :FAIL

:SETUP
call %CD%\gradlew.bat -PSV=%slimevoid% clean
call %CD%\gradlew.bat -PSV=%slimevoid% setupDecompWorkspace
call %CD%\gradlew.bat -PSV=%slimevoid% eclipse
goto :COMPLETE

:FAIL
echo Please build the Slimevoid library first
set errorlevel=999
pause

:COMPLETE