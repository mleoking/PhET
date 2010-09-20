/* Copyright 2010, University of Colorado */

package edu.colorado.phet.translationutility.jar;

import java.io.IOException;
import java.util.Locale;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import edu.colorado.phet.flashlauncher.FlashLauncher;

/**
 * Creates jar files for Flash simulations.
 */
public class FlashJarCreator extends JarCreator {
    
    /*
     * Reads files from the input JAR that are required by Flash sims.
     */
    protected void readJarEntries( String inputJarName, Locale locale ) {
        // nothing to read
    }
    
    /*
     * Writes localized strings to the output JAR in XML format.
     */
    protected void writeStringsFile( JarOutputStream jarOutputStream, Locale locale, Properties localizedStrings, String stringsFilePath, String header ) throws IOException {
        JarEntry jarEntry = new JarEntry( stringsFilePath );
        jarOutputStream.putNextEntry( jarEntry );
        try {
            DocumentAdapter.writeProperties( localizedStrings, header, jarOutputStream );
        }
        catch ( DocumentIO.DocumentIOException e ) {
            throw new IOException( "problem converting strings to XML", e );
        }
        jarOutputStream.closeEntry();
    }
    
    /*
     * Write files to the output JAR that are required for Flash sims.
     * Any files written here must also be excluded via getCopyExclusions.
     */
    protected void writeJarEntries( JarOutputStream jarOutputStream, String projectName, Locale locale, String header ) throws IOException {
        // flash-launcher-args.txt
        writeFlashLauncherArgs( jarOutputStream, projectName, locale );
    }
    
    /*
     * Regular expressions for files that should be skipped when copying the jar for a Flash sim.
     * These exclusions are specific to Flash sims, general exclusions are provided by the super class.
     */
    protected String[] getCopyExclusions() {
        return new String[] { FlashLauncher.ARGS_FILENAME };
    }

    /*
     * Writes flash-launcher-args.txt, used by older FlashLauncher.
     * All sims now use simulation.properties instead of flash-launcher-args.txt, but this 
     * we still need to generate flash-launcher-args.txt to support translation deployment for older deployed jars.
     * Delete this method when all sims have been redeployed with simulation.properties. See #2463.
     * @deprecated
     */
    private static void writeFlashLauncherArgs( JarOutputStream outputStream, String projectName, Locale locale ) throws IOException {
        // create the args string
        String language = locale.getLanguage();
        String country = locale.getCountry();
        String args = projectName + " " + language;
        if ( country == null || country.length() == 0 ) {
            args += " " + "null";
        }
        else {
            args += " " + country;
        }
        // write the args string
        JarEntry jarEntry = new JarEntry( FlashLauncher.ARGS_FILENAME );
        outputStream.putNextEntry( jarEntry );
        outputStream.write( args.getBytes() );
        outputStream.closeEntry();
    }
}