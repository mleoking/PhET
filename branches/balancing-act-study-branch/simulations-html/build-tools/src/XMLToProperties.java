package edu.colorado.phet.buildtools.html5;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import edu.colorado.phet.translationutility.simulations.FlashSimulation;
import edu.colorado.phet.translationutility.simulations.Simulation;

/**
 * Transform Flash XML string translations to RequireJSI18n format.
 */
public class XMLToProperties {
    public static void main( String[] args ) throws Simulation.SimulationException, IOException {
        //Source is the localization directory, such as "C:\workingcopy\phet\svn-1.7\trunk\simulations-java\simulations\energy-skate-park\data\energy-skate-park\localization"
        File source = new File( args[0] );

        //Destination is the target nls root, such as "C:\workingcopy\phet\svn-1.7\trunk\simulations-html5\simulations\energy-skate-park-easeljs-jquerymobile\src\app\nls"
        File destination = new File( args[1] );
        destination.mkdirs();

        for ( File file : source.listFiles( new FilenameFilter() {
            public boolean accept( File dir, String name ) {
                return name.endsWith( ".xml" );
            }
        } ) ) {
            FlashSimulation flash = new FlashSimulation( "jarFileName", "projectName", "simulationName" );
            Properties s = flash.loadStrings( file );
            File destFile = new File( destination, file.getName().replace( ".xml", ".properties" ).replace( "_en.properties", ".properties" ) );
            try {
                String header = "";
                // write properties to file
                OutputStream outputStream = new FileOutputStream( destFile );
                s.store( outputStream, header );
                outputStream.close();
            }
            catch( IOException e ) {
                throw new Simulation.SimulationException( e );
            }

        }
    }
}
