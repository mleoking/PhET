@ECHO OFF
set OLD_DIR=%CD%

cd /d %~dp0
set ROOT_DIR=%CD%
set ANT_HOME=%ROOT_DIR%\contrib\apache-ant
set ANT_OPTS=-Xmx640m -Xss1040K
set PATH=%ANT_HOME%\bin;%PATH%

if "%JAVA_HOME%"=="" (echo The environment variable JAVA_HOME must be set to the location of a valid JDK.) else (
    if "%1"=="" (ant.bat -buildfile test-build.xml) else (if "%2"=="" (ant.bat -buildfile test-build.xml %1) else (ant.bat -buildfile test-build.xml %1 -Dsim.name=%2))
)

cd "%OLD_DIR%
