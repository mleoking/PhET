:rem
:rem Runs the ant script to jar a translated properties file
:rem
:rem Calling sequence:
:rem	jar-xlate <language-spec> <properties file>
:rem

set JAVA_HOME=C:\j2sdk1.4.2_06

jar cvf %2_%1.jar %2_%1.properties 