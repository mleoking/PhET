/* Copyright 2010, University of Colorado */

package edu.colorado.phet.flashlauncher.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.Properties;

/**
 * This encapsulates a .properties file named "simulation.properties" that contains
 * information used by FlashLauncher and TranslationUtility for launching Java and Flash sims.
 * <p/>
 * This class lives in the flashlauncher package because FlashLauncher needs to be Java 1.4 compliant,
 * and we want to keep FlashLauncher as small as possible.
 *
 * @author Sam Reid
 * @author Jon Olson
 */
public class SimulationProperties {
    
    public static final String SIMULATION_PROPRTIES_FILENAME = "simulation.properties";

    private static final String KEY_PROJECT = "project";
    private static final String KEY_SIMULATION = "simulation";
    private static final String KEY_LANGUAGE = "language";
    private static final String KEY_COUNTRY = "country";
    private static final String KEY_TYPE = "type";
    
    // simulation types. An enum would be preferable, but flash-launcher must be Java 1.4 compatible.
    public static final String TYPE_JAVA = "java";
    public static final String TYPE_FLASH = "flash";
    
    // properties in this file
    private String project;
    private String simulation;
    private String language;
    private String country;
    private String type; // the type of simulation

    public SimulationProperties() {
        this( Thread.currentThread().getContextClassLoader().getResourceAsStream( SIMULATION_PROPRTIES_FILENAME ) );
    }
    
    public SimulationProperties(InputStream inputStream) {
        // read sim and language from args file (JAR resource)        
        Properties properties = new Properties();
        try {
            properties.load( inputStream );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        this.project = properties.getProperty( KEY_PROJECT );
        this.simulation = properties.getProperty( KEY_SIMULATION );
        this.language = properties.getProperty( KEY_LANGUAGE );
        this.country = properties.getProperty( KEY_COUNTRY );
        this.type = properties.getProperty( KEY_TYPE );
    }

    public SimulationProperties( String project, String simulation, Locale locale, String type ) {
        this( project, simulation, locale.getLanguage(), locale.getCountry(), type );
    }
    
    public SimulationProperties( String project, String simulation, String language, String country, String type ) {
        this.project = project;
        this.simulation = simulation;
        this.language = language;
        this.country = country;
        this.type = type;
    }

    //TODO: This is currently a stub for which we may drop or add support, not sure now
    public boolean isDevelopment() {
        return false;
    }

    public String getSimulation() {
        return simulation;
    }

    public String getProject() {
        return project;
    }

    public String getLanguage() {
        return language;
    }

    public String getCountry() {
        return country;
    }

    public String getType() {
        return type;
    }
    
    public boolean isJava(){
        return type.equals( TYPE_JAVA );
    }
    
    public boolean isFlash(){
        return type.equals( TYPE_FLASH );
    }
    
    public void store( OutputStream fileOutputStream, String comments ) throws IOException {
        Properties properties = new Properties();
        properties.setProperty( KEY_PROJECT, project );
        properties.setProperty( KEY_SIMULATION, simulation );
        properties.setProperty( KEY_LANGUAGE, language );
        properties.setProperty( KEY_COUNTRY, country );
        properties.setProperty( KEY_TYPE, type );
        properties.store( fileOutputStream, comments );
    }
}
