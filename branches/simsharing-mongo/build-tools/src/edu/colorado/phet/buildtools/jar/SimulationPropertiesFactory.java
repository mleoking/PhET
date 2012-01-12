/* Copyright 2010, University of Colorado */

package edu.colorado.phet.buildtools.jar;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.logging.Logger;

import edu.colorado.phet.common.phetcommon.application.JARLauncher;
import edu.colorado.phet.flashlauncher.FlashLauncher;
import edu.colorado.phet.flashlauncher.util.SimulationProperties;

/**
 * Factory that extracts a SimulationProperties object from a jar.
 * For newer jars, simulation.properties is an actual file at the top level of the jar.
 * For older jars, simulation.properties does not exist, and the information must
 * be assembled via inspection of various aspects of the jar.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SimulationPropertiesFactory {

    private static final Logger LOGGER = Logger.getLogger( SimulationPropertiesFactory.class.getName() );

    /* not intended for instantiation */
    private SimulationPropertiesFactory() {
    }

    /**
     * Creates a SimulationProperties object by either reading the simulation.properties file
     * from the jar, or (for older sims) constructing the information that would be in
     * simulation.properties using a hodgepodge of inspection techniques.
     * <p/>
     * Do not use this method directly, use JarUtils.readSimulationProperties.
     */
    public static SimulationProperties createSimulationProperties( String jarFileName ) throws IOException {

        SimulationProperties simulationProperties = null;

        if ( JarUtils.containsFile( jarFileName, SimulationProperties.FILENAME ) ) {
            LOGGER.info( "new-style simulation, reading " + SimulationProperties.FILENAME );
            // jar file contains simulation.properties, read it
            Properties properties = JarUtils.readProperties( jarFileName, SimulationProperties.FILENAME );
            simulationProperties = new SimulationProperties( properties );
        }
        else {
            // simulation.properties was not found, construct it using old-style techniques.
            if ( FlashOldStyle.isFlash( jarFileName ) ) {
                LOGGER.info( "old-style Flash simulation, faking " + SimulationProperties.FILENAME );
                simulationProperties = FlashOldStyle.createSimulationProperties( jarFileName );
            }
            else {
                LOGGER.info( "old-style Java simulation, faking " + SimulationProperties.FILENAME );
                simulationProperties = JavaOldStyle.createSimulationProperties( jarFileName );
            }
        }

        LOGGER.info( SimulationProperties.FILENAME + ": " + simulationProperties.toString() );
        return simulationProperties;
    }

    /**
     * Flash sim utilities for creating SimulationProperties when no such file exists.
     * This class is provide solely for backward compatibility support, and it should
     * be deleted when all sims have been redeployed and have a simulation.properties file.
     *
     * @deprecated
     */
    private static class FlashOldStyle {

        /* not intended for instantiation */
        private FlashOldStyle() {
        }

        /**
         * Does the jar contain a Flash sim?
         * A Flash sim's jar file contains a .swf file.
         *
         * @param jarFileName
         * @returns true or false
         */
        public static boolean isFlash( String jarFileName ) throws IOException {

            // open the jar
            JarInputStream jarInputStream = new JarInputStream( new FileInputStream( jarFileName ) );

            // look for the .swf file
            boolean containsSWF = false;
            JarEntry jarEntry = jarInputStream.getNextJarEntry();
            while ( jarEntry != null ) {
                if ( jarEntry.getName().endsWith( ".swf" ) ) {
                    containsSWF = true;
                    break;
                }
                else {
                    jarEntry = jarInputStream.getNextJarEntry();
                }
            }

            return containsSWF;
        }

        /**
         * Creates simulation properties for a Flash sim jar that
         * doesn't contain a simulation.properties file.
         * All of this information is in flash-launcher-args.txt.
         *
         * @param jarFileName
         * @return SimulationProperties
         * @throws IOException
         */
        public static SimulationProperties createSimulationProperties( String jarFileName ) throws IOException {

            // read flash-launcher-args.txt
            String flashLauncherArgsString = JarUtils.readText( jarFileName, FlashLauncher.ARGS_FILENAME, 100 /* bufferSize */ );
            StringTokenizer tokenizer = new StringTokenizer( flashLauncherArgsString, " " );
            String projectName = tokenizer.nextToken();
            String simulation = projectName; // Flash sims that predate simulations.properties had only one sim per project
            String language = tokenizer.nextToken();
            String country = null;
            if ( tokenizer.hasMoreTokens() ) {
                country = tokenizer.nextToken();
            }

            // create simulation.properties
            Locale locale = null;
            if ( country == null ) {
                locale = new Locale( language );
            }
            else {
                locale = new Locale( language, country );
            }
            String type = SimulationProperties.TYPE_FLASH;
            return new SimulationProperties( projectName, simulation, locale, type );
        }
    }

    /**
     * Java sim utilities for creating SimulationProperties when no such file exists.
     * This class is provide solely for backward compatibility support, and it should
     * be deleted when all sims have been redeployed and have a simulation.properties file.
     *
     * @deprecated
     */
    private static class JavaOldStyle {

        /* not intended for instantiation */
        private JavaOldStyle() {
        }

        /**
         * Creates simulation properties for a Java sim jar that
         * doesn't contain a simulation.properties file.
         * All of this information is in jar-launcher.properties.
         *
         * @param jarFileName
         * @return SimulationProperties
         * @throws IOException
         */
        public static SimulationProperties createSimulationProperties( String jarFileName ) throws IOException {

            // read jar-launcher.properties
            Properties jarLauncherProperties = JarUtils.readProperties( jarFileName, JARLauncher.PROPERTIES_FILE_NAME );
            String projectName = jarLauncherProperties.getProperty( JARLauncher.PROJECT_NAME_KEY );
            String simulation = jarLauncherProperties.getProperty( JARLauncher.FLAVOR_KEY );
            String language = jarLauncherProperties.getProperty( JARLauncher.LANGUAGE_KEY );
            String country = jarLauncherProperties.getProperty( JARLauncher.COUNTRY_KEY );

            // create simulation properties
            Locale locale = null;
            if ( country == null ) {
                locale = new Locale( language );
            }
            else {
                locale = new Locale( language, country );
            }
            String type = SimulationProperties.TYPE_JAVA;
            return new SimulationProperties( projectName, simulation, locale, type );
        }
    }
}
