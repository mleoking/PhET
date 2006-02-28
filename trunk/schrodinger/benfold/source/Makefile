JAVAC = javac
JCFLAGS = -O -target 1.1
JAR = jar
JARFLAGS =
JAVADOC = javadoc
JAVADOCFLAGS = -private -splitindex -nodeprecatedlist 



#all: classes.jar html/docs html/source.tar.gz
all: applets docs archive

applets: classes.jar

archive: html/source.tar.gz

docs:    html/docs




classes.jar: *.java
	$(JAVAC) $(JCFLAGS) *.java
	$(JAR) $(JARFLAGS) cf classes.jar *.class
	cp classes.jar html/

html/source.tar.gz: *.java html/*.html Makefile readme.txt
	-rm html/source.tar.gz
	tar cpfz source.tar.gz *.java Makefile readme.txt html/*.html
	cp source.tar.gz html

html/docs: *.java
	-mkdir html/docs
	$(JAVADOC) -d html/docs $(JAVADOCFLAGS) *.java
	touch html/docs

clean:
	-rm *.class
	-rm classes.jar

veryclean: clean
	-rm -r html/docs
	-rm html/classes.jar
	-rm source.tar.gz
