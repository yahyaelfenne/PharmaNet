@echo off
setlocal
set "PHARMANET_JAVA=java"
for /d %%E in ("%USERPROFILE%\.vscode\extensions\redhat.java-*") do for /d %%J in ("%%~fE\jre\*") do if exist "%%~fJ\bin\java.exe" set "PHARMANET_JAVA=%%~fJ\bin\java.exe"
if defined JAVA_HOME if exist "%JAVA_HOME%\bin\java.exe" set "PHARMANET_JAVA=%JAVA_HOME%\bin\java.exe"
"%PHARMANET_JAVA%" -jar "%~dp0PharmaNet.jar" "%~dp0pharmanet.dat"
exit /b %errorlevel%
