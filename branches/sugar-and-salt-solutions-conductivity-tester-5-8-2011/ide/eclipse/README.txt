--

After checking out this SVN tree as a Java project, 
the files in this directory can be used to quickly configure Eclipse.

If you use Eclipse to alter these files, please copy the changed files
to this directory, so that other developers can take advantage of your work.

If "trunk" is your Eclipse project name, follow these steps:

(1) copy dot-classpath.xml to trunk/.classpath
This sets the Java classpath, and associates source code with JAR files.

(2) copy org.eclipse.jdt.core.prefs to trunk/.settings/org.eclipse.jdt.core.prefs
This configures compiler settings for your Eclipse project.

(3) Import the PhET code format conventions.
In Eclipse 3.3, go to Eclipse>Preferences>Java>Code Style>Formatter, then import phet-code-format.xml.

(4) Import the PhET code templates.
In Eclipse 3.3, go to Eclipse>Preferences>Java>Code Style>Code Templates, then import phet-code-templates.xml.

(5) Select your project in the Package Explorer view, then refresh by pressing F5 or choose File>Refresh.
This tells Eclipse that files have been changed outside of Eclipse.

(6) Use eclipse-dictionary.txt as the user-defined spellcheck dictionary.
See Preferences>General>Editors>Text Editors>Spelling>User defined dictionary.


--
