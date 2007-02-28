javac com\graphbuilder\struc\*.java
javac com\graphbuilder\math\func\*.java
javac com\graphbuilder\math\*.java
javac com\graphbuilder\geom\*.java
javac com\graphbuilder\curve\*.java

jar cfv Capi.jar com\graphbuilder\struc\*.class com\graphbuilder\math\func\*.class com\graphbuilder\math\*.class com\graphbuilder\geom\*.class com\graphbuilder\curve\*.class license.txt