
:The target installation directory ${installdir} is passed as the first
:argument to the script. This script in turns creates a demo.bat script
:and substitutes the @@installdir@@ placefolder with the actual
:installation directory value

set string=%1
set string=%string:\=/%
"%1\bin\sed" "s|@@installdir@@|{%string%}|g" "%1\bin\demo.txt" > "%1\bin\demo1.txt"
"%1\bin\sed" "s/{//;s/}//" "%1\bin\demo1.txt" > "%1\bin\demo.bat"
del "%1\bin\demo.txt"
del "%1\bin\demo1.txt"

