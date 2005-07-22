:rem
:rem Runs the ant script to jar a translated properties file
:rem
:rem Calling sequence:
:rem	jar-xlate <language-spec> <properties file>
:rem

set JAVA_HOME=C:\j2sdk1.4.2_06
C:\ant\apache-ant-1.5.3-1\bin\ant -buildfile C:\PhET\i18n\localizations\spanish\translation-jar.xml -Dlanguage-id=%1 -Dinput-file=%2