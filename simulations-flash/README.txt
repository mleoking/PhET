--

This directory contains the software for PhET simulations that are written using Flash.

Running the build script (build.bat for Windows, build.sh for UNIX-based systems)
will display a graphical user interface to assist you in building and running
PhET simulations.

Please see LICENSE.txt and LICENSE-PHET.txt for PhET's licensing policy.
Third-party libraries and tools (located in the "contrib" directory)
have different licensing policies. Please see each contrib subdirectory
for more information.

--

DESCRIPTION OF DIRECTORIES

build-tools :
    Code for building Flash simulations (HTML generation, etc.)
    
flash-launcher :
   Mechanism for launching Flash simulations from a bundled JAR file.

common :
   Common code used in multiple simulations.

contrib :
    Third party libraries that are used in Flash simulations

simulations:
    Source code and related resources for the Flash-based simulations.
    Each subdirectory contains the code for one simulation.

old-simulations:
    Copies of the simulations before common code was added (pre-IOM)

--
