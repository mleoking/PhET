--

This directory contains the software for PhET simulations that are written using Java.

Running the build script (build.bat for Windows, build.sh for UNIX-based systems)
will display a graphical user interface to assist you in building and running
PhET simulations.

Please see LICENSE.txt and LICENSE-PHET.txt for PhET's licensing policy.
Third-party libraries and tools (located in the "contrib" directory)
have different licensing policies. Please see each contrib subdirectory
for more information.

--

DESCRIPTION OF DIRECTORIES

build*.xml, build.bat, build.sh :
    Ant buildfiles and scripts. 
    See documentation at the top of build.xml for common build targets.
    
build_tools :
   Third-party and PhET tools used to build simulations.

common :
   PhET's library of common code, the framework upon which simulations are built.

contrib :
    Third-party libraries

docs :
    Documents that are applicable to all Java-based PhET simulations.
    
ide:
    Files that are useful for configuring various IDEs (Integrated Development Environments),
    such as Eclipse and IntelliJ IDEA.

simulations:
    Source code and related resources for the Java-based simulations.
    Each subdirectory contains the code for one or more related simulations.

--