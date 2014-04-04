@echo off
call %CD%\gradlew.bat clean
call %CD%\gradlew.bat setupDecompWorkspace
call %CD%\gradlew.bat eclipse