@ECHO OFF
set OLD_DIR=%CD%

cd /d %~dp0
set ROOT_DIR=%CD%
set ANT_HOME=%ROOT_DIR%\contrib\apache-ant
set ANT_OPTS=-Xmx640m -Xss1040K
set PATH=%ANT_HOME%\bin;%PATH%

if "%JAVA_HOME%"=="" (echo The environment variable JAVA_HOME must be set to the location of a valid JDK.) else (
    if "%1"=="" (ant.bat phet-build-gui-jar) else (if "%2"=="" (ant.bat %1) else (ant.bat %1 -Dsim.name=%2))
)

cd "%OLD_DIR%

java -Xmx640m -Xss1040K -jar ant_output\phet-build-gui.jar %ROOT_DIR%\..
