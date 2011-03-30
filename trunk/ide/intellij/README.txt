This directory contains files related to IntelliJ for PhET simulations and projects

To create an IntelliJ project for phet:
1. Create a new empty IntelliJ project.
2. When prompted for a module, add the trunk/phet.iml IntelliJ module file (or use Project Settings -> Project Settings -> Modules -> +)
3. Increase the compiler memory heap to 512M File -> Settings -> Compiler -> Java Compiler -> Maximum heap size 512M
4. Use the phet coding style:
    a. close IntelliJ
    b. copy trunk/ide/intellij/PhetStyle.xml to the IntelliJ code style directory, something like C:\Users\[username]\.IntelliJIdea10\config\codestyles
    c. start IntelliJ, opening the phet project
    d. File -> Settings -> Code Style -> Use Global Settings : PhetStyle
    (optional) e. If you are having trouble finding the directory in step b., one way to track it down is to create a dummy code style with a unique name, save it, close intellij, then search your file system for the unique name