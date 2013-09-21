@echo off

set programdir=C:\Programming\Minecraft\1.5.2
set repodir=%programdir%\Git
set packagedir=%programdir%\Packages
set forgedir=%programdir%\Forge
set fmldir=%forgedir%\fml
set mcpdir=%forgedir%\mcp
set littleblocks=%repodir%\LittleBlocks-FML
set slimelib=%repodir%\SlimevoidLibrary
cd %mcpdir%

if not exist %slimelib% GOTO :LBFAIL
if exist %littleblocks% GOTO :LITTLEBLOCKS
GOTO :LBFAIL

:LITTLEBLOCKS
if exist %mcpdir%\src GOTO :COPYSRC
GOTO :LBFAIL

:COPYSRC
if not exist "%mcpdir%\src-work" GOTO :CREATESRC
GOTO :LBFAIL

:CREATESRC
mkdir "%mcpdir%\src-work"
xcopy "%mcpdir%\src\*.*" "%mcpdir%\src-work\" /S
if exist "%mcpdir%\src-work" GOTO :COPYLB
GOTO :LBFAIL

:COPYLB
xcopy "%slimelib%\SV-common\*.*" "%mcpdir%\src\minecraft\" /S
xcopy "%littleblocks%\LB-source\*.*" "%mcpdir%\src\minecraft\" /S
pause
call %mcpdir%\recompile.bat
call %mcpdir%\reobfuscate.bat
echo Recompile and Reobf Completed Successfully
pause

:REPACKAGE
if not exist "%mcpdir%\reobf" GOTO :LBFAIL
if exist "%packagedir%\LittleBlocks" (
del "%packagedir%\LittleBlocks\*.*" /S /Q
rmdir "%packagedir%\LittleBlocks" /S /Q
)
mkdir "%packagedir%\LittleBlocks\slimevoid\littleblocks"
xcopy "%mcpdir%\reobf\minecraft\slimevoid\littleblocks\*.*" "%packagedir%\LittleBlocks\slimevoid\littleblocks\" /S
xcopy "%littleblocks%\LB-resources\*.*" "%packagedir%\LittleBlocks\" /S
echo "LittleBlocks Packaged Successfully
pause
ren "%mcpdir%\src" src-old
echo Recompiled Source folder renamed
pause
ren "%mcpdir%\src-work" src
echo Original Source folder restored
pause
del "%mcpdir%\src-old" /S /Q
del "%mcpdir%\reobf" /S /Q
if exist "%mcpdir%\src-old" rmdir "%mcpdir%\src-old" /S /Q
if exist "%mcpdir%\reobf" rmdir "%mcpdir%\reobf" /S /Q
echo Folder structure reset
GOTO :LBCOMPLETE

:LBFAIL
echo Could not compile littleblocks
GOTO :LBEND

:LBCOMPLETE
echo Littleblocks completed compile successfully
GOTO :LBEND

:LBEND
pause