/* Copyright 2010, University of Colorado */

package edu.colorado.phet.translationutility.jar;

import java.io.IOException;
import java.util.Locale;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import edu.colorado.phet.common.phetcommon.application.JARLauncher;

/**
 * Creates jar files for Java simulations.
 */
public class JavaJarCreator extends JarCreator {
    
    Properties jarLauncherProperties;
    
    /*
     * Reads files from the input JAR that are required by Java sims.
     */
    protected void readJarEntries( String inputJarName, Locale locale ) throws IOException {
        // read jar-launcher.properties
        jarLauncherProperties = JarUtils.readProperties( inputJarName, JARLauncher.PROPERTIES_FILE_NAME );
        if ( jarLauncherProperties == null ) {
            throw new IOException( "Cannot read " + JARLauncher.PROPERTIES_FILE_NAME + ". Are you using the most current simulation JAR from the PhET website?" );
        }
    }
    
    /*
     * Writes localized strings to the output JAR in Properties format.
     */
    protected void writeStringsFile( JarOutputStream jarOutputStream, Locale locale, Properties localizedStrings, String stringsFilePath, String header ) throws IOException {
        JarEntry jarEntry = new JarEntry( stringsFilePath );
        jarOutputStream.putNextEntry( jarEntry );
        localizedStrings.store( jarOutputStream, header );
        jarOutputStream.closeEntry();
    }
    
    /*
     * Write files to the output JAR that are required for Java sims.
     * Any files written here must also be excluded via getCopyExclusions.
     */
    protected void writeJarEntries( JarOutputStream jarOutputStream, String projectName, Locale locale, String header ) throws IOException {
        // add jar-lanucher.properties, after changing locale
        jarLauncherProperties.setProperty( "language", locale.getLanguage() );
        String country = locale.getCountry();
        if ( country != null && country.length() > 0 ) {
            jarLauncherProperties.setProperty( "country", country );
        }
        JarEntry jarEntry = new JarEntry( JARLauncher.PROPERTIES_FILE_NAME );
        jarOutputStream.putNextEntry( jarEntry );
        jarLauncherProperties.store( jarOutputStream, header );
        jarOutputStream.closeEntry();
    }
    
    /*
     * Regular expressions for files that should be skipped when copying the jar for a Java sim.
     * These exclusions are specific to Java sims, general exclusions are provided by the super class.
     */
    protected String[] getCopyExclusions() {
        return new String[] { JARLauncher.PROPERTIES_FILE_NAME };
    }
}