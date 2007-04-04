This directory contains the software for PhET simulations.

#########################
#   Building
#########################
As a convenience, the targets may be invoked with the 'build' script (build.bat on Windows), which allows you to avoid specifying -Dname.sim on the command-line for Ant. The syntax then simplifies to 'build [target] [simname]'.

The important targets are as follows:

•	build: Builds the specified simulation, and any dependencies (if required).
•	test: Tests the specified simulation, and any dependencies.
•	create: Creates a new simulation.
•	clean: Deletes any simulation-specific files created by the build process (but not any dependency-specific files).
•	edit: Edits the build or version parameters of an existing simulation.
•	deploy-dev: Builds and deploys a development version. This will auto-increment the dev number.
•	deploy-prod: Builds and deploys a production version (as well as the development version). This will auto-increment the minor version number, and reset the dev number.
•	build-all: Builds all simulations (this won't work until all simulations and dependencies have build properties).
•	test-all: Tests all simulations.

In order to use the new build process, define a 'build.properties' file in the simulation/dependency root. It should look something like this:

--------------------------
project.name=Reactions &amp; Rates
project.mainclass=edu.colorado.phet.molecularreactions.MRApplication
project.description=What makes a reaction happen? Find out what affects the rate of a reaction. Do experiments, collect data, and then calculate rate coefficients. Play with different reactions, concentrations, and energy.
project.depends.lib=lib/commons-collections-3.2.jar : charts : collision : jfreechart-phet : mechanics : phetgraphics : piccolo : piccolo-phet : phetcommon
project.depends.source=src
project.depends.data=data
project.screenshot=screenshot.jpg