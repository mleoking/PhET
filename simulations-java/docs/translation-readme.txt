PhET Translation Readme File
5-19-2007

In order to translate PhET simulations, you need:
-A Java JDK must be installed on your machine.  This is available from http://java.sun.com/javase/downloads/index.jsp
-The tools from the Java JDK must be on your path (i.e. "java" and "jar" commands)

The directory structure for a simulation is:
sims/simulation.jar
sims/simulation/localization/simulation-strings.properties
sims/simulation/localization/simulation-strings_es.properties

Translating a Simulation:
1. Create a new file in the localization directory, with the 2-character abbreviation for the language you wish to create.
    -make a copy of the language file you wish to translate from 
2. Open the file for editing in a text editor.
3. Open a terminal window in the directory of the simulation
4. To integrate the translation file into the program:  jar -ufv simulation.jar simulation
where "simulation" is the name of the simulation to update
5. To run the translated program:                       java -Djavaws.phet.locale=la -jar simulation.jar
where "la" is the 2-character abbreviation for the language 

For example, if the simulation is moving-man and the language is french, the commands would be:
jar -ufv moving-man.jar moving-man
java -Djavaws.phet.locale=fr -jar moving-man.jar

You can repeat this process as many times as you like to fine-tune the translation.
Don't forget to save the translated file in your text editor (and integrate into the simulation) before each test.

Please email the final translation files to phethelp [PhEThelp@Colorado.edu].