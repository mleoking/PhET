@ECHO OFF
set OLD_DIR=%CD%

cd /d %~dp0
set ROOT_DIR=%CD%
set ANT_HOME=%ROOT_DIR%\build-tools\apache-ant-1.7.0

set PATH=%ANT_HOME%\bin;%PATH%

if "%JAVA_HOME%"=="" (echo The environment variable JAVA_HOME must be set to the location of a valid JDK.) else (
    if "%1"=="" (ant.bat) else (if "%2"=="" (ant.bat %1) else (ant.bat %1 -Dname.sim=%2))
)

cd "%OLD_DIR%
