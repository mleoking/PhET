/* Copyright 2008-2010, University of Colorado */

package edu.colorado.phet.translationutility.simulations;

import java.io.*;
import java.util.Locale;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.logging.Logger;

import edu.colorado.phet.common.phetcommon.application.JARLauncher;
import edu.colorado.phet.flashlauncher.util.SimulationProperties;
import edu.colorado.phet.translationutility.TUConstants;
import edu.colorado.phet.translationutility.simulations.ISimulation.SimulationException;
import edu.colorado.phet.translationutility.util.JarUtils;

/**
 * SimulationFactory creates an ISimulation based on information in the simulation JAR file.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SimulationFactory {
    
    private static final Logger LOGGER = Logger.getLogger( SimulationFactory.class.getName() );

    private SimulationFactory() {}

    /**
     * Creates an ISimulation based on what's in the JAR file.
     * Ideally we get this information from the simulation.properties file.
     * But that file may not exist for older sims, so we may need to resort to other techniques.
     * See #2463.
     * 
     * @param jarFileName
     * @return
     * @throws SimulationException
     */
    public static ISimulation createSimulation( String jarFileName ) throws SimulationException {

        ISimulation simulation = null;

        if ( isJar( jarFileName ) ) {
            SimulationProperties properties = createSimulationProperties( jarFileName );
            if ( properties.isFlash() ) {
                simulation = new FlashSimulation( jarFileName, properties.getProject(), properties.getSimulation() );
            }
            else {
                simulation = new JavaSimulation( jarFileName, properties.getProject(), properties.getSimulation() );
            }
        }
        else {
            throw new SimulationException( "not a jar file: " + jarFileName );
        }

        return simulation;
    }

    /*
     * Is the JAR file actually a JAR?
     * Not a great test, but the JAR API stinks, and this is better than nothing.
     */
    private static boolean isJar( String jarFileName ) {
        return jarFileName.endsWith( ".jar" );
    }

    /*
     * Creates a SimulationProperties object by either reading the simulation.properties file 
     * from the jar, or (for older sims) constructing the information that would be in 
     * simulation.properties using a hodgepodge of inspection techniques.
     */
    private static SimulationProperties createSimulationProperties( String jarFileName ) throws SimulationException {

        SimulationProperties simulationProperties = null;

        if ( JarUtils.containsFile( jarFileName, SimulationProperties.FILENAME ) ) {
            LOGGER.info( "new-style simulation, reading " + SimulationProperties.FILENAME );
            // jar file contains simulation.properties, read it
            Properties properties = JarUtils.readPropertiesFromJar( jarFileName, SimulationProperties.FILENAME );
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
     * @deprecated
     */
    private static class FlashOldStyle {

        private FlashOldStyle() {}

        /*
         * A Flash sim's jar file contains a .swf file. 
         */
        public static boolean isFlash( String jarFileName ) throws SimulationException {

            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream( jarFileName );
            }
            catch ( FileNotFoundException e ) {
                e.printStackTrace();
                throw new SimulationException( "jar file not found: " + jarFileName, e );
            }

            JarInputStream jarInputStream = null;
            boolean containsSWF = false;
            try {
                jarInputStream = new JarInputStream( inputStream );

                // look for the .swf file
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
            }
            catch ( IOException e ) {
                e.printStackTrace();
                throw new SimulationException( "error reading jar file: " + jarFileName, e );
            }

            return containsSWF;
        }

        public static SimulationProperties createSimulationProperties( String jarFileName ) throws SimulationException {
            String projectName = getProjectName( jarFileName );
            String simulation = projectName; // Flash sims that predate simulations.properties had only one sim per project
            Locale locale = TUConstants.SOURCE_LOCALE;
            String type = SimulationProperties.TYPE_FLASH;
            return new SimulationProperties( projectName, simulation, locale, type );
        }

        /*
         * Gets the project name, based on the JAR file name.
         * The JAR file name may or may not contain a language code.
         * For example, acceptable file names for the "curve-fit" project are curve-fit.jar and curve-fit_fr.jar.
         * Returns null if the project name wasn't found.
         */
        private static String getProjectName( String jarFileName ) throws SimulationException {
            String projectName = null;
            File jarFile = new File( jarFileName );
            String name = jarFile.getName();
            int index = name.indexOf( "_" );
            if ( index != -1 ) {
                // eg, curve-fit_fr.jar
                projectName = name.substring( 0, index );
            }
            else {
                // eg, curve-fit.jar
                index = name.indexOf( "." );
                if ( index != -1 ) {
                    projectName = name.substring( 0, index );
                }
            }
            return projectName;
        }
    }

    /**
     * Java sim utilities for creating SimulationProperties when no such file exists.
     * This class is provide solely for backward compatibility support, and it should 
     * be deleted when all sims have been redeployed and have a simulation.properties file.
     * @deprecated
     */
    private static class JavaOldStyle {

        private JavaOldStyle() {}

        public static SimulationProperties createSimulationProperties( String jarFileName ) throws SimulationException {
            String projectName = getProjectName( jarFileName );
            String simulation = getSimulationName( jarFileName );
            Locale locale = TUConstants.SOURCE_LOCALE;
            String type = SimulationProperties.TYPE_JAVA;
            return new SimulationProperties( projectName, simulation, locale, type );
        }

        /*
         * Gets the project name for the simulation.
         * The project name is identified in the properties file read by JARLauncher.
         * Returns null if the project name wasn't found.
         */
        private static String getProjectName( String jarFileName ) throws SimulationException {
            String projectName = null;
            Properties jarLauncherProperties = JarUtils.readPropertiesFromJar( jarFileName, JARLauncher.PROPERTIES_FILE_NAME );
            if ( jarLauncherProperties != null ) {
                projectName = jarLauncherProperties.getProperty( JARLauncher.PROJECT_NAME_KEY );
            }
            return projectName;
        }
        
        /*
         * Gets the simulation name (aka flavor) for the simulation.
         * The simulation name is identified in the properties file read by JARLauncher.
         * Returns null if the simulation name wasn't found.
         */
        private static String getSimulationName( String jarFileName ) throws SimulationException {
            String projectName = null;
            Properties jarLauncherProperties = JarUtils.readPropertiesFromJar( jarFileName, JARLauncher.PROPERTIES_FILE_NAME );
            if ( jarLauncherProperties != null ) {
                projectName = jarLauncherProperties.getProperty( JARLauncher.FLAVOR_KEY );
            }
            return projectName;
        }
    }
}
