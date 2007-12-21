This directory contains files that are specific to Chris Malley's Eclipse development environment.

The .jardesc and .mf files pertain to projects.
A .jardesc file tells Eclipse how to build a self-contained JAR for a project.
A .mf file defines the contents of the JAR's MANIFEST.MF file, and identifies the main class.

ProGuard configuration files (*-mac.pro) are included for Macintosh,
which requires including a different set of JAR files for the Java libraries.

Here's my process for building a project:
1. run the .jardesc file from Eclipse to create the unprocessed JAR
2. run the .pro file from ProGuard to create the processed JAR
