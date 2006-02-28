To build, use the makefile provided.  Targets are:

	applets:    Builds archive of classes (classes.jar).  This is the minimum
				necessary for the applets to work.

    docs:       Constructs the Javadoc documentation.  This will make the
				documentation links work.

	archive:	Creates a "source.tar.gz", containing everything (except
				itself).

	all (default):  Builds all of the above

	clean:      Removes all intermediate files (just the class files)

	veryclean:	Removes all generated files


Output is into the html/ subdirectory, which should already contain a page for
each applet.   The classes.jar file will be placed here, and a  docs/
directory created for the javadoc pages.  Finally, the source.tar.gz file will
be placed in this directory.


Structure when built is:

[root]
  |
  +-- Java source files (*.java)
  +-- classes.jar
  +-- source.tar.gz
  +-- readme.txt
  +-- Makefile
  +-- html
        |
        +-- Web pages for applets (*.html)
        +-- classes.jar   (copied from parent dir)
        +-- source.tar.gz (copied from parent dir)
        +-- docs
              |
              +-- Javadoc documentation