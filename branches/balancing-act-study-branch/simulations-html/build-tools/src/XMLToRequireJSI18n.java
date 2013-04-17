package edu.colorado.phet.buildtools.html5;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import edu.colorado.phet.translationutility.simulations.Simulation;

//Convert Flash XML strings to JavaScript RequireJS i18n format, requires Java 7
public class XMLToRequireJSI18n {
    public static void main( String[] args ) throws IOException, Simulation.SimulationException {
        Path tempDir = Files.createTempDirectory( "phet-xml-to-requirejs-temp" );

        System.out.println( "Converting XML to properties with temp path: " + tempDir );
        XMLToProperties.main( new String[]{args[0], tempDir.toFile().getAbsolutePath()} );

        System.out.println( "Converting Properties to RequireJS" );
        PropertiesToRequireJSI18n.main( new String[]{tempDir.toFile().getAbsolutePath(), args[1]} );

        System.out.println( "Done" );
    }
}
