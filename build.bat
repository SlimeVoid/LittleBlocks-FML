call %CD%\setup.bat
if %errorlevel%==999 goto :FAIL

call %CD%\gradlew.bat build
goto :END

:FAIL

:END