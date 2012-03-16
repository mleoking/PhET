ScalaTest 0.9.5

ScalaTest is a free, open-source testing toolkit for Scala and
Java programmers.  Because different developers take different approaches to creating
software, no single approach to testing is a good fit for everyone. In light of
this reality, ScalaTest is designed to facilitate different styles of testing. ScalaTest
provides several traits that you can mix together into whatever combination makes you feel the most productive.
For some examples of the various styles that ScalaTest supports, see:

http://www.artima.com/scalatest

GETTING STARTED

Although ScalaTest 0.9.5 sports a pre-1.0 version number, ScalaTest is mature,
fully featured, and thoroughly documented.
You can already use it to write tests in Scala against
production Scala or Java code. To learn how to use ScalaTest, please
open in your browser the scaladoc documentation in the
/scalatest-0.9.5/doc directory. Look first at the documentation for trait
org.scalatest.Suite, which gives a decent intro. All the other types are
documented as well, so you can hop around to learn more.
org.scalatest.tools.Runner explains how to use the application. The
Ignore class is written in Java, and isn't currently shown in the Scaladoc.

To try it out, you can use ScalaTest to run its own tests, i.e., the tests
used to test ScalaTest itself. This command will run the GUI:

scala -classpath scalatest-0.9.5.jar org.scalatest.tools.Runner -p "scalatest-0.9.5-tests.jar" -g -s org.scalatest.SuiteSuite

This command will run and just print results to the standard output:

scala -classpath scalatest-0.9.5.jar org.scalatest.tools.Runner -p "scalatest-0.9.5-tests.jar" -o -s org.scalatest.SuiteSuite

ScalaTest 0.9.5 was tested with Scala version 2.7.3.final, so it is not
guaranteed to work with earlier Scala versions.

ABOUT SCALATEST

ScalaTest was written by Bill Venners, Josh Cough, and George Berger starting
in late 2007. ScalaTest, which is almost exclusively written in Scala,
follows and improves upon the Java code and design of Artima SuiteRunner,
a testing tool also written primarily by Bill Venners, starting in 2001. Over
the years a few other people have contributed to SuiteRunner as
well, including:

Chris Daily
Chua Chee Seng
Frank Sommers
John Mitchel
Mark Brouwer
Matt Gerrans

Note: This is an updated 0.9.5 zip file, in which several unneeded files (such
as with names ':w' and delme) were removed from the src and test areas. Other than the
removal of those files, and this note in README.txt, the zip file contents
are identical with the original 0.9.5 zip file.

