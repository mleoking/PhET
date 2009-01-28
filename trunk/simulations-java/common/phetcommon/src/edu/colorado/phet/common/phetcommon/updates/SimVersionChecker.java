package edu.colorado.phet.common.phetcommon.updates;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import edu.colorado.phet.common.phetcommon.PhetCommonConstants;
import edu.colorado.phet.common.phetcommon.resources.PhetVersion;

/**
 * Gets the most up-to-date version information from the PhET website's version service.
 */
public class SimVersionChecker {

    public SimVersionChecker() {}

    /**
     * Gets the version info for the sim, as it is currently posted on the PhET web site. 
     * @param project
     * @param simulation
     * @return
     * @throws IOException
     */
    public PhetVersion getVersion( String project, String simulation ) throws IOException {
        String xmlString = getXMLString( project, simulation );
         System.out.println( "SimVersionChecker xmlString=" + xmlString );
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse( new ByteArrayInputStream( xmlString.getBytes() ) );
            String version = document.getDocumentElement().getAttribute( "version" );
            String revision = document.getDocumentElement().getAttribute( "revision" );
            StringTokenizer t = new StringTokenizer( version, "." );
            return new PhetVersion( t.nextToken(), t.nextToken(), t.nextToken(), revision );
        }
        catch ( Exception e ) {
            //bundle this exception as IOException to use same error handling on the client side
            throw new IOException( "Failed to obtain version information from XML" );
        }
    }

    /*
     * Reads an XML string from a web service.
     */
    private static String getXMLString( String project, String simulation ) throws IOException {
        URL url = getServiceURL( project, simulation );
        BufferedReader in = new BufferedReader( new InputStreamReader( url.openStream() ) );
        String inputLine;
        String totalText = "";
        while ( ( inputLine = in.readLine() ) != null ) {
            if ( totalText.length() > 0 ) {
                totalText += "\n";
            }
            totalText += inputLine;
        }
        in.close();
        return totalText;
    }
    
    /*
     * Gets the URL of a PHP script that returns the sim version info.
     * This service provides the info in XML form, see Unfuddle #969 and #1063.
     */
    private static URL getServiceURL( String project, String sim ) throws MalformedURLException {
        return new URL( PhetCommonConstants.PHET_HOME_URL + "/simulations/sim-version-info.php?project=" + project + "&sim=" + sim );
    }

    public static void main( String[] args ) throws IOException {
        System.out.println( "SimVersionChecker.main" );
        PhetVersion phetVersionInfo = new SimVersionChecker().getVersion( "balloons", "balloons" );
        System.out.println( "phetVersionInfo = " + phetVersionInfo );
    }
}