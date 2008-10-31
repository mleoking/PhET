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

/**
 * FlashLauncher is the mechanism for launching Flash simulations.
 * It is bundled into a JAR file as the mainclass.
 * An args file in the JAR identifies the simulation name and language code,
 * which are used to fill in an HTML template.
 * The JAR file is unzipped to a temporary directory.
 * A web browser is launched, and pointed at the HTML.
 *
 * @author Sam Reid
 */
public class FlashLauncher {

    private static final String ARGS_FILENAME = "flash-launcher-args.txt";
    private static final String HTML_TEMPLATE = "flash-launcher-template.html";
    private static final String VERSION_FORMAT = "{0}.{1}.{2} ({3})";

    private String sim;
    private String language;
    private static JTextArea jTextArea;

    public FlashLauncher() throws IOException {

        // read sim and language from args file (JAR resource)
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream( ARGS_FILENAME );
        BufferedReader bu = new BufferedReader( new InputStreamReader( inputStream ) );
        String line = bu.readLine();
        StringTokenizer stringTokenizer = new StringTokenizer( line, " " );
        println( "line = " + line );
        this.sim = stringTokenizer.nextToken();
        this.language = stringTokenizer.nextToken();

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
        String unzipDirName = System.getProperty( "java.io.tmpdir" ) + System.getProperty( "file.separator" ) + "phet-" + sim;
        println( "unzipping to directory = " + unzipDirName );
        File unzipDir = new File( unzipDirName );

        // unzip the JAR into temp directory
        File jarfile = getJARFile();
        println( "jarfile = " + jarfile );
        println( "Starting unzip jarfile=" + jarfile + ", unzipDir=" + unzipDir );
        FileUtils.unzip( jarfile, unzipDir );
        println( "Finished unzip" );

        // read the properties file
        Properties properties = readProperties( sim );

        // read the version properties
        String version = readVersion( properties );

        // read the background color property
        String bgcolor = readBackgroundColor( properties );

        // dynamically generate an HTML file
        String html = generateHTML( sim, language, version, bgcolor );
        File htmlFile = new File( unzipDir, sim + "_" + language + ".html" );
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

    private static String readVersion( Properties properties ) {
        // read the version properties
        String major = properties.getProperty( "version.major" );
        String minor = properties.getProperty( "version.minor" );
        String dev = properties.getProperty( "version.dev" );
        String revision = properties.getProperty( "version.revision" );

        // format the version string
        Object[] args = {major, minor, dev, revision};

        return MessageFormat.format( VERSION_FORMAT, args );
    }

    private static String readBackgroundColor( Properties properties ) {
        String bgcolor = properties.getProperty( "bgcolor" );
        if ( bgcolor == null ) {
            bgcolor = "#ffffff"; // white
        }
        return bgcolor;
    }

    /*
     * Reads the HTML template and fills in the blanks for sim, language and version.
     */
    private static String generateHTML( String sim, String language, String version, String bgcolor ) throws IOException {
        String s = "";
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream( HTML_TEMPLATE );
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( inputStream ) );
        String line = bufferedReader.readLine();
        while ( line != null ) {
            s += line;
            line = bufferedReader.readLine();
            if ( line != null ) {
                s += System.getProperty( "line.separator" );
            }
        }
        s = s.replaceAll( "@SIM@", sim );
        s = s.replaceAll( "@LANGUAGE@", language );
        s = s.replaceAll( "@VERSION@", version );
        s = s.replaceAll( "@BGCOLOR@", bgcolor );
        return s;
    }

    /*
     * Gets the JAR file that this class was launched from.
     */
    private File getJARFile() {
        URL url = FlashLauncher.class.getProtectionDomain().getCodeSource().getLocation();
        try {
            URI uri = new URI( url.toString() );
            return new File( uri.getPath() );
        }
        catch( URISyntaxException e ) {
            println( e.getMessage() );
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }

    public static void main( String[] args ) throws IOException {
//        JOptionPane.showMessageDialog( null, System.getProperty( "java.class.path" ) );
        new FlashLauncher().start();
    }
}
