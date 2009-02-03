/* Copyright 2008, University of Colorado */

package edu.colorado.phet.flashlauncher;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.*;

import edu.colorado.phet.flashlauncher.util.BareBonesBrowserLaunch;
import edu.colorado.phet.flashlauncher.util.FileUtils;
import edu.colorado.phet.flashbuild.util.FlashHTML;

/**
 * FlashLauncher is the mechanism for launching Flash simulations.
 * It is bundled into a JAR file as the mainclass.
 * An args file in the JAR identifies the simulation properties
 * which are used to fill in an HTML template.
 * The JAR file is unzipped to a temporary directory.
 * A web browser is launched, and pointed at the HTML.
 *
 * @author Sam Reid
 *
 * Modified by Jonathan Olson to support the new HTML generation for Flash simulations
 *
 */
public class FlashLauncher {

    private static final String ARGS_FILENAME = "flash-launcher-args.txt";

    private String simName;
    private String language;
    private String country;
    private String deployment;
    private String distributionTag;
    private String installationTimestamp;
    private String installerCreationTimestamp;
    private static JTextArea jTextArea;

    public FlashLauncher() throws IOException {

        // read sim and language from args file (JAR resource)
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream( ARGS_FILENAME );
        BufferedReader bu = new BufferedReader( new InputStreamReader( inputStream ) );
        String line = bu.readLine();
        StringTokenizer stringTokenizer = new StringTokenizer( line, " " );
        println( "line = " + line );
        this.simName = stringTokenizer.nextToken();
        this.language = stringTokenizer.nextToken();
        this.country = stringTokenizer.nextToken();
        this.deployment = stringTokenizer.nextToken();
        this.distributionTag = stringTokenizer.nextToken();
        this.installationTimestamp = stringTokenizer.nextToken();
        this.installerCreationTimestamp = stringTokenizer.nextToken();

        // if the developer flag is specified in args file, open a window to show debug output
        if ( stringTokenizer.hasMoreTokens() && stringTokenizer.nextToken().equals( "-dev" ) ) {
            println( "FlashLauncher.FlashLauncher dev" );
            JFrame frame = new JFrame( "Text" );
            jTextArea = new JTextArea( 10, 50 );
            frame.setContentPane( new JScrollPane( jTextArea ) );
            frame.setVisible( true );
            frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
            frame.setSize( 800, 600 );
        }
    }

    /**
     * Prints to the debug window, if it exists.
     */
    public static void println( String string ) {
        System.out.println( string );
        if ( jTextArea != null ) {
            jTextArea.append( string + "\n" );
        }
    }

    /*
     * Launches the simulation in a web browser.
     */
    private void start() throws IOException {
        println( "FlashLauncher.start" );
        String unzipDirName = System.getProperty( "java.io.tmpdir" ) + System.getProperty( "file.separator" ) + "phet-" + simName;
        println( "unzipping to directory = " + unzipDirName );
        File unzipDir = new File( unzipDirName );

        // unzip the JAR into temp directory
        File jarfile = getJARFile();
        println( "jarfile = " + jarfile );
        println( "Starting unzip jarfile=" + jarfile + ", unzipDir=" + unzipDir );
        FileUtils.unzip( jarfile, unzipDir );
        println( "Finished unzip" );

        // read the properties file
        Properties properties = readProperties( simName );

        // pull the version information from the properties file
        String versionMajor, versionMinor, versionDev, versionRevision;
        versionMajor = properties.getProperty( "version.major" );
        versionMinor = properties.getProperty( "version.minor" );
        versionDev = properties.getProperty( "version.dev" );
        versionRevision = properties.getProperty( "version.revision" );

        // read the background color property
        String bgcolor = readBackgroundColor( properties );

        // get the locale string
        String locale = FlashHTML.localeString( language, country );

        // files where the simulation and common internationalization XML should be.
        // if they don't exist, replace with defaults
        File simXMLFile = new File( unzipDir, simName + "-strings_" + locale + ".xml" );
        if ( !simXMLFile.exists() ) {
            simXMLFile = new File( unzipDir, simName + "-strings_en.xml" );
            println( "WARNING: could not find sim strings for " + locale + ", using default en." );
        }
        File commonXMLFile = new File( unzipDir, "common-strings_" + locale + ".xml" );
        if ( !commonXMLFile.exists() ) {
            commonXMLFile = new File( unzipDir, "common-strings_en.xml" );
            println( "WARNING: could not find common strings for " + locale + ", using default en." );
        }

        // encoded XML for sim and common strings
        String simEncodedXML = FlashHTML.encodeXMLFile( simXMLFile );
        String commonEncodedXML = FlashHTML.encodeXMLFile( commonXMLFile );

        // dynamically generate an HTML file
        String html = FlashHTML.generateHTML( simName, language, country, deployment, distributionTag, installationTimestamp,
                installerCreationTimestamp, versionMajor, versionMinor, versionDev, versionRevision, bgcolor,
                simEncodedXML, commonEncodedXML, "8" );
        File htmlFile = new File( unzipDir, simName + "_" + language + ".html" );
        FileOutputStream outputStream = new FileOutputStream( htmlFile );
        outputStream.write( html.getBytes() );
        outputStream.close();

        // open the browser, point it at the HTML file
        println( "Starting openurl" );
        BareBonesBrowserLaunch.openURL( "file://" + htmlFile.getAbsolutePath() );
    }

    private static Properties readProperties( String sim ) {
        String propertiesFileName = sim + ".properties";
        InputStream inStream = Thread.currentThread().getContextClassLoader().getResourceAsStream( propertiesFileName );
        Properties properties = new Properties();
        try {
            properties.load( inStream );
        }
        catch( IOException e ) {
            e.printStackTrace(); //TODO handle this better
        }
        return properties;
    }

    private static String readBackgroundColor( Properties properties ) {
        String bgcolor = properties.getProperty( "bgcolor" );
        if ( bgcolor == null ) {
            bgcolor = "#ffffff"; // white
        }
        return bgcolor;
    }

    /*
     * Gets the JAR file that this class was launched from.
     */
    private File getJARFile() {
        URL url = FlashLauncher.class.getProtectionDomain().getCodeSource().getLocation();
        return new File( url.getFile() );
    }

    public static void main( String[] args ) throws IOException {
//        JOptionPane.showMessageDialog( null, System.getProperty( "java.class.path" ) );
        new FlashLauncher().start();
    }
}
