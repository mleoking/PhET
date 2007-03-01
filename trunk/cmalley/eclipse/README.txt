This directory contains files that are specific to Chris Malley's Eclipse development environment.

Each simulation project that I've worked on has a .jardesc and a .mf file.
A .jardesc file tells Eclipse how to build a self-contained JAR for the simulation.
A .mf file defines the contents of a simulation's MANIFEST.MF file, and includes 
the CVS tag information and main class information for the simulation.

ProGuard configuration files (*-mac.pro) are included for Macintosh,
which requires including a different set of JAR files for the Java libraries.

Here's my process for building and publishing a simulation:

1. edit the .mf file to contain the CVS tag for the release
2. run the .jardesc file from Eclipse to create the unprocessed JAR
3. run the .pro file from ProGuard to create the processed JAR
4. scp the processed JAR to spot.colorado.edu
5. move the processed JAR to the proper subdirectory in spot:/htdocs/physics/phet/dev/


