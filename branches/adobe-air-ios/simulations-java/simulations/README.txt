--

This directory contains the source code and related resources for PhET simulations 
that were written using Java.

Each subdirectory is a "project" that contains one or more simulations (aka "flavors").  

Here's an example that illustrates the typical directory layout for a project named "foo"
that contains simulations "foo1" and "foo2":

foo/
   assets/  - files used to create resources in data/foo/, not deployed
   data/foo/ - resources that will be deployed in JAR
      audio/ - audio files
      images/ - image files
      localization/ - localized string files
          foo-strings.properties - English strings
          foo-strings_es.properties - Spanish (es) strings
          ...
      foo.properties - project properties
   deploy/ - any files in here will be deployed, build process creates JAR here
   doc/ - any documents related to this project
   screenshots/ - high-resolution screenshots, one for each flavor, used on website
      foo1-screenshot.png
      foo2-screenshot.png
   src/edu/colorado/phet/foo - Java source code, note package name
   foo-build.properties - describes dependencies for build process
   
   
CAVEATS: 
   Some older simulations may be missing parts of the above directory structure,
   or may have additional directories.
   
--